package com.zhangbin.spring.mvcframework.webservlet;

import java.io.File;

public class BNViewResolver {
    //.vm   .ftl  .jsp  .gp  .tom
    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";
    private File templateRootDir;

    public BNViewResolver(String templateRoot) {
        String file = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(file);
    }

    // 有多钟前段文件格式
    public BNView resolverViewName(String viewName) {
        if (viewName==null || "".equals(viewName.trim())){return null;}
        viewName=viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX)?viewName:viewName+DEFAULT_TEMPLATE_SUFFIX;
        // 这里找不到 应该会报错
        File file = new File(templateRootDir.getPath() + "/" + viewName);
        return new BNView(file);
    }
}
