package cn.pipilu.plus.common.exception;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class AppException extends RuntimeException {

    private static final long serialVersionUID = 1715408745515734910L;
    /**
     * 返回码
     */
    private String errorCode;
    /**
     * 信息
     */
    private String errorMessage;


    public AppException(String errorCode) {
        super(errorCode);
    }

    public AppException(Throwable cause) {
        super(cause);
    }

    public AppException(String errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public AppException(String errorCode, String message) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public AppException(String errorCode, String message, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

}
