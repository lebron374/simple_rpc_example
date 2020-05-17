package com.netty.rpc.util;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lebron374
 * https://www.jianshu.com/u/1e38a3346f93
 * https://blog.csdn.net/oppo5630/article/details/80173520
 */
public class SerializationUtil {

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();
    private static Objenesis objenesis = new ObjenesisStd(true);

    private SerializationUtil(){

    }

    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (null == schema) {
            schema = RuntimeSchema.createFrom(cls);
            if (null != schema) {
                cachedSchema.put(cls, schema);
            }
        }

        return schema;
    }

    /**
     * 序列化
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T obj) {

        Class<T> cls = (Class<T>)obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        try {
            Schema<T> schema = getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    /**
     * 反序列化
     * @param data
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T deSerialize(byte[] data, Class<T> cls) {

        try {
            T message = objenesis.newInstance(cls);
            Schema<T> schema = getSchema(cls);
            ProtostuffIOUtil.mergeFrom(data, message, schema);

            return message;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
