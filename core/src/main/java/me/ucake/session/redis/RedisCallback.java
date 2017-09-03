package me.ucake.session.redis;

import redis.clients.jedis.Jedis;

/**
 * Created by alexqdjay on 2017/9/3.
 */
@FunctionalInterface
public interface RedisCallback<T> {

    T execute(Jedis jedis);

}
