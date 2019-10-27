package cn.pipilu.plus.common.util;

import cn.pipilu.plus.common.constant.ExcelExportTypeE;
import cn.pipilu.plus.common.excel.annotation.ExportField;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.*;

/**
 * 导出 Excel 文件，（导出“XLSX" 格式，支持大数据量导出）
 */
public class ExportUtil {
    private static Logger logger = LoggerFactory.getLogger(ExportUtil.class);

    private SXSSFWorkbook workbook;                                                       // 工作簿对象
    private Sheet sheet;                                                          // 工作表对象
    private Map<String, CellStyle> stylesMap;                                                      // 样式列表
    private int rowNum;                                                         // 当前行号
    private List<Object[]> annotationList = Lists.newArrayList();                          // 注解列表
    /**
     * 构造方法
     *
     * @param cls   实体对象，通过 annotation.ExportField 获取标题
     */
    public ExportUtil(Class<?> cls) {
        this(null, "Export", cls, ExcelExportTypeE.EXPORT_DATA);
    }

    /**
     * 构造方法
     *
     * @param title 表格标题，传“空值“，表示无标题
     * @param cls   实体对象，通过 annotation.ExportField 获取标题
     */
    public ExportUtil(String title, Class<?> cls) {
        this(title, "Export", cls, ExcelExportTypeE.EXPORT_DATA);
    }

    /**
     * 构造方法
     *
     * @param title     表格标题，传“空值“，表示无标题
     * @param sheetName 工作表名
     * @param cls       实体对象，通过 annotation.ExportField 获取标题
     */
    public ExportUtil(String title, String sheetName, Class<?> cls) {
        this(title, sheetName, cls, ExcelExportTypeE.EXPORT_DATA);
    }

    /**
     * 构造函数
     *
     * @param title 表格标题
     * @param cls   实体对象，通过 annotation.ExportField 获取标题
     * @param type  导出类型，1-导出数据、2-导出模板
     */
    public ExportUtil(String title, String sheetName, Class<?> cls, ExcelExportTypeE type) {
        // 获取 注解字段
        Field[] fields = cls.getDeclaredFields();
        ExportField exportField;
        for (Field field : fields) {
            exportField = field.getAnnotation(ExportField.class);
            if (Objects.nonNull(exportField)) {
                annotationList.add(new Object[]{exportField, field});
            }
        }

        // 获取注解方法
        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods) {
            exportField = method.getAnnotation(ExportField.class);
            if (Objects.nonNull(exportField)) {
                annotationList.add(new Object[]{exportField, method});
            }
        }

