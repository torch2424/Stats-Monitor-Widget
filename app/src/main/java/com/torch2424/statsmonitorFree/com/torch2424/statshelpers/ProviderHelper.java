package com.torch2424.statsmonitorFree.com.torch2424.statshelpers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.torch2424.statsmonitorFree.SmAlarm;

/**
 * Created by torch2424 on 1/21/16.
 */
public class ProviderHelper {

    //Our update interval
    final int updateInterval = 1000;

    //Flag to stop sending the intent on stop alarm
    static boolean quit;

    //Our handler
    Handler handler;

    //Our runnable
    Runnable runUpdate;

    //Our screen on/off receiver, gotten from: http://stackoverflow.com/questions/1588061/android-how-to-receive-broadcast-intents-action-screen-on-off
    private BroadcastReceiver sleepReceiver;


    //Add some functions that will handle on destroy and on create
    //calling of alarms in one place
    //Need to pass context to our alarm for the boradcast receiver,

    public void callAlarm(final Context context) {

        //creating Handler to update every second
        handler = new Handler();

        //Start our sleep receiver
        registBroadcastReceiver(context);

        //Set our quit to false
        quit = false;

         runUpdate = new Runnable() {
            public void run() {

                //Send the broadcast to the pending intent
                try {
                    if(!ProviderHelper.isQuitting())
                    {
                        //Create an update intent
                        //Create our intents
                        Intent intent = new Intent(context, SmAlarm.class);

                        PendingIntent updateIntent = PendingIntent.getBroadcast(context.getApplicationContext() , 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        updateIntent.send();
                    }
                } catch (PendingIntent.CanceledException e) {
                    Log.d("DEBUG", "PENDING ERROR");
                }

                //Quit the handler
                if(ProviderHelper.isQuitting()) {

                    //Remove all pending callbacks, and then return
                    handler.removeCallbacks(this);
                    return;
                }
                //Use post at time to only update when the phone is not in deep sleep :)
                else handler.postAtTime(this, SystemClock.uptimeMillis() + updateInterval);


            }
        };

        handler.post(runUpdate);
    }


    //Function to stop all callbacks
    public void stopAlarm(Context context) {
        //Stop Alarm here
        //simply set quit to true
        quit = true;

        //Unregister our broadcast receiver
        unregisterReceiver(context);

    }

    //Need a static function to check our quitting status for the runnable
    public static boolean isQuitting() {
        return quit;
    }



    //Our broadcast receiver to receive events when the device is sleeping or locked
    private void registBroadcastReceiver(Context context) {
        final IntentFilter theFilter = new IntentFilter();
        /** System Defined Broadcast */
        theFilter.addAction(Intent.ACTION_SCREEN_ON);
        theFilter.addAction(Intent.ACTION_SCREEN_OFF);

        sleepReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String strAction = intent.getAction();

                if (strAction.equals(Intent.ACTION_SCREEN_OFF)) {

                    //Quit the handler
                    quit = true;
                }
                else if (strAction.equals(Intent.ACTION_SCREEN_ON)) {

                    //First make sure there aren't any other stray runnables out there
                    handler.removeCallbacks(runUpdate);
                    quit = true;

                    //Start updating again, after the above has been assured
                    Handler timeout = new Handler();
                    timeout.postDelayed(new Runnable() {

                        public void run() {
                            //Update once more
                            quit=false;
                            handler.post(runUpdate);
                        }
                    }, 250);
                }
            }
        };

        context.getApplicationContext().registerReceiver(sleepReceiver, theFilter);
    }

    private void unregisterReceiver(Context context) {

        int apiLevel = Build.VERSION.SDK_INT;

        if (apiLevel >= 7) {
            try {
                context.getApplicationContext().unregisterReceiver(sleepReceiver);
            }
            catch (IllegalArgumentException e) {
                sleepReceiver = null;
            }
        }
        else {
            context.getApplicationContext().unregisterReceiver(sleepReceiver);
            sleepReceiver = null;
        }
    }

}
