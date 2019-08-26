package cn.pipilu.plus.common.validate;

public enum RegexType {

    /**
     * 不需要校验
     */
    NONE,

    /**
     * 校验是否含有特殊字符
     */
    SPECIALCHAR,

    /**
     * 校验是否含有中文
     */
    CHINESE,

    /**
     * 校验是否是合法邮箱地址
     */
    EMAIL,

    /**
     * 校验是否是合法IP
     */
    IP,

    /**
     * 校验是否是正整数
     */
    NUMBER,

    /**
     * 校验是否是合法手机号
     */
    PHONENUMBER
}