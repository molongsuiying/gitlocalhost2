package org.jeecg.modules.college.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Excel {

    /**
     * 导出到Excel中的名字
     */
    String name() default "";

    /**
     * 日期格式, 如: yyyy-MM-dd
     */
    String dateFormat() default "";

    /**
     * 字典的key值
     */
    String dictKey() default "";

    /**
     * 读取内容转表达式 (如: 0=男,1=女,2=未知)
     */
    String dictExp() default "";
}
