package com.ob.common.collection;

import com.ob.common.util.UnsafeHolder;
import sun.misc.Unsafe;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ob.common.collection.QueueUtil.*;

public class UnsafeNonBlockingBoundedQueue<T> implements Queue<T> {

    private final Unsafe UNSAFE = UnsafeHolder.UNSAFE;
    private int arrayBase = UNSAFE.arrayBaseOffset(Object[].class);
    private int arrayScale = calculateShiftForScale(UNSAFE.arrayIndexScale(Object[].class));

    //Make sure the capacity is some value that is power of 2. This important for the efficient bitwise module
    //calculation.
    private final int actualCapacity ;
    private final int ringSize;
    private Object [] buffer ;

    private final AtomicInteger head = new AtomicInteger(0);
    private final AtomicInteger tail = new AtomicInteger(0);

    public UnsafeNonBlockingBoundedQueue(int capacity) {
        actualCapacity = findNextPositivePowerOfTwo(capacity);
        ringSize = actualCapacity - 1;
        buffer = newArray(Object[].class,  actualCapacity);
    }
    public static <T> T[] newArray(Class<T[]> type, int size) {
        return type.cast(Array.newInstance(type.getComponentType(), size));
    }

    private T getElementVolatile(int index){
        return (T)UNSAFE.getObjectVolatile(buffer, calculateOffset(index, arrayBase, arrayScale));
    }




    @Override
    public boolean offer(T t) {
        int currentTail;
        do {
            currentTail = tail.get();
        } while (!tail.compareAndSet(currentTail, currentTail + 1));

        //checking whether we need to override oldest queue element
        int currentHead = head.get();
        if (currentTail >= (currentHead + actualCapacity)) {
            //move the head to the next oldest message in the queue
            int newHead = currentTail - ringSize;
            //avoiding possible race here to make sure that we don't move head backward
            while(newHead > currentHead && !head.compareAndSet(currentHead, newHead)) {
                currentHead = head.get();
            }
        }
        int index  = currentTail & ringSize;
        UNSAFE.putOrderedObject(buffer, calculateOffset(index, arrayBase, arrayScale), t);
        return true;
    }

    @Override
    public T poll() {
        int currentHead;
        int currentTail = tail.get();
        do {
            currentHead = head.get();
            if (currentHead >= currentTail) {
                return null;
            }
        } while (!head.compareAndSet(currentHead, currentHead + 1));

        int index  = currentHead & ringSize;
        return getElementVolatile(index);
    }

    @Override
    public int size() {
        return tail.get() - head.get();
    }

    @Override
    public boolean isEmpty() {
        return tail.get() == head.get();
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return null;
    }

    @Override
    public boolean add(T t) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public T remove() {
        return null;
    }

    @Override
    public T element() {
        return null;
    }

    @Override
    public T peek() {
        return null;
    }
}
