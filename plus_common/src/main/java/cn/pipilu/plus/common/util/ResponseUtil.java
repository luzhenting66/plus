package cn.pipilu.plus.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.ValidationException;

import cn.pipilu.plus.common.exception.AppException;
import cn.pipilu.plus.common.exception.SysException;
import cn.pipilu.plus.common.response.Response;
import cn.pipilu.plus.common.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ResponseUtil {
    protected static Logger logger = LoggerFactory.getLogger(ResponseUtil.class);

    public  void setRespParam(Response resp, Exception e) {
        resp.setRespTime(new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
        if (e instanceof AppException) {
            AppException exce = (AppException) e;
            resp.setResultCode(exce.getErrorCode());
            resp.setResultMsg(exce.getErrorMessage());
        } else if (e instanceof SysException) {
            SysException exce = (SysException) e;
            resp.setResultCode(exce.getErrorCode());
            resp.setResultMsg(exce.getErrorMessage());
        } else if (e instanceof ValidationException) {
            resp.setResultCode(ResultCode.VALIDATE_ERROR.code);
            resp.setResultMsg(ResultCode.VALIDATE_ERROR.lable);
        } else {
            logger.error(ResultCode.SYSTEM_ERROR.lable, e);
            resp.setResultCode(ResultCode.SYSTEM_ERROR.code);
            resp.setResultMsg(ResultCode.SYSTEM_ERROR.lable);
        }
    }
    public void setRespParam(Response resp) {
        resp.setRespTime(new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
        resp.setResultCode(ResultCode.SUCCESS.code);
        resp.setResultMsg(ResultCode.SUCCESS.lable);
    }
}
