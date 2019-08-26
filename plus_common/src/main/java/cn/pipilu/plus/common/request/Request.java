package cn.pipilu.plus.common.request;
import java.io.Serializable;
import lombok.Data;
import lombok.ToString;
@Data
@ToString
public class Request<T> implements Serializable {
    private static final long serialVersionUID = -3979820685197989744L;
    private String            token;
    private String            sign;                                                     // 签名
    private String            reqTime;
    private T                 reqData;
}
