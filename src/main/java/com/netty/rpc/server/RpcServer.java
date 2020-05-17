package com.netty.rpc.server;

import com.netty.rpc.annotation.RpcService;
import com.netty.rpc.bean.RpcRequest;
import com.netty.rpc.bean.RpcResponse;
import com.netty.rpc.codec.RpcDecoder;
import com.netty.rpc.codec.RpcEncoder;
import com.netty.rpc.handler.RpcHandler;
import com.netty.rpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lebron374
 * https://www.jianshu.com/u/1e38a3346f93
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    // 保存server地址
    private String serverAddress;

    // 保存注册中心
    private ServiceRegistry serviceRegistry;

    // 保存接口和服务对象的映射关系
    private Map<String, Object> handlerMap = new HashMap<>();

    public RpcServer(String serverAddress, ServiceRegistry serviceRegistry) {
        this.serverAddress = serverAddress;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        EventLoopGroup masterGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(masterGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new RpcDecoder(RpcRequest.class))
                                    .addLast(new RpcEncoder(RpcResponse.class))
                                    .addLast(new RpcHandler(handlerMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            //解析IP地址和端口信息
            String[] array = serverAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);

            //启动RPC服务端
            ChannelFuture channelFuture = bootstrap.bind(host, port).sync();
            LOGGER.debug("server started on port: {}", port);

            if(null != serviceRegistry){
                //注册服务地址
                serviceRegistry.register(serverAddress);
                LOGGER.debug("register service:{}", serverAddress);
            }

            //关闭RPC服务器
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {

        } finally {
            workerGroup.shutdownGracefully();
            masterGroup.shutdownGracefully();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                handlerMap.put(interfaceName, serviceBean);
            }
        }
    }
}
