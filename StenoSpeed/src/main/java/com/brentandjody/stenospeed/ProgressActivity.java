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
        long day;
        int words, minutes, avg, max;
        double ratio;
        Database db = new Database(this);
        Cursor c = db.getAllData();
        long first_day = 0;
        int i=0;
        GraphViewData[] avgSpeedData = new GraphViewData[c.getCount()];
        GraphViewData[] maxSpeedData = new GraphViewData[c.getCount()];
        GraphViewData[] durationData = new GraphViewData[c.getCount()];
        GraphViewData[] ratioData = new GraphViewData[c.getCount()];
        while (c.moveToNext()) {
            day = c.getInt(0);
            minutes = c.getInt(1);
            words = c.getInt(2);
            max = c.getInt(3);
            ratio = c.getInt(4)/100;
            if (ratio==0) ratio=10;
            if (first_day==0) first_day=day;
            if (minutes==0)
                avg=0;
            else
                avg = Math.round(words/minutes);
            avgSpeedData[i] = new GraphViewData(day-first_day, avg);
            maxSpeedData[i] = new GraphViewData(day-first_day, max);
            durationData[i] = new GraphViewData(day-first_day, minutes);
            ratioData[i] = new GraphViewData(day-first_day, Math.round(100/ratio));
            i++;
        }
        db.close();
        GraphView graphView = new LineGraphView(this, "Progress over time");
        GraphViewSeries.GraphViewSeriesStyle avgStyle = new GraphViewSeries.GraphViewSeriesStyle(Color.parseColor("#33B5E5"), 2);
        GraphViewSeries.GraphViewSeriesStyle maxStyle = new GraphViewSeries.GraphViewSeriesStyle(Color.parseColor("#99cc00"), 2);
        //GraphViewSeries.GraphViewSeriesStyle durStyle = new GraphViewSeries.GraphViewSeriesStyle(Color.parseColor("#008800"), 4);
        GraphViewSeries.GraphViewSeriesStyle ratStyle = new GraphViewSeries.GraphViewSeriesStyle(Color.parseColor("#AA0000"), 2);
        graphView.addSeries(new GraphViewSeries("Average Speed", avgStyle ,avgSpeedData));
        graphView.addSeries(new GraphViewSeries("Top Speed", maxStyle , maxSpeedData));
        graphView.addSeries(new GraphViewSeries("Ratio", ratStyle , ratioData));
        graphView.setScalable(true);
        graphView.setShowLegend(true);
        graphView.setLegendAlign(GraphView.LegendAlign.BOTTOM);
        graphView.getGraphViewStyle().setLegendWidth(300);
        graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.argb(0,0,0,0));
        return graphView;
    }

}