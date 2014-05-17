package com.brentandjody.stenospeed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Listen for stroke broadcasts from stenoIME
 */
public class StrokeReceiver extends BroadcastReceiver {
    private int strokes=0;

    @Override
    public void onReceive(Context context, Intent intent) {
        strokes++;
    }

    public int getStrokes() {
        return strokes;
    }
}
