package com.brentandjody.stenospeed;

import android.app.Activity;

import android.database.Cursor;
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
        double day, offset;
        int  words, minutes, avg, max;
        double ratio;
        Database db = new Database(this);
        Cursor c = db.getAllData();
        double first_day = 0;
        int i=0;
        GraphViewData[] avgSpeedData = new GraphViewData[c.getCount()];
        GraphViewData[] maxSpeedData = new GraphViewData[c.getCount()];
        GraphViewData[] durationData = new GraphViewData[c.getCount()];
        GraphViewData[] ratioData = new GraphViewData[c.getCount()];
        while (c.moveToNext()) {
            day = (c.getDouble(0)/(1000L*25*60*60)); //convert from milliseconds to days
            minutes = c.getInt(1);
            words = c.getInt(2);
            max = c.getInt(3);
            ratio = c.getInt(4)/100;
            if (ratio==0) ratio=5.5; // (this should make the line appear at the bottom of the graph)
            if (first_day==0) first_day=day;
            if (minutes==0)
                avg=0;
            else
                avg = Math.round(words/minutes);
            offset = day-first_day;
            avgSpeedData[i] = new GraphViewData(offset, avg);
            maxSpeedData[i] = new GraphViewData(offset, max);
            durationData[i] = new GraphViewData(offset, minutes);
            ratioData[i] = new GraphViewData(offset, Math.round(110/ratio) - 20); // ((110/x)-20) maps a range of 5-.5 strokes/word to 0-200 on the graph
            i++;
        }
        db.close();
        GraphView graphView = new LineGraphView(this, "Progress over time");
        graphView.getGraphViewStyle().setNumVerticalLabels(4);
        graphView.getGraphViewStyle().setVerticalLabelsWidth(80);
        graphView.getGraphViewStyle().setNumHorizontalLabels(6);
        GraphViewSeries.GraphViewSeriesStyle avgStyle = new GraphViewSeries.GraphViewSeriesStyle(Color.parseColor("#33B5E5"), 2);
        GraphViewSeries.GraphViewSeriesStyle maxStyle = new GraphViewSeries.GraphViewSeriesStyle(Color.parseColor("#99cc00"), 2);
        GraphViewSeries.GraphViewSeriesStyle durStyle = new GraphViewSeries.GraphViewSeriesStyle(Color.parseColor("#888888"), 2);
        GraphViewSeries.GraphViewSeriesStyle ratStyle = new GraphViewSeries.GraphViewSeriesStyle(Color.parseColor("#AA0000"), 2);
        graphView.addSeries(new GraphViewSeries("Average Speed", avgStyle ,avgSpeedData));
        graphView.addSeries(new GraphViewSeries("Top Speed", maxStyle , maxSpeedData));
        graphView.addSeries(new GraphViewSeries("Ratio", ratStyle , ratioData));
        //graphView.addSeries(new GraphViewSeries("Minutes", durStyle, durationData));
        graphView.setScalable(true);
        graphView.setShowLegend(true);
        graphView.setLegendAlign(GraphView.LegendAlign.BOTTOM);
        graphView.getGraphViewStyle().setLegendWidth(300);
        return graphView;
    }

}