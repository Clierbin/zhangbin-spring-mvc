package com.zhangbin.spring.mvcframework.v4;

import com.zhangbin.spring.mvcframework.annotition.BNController;
import com.zhangbin.spring.mvcframework.annotition.BNRequestMapping;
import com.zhangbin.spring.mvcframework.beans.BNBeanWrapper;
import com.zhangbin.spring.mvcframework.context.BNApplicationContext;
import com.zhangbin.spring.mvcframework.webservlet.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * v4 引入 三级缓存 解决 循环依赖问题
 */
public class BNDispatcherservletV4 extends HttpServlet {


    private List<BNHandlerMapping> handlermappings = new ArrayList<>();

    private Map<BNHandlerMapping, BNHandlerAdapter> handlerAdapters = new HashMap<>();


    private BNApplicationContext bnApplicationContext = null;
    private List<BNViewResolver> viewResolvers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                BNModleAndView modleAndView = new BNModleAndView();
                modleAndView.setViewName("500");
                Map<String,Object> modle=new HashMap<>();
                modle.put("detail","500 Exception,Detail");
                modle.put("detail",Arrays.toString(e.getStackTrace()));
                pressDispatchServlet(req, resp,modleAndView);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String requestURI = req.getRequestURI();
        if ("".equals(requestURI) || handlermappings.isEmpty()) {
            return;
        }
        // 通过url 找到 handlermapping
        BNHandlerMapping handler = getHandler(req);
        if (handler==null){
            if (req.getRequestURI().contains("favicon.ico")){
                return;
            }
            pressDispatchServlet(req, resp,new BNModleAndView("404"));
            return;
        }
        // 通过handlingMapping 找到 HandlingAdapter
        BNHandlerAdapter ha = getAdapter(handler);
        // 通过 handlingAdapter 执行handler方法,放回modleView
        BNModleAndView modleAndView = ha.handler(req, resp, handler);


        // 根据ViewResolver找到对应的view对象
        pressDispatchServlet(req, resp, modleAndView);


    }

    private void pressDispatchServlet(HttpServletRequest req, HttpServletResponse resp, BNModleAndView modleAndView) throws Exception{
        if (modleAndView == null) {
            return;
        }
        if (this.viewResolvers.isEmpty()) {
            return;
        }
        for (BNViewResolver viewResolver : this.viewResolvers) {
            // 只有找到了 view 对象的才正常执行
            BNView bnView = viewResolver.resolverViewName(modleAndView.getViewName());
            if (bnView.hasViewFile()) {
                bnView.read(modleAndView.getModle(), req, resp);
                return;
            }
        }
    }

    private BNHandlerAdapter getAdapter(BNHandlerMapping handler) {
        if (handlerAdapters.isEmpty()) {
            return null;
        }
        return handlerAdapters.get(handler);
    }

    private BNHandlerMapping getHandler(HttpServletRequest req) {
        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI();
        String url = requestURI.replaceAll(contextPath, "").replaceAll("/+", "/");
        for (BNHandlerMapping handlermapping : this.handlermappings) {
            Matcher matcher = handlermapping.getPattern().matcher(url);
            if (matcher.matches()) {
                return handlermapping;
            }
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        this.bnApplicationContext = new BNApplicationContext(config.getInitParameter("contextConfigLocation"));

        //5、初始化HandlerMapping
        initStrategies(bnApplicationContext);

        System.out.println("zb BNDispatcherServlet is init !!!");
    }

    protected void initStrategies(BNApplicationContext bnApplicationContext) {
//        initMultipartResolver(context);
//        initLocaleResolver(context);
//        initThemeResolver(context);
        //
        initHandlerMappings(bnApplicationContext);
        initHandlerAdapters(bnApplicationContext);
//        initHandlerExceptionResolvers(context);
//        initRequestToViewNameTranslator(context);
        initViewResolvers(bnApplicationContext);
//        initFlashMapManager(context);
    }

    private void initViewResolvers(BNApplicationContext bnApplicationContext) {
        String templateRoot = this.bnApplicationContext.getconfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File files = new File(templateRootPath);
        for (File file : files.listFiles()) {
            this.viewResolvers.add(new BNViewResolver(templateRoot));
        }
    }

    private void initHandlerAdapters(BNApplicationContext bnApplicationContext) {
        for (BNHandlerMapping bnHandlerMapping : handlermappings) {
            handlerAdapters.put(bnHandlerMapping, new BNHandlerAdapter());
        }
    }

    private void initHandlerMappings(BNApplicationContext bnApplicationContext) {
        // 是否有被扫描到的BeanDifinitonWrapper
        if (bnApplicationContext.getBeanDefinitionCount() == 0) {
            return;
        }
        List<BNBeanWrapper> listBeanWrapper = bnApplicationContext.getListBeanWrapper();
        if (listBeanWrapper.isEmpty()) {
            return;
        }
        for (String beanName : this.bnApplicationContext.getBeanDefinitionNames()) {
            // 通过beanName 去 application 中取实例
            Object instance = this.bnApplicationContext.getBean(beanName);
            Class<?> clazz = instance.getClass();
            // 如果是controller 那么我就处理一下
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
                //  //demo//query
                String url = (baseUrl + "/" + urlValue)
                        .replaceAll("\\*", ".*")
                        .replaceAll("/+", "/");
                // 这里生成一个正则 通过 正则去匹配url
                Pattern pattern = Pattern.compile(url);
                handlermappings.add(new BNHandlerMapping(pattern, instance, method));
            }
        }
    }


    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return new String(chars);
    }


}
