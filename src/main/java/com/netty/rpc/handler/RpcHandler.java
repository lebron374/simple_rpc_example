package com.netty.rpc.handler;

import com.netty.rpc.bean.RpcRequest;
import com.netty.rpc.bean.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcHandler.class);

    private final Map<String, Object> handlerMap;

    public RpcHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {

        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setReponseId(rpcRequest.getRequestId());

        try {

            Object result = this.handle(rpcRequest);
            rpcResponse.setResult(result);
        } catch (Exception e) {
            rpcResponse.setError(e);
        }

        channelHandlerContext.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE);

    }

    private Object handle(RpcRequest request) throws InvocationTargetException {
        String className = request.getClassName();
        Object serviceBean = this.handlerMap.get(className);

        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        Class<?> serviceClass = serviceBean.getClass();
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);

        return serviceFastMethod.invoke(serviceBean, parameters);
    }
}
