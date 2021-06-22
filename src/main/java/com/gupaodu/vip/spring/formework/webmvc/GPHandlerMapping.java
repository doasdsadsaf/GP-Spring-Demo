package com.gupaodu.vip.spring.formework.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/** 保存controller中 方法 url对应关系
 * @创建人 dw
 * @创建时间 2021/5/27
 * @描述
 */
public class GPHandlerMapping {
    // 目标方法对应的controller
    private Object controller;
    // url对应的方法
    private Method method;
    //url的封装
    private Pattern pattern;

    public GPHandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
