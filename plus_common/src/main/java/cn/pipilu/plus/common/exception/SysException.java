package cn.pipilu.plus.common.exception;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SysException extends RuntimeException {

    private static final long serialVersionUID = 1715408745515734910L;
    /**
     * 返回码
     */
    private String errorCode;
    /**
     * 信息
     */
    private String errorMessage;


    public SysException(String errorCode) {
        super(errorCode);
    }

    public SysException(Throwable cause) {
        super(cause);
    }

    public SysException(String errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public SysException(String errorCode, String message) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public SysException(String errorCode, String message, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

}
