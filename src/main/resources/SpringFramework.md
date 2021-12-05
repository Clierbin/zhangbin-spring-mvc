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

## 自由主题

