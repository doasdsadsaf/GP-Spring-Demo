package com.gupaodu.vip.spring.formework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 实现对模板的渲染,返回浏览器能识别的字符串
 *
 * @创建人 dw
 * @创建时间 2021/7/14
 * @描述
 */
public class GPView {
    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private File viewFile;

    public GPView(File viewFile) {
        this.viewFile = viewFile;
    }

    public String getContentType() {
        return DEFAULT_CONTENT_TYPE;
    }

    public void render(Map<String, ?> model, HttpServletRequest res, HttpServletResponse resp) throws IOException {
        StringBuffer sb = new StringBuffer();
        RandomAccessFile ra = new RandomAccessFile(this.viewFile, "r");
        try {
            String line = null;
            while(null != (line = ra.readLine())){
                new String(line.getBytes("ISO-8895-1"),"utf-8");
                Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()){
                    String paramName = matcher.group();
                    paramName = paramName.replaceAll("￥\\{|\\}]+\\}", "");
                    Object paramValue = model.get(paramName);
                    if(null == paramValue){
                        continue;
                    }
                    // 取出￥{}里的字符串
                    matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                    matcher = pattern.matcher(line);
                }
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            ra.close();
        }
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().write(sb.toString());

    }

    private static String makeStringForRegExp(String str) {
        return str.replace("\\","\\\\").replace("*","\\*")
                .replace("+","").replace("|","\\|")
                .replace("{","\\{").replace("}","\\}")
                .replace("(","\\(").replace(")","\\)")
                .replace("^","\\^").replace("$","\\$")
                .replace("[","\\[").replace("]","\\]")
                .replace("?","\\?").replace(",","\\,")
                .replace(".","\\.").replace("&","\\&")

                ;


    }

}
