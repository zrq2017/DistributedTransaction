package com.zrq.transaction.server.aspect;

import com.zrq.transaction.server.connection.ZrqConnection;
import com.zrq.transaction.server.transactional.ZrqTransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.sql.Connection;

@Aspect
@Component
public class ZrqDataSourceAspect {

//    @Around("execution(* javax.sql.DataSource.getConnection(..))")
//    public Connection around(ProceedingJoinPoint point){
//        try {
//            Connection connection = (Connection)point.proceed();
//            return new ZrqConnection(connection, ZrqTransactionManager.getCurrent());
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//        return null;
//    }

    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Connection around(ProceedingJoinPoint point) throws Throwable{
        if(ZrqTransactionManager.getCurrent()!=null){
//            Connection connection = (Connection)point.proceed();
            return new ZrqConnection((Connection)point.proceed(), ZrqTransactionManager.getCurrent());
        } else {
            return (Connection) point.proceed();
        }
    }
}
