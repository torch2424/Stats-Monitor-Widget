package com.torch2424.simplemonitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;


public class ProviderBig extends AppWidgetProvider
{
	//change to 2x2
		
		//Time, System (cpu and uptime), memory (hdd and ram), network
		//need to add: complete network stats (3g not just wifi) and (ram stats not currently correct)
		//to remove textviews cant set text size to zero, you have to set visibility to gone
		//http://stackoverflow.com/questions/6721616/how-can-i-to-change-text-size-in-remoteviews

		//declaring here to access in on disabled
		PendingIntent pending;
		Intent intent;
		AlarmManager manager;
		
		//need peniding intent flags to properly create and destroy alarm
		public void onEnabled(Context context)
		{
			//creating alarm manager to update every second
			manager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			intent = new Intent(context, SmAlarm.class);
			pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			manager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),1000, pending);
		}
		
		//remember toasts that couldnt exeute fast enough, are put into stack, and you have to 
		//wait for all to be executed even after you remove widget
		public void onDisabled(Context context)
		{
			//killing alarm manager so widget doesnt hog resources
			//in the future need to set things up for multiple widgets
			manager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			intent = new Intent(context, SmAlarm.class);
			pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			manager.cancel(pending);
		}
}
