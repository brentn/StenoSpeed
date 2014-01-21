package com.brentandjody.stenospeed;

import java.util.Date;

/**
 * Created by brentn on 20/01/14.
 */
public class HistoryItem {

    private int total_letters;
    private long timestamp;

    public HistoryItem(int total_letters) {
        this.total_letters = total_letters;
        timestamp = new Date().getTime();
    }

    public int getLetters() {
        return total_letters;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
