package com.pjh.task;

import com.alibaba.fastjson2.JSON;
import com.pjh.domain.LecUser;
import com.pjh.pool.JedisPoolUtils;
import lombok.extern.log4j.Log4j;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Log4j
public class ForwardThread implements Runnable, PtpThread {

    private LecUser targetUser;
    private String threadName;

    public ForwardThread(LecUser lecUser) {
        this.targetUser = lecUser;
        this.threadName = "Forward Thread to" + lecUser.getName();
    }

    @Override
    public void run() {
        if(!JedisPoolUtils.existJedisPool()) {
            JedisPoolUtils.newJedisPool();
        }
        Jedis jedis = JedisPoolUtils.getResource();
        Socket targetSocket = new Socket();
        try {
            try {
                targetSocket.connect(new InetSocketAddress(targetUser.getAddress(), 9090));
            } catch (IOException e) {
                log.warn(e.getMessage());
                targetUser.setStatus((byte) 0);
                jedis.set("user:" + targetUser.getId(), JSON.toJSONString(targetUser));
            }

            if(targetUser.getStatus() == 1) {
                List<String> forwards = jedis.lrange("wait:" + targetUser.getId(), 0, -1);
                OutputStream outputStream = targetSocket.getOutputStream();
                outputStream.write(forwards.toString().getBytes(StandardCharsets.UTF_8));
                log.info("转发成功");
                targetSocket.shutdownOutput();
                jedis.del("wait:" + targetUser.getId());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                targetSocket.close();
                jedis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getThreadName() {
        return threadName;
    }
}
