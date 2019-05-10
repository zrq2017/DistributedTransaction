package com.zrq.transaction.server.aspect;

import com.zrq.transaction.server.annotation.ZrqTransactional;
import com.zrq.transaction.server.transactional.TransactionType;
import com.zrq.transaction.server.transactional.ZrqTransaction;
import com.zrq.transaction.server.transactional.ZrqTransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class TransactionAspect implements Ordered {

    @Around("@annotation(com.zrq.transaction.server.annotation.ZrqTransactional)")
    public void invoke(ProceedingJoinPoint point){
        //打印出这个注解所对应的方法
        MethodSignature signature=(MethodSignature)point.getSignature();
        Method method=signature.getMethod();
        ZrqTransactional zrqAnnotation=method.getAnnotation(ZrqTransactional.class);

        System.out.println(zrqAnnotation.isStart());

        //创建事务组，提交到事务组
        String groupId="";
        if(zrqAnnotation.isStart()){
            groupId=ZrqTransactionManager.createTransactionGroup();
        } else {
            groupId=ZrqTransactionManager.getCurrentGroupId();
        }
        ZrqTransaction zrqTransaction=ZrqTransactionManager.createTranscation(groupId);

        try {
            //Spring会开启MySQL事务
            point.proceed();
            ZrqTransactionManager.addTranscation(zrqTransaction,zrqAnnotation.isEnd(), TransactionType.commit);
        } catch (Exception e) {
            ZrqTransactionManager.addTranscation(zrqTransaction,zrqAnnotation.isEnd(), TransactionType.rollback);
            e.printStackTrace();
        } catch (Throwable throwable) {
            ZrqTransactionManager.addTranscation(zrqTransaction,zrqAnnotation.isEnd(), TransactionType.rollback);
            throwable.printStackTrace();
        }
    }

    @Override
    public int getOrder() {
        return 10000;
    }
}
