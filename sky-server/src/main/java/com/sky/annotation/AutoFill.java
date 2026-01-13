package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动填充,标识某个方法需要进行功能字段自动填充处理
 *
 * @author maziy
 * @date 2025/12/17
 */
@Target(ElementType.METHOD) // 注解所修饰的元素类型为方法
@Retention(RetentionPolicy.RUNTIME) // 注解所修饰的元素在运行时保留
public @interface AutoFill {
    // 数据库操作类型 UPDATE INSERT
    OperationType value();
}
