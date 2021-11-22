package com.zhangbin.spring.demo.controller;

import com.zhangbin.spring.demo.service.DemoService;
import com.zhangbin.spring.mvcframework.annotition.BNAutowired;
import com.zhangbin.spring.mvcframework.annotition.BNController;
import com.zhangbin.spring.mvcframework.annotition.BNRequestMapping;
import com.zhangbin.spring.mvcframework.annotition.BNRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@BNController
@BNRequestMapping(url = "demo")
public class DemoAction {

    @BNAutowired
    private DemoService demoService;


    @BNRequestMapping(url = "/hello/request")
    public void request(HttpServletRequest req, HttpServletResponse resp,
                          @BNRequestParam("name")String name){
        demoService.doSomeThing();
        try {
            resp.getWriter().print("Hello,"+name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
