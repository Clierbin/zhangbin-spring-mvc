package com.zhangbin.spring.mvcframework.annotition;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BNAutowired {
    String value() default "";
}
