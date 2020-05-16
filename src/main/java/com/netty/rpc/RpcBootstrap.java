package com.netty.rpc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RpcBootstrap {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("provider/application-server.xml");
    }
}
