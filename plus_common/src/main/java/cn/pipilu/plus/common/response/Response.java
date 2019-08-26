package cn.pipilu.plus.common.response;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 372413562988756077L;
    private String            respTime;
    private String            resultCode;
    private String            resultMsg;
    private T                 respData;

    public Response(){
        this.resultCode = ResultCode.SUCCESS.code;
        this.resultMsg = ResultCode.SUCCESS.lable;
    }

    public Response(ResultCode resultCode,T t){
        this.resultMsg = resultCode.lable;
        this.resultCode = resultCode.code;
        this.respData = t;
    }
}
