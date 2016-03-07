package com.torch2424.statsmonitor;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RemoteViews;

import com.torch2424.statsmonitor.com.torch2424.statshelpers.BatteryHelper;
import com.torch2424.statsmonitor.com.torch2424.statshelpers.CPUHelper;
import com.torch2424.statsmonitor.com.torch2424.statshelpers.MemoryHelper;
import com.torch2424.statsmonitor.com.torch2424.statshelpers.NetworkHelper;
import com.torch2424.statsmonitor.com.torch2424.statshelpers.ProviderHelper;
import com.torch2424.statsmonitor.com.torch2424.statshelpers.TimeHelper;
import com.torch2424.statsmonitor.com.torch2424.statsproviders.ProviderBig;
import com.torch2424.statsmonitor.com.torch2424.statsproviders.ProviderBigger;
import com.torch2424.statsmonitor.com.torch2424.statsproviders.ProviderSmall;
import com.torch2424.statsmonitor.com.torch2424.statsproviders.SmProvider;
import com.torch2424.statsmonitor.com.torch2424.statsproviders.SuperSmall;
import com.torch2424.statsmonitorwidget.R;


public class WidgetUpdater extends BroadcastReceiver
{

        //Making Many variables static so they are not reinitialized every call

	    //creating remote views out here for access anywhere in class
		RemoteViews views;

        //Our Provider Components
        ComponentName thiswidget;
        ComponentName thiswidgetsmallest;
        ComponentName thiswidgetsmall;
        ComponentName thiswidgetbig;
        ComponentName thiswidgetbigger;

        //Our Helper Classes
        static TimeHelper timeMan;
        static BatteryHelper battMan;
        static CPUHelper cpuMan;
        static MemoryHelper diskMan;
        static NetworkHelper networkMan;


		//getting prefs and boolean values for which sections to display
		SharedPreferences prefs;
		boolean boolTimeTitle;
		boolean boolSystemTitle;
		boolean boolMemoryTitle;
		boolean boolNetworkTitle;

		//advanced settings
		boolean tapConfig;
		boolean rightBool;
		boolean centerBool;
		boolean TitleBool;
		boolean threeBool;
		boolean fiveBool;


		//setting up colors
		int textColor;
		int backColor;


		//setting up text sizes
		int textSize;
		int textTitleSize;
		
		
		//To Control when the app should be updating, and reinitializing
		static boolean shouldUpdate;
        static boolean reInit = true;

    @Override
    public void onReceive(Context parentContext, Intent intent) {

        //Grab our views
        //MUST SET IT ONCE THIS CAN OVERRIDE ITSELF
        views = new RemoteViews(parentContext.getPackageName(), R.layout.widget_layout);

        //Call all of our initialization functions
        //getting which sections to omit
        prefsConfig(parentContext);
        //call title config methods to omit methods
        viewConfig(parentContext);

        //First Check if we need to reinitialize our settings
        if (reInit) {

            //Re-Initialize all of our helper classes
            helperConfig(parentContext);

            //Set reInit to false and should update to true
            reInit = false;
            shouldUpdate = true;
        }
        //Re-set helper views every update
        else helperViews();


        //Grab all of our providers
        thiswidget = new ComponentName(parentContext, SmProvider.class);
        thiswidgetsmallest = new ComponentName(parentContext, SuperSmall.class);
        thiswidgetsmall = new ComponentName(parentContext, ProviderSmall.class);
        thiswidgetbig = new ComponentName(parentContext, ProviderBig.class);
        thiswidgetbigger = new ComponentName(parentContext, ProviderBigger.class);

        if (shouldUpdate) {

            if (threeBool) {
                if (ProviderHelper.getSeconds() > 1) {
                    update(parentContext);
                    ProviderHelper.setSeconds(0);
                } else ProviderHelper.setSeconds(ProviderHelper.getSeconds() + 1);
            } else if (fiveBool) {
                if (ProviderHelper.getSeconds() > 3) {
                    update(parentContext);
                    ProviderHelper.setSeconds(0);
                } else ProviderHelper.setSeconds(ProviderHelper.getSeconds() + 1);
            } else {
                update(parentContext);
            }
        }
    }


        //Static function to stop updating
        public static void setUpdating(boolean update) {

            //Set the two controlling booleans to false
            if(update) {
                shouldUpdate = true;
                reInit = true;
            }
            else {
                shouldUpdate = false;
                reInit = false;
            }
        }

