package com.gupaodu.vip.spring.formework.context.support;

/**
 * IOC容器实现的顶层设计
 * @创建人 dw
 * @创建时间 2021/5/17
 * @描述
 */
public abstract class GPAbstractApplicationContext {
    // 受保护,只提供给子类重写
    public void refresh() throws Exception{}

}
