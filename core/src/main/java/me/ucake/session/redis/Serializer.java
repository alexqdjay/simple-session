package me.ucake.session.redis;

/**
 * Created by alexqdjay on 2017/9/3.
 */
public interface Serializer {

    byte[] encode(Object object) throws Exception;

    Object decode(byte[] bytes) throws Exception;

}
