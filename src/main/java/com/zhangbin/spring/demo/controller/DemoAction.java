package com.zhangbin.spring.demo.controller;

import com.zhangbin.spring.demo.service.DemoService;
import com.zhangbin.spring.mvcframework.annotition.BNAutowired;
import com.zhangbin.spring.mvcframework.annotition.BNController;
import com.zhangbin.spring.mvcframework.annotition.BNRequestMapping;
import com.zhangbin.spring.mvcframework.annotition.BNRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@BNController
@BNRequestMapping(url = "demo")
public class DemoAction {

    @BNAutowired
    private DemoService demoService;


    @BNRequestMapping(url = "/hello/request")
    public String request(HttpServletRequest req, HttpServletResponse resp,
                          @BNRequestParam("name")String name){
        String s = demoService.doSomeThing();
        return "Hello,"+name+"\n"+s;
    }
}
