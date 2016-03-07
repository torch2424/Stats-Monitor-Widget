package com.torch2424.statsmonitorFree.com.torch2424.statshelpers;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.view.View;
import android.widget.RemoteViews;

import com.torch2424.statsmonitorwidgetFree.R;

/**
 * Created by torch2424 on 1/25/16.
 */
public class BatteryHelper extends UpdateHelper{

    //Boolean for temperature
    boolean farenBool;

    //Ifilter for Battery
    //get an intent filter to get battery status, which returns a function call
    //we just call this intent to get whatever info we want
    IntentFilter ifilter;

    //View Boolean
    boolean percentView;
    boolean tempView;

    public BatteryHelper(RemoteViews parentView, SharedPreferences prefs) {

        //Call the parent constructor
        super(parentView);

        //Get our preferences
        farenBool = prefs.getBoolean("DEGREESF", false);

        //Set up our I filter for grabbing our batteyr stats
        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        //Setting our views
        percentView = prefs.getBoolean("BATTERY", true);
        tempView = prefs.getBoolean("BATTERYTEMP", true);

        if(percentView)  views.setViewVisibility(R.id.battery, View.VISIBLE);
        else views.setViewVisibility(R.id.battery, View.GONE);

        if(tempView) views.setViewVisibility(R.id.batteryTemp, View.VISIBLE);
        else views.setViewVisibility(R.id.batteryTemp, View.GONE);
    }


    public void getBatteryPercent(Context context) {

        //taken from android devs
        //android bug where if we use context.register reciver is excepted when it shouldnt be
        //we need get application context to work since it calls correctly and has greater lifetime
        Intent battery = context.getApplicationContext().registerReceiver(null, ifilter);
        int level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        //putting it into a percent float
        float percent = (level / (float)scale) * 100;

        String batteryString = "Battery Left: " + (int)(percent) + "%";

        //set into text view
        views.setTextViewText(R.id.battery, batteryString);
    }

    public void getBatteryTemp(Context context)
    {

        //Get our battery receiver
        Intent battery = context.getApplicationContext().registerReceiver(null, ifilter);

        //Get the temperature
        int temperature = battery.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
        String temp = "";
        String temperatureString = "";

        //CHeck for farenheight and celcius
        if(farenBool)
        {
            temp = Integer.toString(((temperature/10) * 9 / 5) + 32);
            temperatureString = "Battery Temp: " + temp + " °F";
        }
        else
        {
            temp = Integer.toString(temperature/10);
            temperatureString = "Battery Temp: " + temp + " °C";
        }
        //set into text view
        views.setTextViewText(R.id.batteryTemp, temperatureString);
    }

    //Return our view status
    public boolean percentStatus() {
        if(percentView) return true;
        else return false;
    }

    //Return our view status
    public boolean tempStatus() {
        if(tempView) return true;
        else return false;
    }
}
