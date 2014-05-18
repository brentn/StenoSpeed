package com.brentandjody.stenospeed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Listen for stroke broadcasts from stenoIME
 */
public class StrokeReceiver extends BroadcastReceiver {
    private int strokes=0;

    public StrokeReceiver(int initial_strokes) {
        super();
        strokes=initial_strokes;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        strokes++;
    }

    public int getStrokes() {
        return strokes;
    }
}
