package com.zhangbin.spring.mvcframework.beans.support;

import com.zhangbin.spring.mvcframework.annotition.BNController;
import com.zhangbin.spring.mvcframework.annotition.BNService;
import com.zhangbin.spring.mvcframework.beans.config.BNBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class BeanDefinitionReader {
    private Properties contextConfig=new Properties();

    private List<String> registryBeanClassNames=new ArrayList<>();

    private Map<String, BNBeanDefinition> beanDefinitionCache=new HashMap<>();

    public BeanDefinitionReader(String... locations) {
        //1、加载Properties文件
        doLoadConfig(locations[0]);

        //2、扫描相关的类
        doScanner(contextConfig.getProperty("scanPackage"));
    }



    private void doScanner(String scanPackage) {
        // 通过scanPackage 转成文件地址 找到.class文件 通过className 反射生成类
        URL resource = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        if (Objects.isNull(resource)) {
            return;
        }
        File files = new File(resource.getFile());
        for (File file : files.listFiles()) {
            // 如果file是文件夹 就递归
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                // 如果是.class
                if (!file.getName().contains(".class")) {
                    continue;
                }
                String beanPath = scanPackage + "." + file.getName().replace(".class", "");
                registryBeanClassNames.add(beanPath);
            }
        }
    }

    private void doLoadConfig(String location) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(location);
        try {
            contextConfig.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<BNBeanDefinition> loadBeanDefinition() throws Exception {
        List<BNBeanDefinition> beanDefinitions = new ArrayList<>();
        for (String registryBeanClassName : this.registryBeanClassNames) {
            Class<?> clazz = Class.forName(registryBeanClassName);
            if (clazz.isInterface()){continue;}
            // 尝试去取 注解 里边的value
            if (clazz.isAnnotationPresent(BNController.class)) {
                String beanName = toLowerFirstCase(clazz.getSimpleName());
                beanDefinitions.add(createBeanDefinition(beanName, registryBeanClassName));
            } else if (clazz.isAnnotationPresent(BNService.class)) {
                BNService annotation = clazz.getAnnotation(BNService.class);
                String beanName = annotation.value();
                if ("".equals(beanName)) {
                    beanName = toLowerFirstCase(clazz.getSimpleName());
                }
                beanDefinitions.add(createBeanDefinition(beanName, registryBeanClassName));
                for (Class interfaceClass : clazz.getInterfaces()) {
                    // 如果有多个实现类 这报错报的莫名其妙啊
                    String interfaceName = interfaceClass.getName();
                    if (beanDefinitions.stream().map(BNBeanDefinition::getBeanName).collect(Collectors.toList())
                            .contains(interfaceName)) {
                        throw new Exception("This bean name is exists!!"+interfaceName);
                    }
                    beanDefinitions.add(createBeanDefinition(interfaceName, registryBeanClassName));
                }
            } else {
                continue;
            }
        }
        return beanDefinitions;
    }
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return new String(chars);
    }
    private BNBeanDefinition createBeanDefinition(String beanName,String beanClassName) {
        BNBeanDefinition beanDefinition=new BNBeanDefinition();
        beanDefinition.setBeanName(beanName);
        beanDefinition.setBeanClassName(beanClassName);
        return beanDefinition;
    }
}
