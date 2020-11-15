package com.netty.rpc.demo;

import com.netty.rpc.server.RpcServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by lebron374 on 2020/11/15.
 */
public class Provider {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("provider/application-provider.xml");
        RpcServer rpcServer = context.getBean(RpcServer.class);

        while (true) {
            Thread.sleep(10000);
        }
    }
}
