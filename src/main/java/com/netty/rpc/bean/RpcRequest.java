package com.netty.rpc.bean;

import java.io.Serializable;

/**
 * @author lebron374
 * https://www.jianshu.com/u/1e38a3346f93
 */
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 5623523207288226903L;

    /**
     * 请求ID
     */
    private String requestId;
    /**
     * 调用class类名
     */
    private String className;
    /**
     * 调用方法名
     */
    private String methodName;
    /**
     * 调用参数类型集合
     */
    private Class<?>[] parameterTypes;
    /**
     * 调用参数集合
     */
    private Object[] parameters;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
