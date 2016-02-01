package com.torch2424.statsmonitor.com.torch2424.statshelpers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by torch2424 on 1/21/16.
 */
public class ProviderHelper {

    //Our update interval
    int updateInterval = 1000;

    //Flag to stop sending the intent on stop alarm
    boolean pause;
    boolean quit;

    //Our pending intent, that we will update periodically
    PendingIntent updateIntent;

    //Our screen on/off receiver, gotten from: http://stackoverflow.com/questions/1588061/android-how-to-receive-broadcast-intents-action-screen-on-off
    private BroadcastReceiver mPowerKeyReceiver = null;


    //Add some functions that will handle on destroy and on create
    //calling of alarms in one place
    //Need to pass context to our alarm for the boradcast receiver
    public void callAlarm(final PendingIntent pending) {

        //creating Handler to update every second
        final Handler handler = new Handler();

        //Setting our pending intent
        updateIntent = pending;

        //Set our pause/quit to false
        pause = false;
        quit = false;

        handler.post( new Runnable() {
            public void run() {

                Log.d("Running!", "Run");

                //Send the broadcast to the pending intent
                try {
                    if(!pause)  updateIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    Log.d("DEBUG", "PENDING ERROR");
                }

                //Call this again
                if(quit) handler.removeCallbacks(this);
                //Use post at time to only update when the phone is not in deep sleep :)
                else handler.postAtTime(this, SystemClock.uptimeMillis() + updateInterval);


            }
        });
    }


    //Function to stop all callbacks
    public void stopAlarm() {
        //Stop Alarm here

        //simply set quit to true
        quit = true;
    }



    //Our broadcast receiver to receive events when the device is sleeping or locked
    private void registBroadcastReceiver() {
        final IntentFilter theFilter = new IntentFilter();
        /** System Defined Broadcast */
        theFilter.addAction(Intent.ACTION_SCREEN_ON);
        theFilter.addAction(Intent.ACTION_SCREEN_OFF);

        mPowerKeyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String strAction = intent.getAction();

                if (strAction.equals(Intent.ACTION_SCREEN_OFF) || strAction.equals(Intent.ACTION_SCREEN_ON)) {
                    // > Your playground~!
                }
            }
        };

        Context.getApplicationContext().registerReceiver(mPowerKeyReceiver, theFilter);
    }

    private void unregisterReceiver() {

        int apiLevel = Build.VERSION.SDK_INT;

        if (apiLevel >= 7) {
            try {
                getApplicationContext().unregisterReceiver(mPowerKeyReceiver);
            }
            catch (IllegalArgumentException e) {
                mPowerKeyReceiver = null;
            }
        }
        else {
            getApplicationContext().unregisterReceiver(mPowerKeyReceiver);
            mPowerKeyReceiver = null;
        }
    }

}
