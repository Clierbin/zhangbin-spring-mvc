package com.zhangbin.spring.demo.service.impl;

import com.zhangbin.spring.demo.service.IPlayService;
import com.zhangbin.spring.demo.service.IWatchSercice;
import com.zhangbin.spring.mvcframework.annotition.BNAutowired;
import com.zhangbin.spring.mvcframework.annotition.BNService;

@BNService
public class WatchSercice implements IWatchSercice {


    @BNAutowired
    private IPlayService playService;
}
