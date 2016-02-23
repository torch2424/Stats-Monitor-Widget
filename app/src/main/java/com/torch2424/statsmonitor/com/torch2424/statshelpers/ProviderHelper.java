package com.torch2424.statsmonitor.com.torch2424.statshelpers;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.torch2424.statsmonitor.WidgetUpdater;

/**
 * Created by torch2424 on 1/21/16.
 */
public class ProviderHelper extends Service {

    //Our update interval
    final int updateInterval = 1000;

    //Our Widget Updater
    WidgetUpdater updater;

    //Flag to stop sending the intent on stop alarm
    static boolean quit;

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

        //Create our updater
        //updater = new WidgetUpdater();

        //Start our sleep receiver
        registBroadcastReceiver(this);

        //Set our quit to false
        quit = false;

         runUpdate = new Runnable() {
            public void run() {

                //Run our updates in the service
                if(!ProviderHelper.isQuitting())
                {

                    //updater.runUpdate(ProviderHelper.this, manager);

                    Intent intent = new Intent(ProviderHelper.this, WidgetUpdater.class);
                    //PendingIntent pending = PendingIntent.getBroadcast(ProviderHelper.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    sendBroadcast(intent);
                }


                //Quit the handler
                if(ProviderHelper.isQuitting()) {

                    //Remove all pending callbacks, and then return
                    handler.removeCallbacks(this);

                    // to make sure it isnt recreated
                    stopSelf();
                    return;
                }
                //Use post at time to only update when the phone is not in deep sleep :)
                else handler.postAtTime(this, SystemClock.uptimeMillis() + updateInterval);


            }
        };

        handler.post(runUpdate);
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

        Log.d("Stats sleepy", "Button Press!");

        sleepReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                String strAction = intent.getAction();

                if (strAction.equals(Intent.ACTION_SCREEN_OFF)) {

                    //Quit the handler
                    Log.d("Stats sleepy", "Bye");
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
                            Log.d("Stats sleepy", "Hiii");
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


    @Override
    public void onDestroy()
    {
        //Stop Alarm here
        //simply set quit to true
        quit = true;

        //Unregister our broadcast receiver
        //unregisterReceiver(this);
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
