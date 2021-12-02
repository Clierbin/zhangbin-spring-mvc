package com.zhangbin.spring.mvcframework.webservlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class BNView {
    private File viewFile;

    public BNView(File viewFile) {
        this.viewFile=viewFile;
    }
    public boolean hasViewFile(){
        return !Objects.isNull(viewFile);
    }

    public void read(Map<String,?> modle, HttpServletRequest req, HttpServletResponse resp) throws Exception {

        // 无反射不框架
        // 无正则 不框架
        // 创建正则
        String regex="￥\\{[^\\}]+\\}";
        Pattern pattern=Pattern.compile(regex, CASE_INSENSITIVE);
        StringBuilder stringBuilder=new StringBuilder();
        // 将文件成行 遍历
        RandomAccessFile read = new RandomAccessFile(this.viewFile, "r");
        String line=null;
        while (null != (line=read.readLine())){
            line=new String(line.getBytes("iso-8859-1"),"UTF-8");
            Matcher matcher = pattern.matcher(line);
            while(matcher.find()){
                String parmName = matcher.group();
                parmName = parmName.replaceAll("￥\\{|\\}", "");
                if (null == modle){continue;}
                Object paramValue = modle.get(parmName);
                if (null == paramValue){continue;}
                // 替换第一个
                line=matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                //  查找下一处match
                matcher = pattern.matcher(line);
            }
            stringBuilder.append(line);
        }
        // 通过正则赋值 modle中的值
        // resp.write
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().write(stringBuilder.toString());
    }
    //处理特殊字符
    public static String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }
}
