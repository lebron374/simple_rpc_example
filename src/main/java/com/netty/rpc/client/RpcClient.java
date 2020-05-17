package com.netty.rpc.client;

import com.netty.rpc.bean.RpcRequest;
import com.netty.rpc.bean.RpcResponse;
import com.netty.rpc.codec.RpcDecoder;
import com.netty.rpc.codec.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    /**
     * 主机名
     */
    private String host;
    /**
     * 端口
     */
    private int port;
    /**
     * RPC响应对象
     */
    private RpcResponse response;

    private final Object obj = new Object();

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {

        this.response = rpcResponse;

        synchronized (obj) {
            obj.notifyAll();
        }
    }

    public RpcResponse send(RpcRequest rpcRequest) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new RpcEncoder(RpcRequest.class))
                                    .addLast(new RpcDecoder(RpcResponse.class))
                                    .addLast(RpcClient.this);
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.connect(host, port).sync();

            future.channel().writeAndFlush(rpcRequest).sync();

            synchronized (obj){
                //未收到响应，使线程继续等待
                obj.wait();
            }

            if(null != response){
                //关闭RPC请求连接
                future.channel().closeFuture().sync();
            }

            return response;
        } catch (Exception e) {
            LOGGER.error("RpcClient 异常 ", e);
        } finally {
            group.shutdownGracefully();
        }

        return null;
    }
}
