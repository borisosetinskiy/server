package com.ob.server.security;

import com.ob.server.error.BadRequestException;
import com.ob.server.error.UnauthorizedException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;

public class JWTSecurityProcessor extends AbstractSecurityProcessor {
    private final JwtParser jwtParser;

    public JWTSecurityProcessor(String signingKey, SecurityProcessor securityProcessor) {
        super(securityProcessor);
        jwtParser = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build();
    }

    public JWTSecurityProcessor(String signingKey) {
        this(signingKey, null);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, DefaultHttpRequest o) {
        final String token = validateAndJwtToken(o.headers());
        if (!jwtParser.isSigned(token)) {
            throw new UnauthorizedException("Token is not valid.");
        }
    }

    protected String validateAuthorizationHeader(HttpHeaders httpHeaders) {
        final String bearerToken = httpHeaders.get("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer")) {
            throw new BadRequestException();
        }
        return bearerToken;
    }

    protected String jwtToken(String bearerToken) {
        return bearerToken.substring(7);
    }

    protected String validateAndJwtToken(HttpHeaders httpHeaders) {
        return jwtToken(validateAuthorizationHeader(httpHeaders));
    }

    protected JwtParser getJwtParser() {
        return jwtParser;
    }
}
