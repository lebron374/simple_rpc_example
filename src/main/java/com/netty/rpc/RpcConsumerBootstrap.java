package com.netty.rpc;

import com.netty.rpc.client.RpcProxy;
import com.netty.rpc.service.HelloService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;

public class RpcConsumerBootstrap {

    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("consumer/application-consumer.xml");

        HelloService helloService = context.getBean(RpcProxy.class).create(HelloService.class);
        String result = helloService.sayHello("sun boy");

        System.out.println(result);
    }
}
