package com.zhangbin.spring.demo.service.impl;

import com.zhangbin.spring.demo.service.DemoService;
import com.zhangbin.spring.mvcframework.annotition.BNService;

@BNService
public class DemoServiceImpl implements DemoService {

    @Override
    public String doSomeThing() {
        System.out.println("DemoServiceImpl doSomeThing!!!");
        return "Service return!";
    }
}
