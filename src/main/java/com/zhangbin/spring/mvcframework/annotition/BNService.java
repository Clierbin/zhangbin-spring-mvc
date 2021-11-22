package com.zhangbin.spring.mvcframework.annotition;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BNService {
    String value() default "";
}
