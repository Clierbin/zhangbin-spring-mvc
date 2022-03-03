package com.zhangbin.spring.demo.mianshi;

public class Test {
    /**
     * 单例
     */
    private Test test;

    public Test() {
        if (test==null){
        synchronized (this){
            test=new Test();
        }}
    }

    public Test getTest(){
       return test;
    }
}
