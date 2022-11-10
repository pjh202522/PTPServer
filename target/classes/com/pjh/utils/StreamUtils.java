package com.pjh.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
    public static byte[] readStream(InputStream ips) throws IOException {
        ByteArrayOutputStream ops = new ByteArrayOutputStream();
        byte [] buffer = new byte[1024];
        int len = -1;
        while((len = ips.read(buffer)) != -1) {
            ops.write(buffer, 0, len);
            if(ips.available() <= 0) {
                break;
            }
        }
        ops.close();
        return ops.toByteArray();
    }
}
