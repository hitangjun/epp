package com.hihexo.epp.common.aspect;

import java.lang.annotation.*;

/**
 * 自定义注解 拦截Controller
 */

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemControllerLog {
    String description() default "";
}