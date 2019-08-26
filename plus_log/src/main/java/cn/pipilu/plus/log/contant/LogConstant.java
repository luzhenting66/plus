package cn.pipilu.plus.log.contant;

public class LogConstant {
    // 请求开始日志格式
    public static final String reqStart = "<<ReqStart>> token={}|^|sign={}|^|reqTime={}|^|reqData={}";

    // 请求结束日志格式
    public static final String reqEnd = "<<ReqEnd>> respTime={}|^|costTime={}|^|resultCode={}|^|resultMsg={}|^|repData={}";

    // 网关请求开始日志格式
    public static final String gatewayReqStart = "<<ReqStart>> token={}|^|sign={}|^|reqTime={}|^|reqData={}";

    // 网关请求结束日志格式
    public static final String gatewayReqEnd = "<<ReqEnd>> costTime={}|^|resultCode={}|^|resultMsg={}|^|repData={}";

    // 方法执行时间
    public static final String methodPCost = "<MethodTime> method={}|^|costTime={}";
}
