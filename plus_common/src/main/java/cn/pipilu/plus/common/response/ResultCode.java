package cn.pipilu.plus.common.response;

public enum ResultCode {

    /*============================ 通用编码 ==========================*/

    SUCCESS("000000", "成功"),
    FAIL("999999", "失败"),
    SYSTEM_ERROR("999998", "系统异常"),
    VALIDATE_ERROR("999997", "参数校验错误");
    public String code;
    public String lable;

    ResultCode(String key, String value) {
        this.code = key;
        this.lable = value;
    }

}
