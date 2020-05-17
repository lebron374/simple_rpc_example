package com.netty.rpc.bean;

import java.io.Serializable;

/**
 * @author lebron374
 * https://www.jianshu.com/u/1e38a3346f93
 */
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = -2733031642960312811L;

    /**
     * 响应ID
     */
    private String reponseId;

    /**
     * 异常对象
     */
    private Throwable error;

    /**
     * 响应结果
     */
    private Object result;

    public String getReponseId() {
        return reponseId;
    }

    public void setReponseId(String reponseId) {
        this.reponseId = reponseId;
    }

    public boolean hasError(){
        return error != null;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
