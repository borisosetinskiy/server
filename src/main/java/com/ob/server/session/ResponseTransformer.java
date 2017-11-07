package com.ob.server.session;


public interface ResponseTransformer<T1,T2> {
   T1 transform(T2 object);
}
