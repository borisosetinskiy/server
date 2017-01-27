package com.ob.server.http;

import com.google.common.collect.ImmutableMap;
import com.ob.server.resolvers.Responder;
import com.ob.server.resolvers.ResponderResolver;

import java.util.Map;

/**
 * Created by boris on 12/1/2016.
 */
public class DefaultResponderResolver implements ResponderResolver {
    private final Map<String, Responder> responders;
    private final Responder notFoundResponder = new NotFoundResponder();

    public DefaultResponderResolver(Map<String, Responder> responders) {
        this.responders = ImmutableMap.copyOf(responders);
    }

    @Override
    public Responder resolve(String path) {
        final Responder pathResponder = responders.get(path);
        if (pathResponder == null) {
            return notFoundResponder;
        }
        return pathResponder;
    }


    @Override
    public String toString() {
        return "DefaultResponderResolver{" +
                "responders=" + responders +
                ", notFoundResponder=" + notFoundResponder +
                '}';
    }
}
