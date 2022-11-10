package com.pjh.pool;

import com.pjh.anno.YmlValue;
import com.pjh.utils.YmlUtils;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author yueyinghaibao
 */

public class JedisPoolFactory {

    @YmlValue("jedis.pool.address")
    private String address;

    @YmlValue("jedis.pool.port")
    private int port;

    @YmlValue("jedis.pool.timeout")
    private int timeout;

    @YmlValue("jedis.pool.testOnBorrow")
    private boolean testOnBorrow;

    @YmlValue("jedis.pool.maxIdle")
    private int maxIdle;

    public JedisPool initialize() {

        YmlUtils.getYmlValue(this, JedisPoolFactory.class);

        System.out.println(address);

        JedisPool jedisPool = null;
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxIdle(maxIdle);
            config.setTestOnBorrow(testOnBorrow);
            jedisPool = new JedisPool(config, address, port, timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jedisPool;
    }
}
