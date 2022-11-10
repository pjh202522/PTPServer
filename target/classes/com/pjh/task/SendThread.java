package com.pjh.task;

import com.pjh.domain.LecUser;
import com.pjh.domain.PtpRequest;
import com.pjh.pool.JedisPoolUtils;
import com.pjh.pool.PTPThreadManager;
import com.pjh.utils.JsonUtils;
import com.pjh.utils.PtpUtils;
import lombok.extern.log4j.Log4j;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Log4j
public class SendThread implements Runnable, PtpThread {

    private Socket socket;

    private PtpRequest ptpRequest;

    private String threadName;

    public SendThread(Socket socket, PtpRequest ptpRequest) {
        this.socket = socket;
        this.ptpRequest = ptpRequest;
        this.threadName = socket.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        if(!JedisPoolUtils.existJedisPool()) {
            JedisPoolUtils.newJedisPool();
        }
        Jedis jedis = JedisPoolUtils.getResource();

        String ptpStatus = "OK";
        String message = "OK";

        try {
            LecUser targetUser = null;
            LecUser sourceUser = null;

            if(jedis != null) {
                String json1 = jedis.get("user:" + ptpRequest.getProperties().get("TargetUID:"));
                String json2 = jedis.get("user:" + ptpRequest.getProperties().get("SourceUID:"));
                if(json1 == null || json2 == null) {
                    throw new NullPointerException("查无此用户");
                }
                targetUser = JsonUtils.jsonToObject(LecUser.class, json1);
                sourceUser = JsonUtils.jsonToObject(LecUser.class, json2);
                ptpRequest.setTargetAddress(InetAddress.getByName(targetUser.getAddress()));
                ptpRequest.setTargetPort(targetUser.getPort());
            } else {
                throw new RuntimeException("jedis 获取失败");
            }

            if(sourceUser.getStatus() == 0) {
                throw new RuntimeException("当前用户未上线");
            }

            String forward = PtpUtils.getForward(ptpRequest);
            String key = "wait:" + ptpRequest.getProperties().get("TargetUID:");
            jedis.lpush(key, forward);

            PTPThreadManager.submit(new ForwardThread(targetUser));
        } catch (Exception e) {
            log.error(e.getMessage());
            ptpStatus = "ERROR";
            message = e.getMessage();
        }

        String response = PtpUtils.getResponse(ptpRequest, ptpStatus, message);

        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
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
