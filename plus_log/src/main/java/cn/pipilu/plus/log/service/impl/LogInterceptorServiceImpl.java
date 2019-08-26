package cn.pipilu.plus.log.service.impl;

import cn.pipilu.plus.common.request.Request;
import cn.pipilu.plus.common.response.Response;
import cn.pipilu.plus.log.aop.ControllerRespTimeAnno;
import cn.pipilu.plus.log.aop.ServiceMethodRespTimeAnno;
import cn.pipilu.plus.log.contant.LogConstant;
import cn.pipilu.plus.log.service.LogInterceptorService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(value = Ordered.LOWEST_PRECEDENCE)
public class LogInterceptorServiceImpl implements LogInterceptorService {
    private Logger logger = LoggerFactory.getLogger(LogInterceptorServiceImpl.class);
    @Value("${spring.application.name}")
    private String appName;

    @Override
    @Around(value = "@annotation(anno)")
    public Object controllerRespTime(ProceedingJoinPoint pjp, ControllerRespTimeAnno anno) throws Throwable {
        long beginTime = System.currentTimeMillis();
        String serviceName = pjp.getTarget().getClass().getSimpleName() + "." + pjp.getSignature().getName();
        //获取请求参数
        Request request = null;
        Object[] args = pjp.getArgs();
        if (args != null && args.length > 0) {
            for (Object obj : args) {
                if (obj instanceof Request) {
                    request = (Request) obj;
                    break;
                }
            }
        }

        if (request == null) {
            logger.error(serviceName + "服务入参不符合规范，无法获取入参");
        } else {
            logger.info(LogConstant.reqStart, new Object[]{request.getToken(), request.getSign(), request.getReqTime(),
                    request.getReqData() != null ? request.getReqData().toString() : ""});
        }

        //执行方法获取返回值
        Object repObj = pjp.proceed();
        long endTime = System.currentTimeMillis();
        //获取返回参数
        if (repObj != null && repObj instanceof Response) {
            Response reponse = (Response) repObj;
            logger.info(LogConstant.reqEnd, new Object[]{ reponse.getRespTime(), endTime - beginTime, reponse.getResultCode(),
                    reponse.getResultMsg(), reponse.getRespData() != null ? reponse.getRespData().toString() : ""});
        } else {
            logger.error(serviceName + "服务出参不符合规范，无法获取出参");
        }

        return repObj;
    }

    @Override
    @Around(value = "@annotation(anno)")
    public Object serviceMethodRespTime(ProceedingJoinPoint pjp, ServiceMethodRespTimeAnno anno) throws Throwable {
        long beginTime = System.currentTimeMillis();
        String methodName = pjp.getTarget().getClass().getSimpleName() + "." + pjp.getSignature().getName();

        Object obj = pjp.proceed();
        long endTime = System.currentTimeMillis();
        logger.info(LogConstant.methodPCost, new Object[]{methodName, endTime - beginTime});

        return obj;
    }
}
