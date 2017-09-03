package me.ucake.session.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static me.ucake.session.Consts.DEFAULT_CHARSET;

/**
 * Created by alexqdjay on 2017/9/3.
 */
public class RedisTemplate {

    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    private JedisPool jedisPool;
    private Serializer serializer = DEFAULT_SERIALIZER;


    public void hmset(String key, Map<String, Object> map) {
        Map<byte[], byte[]> hash = new HashMap<>();
        // TODO null value process
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            hash.put(keyEncode(k), valueEncode(v));
        }
        this.execute(jedis -> {
            jedis.hmset(keyEncode(key), hash);
            return null;
        });
    }

    public Map<String, Object> hmget(String key) {
        Map<byte[], byte[]> bytesMap = this.execute(jedis -> jedis.hgetAll(keyEncode(key)));
        Map<String, Object> valuesMap = new HashMap<>();
        for (Map.Entry<byte[], byte[]> entry : bytesMap.entrySet()) {
            valuesMap.put(keyDecode(entry.getKey()), valueDecode(entry.getValue()));
        }
        return valuesMap;
    }

    public <T> T execute(RedisCallback<T> redisCallback) {
        try (Jedis jedis = jedisPool.getResource()) {
            return redisCallback.execute(jedis);
        }
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public byte[] keyEncode(String key) {
        try {
            return key.getBytes(DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String keyDecode(byte[] key) {
        try {
            return new String(key, DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] valueEncode(Object v) {
        try {
            return serializer.encode(v);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object valueDecode(byte[] bytes) {
        try {
            return serializer.decode(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
