package com.pjh.pool;

import lombok.extern.log4j.Log4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author yueyinghaibao
 */

@Log4j
public class JedisPoolUtils {
    private static JedisPool jedisPool = null;

    public static void newJedisPool() {
        if(jedisPool == null) {
            jedisPool = new JedisPoolFactory().initialize();
        }
    }

    public static boolean existJedisPool() {
        return jedisPool != null;
    }

    public static Jedis getResource() {
        if(jedisPool != null) {
            Jedis jedis = jedisPool.getResource();
            jedis.select(3);
            return jedis;
        } else {
            log.warn("线程池未初始化");
            return null;
        }
    }

    public static void close() {
        if(jedisPool != null) {
            jedisPool.close();
        } else {
            log.warn("线程池未初始化");
        }
    }
}
