package com.gupaodu.vip.spring.formework.annotation;

import java.lang.annotation.*;

/**
 *  业务逻辑,注入接口
 *
 * @创建人 dw
 * @创建时间 2021/4/30
 * @描述
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GPService {

    String value() default "";
}
