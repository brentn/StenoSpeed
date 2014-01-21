package com.brentandjody.stenospeed;


import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by brentn on 20/01/14.
 */
public class LimitedLengthQueue<Value> extends LinkedBlockingQueue<Value> {
    private int size_limit;
    private Value last_item;

    public LimitedLengthQueue(int limit) {
        size_limit=limit;
    }

    @Override
    public boolean add(Value value) {
        last_item=value;
        super.add(value);
        if (size()>size_limit)
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
