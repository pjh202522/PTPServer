package com.pjh;

import com.pjh.domain.PtpRequest;
import com.pjh.pool.PTPThreadManager;
import com.pjh.task.SendThread;
import com.pjh.task.LoginThread;
import com.pjh.utils.PtpUtils;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Log4j
public class PTPServer {

    private ServerSocket server;

    public PTPServer(int port) {
        try {
            server = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() throws IOException {
        while(true) {
            Socket socket = server.accept();
            PtpRequest ptpRequest = null;
            try {
                ptpRequest = PtpUtils.analysis(socket.getInputStream(), socket.getInetAddress(), socket.getPort());
            } catch (RuntimeException e) {
                log.warn(socket.getInetAddress() + ":" + socket.getPort() + "  " + e.getMessage());
                socket.close();
            }

            if(ptpRequest == null) {
                continue;
            }

            if ("SEND".equals(ptpRequest.getType())) {
                PTPThreadManager.submit(new SendThread(socket, ptpRequest));
            } else if ("LOGIN".equals(ptpRequest.getType())) {
                PTPThreadManager.submit(new LoginThread(socket, ptpRequest));
            }

        }
    }
}
