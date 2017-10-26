package com.ob.server.session;


public interface ResponseFormatter<T1,T2> {
   T1 format(T2 object);
}
