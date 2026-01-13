package com.sky.aspect;

import com.sky.annotation.AutoFill;

import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;


import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自动填充方面
 *
 * @author maziy
 * @date 2025/12/17
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     *
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException {
        log.info("开始进行数据填充");
        //获取当前被拦截的方法的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//获取方法签名
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获取方法上的数据库操作类型
        OperationType value = autoFill.value();
        //获取当前被拦截的方法参数--实体参数
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0){
            return;
        }
        Object object = args[0];
        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        //根据当前的数据库操作类型，为对应的属性赋值
        if(value == OperationType.INSERT){
            //为4个公共序列赋值
            try{
                Method setCreateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //调用反射方法为对应的属性赋值
                setCreateTime.invoke(object, now);
                setCreateUser.invoke(object, currentId);
                setUpdateTime.invoke(object, now);
                setUpdateUser.invoke(object, currentId);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(value == OperationType.UPDATE){
            //为2个公共序列赋值
            try{
                Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //调用反射方法为对应的属性赋值
                setUpdateTime.invoke(object, now);
                setUpdateUser.invoke(object, currentId);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
