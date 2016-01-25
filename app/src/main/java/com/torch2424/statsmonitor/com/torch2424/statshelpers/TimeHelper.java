package com.torch2424.statsmonitor.com.torch2424.statshelpers;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.widget.RemoteViews;

import com.torch2424.statsmonitorwidget.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by torch2424 on 1/25/16.
 */
public class TimeHelper {

    //Set our view
    RemoteViews views;

    //Initialize some preference booleans
    boolean shortBool;
    boolean hourFormat;

    public TimeHelper(RemoteViews parentView, SharedPreferences prefs) {

        //Initialize our view
        views = parentView;

        //Grab some preferences
        shortBool = prefs.getBoolean("SHORTDAYS", false);
        hourFormat = prefs.getBoolean("24HOUR", false);
    }

    //function for time
    @SuppressLint("SimpleDateFormat")
    public void getTime()
    {
        //time, creating date and inserting it to simple date format
        Date date = new Date();
        SimpleDateFormat time= new SimpleDateFormat("hh:mm:ss a");
        SimpleDateFormat day = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
        if(shortBool == true)
        {
            day = new SimpleDateFormat("EEE, MMMM dd, yyyy");
        }
        else
        {
            day = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
        }
        //24 hour settings
        if(hourFormat == true)
        {
            time = new SimpleDateFormat("HH:mm:ss a");
        }
        else
        {
            time = new SimpleDateFormat("hh:mm:ss a");
        }
        //setting text to time
        views.setTextViewText(R.id.time, time.format(date));
        views.setTextViewText(R.id.date, day.format(date));
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
}