		public void prefsConfig(Context context)
		{
			//getting preferences, can't use intents, don't work with broadcast reciever
			prefs = context.getSharedPreferences("MyPrefs", 0);
			boolTimeTitle = prefs.getBoolean("TIMETITLE", true);
			boolSystemTitle = prefs.getBoolean("SYSTEMTITLE", true);
			boolMemoryTitle = prefs.getBoolean("MEMORYTITLE", true);
			boolNetworkTitle = prefs.getBoolean("NETWORKTITLE", true);

			//default widget textcolor is White
			textColor = prefs.getInt("TEXTCOLOR", Color.LTGRAY);
			//default background is 50% transparent
			backColor = prefs.getInt("BACKCOLOR", Color.argb(128, 00, 00, 00));

			//text sizes
			textSize = prefs.getInt("TEXTSIZE", 12);
			textTitleSize = prefs.getInt("TEXTTITLESIZE", 18);

			//advanced settings
			tapConfig = prefs.getBoolean("TAPCONFIG", false);
			centerBool = prefs.getBoolean("TEXTCENTER", false);
			rightBool = prefs.getBoolean("TEXTRIGHT", false);
			TitleBool = prefs.getBoolean("NOCPUTITLE", false);
            threeBool = prefs.getBoolean("THREESEC", false);
            fiveBool = prefs.getBoolean("FIVESEC", false);
		}

		public void viewConfig (Context context)
		{

            //Setting Title visibility
            //views.setfloat to set text sizes, but cant be set to zero!
            //if the preference from the checkbox is false, set the text visibility to gone, external sd check
            if(boolTimeTitle) views.setViewVisibility(R.id.timeTitle, View.VISIBLE);
            else  views.setViewVisibility(R.id.timeTitle, View.GONE);

            if (boolSystemTitle)  views.setViewVisibility(R.id.systemTitle, View.VISIBLE);
            else views.setViewVisibility(R.id.systemTitle, View.GONE);

            if (TitleBool) views.setViewVisibility(R.id.CPUTitle, View.GONE);
            else views.setViewVisibility(R.id.CPUTitle, View.VISIBLE);


            if (boolMemoryTitle)  views.setViewVisibility(R.id.memoryTitle, View.VISIBLE);
            else views.setViewVisibility(R.id.memoryTitle, View.GONE);

            if(boolNetworkTitle) views.setViewVisibility(R.id.networkTitle, View.VISIBLE);
            else views.setViewVisibility(R.id.networkTitle, View.GONE);

            //setting background
            views.setInt(R.id.widgetLayout, "setBackgroundColor", backColor);
			//setting Colors
			views.setTextColor(R.id.timeTitle, textColor);
			views.setTextColor(R.id.systemTitle, textColor);
			views.setTextColor(R.id.memoryTitle, textColor);
			views.setTextColor(R.id.time, textColor);
			views.setTextColor(R.id.date, textColor);
			views.setTextColor(R.id.battery, textColor);
			views.setTextColor(R.id.batteryTemp, textColor);
			//views.setTextColor(R.id.batteryChange, textColor);
			views.setTextColor(R.id.cpu, textColor);
			views.setTextColor(R.id.CPUTitle, textColor);
			views.setTextColor(R.id.uptime, textColor);
			views.setTextColor(R.id.internal, textColor);
			views.setTextColor(R.id.external, textColor);
			views.setTextColor(R.id.internalTitle, textColor);
			views.setTextColor(R.id.externalTitle, textColor);
			views.setTextColor(R.id.ram, textColor);
			views.setTextColor(R.id.networkTitle, textColor);
            views.setTextColor(R.id.ipAddress, textColor);
			views.setTextColor(R.id.networkUp, textColor);
			views.setTextColor(R.id.networkDown, textColor);
			views.setTextColor(R.id.networkType, textColor);

			//setting text sizes
			views.setFloat(R.id.time, "setTextSize", textSize);
			views.setFloat(R.id.date, "setTextSize", textSize);
			views.setFloat(R.id.battery, "setTextSize", textSize);
			views.setFloat(R.id.batteryTemp, "setTextSize", textSize);
			views.setFloat(R.id.cpu, "setTextSize", textSize);
			views.setFloat(R.id.uptime, "setTextSize", textSize);
			views.setFloat(R.id.internal, "setTextSize", textSize);
			views.setFloat(R.id.external, "setTextSize", textSize);
			views.setFloat(R.id.internalTitle, "setTextSize", textSize);
			views.setFloat(R.id.externalTitle, "setTextSize", textSize);
			views.setFloat(R.id.ram, "setTextSize", textSize);
			views.setFloat(R.id.networkType, "setTextSize", textSize);
            views.setFloat(R.id.ipAddress, "setTextSize", textSize);
			views.setFloat(R.id.networkUp, "setTextSize", textSize);
			views.setFloat(R.id.networkDown, "setTextSize", textSize);

			//Setting text title sizes
			views.setFloat(R.id.timeTitle, "setTextSize", textTitleSize);
			views.setFloat(R.id.systemTitle, "setTextSize", textTitleSize);
			views.setFloat(R.id.memoryTitle, "setTextSize", textTitleSize);
			views.setFloat(R.id.CPUTitle, "setTextSize", textTitleSize);
			views.setFloat(R.id.networkTitle, "setTextSize", textTitleSize);

			//setting text orientation
			if(centerBool) views.setInt(R.id.widgetLayout, "setGravity", Gravity.CENTER);
			else if (rightBool) views.setInt(R.id.widgetLayout, "setGravity", Gravity.RIGHT);
			else views.setInt(R.id.widgetLayout, "setGravity", Gravity.LEFT);

            //Check if we want to allow clicking the widget to configure it
            if (!tapConfig) {

                //setting up on click event
                Intent config = new Intent(context, ConfigureWidget.class);
                PendingIntent pendingConfig = PendingIntent.getActivity(context, 0, config, 0);
                views.setOnClickPendingIntent(R.id.widgetLayout, pendingConfig);
            }
		}

