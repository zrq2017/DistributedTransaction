package com.zrq.transaction.server.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ZrqTransactional {
    //代表属于分布式事务
    boolean isStart() default false;
    boolean isEnd() default false;
}
