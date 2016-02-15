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
import android.text.format.Formatter;
import android.view.View;
import android.widget.RemoteViews;

import com.torch2424.statsmonitorwidget.R;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

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

    //View booleans
    boolean networkTypeView;
    boolean ipView;
    boolean upSpeedView;
    boolean downSpeedView;

    //Connectivity managers
    ConnectivityManager conMan;

    public NetworkHelper(RemoteViews parentView, SharedPreferences prefs, Context context) {

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

        //Set view
        networkTypeView = prefs.getBoolean("NETWORKTYPE", true);
        ipView = prefs.getBoolean("IPADDRESS", true);
        upSpeedView = prefs.getBoolean("NETWORKUP", true);
        downSpeedView = prefs.getBoolean("NETWORKDOWN", true);

        if(networkTypeView) views.setViewVisibility(R.id.networkType, View.VISIBLE);
        else views.setViewVisibility(R.id.networkType, View.GONE);

        if(ipView) views.setViewVisibility(R.id.ipAddress, View.VISIBLE);
        else views.setViewVisibility(R.id.ipAddress, View.GONE);

        if(upSpeedView) views.setViewVisibility(R.id.networkUp, View.VISIBLE);
        else views.setViewVisibility(R.id.networkUp, View.GONE);

        if(downSpeedView)  views.setViewVisibility(R.id.networkDown, View.VISIBLE);
        else views.setViewVisibility(R.id.networkDown, View.GONE);

        //gotten from stack, get our connection manager
        //http://stackoverflow.com/questions/2919414/get-network-type
        conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    }

    //method to get network type
    public void getNetworkType(Context context)
    {

        //Get our wifi first
        NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState();

        //if wifi is connected
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)
        {

            //Find and set the wifi name
            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            //Set the wifi name
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
                    views.setTextViewText(R.id.networkType, "Network Name: " + tm.getNetworkOperatorName());
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

    //Method to get our ipv4 adress
    public void getIp(Context context) {

        //Get our wifi first
        NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState();

        //if wifi is connected
        if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)
        {

            //Find and set the wifi name
            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            //Set the ip
            String ip = Formatter.formatIpAddress(wifiInfo.getIpAddress());
            
            if(ipView) {

                //Show the view, and set the text
                views.setViewVisibility(R.id.ipAddress, View.VISIBLE);
                views.setTextViewText(R.id.ipAddress, "Ip: " + ip);
            }
        }
        else {

            //If not on wifi, hide the ip
            views.setViewVisibility(R.id.ipAddress, View.GONE);
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

    //Function to return view Status
    public boolean typeStatus() {
        if(networkTypeView) return true;
        else return false;
    }

    public boolean ipStatus() {
        if(ipView) return true;
        else return false;
    }

    public boolean upSpeedStatus() {
        if(upSpeedView) return true;
        else return false;
    }

    public boolean downSpeedStatus() {
        if(downSpeedView) return true;
        else return false;
    }
}
