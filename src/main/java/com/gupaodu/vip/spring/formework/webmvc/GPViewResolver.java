package com.gupaodu.vip.spring.formework.webmvc;

import java.io.File;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 将静态文件变为动态文件
 * 根据用户传送不同的参数,产生不同的效果,最终交给response
 *  完成模板名称和模板解析引擎的匹配,通过resolveViewName实现
 * @创建人 dw
 * @创建时间 2021/7/14
 * @描述
 */
public class GPViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";
    private File templateRootDir;
    private String viewName;

    public GPViewResolver(String templateRootDir) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRootDir).getFile();
        this.templateRootDir = new File(templateRootPath);
    }

    public String getViewName() {
        return viewName;
    }

    public GPView resolveViewName(String viewName, Locale locale) {
        this.viewName = viewName;
        if (null == viewName || "".equals(viewName.trim())) {
            viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
            File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
            return new GPView(templateFile);
        }
        return null;

    }
}
