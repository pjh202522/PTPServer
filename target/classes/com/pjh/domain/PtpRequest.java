package com.pjh.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.management.ObjectName;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * @author yueyinghaibao
 */

@Data
@NoArgsConstructor
public class PtpRequest {
    private InetAddress sourceAddress;
    private int sourcePort;

    private InetAddress targetAddress;
    private int targetPort;

    private String type;

    private String date;

    private Map<String, Object> properties;

    private String body;

    public void setSource(String s) throws UnknownHostException {
        String[] split = s.split(":");
        setSourceAddress(InetAddress.getByName(split[0]));
        setSourcePort(Integer.parseInt(split[1]));
    }

    public void setTarget(String s) throws UnknownHostException {
        String[] split = s.split(":");
        setTargetAddress(InetAddress.getByName(split[0]));
        setTargetPort(Integer.parseInt(split[1]));
    }

    public String createPtpPacket() {
        StringBuilder packet = new StringBuilder();
        packet.append("PTP/1.0\n")
                .append("Type: ").append(type).append("\n")
                .append("Date: ").append(date).append("\n")
                .append("Source: ");

        if(sourceAddress != null) {
            packet.append(sourceAddress.getHostAddress()).append(":").append(sourcePort);
        }
        packet.append("\n").append("Target: ").append(targetAddress.getHostAddress()).append(":").append(targetPort).append("\n");

        properties.forEach((i, j) -> {packet.append(i).append(" ").append(j).append("\n");});

        if(body != null && ! "".equals(body)) {
            packet.append("Body: ").append(body).append("\n");
        }

        return packet.toString();
    }
}
