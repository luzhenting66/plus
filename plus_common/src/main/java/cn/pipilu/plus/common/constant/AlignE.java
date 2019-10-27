package cn.pipilu.plus.common.constant;

/**
 * 对齐方式
 */
public enum AlignE {
    AUTO(0,"自动"),LEFT(1,"靠左"),CENTER(2,"居中"),RIGHT(3,"靠右");

    public int code;
    public String label;
    AlignE(int code,String label){
        this.code = code;
        this.label= label;
    }
}
