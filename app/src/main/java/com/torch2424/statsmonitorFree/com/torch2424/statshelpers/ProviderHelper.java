package com.torch2424.statsmonitorFree.com.torch2424.statshelpers;

import android.os.Handler;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.torch2424.statsmonitorFree.WidgetUpdater;


/**
 * Created by torch2424 on 1/21/16.
 */
public class ProviderHelper extends Service {

    //Our update interval
    final int updateInterval = 1000;

    //Flag to stop sending the intent on stop alarm
    static boolean quit;

    //Count our seconds here, since the services stays alive
    // (For settings in the updater)
    static int seconds;

    //Flag to completely start/kill the widget, including our broadcast receiver
    static boolean startReceiver;
    static boolean finishReceiver;

    //Our handler
    Handler handler;

    //Our runnable
    Runnable runUpdate;

    //Our screen on/off receiver, gotten from: http://stackoverflow.com/questions/1588061/android-how-to-receive-broadcast-intents-action-screen-on-off
    private BroadcastReceiver sleepReceiver;

    //Our (Unused) binder
    IBinder providerBind = new ProviderBinder();


    @Override
    public void onCreate() {

        //Call the parent on create
        super.onCreate();

        //Start updating
        callAlarm();
    }


    //Add some functions that will handle on destroy and on create
    //calling of alarms in one place
    //Need to pass context to our alarm for the boradcast receiver,

    public void callAlarm() {

        //creating Handler to update every second
        handler = new Handler();

        //Set our quit to false
        quit = false;
        finishReceiver = false;

        //Reset the updater seconds
        seconds = 0;

        //Check if we are just starting
        if(startReceiver) {
            //Register our receiver, and stop is from registering again
            registerBroadcastReceiver(this);
            startReceiver = false;
        }

         runUpdate = new Runnable() {
            public void run() {

                //First, Check if we are quitting the handler
                if(ProviderHelper.isQuitting()) {

                    //Check if we are completely killing the widget, if we aren't do nothing
                    if (ProviderHelper.isFInishing()) {
                        //Remove all pending callbacks, and then return
                        handler.removeCallbacks(this);

                        //Unregister our receiver since we are completely quitting
                        unregisterReceiver(ProviderHelper.this);

                        // to make sure it isnt recreated
                        stopSelf();
                        return;
                    }
                }
                //If we are not quitting, update!
                else {

                    //Run updates
                    Intent intent = new Intent(ProviderHelper.this, WidgetUpdater.class);
                    sendBroadcast(intent);
                }


                //Use post at time to only update when the phone is not in deep sleep :)
                handler.postAtTime(this, SystemClock.uptimeMillis() + updateInterval);

            }
        };

        handler.post(runUpdate);
    }


    //Start the widget here
    public static void startWidget() {

        startReceiver = true;
    }

    //Control our finish variable here
    public static void destroyWidget() {

        //Simply set quit, and stop our broad cast receiver
        finishReceiver = true;
        quit = true;
    }

    //Return if we are finishing
    public static boolean isFInishing() {
        return finishReceiver;
    }

    //Need a static function to check our quitting status for the runnable
    public static boolean isQuitting() {

        return quit;
    }

    //Getter and setter for seconds
    public static int getSeconds() {
        return seconds;
    }

    public static void setSeconds(int input) {
        seconds = input;
    }


    //Our broadcast receiver to receive events when the device is sleeping or locked
    private void registerBroadcastReceiver(Context context) {

        final IntentFilter theFilter = new IntentFilter();

        /** System Defined Broadcast */
        theFilter.addAction(Intent.ACTION_SCREEN_ON);
        theFilter.addAction(Intent.ACTION_SCREEN_OFF);

        sleepReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {

                String strAction = intent.getAction();

                if (strAction.equals(Intent.ACTION_SCREEN_OFF)) {

                    //Simply set quit to true for the service to handle
                    quit = true;
                }
                else if (strAction.equals(Intent.ACTION_SCREEN_ON)) {

                    //First make sure there aren't any other stray runnables out there
                    handler.removeCallbacks(runUpdate);

                    //Start updating again, after the above has been assured
                    Handler timeout = new Handler();
                    timeout.postDelayed(new Runnable() {

                        public void run() {

                            //Update once more
                            quit=false;
                            handler.post(runUpdate);

                            //Start our service
                            Intent providerIntent = new Intent(context, ProviderHelper.class);
                            context.startService(providerIntent);
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


    // binder for the provider
    public class ProviderBinder extends Binder
    {
        public ProviderHelper getService()
        {
            return ProviderHelper.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        //Return our binder
        return providerBind;
    }
}
