package com.zhangbin.spring.demo.service;

public interface IPlayService {
    String doSomeThing(String name);

    String throwMethod(String name) throws Exception;
}
