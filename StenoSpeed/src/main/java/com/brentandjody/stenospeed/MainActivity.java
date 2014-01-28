package com.brentandjody.stenospeed;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends ActionBarActivity {

    private static final int THRESHOLD = 8; // require how many presses before we start calculating speed?
    private static final int THRESHOLD_FOR_MAX = 100; // require how many presses before we calculate max speed?
    private static final int BUFFER_SIZE = 300; // size of the limitedLengthQueue;
    //assert(BUFFER_SIZE > THRESHOLD_FOR_MAX > THRESHOLD);

    private int total_letters=0;
    private LimitedLengthQueue history = new LimitedLengthQueue<HistoryItem>(BUFFER_SIZE);
    private double max_speed=0.0;
    private TextView current_speed_view;
    private TextView max_speed_view;
    private boolean initialized =false;
    private long begin_timestamp;

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
        int id = item.getItemId();
        if (history.isEmpty()) {
            showProgressGraph();
        } else {
            new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Save?")
                .setMessage("Save session stats?")
                .setPositiveButton("Save & Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recordStats();
                        showProgressGraph();
                    }
                })
                .setNegativeButton("Don't Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgressGraph();
                    }
                })
                .show();
        }
        return id == R.id.action_progress || super.onOptionsItemSelected(item);
    }

    private void updateSpeed() {
        if (history.size()<THRESHOLD)  {
            current_speed_view.setText("");
            max_speed_view.setText("");
            return;
        }
        HistoryItem first = (HistoryItem) history.getFirst();
        HistoryItem last = (HistoryItem) history.getLast();
        double words = (last.getLetters()-first.getLetters())/5.0;
        double minutes = (last.getTimestamp()-first.getTimestamp())/60000.0;
        double speed = Math.round(words/minutes);
        if (speed<0) speed=0;
        current_speed_view.setText(getResources().getString(R.string.cur_speed)+speed);
        if (!initialized) {
            if (history.size() >= THRESHOLD_FOR_MAX) {
                begin_timestamp = first.getTimestamp();
                initialized =true;
            }
        }
        if (initialized && speed>max_speed) {
            max_speed=speed;
            max_speed_view.setText(getResources().getString(R.string.max_speed)+max_speed);
        }
    }

    @Override
    public void onBackPressed() {
        if (history.isEmpty())
            finish();
        else {
            new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm Exit")
                .setMessage("Save session stats?")
                .setPositiveButton("Save & Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recordStats();
                        finish();
                    }
                })
                .setNeutralButton("Exit", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Don't Exit", null)
                .show();
        }
    }

    private void showProgressGraph() {
        history = new LimitedLengthQueue<HistoryItem>(BUFFER_SIZE); //erase history
        Intent intent = new Intent(this, ProgressActivity.class);
        startActivity(intent);
    }

    private void recordStats() {
        Database db = new Database(this);
        if (history.isEmpty()) return;
        HistoryItem last = (HistoryItem) history.getLast();
        double words = last.getLetters() / 5;
        double minutes = (last.getTimestamp()-begin_timestamp)/60000.0;
        Date start_time = new Date();
        start_time.setTime(begin_timestamp);  //record the time we began
        ContentValues cv = new ContentValues();
        cv.put(Database.COL_DATE, start_time.getTime());
        cv.put(Database.COL_DUR, minutes);
        cv.put(Database.COL_WORDS, words);
        cv.put(Database.COL_SPEED, max_speed);
        SQLiteDatabase sdb = db.getWritableDatabase();
        sdb.insert(Database.TABLE_RECORDS, null, cv);
        sdb.close();
        db.close();
    }

}
