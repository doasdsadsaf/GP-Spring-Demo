package com.gupaodu.vip.spring.formework.webmvc.servlet;

import com.gupaodu.vip.spring.formework.annotation.GPController;
import com.gupaodu.vip.spring.formework.annotation.GPRequestMapping;
import com.gupaodu.vip.spring.formework.context.support.GPApplicationContext;
import com.gupaodu.vip.spring.formework.webmvc.GPHandlerMapping;
import com.gupaodu.vip.spring.formework.webmvc.GPModelAndView;
import com.gupaodu.vip.spring.formework.webmvc.GPView;
import com.gupaodu.vip.spring.formework.webmvc.GPViewResolver;

import javax.servlet.ServletConfig;
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
 * @创建人 dw
 * @创建时间 2021/4/30
 * @描述
 */
// servlet 只是作为一个MVC的启动入口
public class GPDispatcherServlet extends HttpServlet {

    private final String LOCATION = "contextConfigLocation";

    private List<GPHandlerMapping> handlerMappings = new ArrayList<GPHandlerMapping>();

    private Map<GPHandlerMapping, GPHandlerAdapter> handlerAdapters = new HashMap<GPHandlerMapping, GPHandlerAdapter>();

    private List<GPViewResolver> viewResolvers = new ArrayList<GPViewResolver>();
    private GPApplicationContext context;

    @Override
    public void init(ServletConfig config) {
        context = new GPApplicationContext(config.getInitParameter(LOCATION));
        initStrategies(context);
    }

    protected void initStrategies(GPApplicationContext context) {
        // 使用策略模式,有九大组件
        // 文件上传组件 如果请求类型是multipart,通过MultipartResolver进行文件上传解析
        initMultipartResolver(context);
        // 本地化解析
        initLocaleResolver(context);
        // 主题解析
        initThemeResolver(context);
        // 用来保存controller 中配置的RequestMapping 和method的对应关系
        // 通过HandlerMapping讲请求映射到处理器
        initHandlerMappings(context);
        // 通过Handler进行多类型的参数动态匹配
        // 匹配Method参数 包括类转换,动态赋值
        initHandlerAdapters(context);
        // 如果执行出现异常,交给HandlerExceptionResolver来解析
        initHandlerExceptionResolvers(context);
        // 直接将请求解析到视图名
        initRequestToViewNameTranslator(context);
        // 通过ViewResolvers实现动态模板的解析,将逻辑视图解析到具体视图实现
        initViewResolvers(context);
        // Flash 映射管理器
        initFlashMapManager(context);

    }


    private void initViewResolvers(GPApplicationContext context) {
        // 在页面输入http://localhost/first.html
        // 解决页面名字和模板文件关联问题
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File template : templateRootDir.listFiles()) {
            this.viewResolvers.add(new GPViewResolver(templateRoot));
        }
    }

    /**
     * 将Controller中配置的RequestMapping与Method进行一一对应
     *
     * @param context
     */
    private void initHandlerMappings(GPApplicationContext context) {
        // 拿到所有注册过的对象的beanName
        String[] beanNames = context.getBeanDefinitionNames();
        for (String bean : beanNames) {
            try {
                // 根据beanName拿到这个对象,getClass获得调用该方法的对象的类
                Object controller = context.getBean(bean);
                Class<?> clazz = controller.getClass();
                // 这个类如果上面打了GPController,.说明他是一个controller,跳出去
                if (!clazz.isAnnotationPresent(GPController.class)) {
                    continue;
                }
                String baseUrl = "";
                // 这个类如果上面打了GPRequestMapping
                if (clazz.isAnnotationPresent(GPRequestMapping.class)) {
                    // 获取GPRequestMapping 对象
                    GPRequestMapping requestMapping = clazz.getAnnotation(GPRequestMapping.class);
                    // 拿到GPRequestMapping 注解里的value值
                    baseUrl = requestMapping.value();
                }
                // 扫描所有的public类型的方法
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(GPRequestMapping.class)) {
                        continue;
                    }
                    GPRequestMapping requestMapping = method.getAnnotation(GPRequestMapping.class);
                    String regex = "/" + baseUrl + requestMapping.value().replaceAll("\\*", ".*").replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void initMultipartResolver(GPApplicationContext context) {
    }

    private void initLocaleResolver(GPApplicationContext context) {
    }

    private void initThemeResolver(GPApplicationContext context) {

    }

    private void initHandlerExceptionResolvers(GPApplicationContext context) {
    }

    private void initRequestToViewNameTranslator(GPApplicationContext context) {
    }

    private void initFlashMapManager(GPApplicationContext context) {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        this.doPost(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            doDispatch(req, res);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse res) throws Exception {
        // 根据用户请求获取handler
        GPHandlerMapping handler = getHandler(req);
        if (handler == null) {
            processDispatchResult(req, res, new GPModelAndView("404"));
            return;
        }
        GPHandlerAdapter ha = getHandlerAdapter(handler);
        //得到返回值
        GPModelAndView mv = ha.handle(req, res, handler);
        // 输出
        processDispatchResult(req, res, mv);

    }

    private GPHandlerMapping getHandler(HttpServletRequest req) {
        if(this.handlerMappings.isEmpty()){
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        for(GPHandlerMapping handler: this.handlerMappings){
            Matcher matcher = handler.getPattern().matcher(url);
            if(!matcher.matches()){
                continue;
            }
            return handler;
        }
        return null;
    }

    private GPHandlerAdapter getHandlerAdapter(GPHandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()){
            return null;
        }
        GPHandlerAdapter ha = this.handlerAdapters.get(handler);
        if(ha.supports(handler)){
            return ha;
        }
        return null;
    }


    private void processDispatchResult(HttpServletRequest req, HttpServletResponse res, GPModelAndView mv) throws IOException {
        // 调用viewResolver的resolveViewName方法
        if (null == mv) {
            return;
        }
        if(this.viewResolvers.isEmpty()){
            return;
        }
        if(this.viewResolvers != null){
            for(GPViewResolver viewResolver : this.viewResolvers){
               GPView view = viewResolver.resolveViewName(mv.getViewName(),null);
                if(Objects.nonNull(view)){
                    view.render(mv.getModel(),req,res);
                    return;
                }
            }
        }

    }

    private void initHandlerAdapters(GPApplicationContext context) {
        // 在初始化阶段,将这些参数的名字或类型按一定顺序保存起来
        //后面传的形参是一个数组,通过记录这些参数位置的index,逐个从数组中取值,跟参数的顺序无关
        for (GPHandlerMapping handlerMapping : this.handlerMappings) {
            // 每个方法都有一个参数列表,这里保存的是形参列表
            this.handlerAdapters.put(handlerMapping, new GPHandlerAdapter());
        }
    }

}

