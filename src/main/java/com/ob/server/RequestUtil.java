/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.http.HttpConstants
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 */
package com.ob.server;

import io.netty.handler.codec.http.HttpConstants;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.nio.charset.Charset;

public class RequestUtil {
    public static String decodeComponent(String s) {
        return RequestUtil.decodeComponent(s, HttpConstants.DEFAULT_CHARSET);
    }

    public static void decodeParams(String s, Object2ObjectArrayMap params) {
        RequestUtil.decodeParams(s, params, HttpConstants.DEFAULT_CHARSET);
    }

    public static void decodeParams(String s, Object2ObjectArrayMap params, Charset charset) {
        int i;
        String name = null;
        int pos = 0;
        for (i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '=' && name == null) {
                if (pos != i) {
                    name = RequestUtil.decodeComponent(s.substring(pos, i), charset);
                }
                pos = i + 1;
                continue;
            }
            if (c != '&' && c != ';') continue;
            if (name == null && pos != i) {
                if (!RequestUtil.addParam(params, RequestUtil.decodeComponent(s.substring(pos, i), charset), "")) {
                    return;
                }
            } else if (name != null) {
                if (!RequestUtil.addParam(params, name, RequestUtil.decodeComponent(s.substring(pos, i), charset))) {
                    return;
                }
                name = null;
            }
            pos = i + 1;
        }
        if (pos != i) {
            if (name == null) {
                RequestUtil.addParam(params, RequestUtil.decodeComponent(s.substring(pos, i), charset), "");
            } else {
                RequestUtil.addParam(params, name, RequestUtil.decodeComponent(s.substring(pos, i), charset));
            }
        } else if (name != null) {
            RequestUtil.addParam(params, name, "");
        }
    }

    public static String decodeComponent(String s, Charset charset) {
        if (s == null) {
            return "";
        }
        int size = s.length();
        boolean modified = false;
        for (int i = 0; i < size; ++i) {
            char c = s.charAt(i);
            if (c != '%' && c != '+') continue;
            modified = true;
            break;
        }
        if (!modified) {
            return s;
        }
        byte[] buf = new byte[size];
        int pos = 0;
        block5 : for (int i = 0; i < size; ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '%': {
                    if (i == size - 1) {
                        throw new IllegalArgumentException("unterminated escape sequence at end of string: " + s);
                    }
                    if ((c = s.charAt(++i)) == '%') {
                        buf[pos++] = 37;
                        continue block5;
                    }
                    if (i == size - 1) {
                        throw new IllegalArgumentException("partial escape sequence at end of string: " + s);
                    }
                    c = RequestUtil.decodeHexNibble(c);
                    char c2 = RequestUtil.decodeHexNibble(s.charAt(++i));
                    if (c == '\uffff' || c2 == '\uffff') {
                        throw new IllegalArgumentException("invalid escape sequence `%" + s.charAt(i - 1) + s.charAt(i) + "' at index " + (i - 2) + " of: " + s);
                    }
                    c = (char)(c * 16 + c2);
                }
                default: {
                    buf[pos++] = (byte)c;
                    continue block5;
                }
                case '+': {
                    buf[pos++] = 32;
                }
            }
        }
        return new String(buf, 0, pos, charset);
    }

    public static char decodeHexNibble(char c) {
        if ('0' <= c && c <= '9') {
            return (char)(c - 48);
        }
        if ('a' <= c && c <= 'f') {
            return (char)(c - 97 + 10);
        }
        return 'A' <= c && c <= 'F' ? (char)(c - 65 + 10) : (char)'\uffff';
    }

    public static boolean addParam(Object2ObjectArrayMap params, String name, String value) {
        params.put((Object)name, (Object)value);
        return true;
    }
}