        // 字段排序
        annotationList.sort(Comparator.comparing(obj -> Integer.valueOf(((ExportField) obj[0]).sort())));
        // 初始化
        List<String> headerList = Lists.newLinkedList();
        for (Object[] os : annotationList) {
            String titleName = ((ExportField) os[0]).title();
//            if (ExcelExportTypeE.EXPORT_DATA == type) {
//                String[] ss = StringUtils.split(titleName, "**", 2);
//                if (ss.length == 2) {
//                    titleName = ss[0];
//                }
//            }
            headerList.add(titleName);
        }
        initialize(title, sheetName, headerList);
    }

    /**
     * 构造函数
     *
     * @param title   表格标题，传“空值”，表示无标题
     * @param headers 表头数组
     */
    public ExportUtil(String title, String sheetName, String[] headers) {
        initialize(title, sheetName, Lists.newArrayList(headers));
    }

    /**
     * 构造函数
     *
     * @param title      表格标题，传“空值”，表示无标题
     * @param headerList 表头列表
     */
    public ExportUtil(String title, String sheetName, List<String> headerList) {
        initialize(title, sheetName, headerList);
    }

    public ExportUtil(String title, List<String> headerList) {
        initialize(title, "Export", headerList);
    }

    /**
     * 初始化函数
     *
     * @param title      表格标题
     * @param headerList 表头列表
     */
    private void initialize(String title, String sheetName, List<String> headerList) {
        if (CollectionUtils.isEmpty(headerList)) {
            logger.info("导出表格，标题：{}，头信息为空", title);
            throw new RuntimeException("表头信息不能为空");
        }
        workbook = new SXSSFWorkbook(500);
        sheet = workbook.createSheet(sheetName);
        stylesMap = createStyles(workbook);
        // 创建 title
        int headerSize = headerList.size();
        if (StringUtils.isNoneBlank(title)) {
            Row titleRow = sheet.createRow(rowNum++);
            titleRow.setHeightInPoints(30);

            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellStyle(stylesMap.get("title"));
            titleCell.setCellValue(title);
            int rowNum = titleRow.getRowNum();
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, rowNum, headerSize - 1));
        }
        // 创建头
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.setHeightInPoints(16);
        for (int i = 0; i < headerSize; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellStyle(stylesMap.get("header"));
//            String[] ss = StringUtils.split(headerList.get(i), "**", 2);
//            if (ss.length == 2) {
//                cell.setCellValue(ss[0]);
//                Comment comment = this.sheet.createDrawingPatriarch().createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
//                comment.setString(new XSSFRichTextString(ss[1]));
//                cell.setCellComment(comment);
//            } else {
                cell.setCellValue(headerList.get(i));
           // }
            sheet.autoSizeColumn(i);
        }
        for (int i = 0; i < headerList.size(); i++) {
            int colWidth = sheet.getColumnWidth(i) * 2;
            sheet.setColumnWidth(i, colWidth < 3000 ? 3000 : colWidth);
        }
        logger.info("表格:{},初始化完成 success.", title);
    }

    /**
     * 创建表格样式
     *
     * @param workbook 工作簿对象
     * @return 样式列表
     */
    private Map<String, CellStyle> createStyles(SXSSFWorkbook workbook) {
        Map<String, CellStyle> styles = new HashMap<>();
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(font);
        styles.put("title", style);

        style = workbook.createCellStyle();
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        styles.put("data", style);

        style = workbook.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(CellStyle.ALIGN_LEFT);
        styles.put("data1", style);

        style = workbook.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(CellStyle.ALIGN_CENTER);
        styles.put("data2", style);

        style = workbook.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        styles.put("data3", style);

        style = workbook.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        // style.setWrapText(true);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        styles.put("header", style);
        return styles;
    }

    /**
     * 添加一行
     *
     * @return 行对象
     */
    public Row addRow() {
        return sheet.createRow(rowNum++);
    }

    /**
     * 添加一个单元格
     *
     * @param row    添加的行
     * @param column 添加列号
     * @param val    添加值
     * @return 单元格对象
     */
    public Cell addCell(Row row, int column, Object val) {
        return this.addCell(row, column, val, 0);
    }

    /**
     * 添加一个单元格
     *
     * @param row    添加的行
     * @param column 添加列号
     * @param val    添加值
     * @param align  对齐方式（1：靠左；2：居中；3：靠右）
     * @return 单元格对象
     */
    public Cell addCell(Row row, int column, Object val, int align) {
        Cell cell = row.createCell(column);
        String cellFormatString = "@";
        try {
            if (val == null) {
                cell.setCellValue("");
            } else {
                if (val instanceof String) {
                    cell.setCellValue((String) val);
                } else if (val instanceof Integer) {
                    cell.setCellValue((Integer) val);
                    cellFormatString = "0";
                } else if (val instanceof Long) {
                    cell.setCellValue((Long) val);
                    cellFormatString = "0";
                } else if (val instanceof Double) {
                    cell.setCellValue((Double) val);
                    cellFormatString = "0.00";
                } else if (val instanceof Float) {
                    cell.setCellValue((Float) val);
                    cellFormatString = "0.00";
                } else if (val instanceof Date) {
                    cell.setCellValue((Date) val);
                    cellFormatString = "yyyy-MM-dd HH:mm";
                } else {
                    logger.info("{}",this.getClass().getName());
//                    cell.setCellValue((String) Class.forName(this.getClass().getName().replaceAll(this.getClass().getSimpleName(), "fieldtype." + val.getClass().getSimpleName() + "Type"))
//                            .getMethod("setValue", Object.class).invoke(null, val));
                }
            }
            if (val != null) {
                CellStyle style = stylesMap.get("data_column_" + column);
                if (style == null) {
                    style = workbook.createCellStyle();
                    style.cloneStyleFrom(stylesMap.get("data" + (align >= 1 && align <= 3 ? align : "")));
                    style.setDataFormat(workbook.createDataFormat().getFormat(cellFormatString));
                    stylesMap.put("data_column_" + column, style);
                }
                cell.setCellStyle(style);
            }
        } catch (Exception e) {
            logger.error("Set cell value[{},{}]", row.getRowNum(), column, e);
            cell.setCellValue(val.toString());
        }
        return cell;
    }

    /**
     * 添加数据（通过annotation.ExportField添加数据）
     *
     * @return list 数据列表
     */
    public <E> ExportUtil setDataList(List<E> list) {
        for (E e : list) {
            int colunm = 0;
            Row row = this.addRow();
            StringBuilder sb = new StringBuilder();
            for (Object[] os : annotationList) {
                ExportField ef = (ExportField) os[0];
                Object val = null;

                    if (StringUtils.isNotBlank(ef.value())) {
                        val = Reflections.invokeGetter(e, ef.value());
                    } else {
                        if (os[1] instanceof Field) {
                            val = Reflections.invokeGetter(e, ((Field) os[1]).getName());
                        } else if (os[1] instanceof Method) {
                            val = Reflections.invokeMethod(e, ((Method) os[1]).getName(), new Class[]{}, new Object[]{});
                        }
                    }
                    if (StringUtils.isNotBlank(ef.dictType())) {
                        val = DictUtil.getDictLabel(Objects.isNull(val) ? "" : val.toString(), ef.dictType(), "");
                        if (StringUtils.isBlank(val.toString())){
                            throw new RuntimeException("字典类型错误");
                        }
                    }

                this.addCell(row, colunm++, val, ef.align().code);
                sb.append(val + ", ");
            }
        }
        return this;
    }

    /**
     * 输出数据流
     *
     * @param os 输出数据流
     */
    public ExportUtil write(OutputStream os) throws IOException {
        workbook.write(os);
        return this;
    }

    /**
     * 输出到客户端
     *
     * @param fileName 输出文件名
     */

    public ExportUtil write2(HttpServletResponse response, String fileName) throws IOException {
        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
        write(response.getOutputStream());
        return this;
    }

    public ExportUtil write(HttpServletRequest request, HttpServletResponse response, String fileName) throws IOException {
        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");

        // response.getHeader(arg0)
        // 中文文件名支持
        String header = request.getHeader("User-Agent").toUpperCase();
        if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE")) {
            fileName = URLEncoder.encode(fileName, "utf-8");
            fileName = fileName.replace("+", "%20"); // IE下载文件名空格变+号问题
        } else {
            fileName = new String(fileName.getBytes(), "ISO8859-1");
        }

        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        write(response.getOutputStream());
        return this;
    }

    /**
     * 输出到文件
     *
     * @param fileName 输出文件名
     */
    public ExportUtil writeFile(String fileName) throws IOException {
        FileOutputStream os = new FileOutputStream(fileName);
        this.write(os);
        return this;
    }

    /**
     * 清理临时文件
     */
    public ExportUtil dispose() {
        workbook.dispose();
        return this;
    }
}
