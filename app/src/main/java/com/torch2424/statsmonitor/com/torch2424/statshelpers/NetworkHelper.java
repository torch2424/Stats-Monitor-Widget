package com.torch2424.statsmonitor.com.torch2424.statshelpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

import com.torch2424.statsmonitorwidget.R;

import java.text.DecimalFormat;

/**
 * Created by torch2424 on 1/25/16.
 */
public class NetworkHelper {

    //Set our view
    RemoteViews views;

    //Boolean for kilobytes per second speeds
    boolean kilobytesBool;

    //Static variable to manage up and download speeds
    static long prevD;
    static long prevU;
    static int runCount;

    //Decimal format for setting precision in values
    DecimalFormat format;

    public NetworkHelper(RemoteViews parentView, SharedPreferences prefs) {

        //set our view
        views = parentView;

        //Get if we want our speeds in kilobytes per second
        kilobytesBool = prefs.getBoolean("KILOBYTE", false);

        //Initialize our static variables
        prevD = 0;
        prevU = 0;
        runCount = 0;

        //Initialize our decimal format
        format = new DecimalFormat("0.00");

    }

    //method to get network type
    public void getNetworkType(Context context)
    {

        //gotten from stack, get our connection manager
        //http://stackoverflow.com/questions/2919414/get-network-type
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get our wifi first
        NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState();

        //if wifi is connected
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)
        {

            //Find and set the wifi name
            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            views.setTextViewText(R.id.networkType, wifiInfo.getSSID());
        }
        else {

            //Check for mobile service
            //need to check for tablets to see if they have telephony
            boolean hasTelephony = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);

            if(hasTelephony)
            {
                //Get the service provider name
                NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState();

                //if mobile dtata is conntected
                if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING)
                {

                    //Get and set our telephony manager for service name
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    views.setTextViewText(R.id.networkType, tm.getNetworkOperatorName());
                }
                else
                {
                    views.setTextViewText(R.id.networkType, "None");
                }

            }
            else
            {
                views.setTextViewText(R.id.networkType, "No Telephony found!");
            }
        }
    }

    //method to get network speed
    public void getSpeeds()
    {
        //use traffic stats to get bytes since device boot
        //checking for traffic stats report

        //get the current bytes
        long speedD = TrafficStats.getTotalRxBytes();
        long speedU = TrafficStats.getTotalTxBytes();
        double down = 0;
        double up = 0;


        if(TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED || TrafficStats.getTotalTxBytes() ==
                TrafficStats.UNSUPPORTED)
        {
            views.setTextViewText(R.id.networkDown, "Sorry, but Traffic Stats is unsupported by your device");
            views.setTextViewText(R.id.networkUp, "Sorry, but Traffic Stats is unsupported by your device");
        }
        else
        {

            //Check our static variables
            if(prevD == 0 && prevU == 0 && runCount == 0)
            {
                //store the previous values without setting down or up
                prevD = speedD;
                prevU = speedU;
                runCount = 1;
            }
            else
            {
                if(runCount > 2)
                {
                    //set the values while committing the new value
                    if(kilobytesBool)
                    {
                        //kilobytes
                        down = (speedD - prevD) / 1024.0 / 3.0;
                        up = (speedU - prevU) / 1024.0 / 3.0;

                        //set text of our widget to our values
                        views.setTextViewText(R.id.networkDown, "Download: " + format.format(down) + " kB/s");
                        views.setTextViewText(R.id.networkUp, "Upload: " + format.format(up) + " kB/s");
                    }
                    else
                    {
                        //megabits
                        down = (speedD - prevD) * 8 / 1024.0 / 1024.0 / 3.0;
                        up = (speedU - prevU) * 8 / 1024.0 / 1024.0 / 3.0;

                        //set text of our widget to our values
                        views.setTextViewText(R.id.networkDown, "Download: " + format.format(down) + " Mbps");
                        views.setTextViewText(R.id.networkUp, "Upload: " + format.format(up) + " Mbps");
                    }

                    //Reset our values
                    prevD = 0;
                    prevU = 0;
                    runCount = 0;
                }
                else
                {
                    runCount++;
                }
            }

        }
    }
}
