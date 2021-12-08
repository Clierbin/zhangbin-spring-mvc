package com.zhangbin.spring.demo.aspect;


import com.zhangbin.spring.mvcframework.aop.intercept.BNJoinPoint;

public class LogAspect {

    public void before(BNJoinPoint joinPoint){
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(),System.currentTimeMillis());
        //这个方法中的逻辑，是由我们自己写的
        System.out.println("Invoker Before Method!!!");
    }

    public void after(BNJoinPoint joinPoint){
        long startTime = (Long)joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        System.out.println("Invoker After Method!!!" + "use time :" + (endTime - startTime));
    }

    public void afterThrow(BNJoinPoint joinPoint,Throwable tx){
        System.out.println("方法出现异常!!!!");
    }
}
