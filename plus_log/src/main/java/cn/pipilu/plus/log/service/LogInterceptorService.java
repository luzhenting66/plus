package cn.pipilu.plus.log.service;

import cn.pipilu.plus.log.aop.ControllerRespTimeAnno;
import cn.pipilu.plus.log.aop.ServiceMethodRespTimeAnno;
import org.aspectj.lang.ProceedingJoinPoint;

public interface LogInterceptorService {

    Object controllerRespTime(ProceedingJoinPoint pjp, ControllerRespTimeAnno anno) throws Throwable;



    Object serviceMethodRespTime(ProceedingJoinPoint pjp, ServiceMethodRespTimeAnno anno) throws Throwable;
}
