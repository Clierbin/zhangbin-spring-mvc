package com.zhangbin.spring.mvcframework.context;

import com.zhangbin.spring.mvcframework.beans.BNBeanWrapper;
import com.zhangbin.spring.mvcframework.beans.config.BNBeanDefinition;
import com.zhangbin.spring.mvcframework.beans.support.BNDefaultListableBeanFactory;
import com.zhangbin.spring.mvcframework.beans.support.BeanDefinitionReader;
import com.zhangbin.spring.mvcframework.core.BNBeanFactory;

import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * 用户门面
 */
public class BNApplicationContext implements BNBeanFactory {
    // BeanDefinition bean配置元读取
    private BeanDefinitionReader beanDefinitionReader=null;
    private BNDefaultListableBeanFactory registry=new BNDefaultListableBeanFactory();
    public BNApplicationContext() {
    }
    public BNApplicationContext(String... configs) {
        try {
            // 1 需要一个BeanDefinitionReader 读取配置文件, 扫描包
            this.beanDefinitionReader = new BeanDefinitionReader(configs);

            // load BeanDefinitions
            List<BNBeanDefinition> BNBeanDefinitions = beanDefinitionReader.loadBeanDefinition();
            // 缓存所有的BNBeanDefinition
            this.registry.doRegistBeanDefinition(BNBeanDefinitions);
            registry.setContextConfig(beanDefinitionReader.getConfig());
            // 3、加载非延时加载的所有的Bean
            this.registry.doLoadInstance();

            System.out.println("bean 初始化完成!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getBean(String beanName) {
        return registry.getBean(beanName);
    }

    @Override
    public Object getBean(Class<?> beanName) {
        return null;
    }

    public List<BNBeanWrapper> getListBeanWrapper() {
        return this.registry.getBeanWrappers();
    }

    public int getBeanDefinitionCount() {
        return this.registry.getBeanDefinitionCount();
    }

    public Set<String> getBeanDefinitionNames() {
        return this.registry.getBeanDefinitionNames();
    }

    public Properties getconfig() {
        return this.beanDefinitionReader.getConfig();
    }
}
