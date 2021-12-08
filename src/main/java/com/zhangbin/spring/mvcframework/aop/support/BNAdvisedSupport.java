package com.zhangbin.spring.mvcframework.aop.support;

import com.zhangbin.spring.mvcframework.aop.config.ActiveConfig;
import com.zhangbin.spring.mvcframework.aop.aspect.BNAfterAdvice;
import com.zhangbin.spring.mvcframework.aop.aspect.BNAfterThrowAdvice;
import com.zhangbin.spring.mvcframework.aop.aspect.BNBeforeAdvice;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BNAdvisedSupport {
    private ActiveConfig activeConfig;

    private Object target;

    private Class targetClass;

    private Pattern pointCutClassPattern;

    private Map<Method, List<Object>> methodCache;


    public BNAdvisedSupport(ActiveConfig activeConfig) {
        this.activeConfig = activeConfig;
    }

    public void setTarget(Object target) throws Exception {
        this.target = target;
    }

    private void prcess() {
        //把Spring的Excpress变成Java能够识别的正则表达式
        String pointCut = activeConfig.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");
        //保存专门匹配Class的正则
        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ") + 1));
        // add method aspect method list
        Pattern methodPattern = Pattern.compile(pointCut);
        try {
            Class<?> aspectClass = Class.forName(activeConfig.getAspectClass());
            // 保存 切面类方法map
            Map<String, Method> aspectMethods = new HashMap<>();
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(), method);
            }
            methodCache = new HashMap<>();
            for (Method method : this.getTargetClass().getMethods()) {
                String methodString = method.toString();

                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }

                Matcher matcher = methodPattern.matcher(methodString);
                if (matcher.matches()) {
                    // 切面方法list 按顺序添加. before,after,throw
                    List<Object> advices = new LinkedList<Object>();

                    if (!(null == activeConfig.getBeforeMethod() || "".equals(activeConfig.getBeforeMethod()))) {
                        advices.add(new BNBeforeAdvice(aspectClass.newInstance(), aspectMethods.get(activeConfig.getBeforeMethod())));
                    }
                    if (!(null == activeConfig.getAfterMethod() || "".equals(activeConfig.getAfterMethod()))) {
                        advices.add(new BNAfterAdvice(aspectClass.newInstance(), aspectMethods.get(activeConfig.getAfterMethod())));
                    }
                    if (!(null == activeConfig.getThrowMethod() || "".equals(activeConfig.getThrowMethod()))) {
                        BNAfterThrowAdvice advice = new BNAfterThrowAdvice(aspectClass.newInstance(), aspectMethods
                                .get(activeConfig.getThrowMethod()));
                        advice.setThrowName(activeConfig.getAspectAfterThrowingName());
                        advices.add(advice);
                    }

                    //跟目标代理类的业务方法和Advices建立一对多个关联关系，以便在Porxy类中获得
                    methodCache.put(method, advices);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
        // 需要class 反射获取方法
        prcess();
    }

    //给ApplicationContext首先IoC中的对象初始化时调用，决定要不要生成代理类的逻辑
    public boolean pointCutMath() {
        return pointCutClassPattern.matcher(targetClass.toString()).matches();
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public Object getTarget() {
        return target;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class clazz) throws NoSuchMethodException {
        List<Object> cached = methodCache.get(method);
        if (cached == null) {
            Method m = clazz.getMethod(method.getName(),method.getParameterTypes());
            cached = methodCache.get(m);
            this.methodCache.put(m, cached);
        }
        return cached;
    }
}
