package com.pjh.task;

import com.alibaba.fastjson2.JSON;
import com.pjh.domain.LecUser;
import com.pjh.domain.LecUserVO;
import com.pjh.domain.PtpRequest;
import com.pjh.pool.JedisPoolUtils;
import com.pjh.pool.MysqlPoolUtils;
import com.pjh.pool.PTPThreadManager;
import com.pjh.utils.MySQLUtils;
import com.pjh.utils.PtpUtils;
import lombok.extern.log4j.Log4j;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.List;

/**
 * @author yueyinghaibao
 */
@Log4j
public class LoginThread implements Runnable, PtpThread {

    private Socket socket;

    private PtpRequest ptpRequest;

    private String threadName;

    public LoginThread(Socket socket, PtpRequest ptpRequest) {
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

        String name = (String) ptpRequest.getProperties().get("Name:");
        String password = (String) ptpRequest.getProperties().get("Password:");

        String countSql = "select count(*) as exist from db_lec_user where `name` = ? and password = ?";
        String insertSql = "insert into db_lec_user (`name`, password) values (?, ?)";
        String selectSql = "select id, `name` from db_lec_user where `name` = ? and password = ?";
        String selectAll = "select id, `name` from db_lec_user";

        Connection cnt = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            if(!MysqlPoolUtils.existMysqlPool()) {
                MysqlPoolUtils.newMysqlPool();
            }

            cnt = MysqlPoolUtils.getConnection();

            ps = MySQLUtils.getPs(cnt, countSql, new Object[]{name, password});
            resultSet = ps.executeQuery();

            while(resultSet.next()) {
                if (resultSet.getInt("exist") == 0) {
                    ps = MySQLUtils.getPs(cnt, insertSql, new Object[]{name, password});
                    ps.execute();
                }
            }

            ps = MySQLUtils.getPs(cnt, selectSql, new Object[]{name, password});
            List<LecUser> lecUsers = MySQLUtils.resultSetToObject(ps.executeQuery(), LecUser.class);
            LecUser user;

            if(lecUsers.size() == 1) {
                user = lecUsers.get(0);
                user.setStatus((byte) 1);
                user.setAddress(socket.getInetAddress().getHostAddress());
                user.setPort(socket.getPort());
            } else {
                throw new RuntimeException("密码错误");
            }
            System.out.println(user.getStatus());
            jedis.set("user:" + user.getId(), JSON.toJSONString(user));

            ps = MySQLUtils.getPs(cnt, selectAll, new Object[]{});
            List<LecUserVO> lecUserVOS = MySQLUtils.resultSetToObject(ps.executeQuery(), LecUserVO.class);

            ptpRequest.setBody(userListToString(lecUserVOS));

            PTPThreadManager.submit(new ForwardThread(user));

        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            log.warn(e.getMessage());
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
                MysqlPoolUtils.close(resultSet, ps, cnt);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getThreadName() {
        return threadName;
    }

    private String userListToString(List<LecUserVO> list) {
        StringBuilder res = new StringBuilder();
        for(int i = 0;i < list.size();i++) {
            res.append(list.get(i).getId()).append(":").append(list.get(i).getName());
            if(i != list.size() - 1) {
                res.append(",");
            }
        }
        return res.toString();
    }
}
