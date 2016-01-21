package com.torch2424.simplemonitor;

import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.os.Handler;
import android.util.Log;

/**
 * Created by torch2424 on 1/21/16.
 */
public class ProviderHelper extends AppWidgetProvider {

    //Our update interval
    int updateInterval = 1000;

    //Flag to stop sending the intent on stop alarm
    boolean pause;

    //Add some functions that will handle on destroy and on create
    //calling of alarms in one place

    public void callAlarm(final PendingIntent pending) {

        //creating Handler to update every second
        final Handler handler = new Handler();

        //Set our pause to false
        pause = false;

        handler.post( new Runnable() {
            public void run() {

                //Send the broadcast to the pending intent
                try {
                    if(!pause)  pending.send();
                } catch (PendingIntent.CanceledException e) {
                    Log.d("DEBUG", "PENDING ERROR");
                }

                //Call this again
                handler.postDelayed(this, updateInterval);


            }
        });
    }

    public void stopAlarm(final PendingIntent pending) {
        //Stop Alarm here
    }

}
