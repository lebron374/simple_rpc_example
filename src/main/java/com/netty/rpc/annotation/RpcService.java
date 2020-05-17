package com.netty.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lebron374
 * https://www.jianshu.com/u/1e38a3346f93
 * RPC服务端注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    Class<?> value();
}
