
package com.ob.server;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.internal.StringUtil;

import java.util.Map;

public class PrintUtil {
    public static StringBuilder appendRequest(StringBuilder buf, HttpRequest req) {
        PrintUtil.appendInitialLine(buf, req);
        PrintUtil.appendHeaders(buf, req.headers());
        if (req instanceof FullHttpRequest) {
            PrintUtil.appendHeaders(buf, ((FullHttpRequest) req).trailingHeaders());
        }
        PrintUtil.removeLastNewLine(buf);
        return buf;
    }

    private static void appendInitialLine(StringBuilder buf, HttpRequest req) {
        buf.append((Object) req.method());
        buf.append(' ');
        buf.append(req.uri());
        buf.append(' ');
        buf.append((Object) req.protocolVersion());
        buf.append(StringUtil.NEWLINE);
    }

    private static void appendHeaders(StringBuilder buf, HttpHeaders headers) {
        for (Map.Entry e : headers) {
            buf.append((String) e.getKey());
            buf.append(": ");
            buf.append((String) e.getValue());
            buf.append(StringUtil.NEWLINE);
        }
    }

    private static void removeLastNewLine(StringBuilder buf) {
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
    }

    public static String fromStack(Throwable cause) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cause.getMessage());
        for (StackTraceElement el : cause.getStackTrace()) {
            stringBuilder.append('\n').append(el);
        }
        return stringBuilder.toString();
    }

    public static void main(String[] arg) {
        try {
            throw new Exception("Err");
        } catch (Exception e) {
            System.out.println(PrintUtil.fromStack(e));
            return;
        }
    }
}

