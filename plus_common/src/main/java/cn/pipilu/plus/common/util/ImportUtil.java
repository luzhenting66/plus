package cn.pipilu.plus.common.util;

import cn.pipilu.plus.common.constant.RequiredE;
import cn.pipilu.plus.common.excel.annotation.ImportField;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 导入工具类
 */
@Service
public class ImportUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportUtil.class);
    private Workbook workbook;                                               // 工作薄对象
    private Sheet sheet;                                                  // 工作表对象
    private int headerNum;                                              // 标题行号


    private ImportUtil importExcel(String fileName,InputStream is,int headerNum,int sheetIndex) throws IOException {
        if (StringUtils.isBlank(fileName)) {
            throw new RuntimeException("导入文档为空!");
        } else if (fileName.toLowerCase().endsWith("xls")) {
            this.workbook = new HSSFWorkbook(is);
        } else if (fileName.toLowerCase().endsWith("xlsx")) {
            this.workbook = new XSSFWorkbook(is);
        } else {
            throw new RuntimeException("文档格式不正确!");
        }
        if (this.workbook.getNumberOfSheets() < sheetIndex) {
            throw new RuntimeException("文档中没有工作表!");
        }
        this.sheet = this.workbook.getSheetAt(sheetIndex);
        this.headerNum = headerNum;
        LOGGER.info("初始化 success.");
        return this;
    }
    public ImportUtil importMultipartFile(MultipartFile multipartFile, int headerNum, int sheetIndex) throws IOException {
        return importExcel(multipartFile.getOriginalFilename(),new BufferedInputStream(multipartFile.getInputStream()),headerNum,sheetIndex);
    }
    public ImportUtil importFile(File file, int headerNum, int sheetIndex) throws IOException {
        return importExcel(file.getName(),new BufferedInputStream(new FileInputStream(file)),headerNum,sheetIndex);
    }

    /**
     * 获取行对象
     *
     * @param rownum
     * @return
     */
    private Row getRow(int rownum) {
        return this.sheet.getRow(rownum);
    }

    /**
     * 获取数据行号
     *
     * @return
     */
    private int getDataRowNum() {
        return headerNum + 1;
    }

    /**
     * 获取最后一个数据行号
     *
     * @return
     */
    private int getLastDataRowNum() {
        return this.sheet.getLastRowNum() + headerNum;
    }

    /**
     * 获取最后一个列号
     *
     * @return
     */
    private int getLastCellNum() {
        return this.getRow(headerNum).getLastCellNum();
    }

    /**
     * 获取单元格值
     *
     * @param row    获取的行
     * @param column 获取单元格列号
     * @return 单元格值
     */
    private Object getCellValue(Row row, int column) {
        Object val = "";
        try {
            Cell cell = row.getCell(column);
            if (cell != null) {
                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    val = cell.getNumericCellValue();
                } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                    val = cell.getStringCellValue();
                } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                    val = cell.getCellFormula();
                } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
                    val = cell.getBooleanCellValue();
                } else if (cell.getCellType() == Cell.CELL_TYPE_ERROR) {
                    val = cell.getErrorCellValue();
                }
            }
        } catch (Exception e) {
            return val;
        }
        return val;
    }

    /**
     * 获取导入数据列表
     *
     * @param cls 导入对象类型
     */
    public <E> Map<String,Object> getDataList(Class<E> cls) throws InstantiationException, IllegalAccessException {
        Map<String,Object>  resultMap = new HashMap<>(4);
        List<Object[]> annotationList = Lists.newArrayList();
        Field[] fs = cls.getDeclaredFields();
        ImportField ef2;
        for (Field f : fs) {
            ef2 = f.getAnnotation(ImportField.class);
            if (ef2 != null) {
                annotationList.add(new Object[]{ef2, f});
            }
        }
        // 字段排序
        annotationList.sort(Comparator.comparing(obj -> Integer.valueOf(((ImportField) obj[0]).sort())));


        int dataRowNum = this.getDataRowNum();//数据第一行

        int lastDataRowNum = this.getLastDataRowNum();//数据最后一行
        LOGGER.info("数据第一行：{}，数据最后一行：{}",dataRowNum,lastDataRowNum);
        List<E> dataList = Lists.newLinkedList();
        List<String> errorMsgList = Lists.newLinkedList();
        String line="";
        E e =null;
        Row row = null;
        StringBuilder valueBuilder = null;
        StringBuilder errorBuilder;
        Object val;
        ImportField ef;
        RequiredE require;
        String dictTypeName;
        String dictType;
        Class<?> valType;
        String regex;
        String s;
        for (int i = dataRowNum; i < lastDataRowNum; i++) {
            e = (E) cls.newInstance();
            int column = 0;
            row = this.getRow(i);
            valueBuilder = new StringBuilder();
            errorBuilder = new StringBuilder();//错误信息容器
            line = "第 "+(i+1)+" 行:  ";
            for (Object[] os : annotationList) {
                val = this.getCellValue(row, column++);//拿到列值
                ef = (ImportField) os[0];
                if (StringUtils.isBlank(ef.title())){
                    throw new RuntimeException(((Field) os[1]).getName()+" 字段的title属性缺失");
                }
                //进行非空判断
                require = ef.require();
                if (RequiredE.YES == require &&(Objects.isNull(val) ||StringUtils.isBlank(val.toString()))) {
                    //如果非空，val 却是空，记录错误信息
                    errorBuilder.append(ef.title() + " 没有数据请填写").append(" ; ");
                }

                if (val != null) {

                    // 如果是字典，拿到字典值,先判断字典类型合法性，再判断字典值合法性
                    dictTypeName = ef.dictType();
                    if (StringUtils.isNotBlank(dictTypeName)) {
                        if(!DictUtil.containsKey(dictTypeName)){
                            throw new RuntimeException(((Field) os[1]).getName()+"字段字典类型错误："+dictTypeName);
                        }

                        dictType = DictUtil.getDictValue(val.toString(), dictTypeName, "");
                        if (StringUtils.isBlank(dictType)) {
                            errorBuilder.append(ef.title() + " 不存在此类型" + val.toString()).append(" ; ");
                        }
                        val = dictType;
                    }
                    // 参数类型转换
                    valType = Class.class;
                    if (os[1] instanceof Field) {
                        valType = ((Field) os[1]).getType();
                    }
                    try {
                         regex = ef.regex();//正则匹配规则
                        if (StringUtils.isNotBlank(regex) && !Pattern.matches(regex, val.toString())) {
                            errorBuilder.append(ef.title() + " 不符合匹配规则" + val.toString()).append(" ; ");
                        }
                        if (valType == String.class) {
                            s = String.valueOf(val.toString());
                            if (StringUtils.endsWith(s, ".0")) {
                                val = StringUtils.substringBefore(s, ".0");
                            } else {
                                val = String.valueOf(val.toString());
                                //判断字符串长度是否大于最大长度
                                int maxLength = ef.maxLength();
                                int valLength = val.toString().length();
                                if (maxLength > 0 && valLength > maxLength) {
                                    errorBuilder.append(ef.title() + "限长" + maxLength + ",长度" + valLength).append(" ; ");
                                }
                            }
                        } else if (valType == Integer.class) {
                            val = Double.valueOf(val.toString()).intValue();
                        } else if (valType == Long.class) {
                            val = Double.valueOf(val.toString()).longValue();
                        } else if (valType == Double.class) {
                            val = Double.valueOf(val.toString());
                        } else if (valType == Float.class) {
                            val = Float.valueOf(val.toString());
                        } else if (valType == Date.class) {
                            val = DateUtil.getJavaDate((Double) val);
                        } else {
                            LOGGER.info("类型名：{}", valType);
                        }
                    } catch (Exception ex) {
                        LOGGER.error("导入异常： ", ex);
                        errorBuilder.append("发生异常："+ex.getMessage());
                        val = null;
                    }
                    if (os[1] instanceof Field) {
                        Reflections.invokeSetter(e, ((Field) os[1]).getName(), val);
                    }
                }
                valueBuilder.append(val + ", ");
            }

            LOGGER.info("Read success:{}，内容：{}", i+1, valueBuilder.toString());
            if (StringUtils.isNotBlank(errorBuilder.toString())) {
                errorMsgList.add(line + errorBuilder.toString());
            }
            if (CollectionUtils.isEmpty(errorMsgList)){
                dataList.add(e);
            }
        }

        //如果有不合法的数据,不能让导入，返回错误信息
        if (!CollectionUtils.isEmpty(errorMsgList)){
            resultMap.put("errorMsg",errorMsgList);
            dataList=null;
            return resultMap;
        }
        resultMap.put("dataList",dataList);
        return resultMap;
    }
}
