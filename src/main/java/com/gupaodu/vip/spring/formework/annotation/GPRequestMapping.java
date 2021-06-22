package com.gupaodu.vip.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * 请求url
 *
 * @创建人 dw
 * @创建时间 2021/4/30
 * @描述
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GPRequestMapping {
    String value() default "";
}
