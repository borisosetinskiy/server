package com.ob.server.security;

import com.ob.server.error.BadRequestException;
import com.ob.server.error.UnauthorizedException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;

public class JWTSecurityProcessor extends AbstractSecurityProcessor<HttpMessage> {
    private JwtParser jwtParser;
    public JWTSecurityProcessor(String signingKey, SecurityProcessor securityProcessor) {
        super(securityProcessor);
        jwtParser = Jwts.parser().setSigningKey(signingKey);
    }
    public JWTSecurityProcessor(String signingKey) {
        this(signingKey, null);
    }

    @Override
    public void process(ChannelHandlerContext channelHandlerContext, HttpMessage o) {
        HttpHeaders httpHeaders = o.headers();
        final String bearerToken = httpHeaders.get("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer")){
            throw new BadRequestException();
        }
        final String token = bearerToken.substring(7);
        if(!jwtParser.isSigned(token)){
            throw new UnauthorizedException("Token is not valid.");
        }
    }
}
