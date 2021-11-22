package com.zhangbin.spring.mvcframework.annotition;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BNController {
    String value() default "";
}
