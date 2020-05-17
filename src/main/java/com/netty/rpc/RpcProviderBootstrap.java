package com.netty.rpc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author lebron374
 * https://www.jianshu.com/u/1e38a3346f93
 */
public class RpcProviderBootstrap {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("provider/application-provider.xml");
    }
}
