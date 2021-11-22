package com.zhangbin.spring.mvcframework.annotition;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BNRequestParam {
    String value() default "";
}
