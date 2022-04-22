package com.zhangbin.spring.mvcframework.beans.support;

import com.zhangbin.spring.mvcframework.annotition.BNAutowired;
import com.zhangbin.spring.mvcframework.aop.config.ActiveConfig;
import com.zhangbin.spring.mvcframework.aop.BNDefaultAopProxyFactory;
import com.zhangbin.spring.mvcframework.aop.support.BNAdvisedSupport;
import com.zhangbin.spring.mvcframework.beans.BNBeanWrapper;
import com.zhangbin.spring.mvcframework.beans.config.BNBeanDefinition;
import com.zhangbin.spring.mvcframework.core.BNBeanFactory;

import java.lang.reflect.Field;
import java.util.*;

public class BNDefaultListableBeanFactory implements BNBeanFactory {

    private Properties contextConfig;
    // 一级缓存 完成 依赖注入后的bean cache
    private Map<String, Object> singletonObjects = new HashMap<>();
    // 二级缓存 刚刚被反射创建生成的bean cache
    private Map<String, Object> earlySingletonObjects = new HashMap<>();
    // 三级缓存  实例化后多个beanName 指向同一个实例的  cache
    private Map<String, Object> factoryBeanObjectCache = new HashMap<>();
    // 已经实例化 的bean
    private Set<String> singletonsCurrentlyInCreation = new HashSet<>();

    private Map<String, BNBeanDefinition> beanDefinitionMap = new HashMap<>();

    // 三级缓存 终极缓存
    private Map<String, BNBeanWrapper> factoryBeanInstanceCache = new HashMap<>();

    @Override
    public Object getBean(String beanName) {
        try {
            Object bean = getSingleton(beanName);
            if (bean!=null){
                return bean;
            }

            BNBeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

            Object instance = instantiateBean(beanDefinition);

            if (!singletonsCurrentlyInCreation.contains(beanName)) {
                singletonsCurrentlyInCreation.add(beanName);
            }
            // 创建BeaWrapper
            BNBeanWrapper bnBeanWrapper = doCreateBeanWrapper(instance);
            // 4 填充Bean  DI
            populateBean(bnBeanWrapper);

            this.singletonObjects.put(beanName, instance);
            this.factoryBeanInstanceCache.put(beanName, bnBeanWrapper);

            return bnBeanWrapper.getWrapperInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    private Object getSingleton(String beanName) {
        // 1 先从 二级缓存 找
        Object bean = this.singletonObjects.get(beanName);
        // 2 找不到  从 二级缓存并且正在被创建 找
        if (bean == null && singletonsCurrentlyInCreation.contains(beanName)) {
            bean = earlySingletonObjects.get(beanName);
            if (bean == null) {
        // 3 找不到从三级缓存找, 找到放入二级缓存 删除三级缓存
//                Object cache = secondCache.get(beanName);
                try {
                    bean = instantiateBean(beanDefinitionMap.get(beanName));
                    this.earlySingletonObjects.put(beanName, bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }

    private void populateBean(BNBeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrapperInstance();
        try {
            // 反射 注入了
            //忽略字段的修饰符，不管你是 private / protected / public / default
            for (Field field : instance.getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(BNAutowired.class)) {
                    continue;
                }
                BNAutowired annotation = field.getAnnotation(BNAutowired.class);
                String beanName = annotation.value().trim();
                if ("".equals(beanName)) {
                    beanName = field.getType().getName();
                }
                //代码在反射面前，那就是裸奔
                //强制访问. 强吻
                field.setAccessible(true);
                //相当于 demoAction.demoService = ioc.get("com.gupaoedu.demo.service.IDemoService");
                // 递归调用getBean
                field.set(instance, getBean(beanName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private BNBeanWrapper doCreateBeanWrapper(Object instance) {
        BNBeanWrapper bnBeanWrapper = new BNBeanWrapper(instance);
        return bnBeanWrapper;
    }

    private Object instantiateBean(BNBeanDefinition beanDefinition) throws Exception {
        // 只能是单例
        if (this.factoryBeanObjectCache.containsKey(beanDefinition.getBeanName())
                && beanDefinition.isSingleton()) {
            return factoryBeanObjectCache.get(beanDefinition.getBeanName());
        }
        Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
        Object instance = clazz.newInstance();

        // AOP begin
        BNAdvisedSupport bNAdvisedSupport =instanceAopConfig(beanDefinition);
        bNAdvisedSupport.setTargetClass(clazz);
        bNAdvisedSupport.setTarget(instance);
        if (bNAdvisedSupport.pointCutMath()){
            instance= BNDefaultAopProxyFactory.getProxy(bNAdvisedSupport).getProxy();
        }
        // AOP end
        //
        this.factoryBeanObjectCache.put(beanDefinition.getBeanName(), instance);
        this.factoryBeanObjectCache.put(clazz.getName(), instance);
        for (Class<?> insterface : clazz.getInterfaces()) {
            this.factoryBeanObjectCache.put(insterface.getName(), instance);
        }
        return instance;
    }

    private BNAdvisedSupport instanceAopConfig(BNBeanDefinition beanDefinition) {
        ActiveConfig activeConfig = new ActiveConfig();
        activeConfig.setPointCut(this.contextConfig.getProperty("pointCut"));
        activeConfig.setAspectClass(this.contextConfig.getProperty("aspectClass"));
        activeConfig.setBeforeMethod(this.contextConfig.getProperty("aspectBefore"));
        activeConfig.setAfterMethod(this.contextConfig.getProperty("aspectAfter"));
        activeConfig.setThrowMethod(this.contextConfig.getProperty("aspectAfterThrow"));
        activeConfig.setAspectAfterThrowingName(this.contextConfig.getProperty("aspectAfterThrowingName"));
        return new BNAdvisedSupport(activeConfig);
    }

    @Override
    public Object getBean(Class<?> beanName) {
        return getBean(beanName.getName());
    }

    public void doRegistBeanDefinition(List<BNBeanDefinition> bnBeanDefinitions) throws Exception {
        for (BNBeanDefinition bnBeanDefinition : bnBeanDefinitions) {
            if (beanDefinitionMap.containsKey(bnBeanDefinition.getBeanName())) {
                throw new Exception("BeanDefiniton is exist !");
            }
            beanDefinitionMap.put(bnBeanDefinition.getBeanName(), bnBeanDefinition);
        }
    }

    public void doLoadInstance() {
        for (Map.Entry<String, BNBeanDefinition> entry : beanDefinitionMap.entrySet()) {
            // 如果不是延迟加载
            if (!entry.getValue().isLazyInit()) {
                getBean(entry.getKey());
            }
        }
    }

    public List<BNBeanWrapper> getBeanWrappers() {
        return new ArrayList<>(factoryBeanInstanceCache.values());
    }

    public int getBeanDefinitionCount() {
        return beanDefinitionMap.size();
    }

    public Set<String> getBeanDefinitionNames() {
        return beanDefinitionMap.keySet();
    }
    public String getProperty(String property) {
        return contextConfig.getProperty(property);
    }

    public void setContextConfig(Properties contextConfig) {
        this.contextConfig = contextConfig;
    }
}
