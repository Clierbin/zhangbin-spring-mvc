package com.zhangbin.spring.demo.controller;

import com.zhangbin.spring.demo.service.IDemoService;
import com.zhangbin.spring.demo.service.IPlayService;
import com.zhangbin.spring.mvcframework.annotition.BNAutowired;
import com.zhangbin.spring.mvcframework.annotition.BNController;
import com.zhangbin.spring.mvcframework.annotition.BNRequestMapping;
import com.zhangbin.spring.mvcframework.annotition.BNRequestParam;
import com.zhangbin.spring.mvcframework.webservlet.BNModleAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@BNController
@BNRequestMapping(url = "demo")
public class DemoAction {

    @BNAutowired
    private IDemoService demoService;

    @BNAutowired
    private IPlayService iPlayService;

    @BNRequestMapping(url = "/hello/request")
    public String request(HttpServletRequest req, HttpServletResponse resp,
                          @BNRequestParam("name")String name){
        String s = demoService.doSomeThing();
        return "Hello,"+name+"\n"+s;
    }

    @BNRequestMapping(url = "/hello/add")
    public BNModleAndView add(HttpServletRequest req, HttpServletResponse resp,
                          @BNRequestParam("name")String name){
        BNModleAndView modleAndView = new BNModleAndView("first");
        Map<String,Object> modle=new HashMap<>();
        modle.put("teacher",name);
        String data = iPlayService.doSomeThing(name);
        modle.put("data", data);
        modle.put("token", 123456);
        modleAndView.setModle(modle);
        return modleAndView;
    }

    @BNRequestMapping(url = "/hello/throw")
    public BNModleAndView throwMethod(HttpServletRequest req, HttpServletResponse resp,
                              @BNRequestParam("name")String name) throws Exception {
        BNModleAndView modleAndView = new BNModleAndView("first");
        Map<String,Object> modle=new HashMap<>();
        modle.put("teacher",name);
        String data = iPlayService.throwMethod(name);
        modle.put("data", data);
        modle.put("token", 123456);
        modleAndView.setModle(modle);
        return modleAndView;
    }
}
