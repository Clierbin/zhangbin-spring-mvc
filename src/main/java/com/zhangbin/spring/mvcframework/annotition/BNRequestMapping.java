package com.zhangbin.spring.mvcframework.annotition;

import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BNRequestMapping {
    String url() default "";
}
