package com.ob.server.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.internal.StringUtil;

import java.util.Map;

/**
 * Created by boris on 3/28/2017.
 */
public class PrintUtil {
    static StringBuilder appendRequest(StringBuilder buf, HttpRequest req) {
        appendInitialLine(buf, req);
        appendHeaders(buf, req.headers());
        if(req instanceof FullHttpRequest)
            appendHeaders(buf, ((FullHttpRequest)req).trailingHeaders());
        removeLastNewLine(buf);
        return buf;
    }
    private static void appendInitialLine(StringBuilder buf, HttpRequest req) {
        buf.append(req.method());
        buf.append(' ');
        buf.append(req.uri());
        buf.append(' ');
        buf.append(req.protocolVersion());
        buf.append(StringUtil.NEWLINE);
    }

    private static void appendHeaders(StringBuilder buf, HttpHeaders headers) {
        for (Map.Entry<String, String> e: headers) {
            buf.append(e.getKey());
            buf.append(": ");
            buf.append(e.getValue());
            buf.append(StringUtil.NEWLINE);
        }
    }

    private static void removeLastNewLine(StringBuilder buf) {
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
    }

    public static String fromStack(Throwable cause){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cause.getMessage());
        for(StackTraceElement el : cause.getStackTrace()){
            stringBuilder.append('\n').append(el);
        }
        return stringBuilder.toString();
    }
}
