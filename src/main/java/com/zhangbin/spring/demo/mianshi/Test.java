package com.zhangbin.spring.demo.mianshi;

/**
 * 单例
 * 懒汉式，顾名思义就是实例在用到的时候才去创建，“比较懒”，用的时候才去检查有没有实例，如果有则返回，没有则新建。有线程安全和线程不安全两种写法，区别就是synchronized关键字。
 */
//public class Test {
//    private Test(){
//
//    }
//
//    private static Test INSTANCE;
//
//    public static Test getInstance(){
//        if (INSTANCE==null){
//            INSTANCE=new Test();
//        }
//        return INSTANCE;
//    }
//}

/**
 * 饿汉式，从名字上也很好理解，就是“比较勤”，实例在初始化的时候就已经建好了，不管你有没有用到，都先建好了再说。好处是没有线程安全的问题，坏处是浪费内存空间。
 */
//public class Test{
//    private static Test INSTANCE=new Test();
//    private Test(){
//
//    }
//    public static Test getInstance(){
//        return INSTANCE;
//    }
//}

/**
 * 双检锁，又叫双重校验锁，综合了懒汉式和饿汉式两者的优缺点整合而成。看上面代码实现中，特点是在synchronized关键字内外都加了一层 if 条件判断，这样既保证了线程安全，又比直接上锁提高了执行效率，还节省了内存空间。
 */
public class Test{
    private static Test INSTANCE;

    private Test(){}

    public static Test doubleSync(){
        if (INSTANCE==null){
            synchronized (Test.class){
                if (INSTANCE==null){
                    INSTANCE=new Test();
                }
            }
        }
        return INSTANCE;
    }


}
