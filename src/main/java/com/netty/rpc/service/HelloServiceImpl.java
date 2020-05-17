package com.netty.rpc.service;

import com.netty.rpc.annotation.RpcService;

/**
 * @author lebron374
 * https://www.jianshu.com/u/1e38a3346f93
 */
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        return "welcome to " + name;
    }
}
