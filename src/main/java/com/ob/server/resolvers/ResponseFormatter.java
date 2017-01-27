package com.ob.server.resolvers;


public interface ResponseFormatter<T1,T2> {
   T1 format(T2 object);
}
