package com.zhangbin.spring.demo.controller;

import com.zhangbin.spring.demo.service.DemoService;
import com.zhangbin.spring.mvcframework.annotition.BNAutowired;
import com.zhangbin.spring.mvcframework.annotition.BNController;
import com.zhangbin.spring.mvcframework.annotition.BNRequestMapping;
import com.zhangbin.spring.mvcframework.annotition.BNRequestParam;

@BNController
@BNRequestMapping(url = "demo")
public class DemoAction {

    @BNAutowired
    private DemoService demoService;


    @BNRequestMapping(url = "/hello/request")
    public String request(@BNRequestParam("name")String name){
        demoService.doSomeThing();
        return "你好啊,"+name;
    }
}
