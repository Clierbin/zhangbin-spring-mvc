package com.zhangbin.spring.mvcframework.v3;

import com.zhangbin.spring.mvcframework.annotition.*;
import com.zhangbin.spring.mvcframework.beans.BNBeanWrapper;
import com.zhangbin.spring.mvcframework.context.BNApplicationContext;

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

public class BNDispatcherservletV3 extends HttpServlet {
    // 配置文件
    private Properties properties = new Properties();

    // 扫描后的文件path
    private List<String> beanPathList = new ArrayList<String>();

    // ioc 容器
    private Map<String, Object> ioc = new HashMap<String, Object>();

    private Map<String, Method> urlHandling = new HashMap<>();


    private BNApplicationContext bnApplicationContext=null;

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
        String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());
        Object invoke = method.invoke(bnApplicationContext.getBean(beanName), methodParm);
        if (Objects.isNull(invoke)){return;}
        resp.getWriter().print(invoke);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        this.bnApplicationContext = new BNApplicationContext(config.getInitParameter("contextConfigLocation"));

        //5、初始化HandlerMapping
        doInitHandlerMapping();

        System.out.println("zb BNDispatcherServlet is init !!!");
    }

    private void doInitHandlerMapping() {
        List<BNBeanWrapper> listBeanWrapper = bnApplicationContext.getListBeanWrapper();
        if (listBeanWrapper.isEmpty()) {
            return;
        }
        for (BNBeanWrapper beanWrapper : listBeanWrapper) {
            // 如果是controller 那么我就处理一下
            Class<?> clazz = beanWrapper.getWrapperClass();
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






    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return new String(chars);
    }


}
