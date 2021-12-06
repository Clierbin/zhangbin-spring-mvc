# SpringFramework

## [Clierbin Github](https://github.com/Clierbin/zhangbin-spring-mvc/tree/spring-zb-v2)

## 顶层设计IOC/DI

### 1读取配置文件

### 2缓存全部的classpath

### 3初始化IOC容器

- 将所有携带需要被创建的classPath 反射生成类

### 4DI属性赋值

- 通过扫描类的所有成员遍历field上时候有自动注入的注解

### 5 url 对应 method保存

- method上有requestmapping注解,把url和method保存到一个map中

### 6 HttpServlet调用

- method形参的typeClass
- method注解二维数组
- request里边的请求参数
- method.invoke(instance,入参数组)

## 三级缓存

### 1级缓存singletonObjects
已经完成属性注入的bean

### 2级缓存earlySingletonObjects
刚刚被创建的bean

### 3级缓存singletonFactories
创建bean

### alreadyCreated正在被创建的beanName

### 代码

## MVC

### 创建ApplicationContext:
new ApplicationContext(config.getInitParameter("contextConfigLocation"))

- 需要一个BeanDefinitionReader
  读取配置文件, 扫描包

    - 1、加载Properties文件

        - Properties contextConfig=new Properties();

    - 2、扫描相关的类
      保存至registryBeanClassNames

- load BeanDefinitions

    - String beanName
    - String beanClassName

- doLoadInstance
  遍历beanDefinitionMap

    - for循环遍历
      getBean

        - 获取单例Bean
          getSingleton(beanName)
          ![Screenshot](getSingleton(beanName).png)
        - 实例化Bean
          instantiateBean

            - 放入到三级缓存
              factoryBeanObjectCache

        - 保存正在被创建的BeanName
          singletonsCurrentlyInCreation.add(beanName);
        - 创建BeaWrapper

            - Object wrapperInstance
            - Class<?> wrapperClass

        - DI  填充Bean
          populateBean

            - 递归调用getBean
              field.set(instance, getBean(beanName))

        - 保存至一级缓存
          this.singletonObjects.put(beanName, instance)

### 初始化mvc九大组件"initStrategies

- 先写三个最重要的组件
- 初始化处理器映射器
  initHandlerMappings(context)
- 初始化处理器适配器
  initHandlerAdapters(context)
- 初始化视图解析器
  initViewResolvers(context)

    - 将前端(html)文件缓存下来

### 请求处理
doDispatch

- 通过url 找到 handlermapping
- 通过handlingMapping 找到 HandlingAdapter
-  通过 handlingAdapter 执行handler方法,放回modleView
- 根据ViewResolver找到对应的view对象
- 根据View.read 渲染前端页面数据
  bnView.read(modleAndView.getModle(), req, resp)

    - 输出至前端
      resp.getWriter().write(stringBuilder.toString())

## AOP

### AOP配置的工具类
AdvisedSupport

- 初始化AOPConfig

    - pointCut

        - 切面正则

    - aspectClass

        - 切面类

    - aspectBefore

        - before

    - aspectAfter

        - after

    - aspectAfterThrow

        - afterThrow

    - aspectAfterThrowingName

        - afterThrowName

- 需要被代理的类Class
  TargetClass
- 需要被代理的类对象
  Target
- 正则Pattern
  Pattern pointCutClassPattern
- 需要被代理的类对象方法和切面方法List
  Map<Method, List<Object>> methodCache

    - Before 方法执行前
      MethodBeforeAdviceInterceptor
    - After 方法执行后
      AfterReturningAdviceInterceptor
    - AfterThrow 方法出现异常后
      AspectJAfterThrowingAdvice

### 代理类可以被代理
pointCutMath()

- 创建代理类
  proxyFactory.createAopProxy(config){
  AdvisedSupport
  }

    - 根据代理类是否有接口
      targetClass.getInterfaces().length > 0
    - CglibAopPorxy
    - JdkDynamicAopProxy

- JdkDynamicAopProxy.getProxy()

    - Proxy.newProxyInstance(classLoader,
      this.advised.getTargetClass().getInterfaces(),this)
    - 实现InvocationHandler
      当代理类方法被调用时
      执行invoke方法

        - 1初始化
          MethodInvocation

            - 代理类对象
            - 被代理类对象
            - 被代理类对象Class
            - 被代理类对象method
            - method参数args
            - method需要被执行的
              代理方法List

        - 2执行方法
          MethodInvocation.proceed()
          递归调用

            - 1判断method是否有需要
              被执行的代理方法
            - 2按顺序执行代理方法List

                - 方法执行前
                  MethodBeforeAdviceInterceptor

                    - before()
                      MethodInvocation.proceed()

                - 方法执行后
                  AfterReturningAdviceInterceptor

                    - MethodInvocation.proceed()
                      after()

                - 方法有异常后
                  AspectJAfterThrowingAdvice

                    -


try{
MethodInvocation.proceed()
}catch(){
throw()
}
- 这时候currentInterceptorIndex的大小已经等于list的size,执行method.invoke
被代理类的的method方法调用

			- 3当前拦截器索引
currentInterceptorIndex=-1
一直++

## getSingleton(beanName)

## MethodInvocation.proceed()


![Screenshot](MethodInvocation.proceed().png)
![Screenshot](SpringAop.png)
