package com.zhangbin.spring.demo.service.impl;

import com.zhangbin.spring.demo.service.IPlayService;
import com.zhangbin.spring.demo.service.IWatchSercice;
import com.zhangbin.spring.mvcframework.annotition.BNAutowired;
import com.zhangbin.spring.mvcframework.annotition.BNService;

import java.text.SimpleDateFormat;
import java.util.Date;


@BNService
public class PlayService implements IPlayService {
    @BNAutowired
    private IWatchSercice watchSercice;

    @Override
    public String doSomeThing(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
        System.out.println("这是在业务方法中打印的：" + json);
        return json;
    }
}
