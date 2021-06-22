package com.gupaodu.vip.spring.formework.beans;

/**
 * 主要用来封装创建后的对象实例,代理对象或者原生对象都由BeanWrapper来保存
 * @创建人 dw
 * @创建时间 2021/5/17
 * @描述
 */
public class GPBeanWrapper {
    // 原生的对象
    private Object wrappedInstance;
    // 封装了一层原生的对象
    private Class<?> wrappedClass;

    public GPBeanWrapper(Object wrappedInstance){
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    public void setWrappedInstance(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    // 返回代理以后的class,可能会是$Proxy0
    public Class<?> getWrappedClass() {
        return wrappedClass;
    }

    public void setWrappedClass(Class<?> wrappedClass) {
        this.wrappedClass = wrappedClass;
    }
}
