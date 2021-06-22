package com.gupaodu.vip.spring.formework.beans.config;

/**
 * @创建人 dw
 * @创建时间 2021/5/26
 * @描述
 */
public class GPBeanPostProcessor {

    /**
     * bean初始化之前回调
     * @param bean
     * @param beanName
     * @return
     */
    public Object postProcessBeforeInitialization(Object bean,String beanName){
        return bean;
    }

    /**
     * bean初始化之后回调
     * @param bean
     * @param beanName
     * @return
     */
    public Object postProcessAfterInitialization(Object bean,String beanName){
        return bean;
    }
}
