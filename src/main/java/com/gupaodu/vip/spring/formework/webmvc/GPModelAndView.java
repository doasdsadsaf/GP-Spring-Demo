package com.gupaodu.vip.spring.formework.webmvc;

import java.util.Map;

/**
 * @创建人 dw
 * @创建时间 2021/7/14
 * @描述
 */
public class GPModelAndView {
    // 页面模板的名称
    private String viewName;
    //页面传送参数
    private Map<String,?> model;

    public GPModelAndView(String viewName) {
        this(viewName,null);
    }

    public GPModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
