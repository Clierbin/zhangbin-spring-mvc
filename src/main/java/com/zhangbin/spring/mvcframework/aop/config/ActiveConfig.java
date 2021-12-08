package com.zhangbin.spring.mvcframework.aop.config;

import lombok.Data;

@Data
public class ActiveConfig {
    private String pointCut;
    private String aspectClass;
    private String beforeMethod;
    private String afterMethod;
    private String throwMethod;
    private String aspectAfterThrowingName;
}
