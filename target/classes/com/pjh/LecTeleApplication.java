package com.pjh;

import com.pjh.pool.JedisPoolUtils;
import com.pjh.pool.MysqlPoolUtils;
import com.pjh.task.LoginThread;
import com.pjh.utils.PtpUtils;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.net.Socket;

/**
 * @author yueyinghaibao
 */

@Log4j
public class LecTeleApplication {

    public static void main(String[] args) {
        JedisPoolUtils.newJedisPool();
        MysqlPoolUtils.newMysqlPool();
        PTPServer server = new PTPServer(8080);
        try {
            server.run();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
