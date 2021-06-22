package com.gupaodu.vip.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * @创建人 dw
 * @创建时间 2021/5/14
 * @描述
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GPController {
    String value() default "";
}
