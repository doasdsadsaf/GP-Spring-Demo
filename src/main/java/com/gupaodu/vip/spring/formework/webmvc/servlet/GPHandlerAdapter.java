package com.gupaodu.vip.spring.formework.webmvc.servlet;

import com.gupaodu.vip.spring.formework.annotation.GPRequestMapping;
import com.gupaodu.vip.spring.formework.annotation.GPRequestParam;
import com.gupaodu.vip.spring.formework.webmvc.GPHandlerMapping;
import com.gupaodu.vip.spring.formework.webmvc.GPModelAndView;
import jdk.internal.org.objectweb.asm.Handle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationEvent;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

/**
 * 原生的Spring HandlerAdapter 主要完成请求传递到服务端的参数列表与Method实参列表的对应关系
 * 完成参数值的类型转换工作.核心方法是handle 在handle中用反射来调用被适配的目标方法,并将转换包装好的参数列表传递过去
 *
 * @创建人 dw
 * @创建时间 2021/5/27
 * @描述
 */
public class GPHandlerAdapter {
    public boolean supports(Object handler) {
        return (handler instanceof GPHandlerMapping);
    }

    public GPModelAndView handle(HttpServletRequest req, HttpServletResponse res, Object handler) throws InvocationTargetException, IllegalAccessException {
        GPHandlerMapping handlerMapping = (GPHandlerMapping) handler;
        // 每个方法都有一个参数列表,这里保存的是形参
        HashMap<String, Integer> paramMapping = new HashMap<String, Integer>();
        // 给出命名参数
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        for(int i = 0;i<pa.length;i++){
            for(Annotation a : pa[i]){
                if(a instanceof GPRequestParam){
                    String paramName = ((GPRequestParam) a).value();
                    if(!"".equals(paramName.trim())){
                        paramMapping.put(paramName,i);
                    }
                }
            }
        }
        // 根据用户请求的参数信息 跟Method中的参数信息进行动态匹配
        // resp 传进来的目的只有一个 将其赋值给方法参数
        // 只有用户传过来的ModelAndView为空,才会新建一个默认的
        // 1. 准备好这个方法的形参列表
        // 方法重载时形参的决定因素 参数的个数,参数的类型,参数的顺序,方法名字
        // 只处理request response
        Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
        for(int i = 0 ; i < paramTypes.length;i++){
            Class<?> type = paramTypes[i];
            if(type == HttpServletRequest.class || type ==HttpServletResponse.class){
                paramMapping.put(type.getName(),i);
            }
        }
        // 2.得到自定义命名参数所在的位置
        Map<String,String[]> reqParameterMap = req.getParameterMap();
        // 3.构造实参列表
        Object[] paramValues = new Object[paramTypes.length];
        for(Map.Entry<String,String[]> param:reqParameterMap.entrySet()){
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s", "");
            if(!paramMapping.containsKey(param.getKey())){
                continue;
            }
            Integer index = paramMapping.get(param.getKey());
            // 进行参数转换
            paramValues[index] = caseStringValue(value,paramTypes[index]);
            if(paramMapping.containsKey(HttpServletRequest.class.getName())){
                Integer reqIndex = paramMapping.get(HttpServletRequest.class.getName());
                paramValues[reqIndex] = req;
            }
            if(paramMapping.containsKey(HttpServletResponse.class.getName())){
                Integer respIndex = paramMapping.get(HttpServletResponse.class.getName());
                paramValues[respIndex] =  res;
            }
            // 从handler中取出Controller Method ,反射进行调用
            Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
            if(result == null){
                return null;
            }
            if(handlerMapping.getMethod().getReturnType() == GPModelAndView.class){
                return (GPModelAndView) result;
            }
        }
        return null;

    }

    private Object caseStringValue(String value, Class<?> clazz) {
            if(clazz == String.class){
                return value;
            }else if(clazz == Integer.class){
                return Integer.valueOf(value);
            }else{
                return null;
            }
    }
}
