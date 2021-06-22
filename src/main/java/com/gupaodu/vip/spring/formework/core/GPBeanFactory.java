package com.gupaodu.vip.spring.formework.core;

/**
 * 单例工厂的顶层设计
 * @创建人 dw
 * @创建时间 2021/5/17
 * @描述
 */
public interface GPBeanFactory {

    /**
     * 根据beanName从ioc容器中获得一个实例bean
     * @param beanName
     * @return
     * @throws Exception
     */
    Object getBean(String beanName) throws Exception;

    Object getBean(Class<?> beanClass) throws Exception;

}

