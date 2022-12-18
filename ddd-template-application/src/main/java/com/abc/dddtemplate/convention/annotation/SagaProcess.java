package com.abc.dddtemplate.convention.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <template/>
 * @date
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SagaProcess {

    /**
     * 处理名称
     * @return
     */
    String name() default "";

    String preview() default "";

    String parent() default "";

    /**
     * 处理环节编码，区分SAGA不同处理环节
     * @return
     */
    int code() default 0;

}
