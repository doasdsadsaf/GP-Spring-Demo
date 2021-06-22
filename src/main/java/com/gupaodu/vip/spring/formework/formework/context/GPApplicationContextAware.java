package com.gupaodu.vip.spring.formework.formework.context;

import com.gupaodu.vip.spring.formework.context.support.GPApplicationContext;

/**
 * 通过解耦方式获得IOC容器的顶层设计
 * 后面将通过一个监听器去扫描所有的类,只要实现了此接口
 * 将自动调用setApplicationContext()方法,从而将IOC容器注入目标类
 * @创建人 dw
 * @创建时间 2021/5/20
 * @描述
 */

public interface GPApplicationContextAware {

    void setApplicationContext(GPApplicationContext applicationContext);
}
