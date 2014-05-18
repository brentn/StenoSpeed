package com.brentandjody.stenospeed;


import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by brentn on 20/01/14.
 * Implements a ringbuffer style queue, that only keeps
 * the most recent n items
 */
public class LimitedLengthQueue<Value> extends LinkedBlockingQueue<Value> {
    private int size_limit;
    private Value last_item;

    public LimitedLengthQueue(int limit) {
        size_limit=limit;
    }

    @Override
    public boolean add(Value value) {
        if (value==null) return false;
        last_item=value;
        super.add(value);
        while (size()>size_limit)
            remove();
        return true;
    }

    public Value getFirst() {
        return peek();
    }

    public Value getLast() {
        return last_item;
    }

}
