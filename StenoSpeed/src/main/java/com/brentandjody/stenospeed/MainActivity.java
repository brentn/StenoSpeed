package com.brentandjody.stenospeed;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    private static final int THRESHOLD = 8;
    private int total_letters=0;
    private LimitedLengthQueue history = new LimitedLengthQueue<HistoryItem>(100);
    double max_speed=0.0;
    TextView current_speed_view;
    TextView max_speed_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText main_window = (EditText) findViewById(R.id.main_window);
        current_speed_view = (TextView) findViewById(R.id.current_speed);
        max_speed_view = (TextView) findViewById(R.id.maximum_speed);
        main_window.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int new_letters = count-before;
                total_letters+=new_letters;
                history.add(new HistoryItem(total_letters));
                updateSpeed();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSpeed() {
        if (history.size()<THRESHOLD) return;
        HistoryItem first = (HistoryItem) history.getFirst();
        HistoryItem last = (HistoryItem) history.getLast();
        double words = (last.getLetters()-first.getLetters())/5.0;
        double minutes = (last.getTimestamp()-first.getTimestamp())/60000.0;
        double speed = Math.round(words/minutes);
        current_speed_view.setText(getResources().getString(R.string.cur_speed)+speed);
        if (speed>max_speed) {
            max_speed=speed;
            max_speed_view.setText(getResources().getString(R.string.max_speed)+max_speed);
        }
    }

}
