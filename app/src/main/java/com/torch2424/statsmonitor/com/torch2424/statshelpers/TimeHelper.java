package com.torch2424.statsmonitor.com.torch2424.statshelpers;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.view.View;
import android.widget.RemoteViews;

import com.torch2424.statsmonitorwidget.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by torch2424 on 1/25/16.
 */
public class TimeHelper extends UpdateHelper{

    //Initialize some preference booleans
    boolean shortBool;
    boolean hourFormat;

    //View booleans
    boolean timeView;
    boolean dateView;
    boolean upTimeView;

    public TimeHelper(RemoteViews parentView, SharedPreferences prefs) {

        //Call the parent constructor
        super(parentView);

        //Initialize our view
        views = parentView;

        //Grab some preferences
        shortBool = prefs.getBoolean("SHORTDAYS", false);
        hourFormat = prefs.getBoolean("24HOUR", false);

        //Set our views
        timeView = prefs.getBoolean("TIME", true);
        dateView = prefs.getBoolean("DATE", true);
        upTimeView = prefs.getBoolean("UPTIME", true);

        if (timeView) views.setViewVisibility(R.id.time, View.VISIBLE);
        else views.setViewVisibility(R.id.time, View.GONE);

        if (dateView) views.setViewVisibility(R.id.date, View.VISIBLE);
        else views.setViewVisibility(R.id.date, View.GONE);

        if (upTimeView) views.setViewVisibility(R.id.uptime, View.VISIBLE);
        else views.setViewVisibility(R.id.uptime, View.GONE);
    }

    //function for time
    @SuppressLint("SimpleDateFormat")
    public void getTime()
    {
        //time, creating date and inserting it to simple date format
        Date date = new Date();
        SimpleDateFormat time= new SimpleDateFormat("hh:mm:ss a");
        SimpleDateFormat day = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
        if(shortBool) day = new SimpleDateFormat("EEE, MMM dd, yyyy");
        else day = new SimpleDateFormat("EEEE, MMMM dd, yyyy");

        //24 hour settings
        if(hourFormat == true) time = new SimpleDateFormat("HH:mm:ss a");
        else time = new SimpleDateFormat("hh:mm:ss a");

        //setting text to time
        views.setTextViewText(R.id.time, "Time: " + time.format(date));
        views.setTextViewText(R.id.date, "Date: " + day.format(date));
    }

    public void getUptime()
    {
        //getting uptime
        long uptime = SystemClock.elapsedRealtime();

        //Find the amount of days
        int days = (int) (uptime / 86400000) ;
        uptime = uptime - (86400000 * days);

        //Format our string
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH ':' mm ':' ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dayString = Integer.toString(days);
        String timeString = "Uptime: " + dayString + "d " +  timeFormat.format(uptime);


        //setting text to time
        views.setTextViewText(R.id.uptime, timeString);
    }

    //function to return if we are displaying time
    public boolean timeStatus() {
        if(timeView || dateView) return true;
        else return false;
    }

    //function to return if we are displaying uptime
    public boolean upTimeStatus() {
        if(upTimeView) return true;
        else return false;
    }
}