        //Function to initialize all of our helper classes
        public void helperConfig(Context context) {

            //Create our Time manager
            timeMan = new TimeHelper(views, prefs);

            //Create our Battery Manage
            battMan = new BatteryHelper(views, prefs);

            //Create our CPU Helper
            cpuMan = new CPUHelper(views, prefs);

            //Create diskspace manager
            diskMan = new MemoryHelper(views, prefs);

            //Create Network Manager
            networkMan = new NetworkHelper(views, prefs, context);
        }

        //Function to reset all of our helper views
        public void helperViews() {

            //Simply re-set all of the views for the helpers, if not null, and is updating
            if (timeMan != null &&
                    (timeMan.timeStatus() ||
                    timeMan.upTimeStatus()))timeMan.setViews(views);

            if (battMan != null &&
                    (battMan.percentStatus() ||
                    battMan.tempStatus())) {
                battMan.setViews(views);
            }

            if (cpuMan != null &&
                    cpuMan.cpuStatus()) cpuMan.setViews(views);

            if(diskMan != null &&
                    (diskMan.memoryStatus() ||
                    diskMan.ramStatus())) diskMan.setViews(views);

            if(networkMan != null &&
                    (networkMan.ipStatus() ||
                    networkMan.typeStatus() ||
                    networkMan.upSpeedStatus() ||
                    networkMan.downSpeedStatus())) networkMan.setViews(views);
        }
	
	    public void update(Context context) {

            //call time methods, not calling if unchecked
            if (timeMan.timeStatus()) timeMan.getTime();

            //call system methods
            if (battMan.percentStatus() || battMan.tempStatus()) {
                battMan.getBatteryPercent(context);
                battMan.getBatteryTemp(context);
            }

            if (cpuMan.cpuStatus()) cpuMan.getCpuUsage();

            if (timeMan.upTimeStatus()) timeMan.getUptime();

            //call memory methods
            if (diskMan.memoryStatus()) diskMan.getSpace();

            if (diskMan.ramStatus()) diskMan.getRam(context);

            //call network methods
            if (networkMan.typeStatus()) networkMan.getNetworkType(context);

            if (networkMan.ipStatus()) networkMan.getIp(context);

            if (networkMan.downSpeedStatus() || networkMan.upSpeedStatus()) networkMan.getSpeeds();

            //update widget for all size
            Log.d("statsUpdating", "UPDATINGNGGGGNGNGNGN");
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            manager.updateAppWidget(thiswidget, views);
            manager.updateAppWidget(thiswidgetsmall, views);
            manager.updateAppWidget(thiswidgetbig, views);
            manager.updateAppWidget(thiswidgetbigger, views);
            manager.updateAppWidget(thiswidgetsmallest, views);
        }

}
