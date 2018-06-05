package com.ob.server.session;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.*;
import io.netty.channel.group.*;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SessionChannelGroup extends AbstractSet<Channel>  {
    private static final AtomicInteger nextId = new AtomicInteger();
    private final String name;
    private final EventExecutor executor;
    private final ConcurrentMap<ChannelId, Channel> channels;
    private final ChannelFutureListener remover;



    public SessionChannelGroup(EventExecutor executor) {
        this("group-0x" + Integer.toHexString(nextId.incrementAndGet()), executor);
    }

    public SessionChannelGroup(String name, EventExecutor executor) {
        channels = PlatformDependent.newConcurrentHashMap();

        this.remover = future -> remove(future.channel());
        if (name == null) {
            throw new NullPointerException("name");
        } else {
            this.name = name;
            this.executor = executor;
        }
    }

    public String name() {
        return this.name;
    }

    public Channel find(ChannelId id) {
        return  (Channel)this.channels.get(id);
    }

    public boolean isEmpty() {
        return channels.isEmpty();
    }

    public int size() {
        return 0;
    }

    public boolean contains(Object o) {
        if (o instanceof Channel) {
            Channel c = (Channel)o;
            return channels.containsValue(c) ;
        } else {
            return false;
        }
    }

    public boolean add(Channel channel) {
        boolean added = channels.putIfAbsent(channel.id(), channel) == null;
        if (added) {
            channel.closeFuture().addListener(this.remover);
        }
        return added;
    }

    public boolean remove(Object o) {
        Channel c = null;
        if (o instanceof ChannelId) {
            c = channels.remove(o);
        } else if (o instanceof Channel) {
            c = (Channel)o;
            c = (Channel)channels.remove(c.id());
        }
        if (c == null) {
            return false;
        } else {
            c.closeFuture().removeListener(this.remover);
            return true;
        }
    }

    public void clear() {
        this.channels.clear();
    }

    public Iterator<Channel> iterator() {
        return channels.values().iterator();
    }

    public Object[] toArray() {
        return channels.values().toArray();
    }

    public <T> T[] toArray(T[] a) {
           return channels.values().toArray(a);
    }



    private static Object safeDuplicate(Object message) {
        if (message instanceof ByteBuf) {
            return ((ByteBuf)message).retainedDuplicate();
        } else {
            return message instanceof ByteBufHolder ? ((ByteBufHolder)message).retainedDuplicate() : ReferenceCountUtil.retain(message);
        }
    }



    public SessionChannelGroup write(Object message) {
        try {
            if (message == null) {
                throw new NullPointerException("message");
            } else {
                final Iterator var5 = iterator();
                while (var5.hasNext()) {
                    Channel c = (Channel) var5.next();
                    try {
                        c.write(safeDuplicate(message), c.voidPromise());
                    } catch (Exception e) {
                    }
                }
            }
        }finally {
            ReferenceCountUtil.release(message);
        }
        return this;
    }

    public SessionChannelGroup flush() {
        Iterator var2 = iterator();

        while(var2.hasNext()) {
            Channel c = (Channel)var2.next();
            try{
                if(c.isWritable())
                    c.flush();
            }catch (Exception e){}
        }
        return this;
    }





    public int hashCode() {
        return System.identityHashCode(this);
    }

    public boolean equals(Object o) {
        return this == o;
    }

    public int compareTo(ChannelGroup o) {
        int v = this.name().compareTo(o.name());
        return v != 0 ? v : System.identityHashCode(this) - System.identityHashCode(o);
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + "(name: " + this.name() + ", size: " + this.size() + ')';
    }
}

