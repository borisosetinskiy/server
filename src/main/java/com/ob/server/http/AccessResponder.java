package com.ob.server.http;

import com.ob.server.resolvers.ChannelRequest;
import com.ob.server.resolvers.Responder;
import com.ob.server.session.RequestSession;
import io.netty.handler.codec.http.HttpResponseStatus;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.springframework.util.Assert;

import java.util.Map;

import static com.ob.server.http.HttpUtil.sendErrorAndCloseChannel;


/**
 * Created by boris on 07.04.2016.
 */
public class AccessResponder implements Responder {
    private final Responder target;
    private final Map<String, String> credentials;
    private final String passwordName;
    private final String userName;
    public AccessResponder(Responder target, Map<String, String> credentials, String userName, String passwordName) {
        Assert.notEmpty(credentials);
        Assert.notNull(passwordName);
        Assert.notNull(userName);
        this.target = target;
        this.credentials = credentials;
        this.passwordName = passwordName;
        this.userName = userName;
    }

    private boolean match(String user, String password){
        if(password == null || user==null)return false;
        final String value = credentials.get(user);
        if(value == null || !value.equals(password)) return false;
        return true;
    }

    @Override
    public RequestSession respond(ChannelRequest channelRequest) throws Exception{
        final Object2ObjectArrayMap<String, String> context = channelRequest.getContext();
        if(context != null && match(context.get(userName), context.get(passwordName))){
            return target.respond(channelRequest);
        }else{
            sendErrorAndCloseChannel(channelRequest, HttpResponseStatus.FORBIDDEN);
            return null;
        }
    }


}
