package com.zhangbin.spring.mvcframework.v2;

import com.zhangbin.spring.mvcframework.annotition.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class BNDispatcherservletV2 extends HttpServlet {
    // 配置文件
    private Properties properties = new Properties();

    // 扫描后的文件path
    private List<String> beanPathList = new ArrayList<String>();

    // ioc 容器
    private Map<String, Object> ioc = new HashMap<String, Object>();

    private Map<String, Method> urlHandling = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doReal(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().print("500 执行异常e:" + Arrays.toString(e.getStackTrace()));
        }
    }

    private void doReal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String requestURI = req.getRequestURI();
        if ("".equals(requestURI) || urlHandling.isEmpty()) {
            return;
        }
        String contextPath = req.getContextPath();
        String url = requestURI.replaceAll(contextPath, "");
        Method method = urlHandling.get(url);
        if (Objects.isNull(method)) {
            resp.getWriter().println("404 Not Mapping of Url!!!");
            return;
        }
        // 方法的入参都可以取到

        Map<String,Integer> parmIndex=new HashMap<>();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        // 这只能把写到了自定义注解的形参找出来
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            for (Annotation annotation : parameterAnnotation) {
                if (annotation instanceof BNRequestParam){
                    String value =((BNRequestParam) annotation).value();
                    parmIndex.put(value,i);
                }
            }
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class){
                parmIndex.put(parameterType.getSimpleName(),i);
            }
        }
        Object[] methodParm=new Object[parameterTypes.length];
        // 执行  从method 方法中找形参, 从reques 里边找入参
        Map<String, String[]> parameterMap = req.getParameterMap();
        for (String reqParm : parameterMap.keySet()) {
            String[] strings = parameterMap.get(reqParm);
            String parmVale=Arrays.toString(strings)
                    .replaceAll("\\[|\\]","")
                    .replaceAll("\\s","");
            Integer integer = parmIndex.get(reqParm);
            //涉及到类型强制转换
            methodParm[integer]=parmVale;
        }

        if (parmIndex.containsKey(HttpServletRequest.class.getSimpleName())){
            Integer integer = parmIndex.get(HttpServletRequest.class.getSimpleName());
            methodParm[integer]=req;
        }
        if (parmIndex.containsKey(HttpServletResponse.class.getSimpleName())){
            Integer integer = parmIndex.get(HttpServletResponse.class.getSimpleName());
            methodParm[integer]=resp;
        }
        String beanName = getBeanName(method.getDeclaringClass().getSimpleName());
        Object invoke = method.invoke(ioc.get(beanName), methodParm);
        if (Objects.isNull(invoke)){return;}
        resp.getWriter().print(invoke);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 1,读取配置文件
        readConfigurationFile(config);
        // 2,扫描包
        scanPackage(properties.getProperty("scanPackage"));
        // 3,初始化ioc容器
        initializableIoc();
        // 4, DI 属性赋值
        attributeAssignment();
        // 5, requestMapping Hanlder
        requestMappingHanlder();


        System.out.println("zb BNDispatcherServlet is init !!!");
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

            // 把所有public 方法找出来
            // 把带有注解的处理一下  首先看下是否有baseUrl
            String baseUrl = "/";
            if (clazz.isAnnotationPresent(BNRequestMapping.class)) {
                BNRequestMapping annotation = clazz.getAnnotation(BNRequestMapping.class);
                baseUrl = baseUrl + annotation.url();
            }
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(BNRequestMapping.class)) {
                    continue;
                }
                BNRequestMapping annotation = method.getAnnotation(BNRequestMapping.class);
                String urlValue = annotation.url();
                String url = (baseUrl + "/" + urlValue).replaceAll("/+", "/");
                urlHandling.put(url, method);
            }
        }
    }

    private void attributeAssignment() {
        if (ioc.isEmpty()) {
            return;
        }
        try {
            for (Map.Entry<String, Object> entry : ioc.entrySet()) {
                // 反射 注入了
                //忽略字段的修饰符，不管你是 private / protected / public / default
                for (Field field : entry.getValue().getClass().getDeclaredFields()) {
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
                    Object o = ioc.get(beanName);
                    if (Objects.isNull(o)) {
                        throw new Exception("No bean be found!!!!");
                    }
                    //相当于 demoAction.demoService = ioc.get("com.gupaoedu.demo.service.IDemoService");
                    field.set(entry.getValue(), o);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializableIoc() {
        if (beanPathList.isEmpty()) {
            return;
        }
        try {
            for (String path : beanPathList) {
                Class<?> clazz = Class.forName(path);
                // 尝试去取 注解 里边的value
                if (clazz.isAnnotationPresent(BNController.class)) {
                    Object o = clazz.newInstance();
                    String beanName = getBeanName(clazz.getSimpleName());
                    ioc.put(beanName, o);
                } else if (clazz.isAnnotationPresent(BNService.class)) {
                    Object o = clazz.newInstance();
                    BNService annotation = clazz.getAnnotation(BNService.class);
                    String beanName = annotation.value();
                    if ("".equals(beanName)) {
                        beanName = getBeanName(clazz.getSimpleName());
                    }
                    ioc.put(beanName, o);
                    for (Class interfaceClass : clazz.getInterfaces()) {
                        // 如果有多个实现类
                        if (ioc.containsKey(getBeanName(interfaceClass.getSimpleName()))) {
                            throw new Exception("This bean name is exists!!");
                        }
                        String interfaceName = getBeanName(interfaceClass.getSimpleName());
                        ioc.put(interfaceName, o);
                    }
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getBeanName(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return new String(chars);
    }

    private void scanPackage(String scanPackage) {
        // 通过scanPackage 转成文件地址 找到.class文件 通过className 反射生成类
        URL resource = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        if (Objects.isNull(resource)) {
            return;
        }
        File files = new File(resource.getFile());
        for (File file : files.listFiles()) {
            // 如果file是文件夹 就递归
            if (file.isDirectory()) {
                scanPackage(scanPackage + "." + file.getName());
            } else {
                // 如果是.class
                if (!file.getName().contains(".class")) {
                    continue;
                }
                String beanPath = scanPackage + "." + file.getName().replace(".class", "");
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

        } finally {
            try {
                resourceAsStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
