package com.torch2424.statsmonitor.com.torch2424.statsproviders;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.torch2424.statsmonitor.SmAlarm;
import com.torch2424.statsmonitor.com.torch2424.statshelpers.ProviderHelper;


public class ProviderBigger extends AppWidgetProvider
{
    //2X4

    //declaring here to access in on disabled
    PendingIntent pending;
    Intent intent;
    ProviderHelper helper = new ProviderHelper();

    //need peniding intent flags to properly create and destroy alarm
    public void onEnabled(Context context)
    {

        //Create our intents
        intent = new Intent(context, SmAlarm.class);

        pending = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //Use the helper to call the alarm through the runnable
        helper.callAlarm(pending, context);
    }

    public void onDisabled(Context context)
    {
        //Stop the Handler
        helper.stopAlarm(context);
    }
}
