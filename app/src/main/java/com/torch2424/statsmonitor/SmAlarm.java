package com.torch2424.statsmonitor;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.RemoteViews;

import com.torch2424.statsmonitor.com.torch2424.statshelpers.BatteryHelper;
import com.torch2424.statsmonitor.com.torch2424.statshelpers.CPUHelper;
import com.torch2424.statsmonitor.com.torch2424.statshelpers.MemoryHelper;
import com.torch2424.statsmonitor.com.torch2424.statshelpers.NetworkHelper;
import com.torch2424.statsmonitor.com.torch2424.statshelpers.TimeHelper;
import com.torch2424.statsmonitor.com.torch2424.statsproviders.ProviderBig;
import com.torch2424.statsmonitor.com.torch2424.statsproviders.ProviderBigger;
import com.torch2424.statsmonitor.com.torch2424.statsproviders.ProviderSmall;
import com.torch2424.statsmonitor.com.torch2424.statsproviders.SmProvider;
import com.torch2424.statsmonitor.com.torch2424.statsproviders.SuperSmall;
import com.torch2424.statsmonitorwidget.R;


public class SmAlarm extends BroadcastReceiver 
{
	    //creating remote views out here for access anywhere in class
		RemoteViews views;
	    //getting access to the config values
		ConfigureWidget config = new ConfigureWidget();

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
		int secs; //for if people want a slower update interval


		//setting up colors
		int textColor;
		int backColor;


		//setting up text sizes
		int textSize;
		int textTitleSize;
		
		
		//for config activity
		boolean updateBool;
		
		
		
		public void configPrefs(Context context)
		{
			//getting preferences, can't use intents, don't work with broadcast reciever
			prefs = context.getSharedPreferences("MyPrefs", 0);
			boolTimeTitle = prefs.getBoolean("TIMETITLE", true);
			boolSystemTitle = prefs.getBoolean("SYSTEMTITLE", true);
			boolMemoryTitle = prefs.getBoolean("MEMORYTITLE", true);
			boolNetworkTitle = prefs.getBoolean("NETWORKTITLE", true);

			//default widget textcolor is LTGRAY
			textColor = prefs.getInt("TEXTCOLOR", Color.LTGRAY);
			//default background is transparent
			backColor = prefs.getInt("BACKCOLOR", Color.TRANSPARENT);

			//text sizes
			textSize = prefs.getInt("TEXTSIZE", 12);
			textTitleSize = prefs.getInt("TEXTTITLESIZE", 18);

			//advanced settings
			tapConfig = prefs.getBoolean("TAPCONFIG", false);
			centerBool = prefs.getBoolean("TEXTCENTER", false);
			rightBool = prefs.getBoolean("TEXTRIGHT", false);
			TitleBool = prefs.getBoolean("NOCPUTITLE", false);
		}

		public void sectionConfig ()
		{

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
			views.setTextColor(R.id.networkUp, textColor);
			views.setTextColor(R.id.networkDown, textColor);
			views.setTextColor(R.id.networkType, textColor);
			//setting text sizes
			views.setFloat(R.id.time, "setTextSize", textSize);
			views.setFloat(R.id.date, "setTextSize", textSize);
			views.setFloat(R.id.battery, "setTextSize", textSize);
			views.setFloat(R.id.batteryTemp, "setTextSize", textSize);
			//views.setFloat(R.id.batteryChange, "setTextSize", textSize);
			views.setFloat(R.id.cpu, "setTextSize", textSize);
			views.setFloat(R.id.uptime, "setTextSize", textSize);
			views.setFloat(R.id.internal, "setTextSize", textSize);
			views.setFloat(R.id.external, "setTextSize", textSize);
			views.setFloat(R.id.internalTitle, "setTextSize", textSize);
			views.setFloat(R.id.externalTitle, "setTextSize", textSize);
			views.setFloat(R.id.ram, "setTextSize", textSize);
			views.setFloat(R.id.networkType, "setTextSize", textSize);
			views.setFloat(R.id.networkUp, "setTextSize", textSize);
			views.setFloat(R.id.networkDown, "setTextSize", textSize);
			//setting text title sizes
			views.setFloat(R.id.timeTitle, "setTextSize", textTitleSize);
			views.setFloat(R.id.systemTitle, "setTextSize", textTitleSize);
			views.setFloat(R.id.memoryTitle, "setTextSize", textTitleSize);
			views.setFloat(R.id.CPUTitle, "setTextSize", textTitleSize);
			views.setFloat(R.id.networkTitle, "setTextSize", textTitleSize);

			//setting text orientation
			if(centerBool) views.setInt(R.id.widgetLayout, "setGravity", Gravity.CENTER);
			else if (rightBool) views.setInt(R.id.widgetLayout, "setGravity", Gravity.RIGHT);
			else views.setInt(R.id.widgetLayout, "setGravity", Gravity.LEFT);


			//views.setfloat to set text sizes, but cant be set to zero!
			//if the preference from the checkbox is false, set the text visibility to gone, external sd check
			 if(boolTimeTitle) views.setViewVisibility(R.id.timeTitle, View.VISIBLE);
			 else  views.setViewVisibility(R.id.timeTitle, View.GONE);

			 if (boolSystemTitle)  views.setViewVisibility(R.id.systemTitle, View.VISIBLE);
			 else views.setViewVisibility(R.id.systemTitle, View.GONE);

			 if (boolMemoryTitle)  views.setViewVisibility(R.id.memoryTitle, View.VISIBLE);
			 else views.setViewVisibility(R.id.memoryTitle, View.GONE);
		        
            if(boolNetworkTitle) views.setViewVisibility(R.id.networkTitle, View.VISIBLE);
            else views.setViewVisibility(R.id.networkTitle, View.GONE);
		}
	
