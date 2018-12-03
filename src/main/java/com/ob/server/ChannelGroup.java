/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufHolder
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelId
 *  io.netty.channel.ChannelPromise
 *  io.netty.channel.group.ChannelGroup
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.concurrent.EventExecutor
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  io.netty.util.internal.PlatformDependent
 *  io.netty.util.internal.StringUtil
 */
package com.ob.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelId;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelGroup
extends AbstractSet<Channel> {
    private static final AtomicInteger nextId = new AtomicInteger();
    private final String name;
    private final EventExecutor executor;
    private final ConcurrentMap<ChannelId, Channel> channels = PlatformDependent.newConcurrentHashMap();
    private final ChannelFutureListener remover = future -> this.remove(future.channel());

    public ChannelGroup(EventExecutor executor) {
        this("group-0x" + Integer.toHexString(nextId.incrementAndGet()), executor);
    }

    public ChannelGroup(String name, EventExecutor executor) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
        this.executor = executor;
    }

    public String name() {
        return this.name;
    }

    public Channel find(ChannelId id) {
        return this.channels.get(id);
    }

    @Override
    public boolean isEmpty() {
        return this.channels.isEmpty();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Channel) {
            Channel c = (Channel)o;
            return this.channels.containsValue(c);
        }
        return false;
    }

    @Override
    public boolean add(Channel channel) {
        boolean added;
        boolean bl = added = this.channels.putIfAbsent(channel.id(), channel) == null;
        if (added) {
            channel.closeFuture().addListener(this.remover);
        }
        return added;
    }

    @Override
    public boolean remove(Object o) {
        Channel c = null;
        if (o instanceof ChannelId) {
            c = this.channels.remove(o);
        } else if (o instanceof Channel) {
            c = (Channel)o;
            c = this.channels.remove(c.id());
        }
        if (c == null) {
            return false;
        }
        c.closeFuture().removeListener(this.remover);
        return true;
    }

    @Override
    public void clear() {
        this.channels.clear();
    }

    @Override
    public Iterator<Channel> iterator() {
        return this.channels.values().iterator();
    }

    @Override
    public Object[] toArray() {
        return this.channels.values().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.channels.values().toArray(a);
    }

    private static Object safeDuplicate(Object message) {
        if (message instanceof ByteBuf) {
            return ((ByteBuf)message).retainedDuplicate();
        }
        return message instanceof ByteBufHolder ? ((ByteBufHolder)message).retainedDuplicate() : ReferenceCountUtil.retain((Object)message);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ChannelGroup write(Object message) {
        try {
            if (message == null) {
                throw new NullPointerException("message");
            }
            for (Channel c : this) {
                try {
                    c.write(ChannelGroup.safeDuplicate(message), c.voidPromise());
                }
                catch (Exception exception) {}
            }
        }
        finally {
            ReferenceCountUtil.release(message);
        }
        return this;
    }

    public ChannelGroup flush() {
        for (Channel c : this) {
            try {
                if (!c.isWritable()) continue;
                c.flush();
            }
            catch (Exception exception) {}
        }
        return this;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    public int compareTo(io.netty.channel.group.ChannelGroup o) {
        int v = this.name().compareTo(o.name());
        return v != 0 ? v : System.identityHashCode(this) - System.identityHashCode((Object)o);
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(name: " + this.name() + ", size: " + this.size() + ')';
    }
}

