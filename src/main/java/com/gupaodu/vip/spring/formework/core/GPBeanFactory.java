package com.gupaodu.vip.spring.formework.core;

/**
 * 单例工厂的顶层设计
 * @创建人 dw
 * @创建时间 2021/5/17
 * @描述
 */
public interface GPBeanFactory {

    //对FactoryBean的转义定义，因为如果使用bean的名字检索FactoryBean得到的对象是工厂生成的对象，
    //如果需要得到工厂本身，需要转义
    String FACTORY_BEAN_PREFIX = "&";

    /**
     * 根据beanName从ioc容器中获得一个实例bean
     * @param beanName
     * @return
     * @throws Exception
     */
    Object getBean(String beanName) throws Exception;

    //     根据beanClass从ioc容器中获得一个实例bean
    Object getBean(Class<?> beanClass) throws Exception;

}