	public void update(Context context, Intent intent)
	{
		views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		//getting which sections to omit
		configPrefs(context);
		//call title config methods to omit methods
		sectionConfig();

        //Create our Time manager
        TimeHelper timeMan = new TimeHelper(views, prefs);

        //Create our Battery Manage
        BatteryHelper battMan = new BatteryHelper(views, prefs);

        //Create our CPU Helper
        CPUHelper cpuMan = new CPUHelper(views, prefs);

        //Create diskspace manager
        MemoryHelper diskMan = new MemoryHelper(views, prefs);

        //Create Network Manager
        NetworkHelper networkMan = new NetworkHelper(views, prefs);


		//call time methods, not calling if unchecked
		if(timeMan.timeStatus()) timeMan.getTime();
		
		//call system methods
		if(battMan.percentStatus() || battMan.tempStatus())
		{
			battMan.getBatteryPercent(context);
            battMan.getBatteryTemp(context);
		}
		
		if(cpuMan.cpuStatus()) cpuMan.getCpuUsage();
		
		if(timeMan.upTimeStatus()) timeMan.getUptime();
		
		//call memory methods
		if(diskMan.memoryStatus()) diskMan.getSpace();
		
		if(diskMan.ramStatus()) diskMan.getRam(context);
		
		//call netowork methods
		if(networkMan.typeStatus()) networkMan.getNetworkType(context);
		
		if(networkMan.downSpeedStatus() || networkMan.upSpeedStatus()) networkMan.getSpeeds();
		
		
		
		if(tapConfig == false)
		{
			//setting up on click event
			Intent config = new Intent(context, ConfigureWidget.class);
			PendingIntent pendingConfig = PendingIntent.getActivity(context, 0, config, 0);
			views.setOnClickPendingIntent(R.id.widgetLayout, pendingConfig);
		}
		
		//cpu title
		if(TitleBool)
		{
			views.setViewVisibility(R.id.CPUTitle, View.GONE); 
		}
		else
		{
		    views.setViewVisibility(R.id.CPUTitle, View.VISIBLE);
		}
		
		//update widget for all sizes
		ComponentName thiswidget = new ComponentName(context, SmProvider.class);
		ComponentName thiswidgetsmallest = new ComponentName(context, SuperSmall.class);
		ComponentName thiswidgetsmall = new ComponentName(context, ProviderSmall.class);
		ComponentName thiswidgetbig = new ComponentName(context, ProviderBig.class);
		ComponentName thiswidgetbigger = new ComponentName(context, ProviderBigger.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		manager.updateAppWidget(thiswidget, views);
		manager.updateAppWidget(thiswidgetsmall, views);
		manager.updateAppWidget(thiswidgetbig, views);
		manager.updateAppWidget(thiswidgetbigger, views);
		manager.updateAppWidget(thiswidgetsmallest, views);
	}
		
	@Override
	public void onReceive(Context context, Intent intent) 
	{

		//use our preferences as a quick and easy way to store the previous value
		prefs = context.getSharedPreferences("MyPrefs", 0);
		Editor editor = prefs.edit();	
		//to tell if user is configuring app or not currently
		updateBool = prefs.getBoolean("UPDATE", true);
		secs = prefs.getInt("SECS", 0);
		threeBool = prefs.getBoolean("THREESEC", false);
		fiveBool = prefs.getBoolean("FIVESEC", false);
		
		if(updateBool)
		{
		
		 if(threeBool)
		 {
			if(secs > 1)
			{
				update(context, intent);
				editor.putInt("SECS", 0);
				editor.commit();
			}
			else
			{
				secs++;
				editor.putInt("SECS", secs);
				editor.commit();
			}
		 }
		 else if(fiveBool)
		 {
			if(secs > 3)
			{
				update(context, intent);
				editor.putInt("SECS", 0);
				editor.commit();
			}
			else
			{
				secs++;
				editor.putInt("SECS", secs);
				editor.commit();
			}
		 }
		 else
		 {
			 update(context, intent);
		 }
	}
		
	}

}
