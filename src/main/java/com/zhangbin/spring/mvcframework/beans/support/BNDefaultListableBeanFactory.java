package com.zhangbin.spring.mvcframework.beans.support;

import com.zhangbin.spring.mvcframework.annotition.BNAutowired;
import com.zhangbin.spring.mvcframework.beans.BNBeanWrapper;
import com.zhangbin.spring.mvcframework.beans.config.BNBeanDefinition;
import com.zhangbin.spring.mvcframework.core.BNBeanFactory;

import java.lang.reflect.Field;
import java.util.*;

public class BNDefaultListableBeanFactory implements BNBeanFactory {


    private Map<String, BNBeanDefinition> beanDefinitionMap = new HashMap<>();

    private Map<String, Object> instantiateBeanCache = new HashMap<>();
    // 三级缓存 终极缓存
    private Map<String,BNBeanWrapper> factoryBeanInstanceCache=new HashMap<>();
    @Override
    public Object getBean(String beanName) {
        try {
            BNBeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

            Object instance = instantiateBean(beanDefinition);
            // 创建BeaWrapper
            BNBeanWrapper bnBeanWrapper=doCreateBeanWrapper(instance);
            // 4 填充Bean  DI
            populateBean(bnBeanWrapper);

            this.factoryBeanInstanceCache.put(beanName,bnBeanWrapper);

            return bnBeanWrapper.getWrapperInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
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
                        beanName = field.getName();
                    }
                    //代码在反射面前，那就是裸奔
                    //强制访问. 强吻
                    field.setAccessible(true);
                    // 这里我要自己操作一下 我认为取不到的得从缓存里取
                    BNBeanWrapper o = this.factoryBeanInstanceCache.get(beanName);
                    if (Objects.isNull(o)) {
                        throw new Exception("No bean be found!!!!");
                    }
                    //相当于 demoAction.demoService = ioc.get("com.gupaoedu.demo.service.IDemoService");
                    field.set(instance, o.getWrapperInstance());
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private BNBeanWrapper doCreateBeanWrapper(Object instance) {
        BNBeanWrapper bnBeanWrapper=new BNBeanWrapper(instance);
        return bnBeanWrapper;
    }

    private Object instantiateBean(BNBeanDefinition beanDefinition) throws Exception {
        Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
        Object instance = clazz.newInstance();
        instantiateBeanCache.put(beanDefinition.getBeanName(), instance);
        return instance;
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
}
