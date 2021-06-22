package com.gupaodu.vip.spring.formework.beans.support;

import com.gupaodu.vip.spring.formework.beans.config.GPBeanDefinition;
import com.gupaodu.vip.spring.formework.context.support.GPAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @创建人 dw
 * @创建时间 2021/5/17
 * @描述
 */
public class GPDefaultListableBeanFactory extends GPAbstractApplicationContext {
    // 存储注册信息的BeanDefinition
    protected final Map<String, GPBeanDefinition> beanDefinitionMap =
            new ConcurrentHashMap<String, GPBeanDefinition>();
}
