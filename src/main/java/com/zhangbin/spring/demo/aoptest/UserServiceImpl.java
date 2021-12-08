package com.zhangbin.spring.demo.aoptest;

public class UserServiceImpl implements UserService{

    @Override
    public int insert() {
        System.out.println("insert");
        return 0;
    }

    @Override
    public String query() {
        System.out.println("query");
        oo();
        return null;
    }

    public String oo(){
        System.out.println("oo");
        return null;
    }
}
