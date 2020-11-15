package com.netty.rpc.demo;

import com.netty.rpc.client.RpcProxy;
import com.netty.rpc.server.RpcServer;
import com.netty.rpc.service.HelloService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by lebron374 on 2020/11/15.
 */
public class Consumer {

    public static void main(String[] args) throws Exception {

        ApplicationContext context = new ClassPathXmlApplicationContext("consumer/application-consumer.xml");
        RpcProxy rpcProxy = context.getBean(RpcProxy.class);
        HelloService helloService = rpcProxy.create(HelloService.class);
        String result = helloService.sayHello("123");

        System.out.println(result);
    }
}
