package com.brentandjody.stenospeed;

import android.app.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.LineGraphView;

public class ProgressActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        LinearLayout layout = (LinearLayout) findViewById(R.id.graph_area);
        GraphView graphView = getData();
        layout.addView(graphView);
    }

    private GraphView getData() {
        int day, words, minutes, avg, max;
        Database db = new Database(this);
        Cursor c = db.getAllData();
        int first_day = 0;
        int i=0;
        GraphViewData[] avgSpeedData = new GraphViewData[c.getCount()];
        GraphViewData[] maxSpeedData = new GraphViewData[c.getCount()];
        while (c.moveToNext()) {
            day = c.getInt(0);
            minutes = c.getInt(1);
            words = c.getInt(2);
            max = c.getInt(3);
            if (first_day==0) first_day=day;
            avg = Math.round(words/minutes);
            avgSpeedData[i] = new GraphViewData(day-first_day, avg);
            maxSpeedData[i] = new GraphViewData(day-first_day, max);
            i++;
        }
        db.close();
        GraphView graphView = new LineGraphView(this, "Progress over time");
        GraphViewSeries.GraphViewSeriesStyle avgStyle = new GraphViewSeries.GraphViewSeriesStyle(Color.parseColor("#33B5E5"), 2);
        GraphViewSeries.GraphViewSeriesStyle maxStyle = new GraphViewSeries.GraphViewSeriesStyle(Color.parseColor("#99cc00"), 2);
        graphView.addSeries(new GraphViewSeries("Average Speed", avgStyle ,avgSpeedData));
        graphView.addSeries(new GraphViewSeries("Top Speed", maxStyle , maxSpeedData));
        graphView.setScalable(true);
        graphView.setShowLegend(true);
        graphView.setLegendAlign(GraphView.LegendAlign.BOTTOM);
        graphView.getGraphViewStyle().setLegendWidth(300);
        graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.argb(0,0,0,0));
        return graphView;
    }

}