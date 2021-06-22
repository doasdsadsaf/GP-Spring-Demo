package com.gupaodu.vip.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * @创建人 dw
 * @创建时间 2021/5/14
 * @描述
 */
// 定义这个注解能用在哪里
@Target({ElementType.FIELD})
// 定义这个注解的生命周期
@Retention(RetentionPolicy.RUNTIME)
// 在生成java文档时 把这个注解标注出来
@Documented
public @interface GPAutowired {
    String value() default "";
}

