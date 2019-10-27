package cn.pipilu.plus.common.excel.annotation;

import cn.pipilu.plus.common.constant.AlignE;
import cn.pipilu.plus.common.constant.FieldExportTypeE;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * excel 字段导出注解
 */
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExportField {
    /**
     * 字段名（默认调用当前字段的 "get" 方法，如果指定的导出字段为对象，填写为“对象名.对象属性”
     * 例如：导出用户名
     * @ExcelField(value = "user.name")
     *  private User user;
     * @return
     */
    String value() default StringUtils.EMPTY;

    /**
     * 导出字段的标题（需要添加批注，请用“**”分割，仅对导出模板有效）
     * @return
     */
    String title();

    /**
     * 导出对齐方式（0-自动、1-靠左、2-居中、3-靠右）
     * @return
     */
    AlignE align() default AlignE.CENTER;

    /**
     * 导出字段排序，升序
     * @return
     */
    int sort() default 0;

    /**
     * 如果是字典类型，设置字典类型的 type
     * 例如：
     * @ExcelField(title = "用户类型",type = 1,align=2,sort = 2,dictType="sys_user_type")
     * private String userType;
     * @return
     */
    String dictType() default StringUtils.EMPTY;

}
