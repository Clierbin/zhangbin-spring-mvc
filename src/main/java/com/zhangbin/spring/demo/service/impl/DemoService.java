package com.zhangbin.spring.demo.service.impl;

import com.zhangbin.spring.demo.service.IDemoService;
import com.zhangbin.spring.mvcframework.annotition.BNService;

@BNService
public class DemoService implements IDemoService {

    @Override
    public String doSomeThing() {
        System.out.println("DemoServiceImpl doSomeThing!!!");
        return "Service return!";
    }
}
