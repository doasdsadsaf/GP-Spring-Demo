package com.gupaodu.vip.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * 请求映射参数
 *
 * @创建人 dw
 * @创建时间 2021/4/30
 * @描述
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GPRequestParam {
    String value() default "";
}
