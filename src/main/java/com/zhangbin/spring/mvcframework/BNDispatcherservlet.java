package com.zhangbin.spring.mvcframework;

import com.zhangbin.spring.mvcframework.annotition.BNAutowired;
import com.zhangbin.spring.mvcframework.annotition.BNController;
import com.zhangbin.spring.mvcframework.annotition.BNRequestMapping;
import com.zhangbin.spring.mvcframework.annotition.BNService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

public class BNDispatcherservlet extends HttpServlet {
    // 配置文件
    private Properties properties;

    // 扫描后的文件path
    private List<String> beanPathList=new ArrayList<String>();

    // ioc 容器
    private Map<String,Object> ioc = new HashMap<String,Object>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 1,读取配置文件
        readConfigurationFile(config);
        // 2,扫描包
        scanPackage(properties.getProperty("com.zhangbin.spring.demo"));
        // 3,初始化ioc容器
        initializableIoc();
        // 4, DI 属性赋值
            attributeAssignment();
        // 5, requestMapping Hanlder
        requestMappingHanlder();
    }

    private void requestMappingHanlder() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            // 如果是controller 那么我就处理一下
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(BNController.class)) {
                continue;
            }

        }
    }

    private void attributeAssignment()  {
        if (ioc.isEmpty()){return;}
        try {
            for (Map.Entry<String, Object> entry : ioc.entrySet()) {
                // 反射 注入了
                //忽略字段的修饰符，不管你是 private / protected / public / default
                Field[] declaredFields = entry.getValue().getClass().getDeclaredFields();
                for (Field field : declaredFields) {
                    if (!field.isAnnotationPresent(BNAutowired.class)){ continue; }
                    BNAutowired annotation = field.getAnnotation(BNAutowired.class);
                    String beanName=annotation.value().trim();
                    if ("".equals(beanName)){
                        beanName = field.getType().getName();
                    }
                    //代码在反射面前，那就是裸奔
                    //强制访问. 强吻
                    field.setAccessible(true);
                    Object o = ioc.get(beanName);
                    if (Objects.isNull(o)){throw new Exception("No bean be found!!!!");}
                    //相当于 demoAction.demoService = ioc.get("com.gupaoedu.demo.service.IDemoService");
                    field.set(entry.getValue(),o);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializableIoc() {
        if (beanPathList.isEmpty()){return;}
        try {
        for (String path : beanPathList) {
                Class<?> clazz = Class.forName(path);
                // 尝试去取 注解 里边的value
                if (clazz.isAnnotationPresent(BNController.class)){
                    Object o = clazz.newInstance();
                    String beanName=getBeanName(clazz.getSimpleName());
                    ioc.put(beanName,o);
                }else if (clazz.isAnnotationPresent(BNService.class)){
                    Object o = clazz.newInstance();
                    BNService annotation = clazz.getAnnotation(BNService.class);
                    String beanName=annotation.value();
                    if ("".equals(beanName)){
                        beanName=getBeanName(clazz.getSimpleName());
                    }
                    ioc.put(beanName,o);
                    for (Class interfaceClass:clazz.getInterfaces()){
                        // 如果有多个实现类
                        if (ioc.containsKey(getBeanName(interfaceClass.getSimpleName()))){
                            throw new Exception("This bean name is exists!!");
                        }
                        String interfaceName=getBeanName(interfaceClass.getSimpleName());
                        ioc.put(interfaceName,o);
                    }
                }else{
                    continue;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getBeanName(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] +=32;
        return new String(chars);
    }

    private void scanPackage(String scanPackage) {
        // 通过scanPackage 转成文件地址 找到.class文件 通过className 反射生成类
        URL resource = this.getClass().getClassLoader().getResource("/"+scanPackage.replaceAll("\\.", "/"));
        File files = new File(resource.getFile());
        for (File file : files.listFiles()) {
            // 如果file是文件夹 就递归
            if (file.isDirectory()){
                scanPackage(scanPackage+file.getName());
            }else{
                // 如果是.class
                if (!file.getName().contains(".class")){continue;}
                String beanPath = scanPackage + file.getName().replace(".class","");
                beanPathList.add(beanPath);
            }
        }
    }

    private void readConfigurationFile(ServletConfig config) {
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);

        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();

        }finally {
            try {
                resourceAsStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
