package cn.pipilu.plus.common.excel.annotation;

import cn.pipilu.plus.common.constant.RequiredE;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportField {
    /***
     * 表格字段显示名称
     * @return
     */
    String title() default StringUtils.EMPTY;
    /**
     * 字段是否必填，默认否
     * @return
     */
    RequiredE require() default RequiredE.YES;

    /**
     * 如果是字典，填入字典类型
     * @return
     */
    String dictType() default StringUtils.EMPTY;

    /**
     * 最大长度
     * @return
     */
    int maxLength() default 0;

    /**
     * 正则表达式，BigDecimal(10,2) "^([1-9][0-9]{7})+(.[0-9]{1,2})?$"
     * @return
     */
    String regex() default StringUtils.EMPTY;

    /**
     * 排序，excel模板中的字段顺序
     * @return
     */
    int sort();
}
