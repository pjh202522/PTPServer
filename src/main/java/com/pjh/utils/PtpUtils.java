package com.pjh.utils;

import com.pjh.domain.PtpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author yueyinghaibao
 */
public class PtpUtils {

    public static String getResponse(PtpRequest ptpRequest, String ptpStatus, String message) {
        return "PTP/1.0\n"
                + "Type: RESPONSE\n"
                + "Date: " + ptpRequest.getDate() + "\n"
                + "Source: \n"
                + "Target: " + ptpRequest.getSourceAddress().getHostAddress() + ":" + ptpRequest.getSourcePort() + "\n"
                + "Status: " + ptpStatus + "\n"
                + "Message: " + message + "\n"
                + "Body: " + ptpRequest.getBody() + "\n";
    }

    public static String getForward(PtpRequest ptpRequest) {
        return "PTP/1.0\n"
                + "Type: FORWARD\n"
                + "Date: " + ptpRequest.getDate() + "\n"
                + "Source: " + ptpRequest.getSourceAddress().getHostAddress() + ":" + ptpRequest.getSourcePort() + "\n"
                + "Target: " + ptpRequest.getTargetAddress().getHostAddress() + ":" + ptpRequest.getTargetPort() + "\n"
                + "SourceUID: " + ptpRequest.getProperties().get("SourceUID:") + "\n"
                + "Body: " + ptpRequest.getBody() + "\n";
    }

    public static PtpRequest analysis(InputStream inputStream, InetAddress inetAddress, int port) throws IOException {
        String[] request = new String(StreamUtils.readStream(inputStream)).split("\\r?\\n");
        String ptpHead = "PTP/1.0";

        PtpRequest ptpRequest = new PtpRequest();
        Map<String, Object> map = new TreeMap<>();

        if(ptpHead.equals(request[0])) {
            for(int i = 1;i < request.length;i++) {
                String [] kv = request[i].split(" ");
                if (kv.length == 1) {
                    continue;
                }
                if ("Type:".equals(kv[0])) {
                    ptpRequest.setType(kv[1]);
                } else if ("Date:".equals(kv[0])) {
                    ptpRequest.setDate(kv[1]);
                } else if ("Target:".equals(kv[0])) {
                    ptpRequest.setTarget(kv[1]);
                } else if ("Body:".equals(kv[0])) {
                    ptpRequest.setBody(kv[1]);
                } else {
                    map.put(kv[0], kv[1]);
                }
            }
            ptpRequest.setSourceAddress(inetAddress);
            ptpRequest.setSourcePort(port);
            ptpRequest.setProperties(map);
        } else {
            throw new RuntimeException("非PTP协议");
        }

        return ptpRequest;
    }
}
