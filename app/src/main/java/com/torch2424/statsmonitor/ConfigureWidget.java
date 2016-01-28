package com.torch2424.statsmonitor;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.torch2424.statsmonitorwidget.R;

public class ConfigureWidget extends Activity 
{
	//initializing widget id and context
	int widgetID;
	public ConfigureWidget context;
	//initialize checkboxes and edittexts and booleans
	CheckBox checkTimeTitle;
	CheckBox checkTime;
	CheckBox checkDate;
	CheckBox checkSystemTitle;
	CheckBox checkBattery;
	CheckBox checkTemp;
	CheckBox checkCpu;
	CheckBox checkUptime;
	CheckBox checkMemoryTitle;
	CheckBox checkMemory;
	CheckBox checkRam;
	CheckBox checkNetworkTitle;
	CheckBox checkNetworkType;
	CheckBox checkNetworkUp;
	CheckBox checkNetworkDown;
	boolean timeTitle;
	boolean time;
	boolean date;
	boolean systemTitle;
	boolean battery;
	boolean temp;
	boolean cpu;
	boolean uptime;
	boolean memoryTitle;
	boolean memory;
	boolean ram;
	boolean networkTitle;
	boolean networkType;
	boolean networkUp;
	boolean networkDown;
	
	//when activity is started
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure_widget);
		//if the user backs out it doesn't save
		setResult(RESULT_CANCELED);
		//get the context
		context = this;
		//need to get appwidget id, got this from google guide
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
		    widgetID = extras.getInt(
		            AppWidgetManager.EXTRA_APPWIDGET_ID, 
		            AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		//Tell the app to stop updating
		SmAlarm.setUpdating(false);
		
		//initialize checkboxes
		checkTimeTitle = (CheckBox) findViewById(R.id.checkTimeTitle);
		checkTime = (CheckBox) findViewById(R.id.checkTime);
		checkDate = (CheckBox) findViewById(R.id.checkDate);
		checkSystemTitle = (CheckBox) findViewById(R.id.checkSystemTitle);
		checkBattery = (CheckBox) findViewById(R.id.checkBattery);
		checkTemp = (CheckBox) findViewById(R.id.checkTemp);
		//checkChange = (CheckBox) findViewById(R.id.checkChange);
		checkCpu = (CheckBox) findViewById(R.id.checkCpu);
		checkUptime = (CheckBox) findViewById(R.id.checkUptime);
		checkMemoryTitle = (CheckBox) findViewById(R.id.checkMemoryTitle);
		checkMemory = (CheckBox) findViewById(R.id.checkMemory);
		checkRam = (CheckBox) findViewById(R.id.checkRam);
		checkNetworkTitle = (CheckBox) findViewById(R.id.checkNetworkTitle);
		checkNetworkType = (CheckBox) findViewById(R.id.checkNetType);
		checkNetworkUp = (CheckBox) findViewById(R.id.checkNetUp);
		checkNetworkDown = (CheckBox) findViewById(R.id.checkNetDown);
		
		//get preferences for each box to set checked or not
		SharedPreferences prefs = context.getSharedPreferences("MyPrefs", 0);
		timeTitle = prefs.getBoolean("TIMETITLE", true);
		time = prefs.getBoolean("TIME", true);
		date = prefs.getBoolean("DATE", true);
		systemTitle = prefs.getBoolean("SYSTEMTITLE", true);
		battery = prefs.getBoolean("BATTERY", true);
		temp = prefs.getBoolean("BATTERYTEMP", true);
		//change = prefs.getBoolean("BATTERYCHANGE", true);
		cpu = prefs.getBoolean("CPU", true);
		uptime = prefs.getBoolean("UPTIME", true);
		memoryTitle = prefs.getBoolean("MEMORYTITLE", true);
		memory = prefs.getBoolean("MEMORY", true);
		ram = prefs.getBoolean("RAM", true);
		networkTitle = prefs.getBoolean("NETWORKTITLE", true);
		networkType = prefs.getBoolean("NETWORKTYPE", true);
		networkUp = prefs.getBoolean("NETWORKUP", true);
		networkDown = prefs.getBoolean("NETWORKDOWN", true);
		
		if(timeTitle == true)
		{
			checkTimeTitle.setChecked(true);
		}
		else
		{
			checkTimeTitle.setChecked(false);
		}
		if(time == true)
		{
			checkTime.setChecked(true);
		}
		else
		{
			checkTime.setChecked(false);
		}
		if(date == true)
		{
			checkDate.setChecked(true);
		}
		else
		{
			checkDate.setChecked(false);
		}
		if(systemTitle == true)
		{
			checkSystemTitle.setChecked(true);
		}
		else
		{
			checkSystemTitle.setChecked(false);
		}
		if(battery == true)
		{
			checkBattery.setChecked(true);
		}
		else
		{
			checkBattery.setChecked(false);
		}
		if (temp == true)
		{
			checkTemp.setChecked(true);
		}
		else
		{
			checkTemp.setChecked(false);
		}
		/*
		if(change)
		{
			checkChange.setChecked(true);
		}
		
		else
		{
			checkChange.setChecked(false);
		}
		*/
		if (cpu == true)
		{
			checkCpu.setChecked(true);
		}
		else
		{
			checkCpu.setChecked(false);
		}
		if (uptime == true)
		{
			checkUptime.setChecked(true);
		}
		else
		{
			checkUptime.setChecked(false);
		}
		if (memoryTitle == true)
		{
			checkMemoryTitle.setChecked(true);
		}
		else
		{
			checkMemoryTitle.setChecked(false);
		}
		if (memory == true)
		{
			checkMemory.setChecked(true);
		}
		else
		{
			checkMemory.setChecked(false);
		}
		if (ram == true)
		{
			checkRam.setChecked(true);
		}
		else
		{
			checkRam.setChecked(false);
		}
		
		if(networkTitle)
		{
			checkNetworkTitle.setChecked(true);
		}
		else
		{
			checkNetworkTitle.setChecked(false);
		}
		if(networkType)
		{
			checkNetworkType.setChecked(true);
		}
		else
		{
			checkNetworkType.setChecked(false);
		}
		if(networkUp)
		{
			checkNetworkUp.setChecked(true);
		}
		else
		{
			checkNetworkUp.setChecked(false);
		}
		if(networkDown)
		{
			checkNetworkDown.setChecked(true);
		}
		else
		{
			checkNetworkDown.setChecked(false);
		}
		
		
		if(Environment.getExternalStorageDirectory().exists() == false)
    	{
			Toast.makeText(this, "No external storage found, please uncheck it!", Toast.LENGTH_LONG).show();
    	}
		
	}

	
	public void saveConfig(View view)
	{
		//getting every config and putting it in an boolean
		timeTitle = checkTimeTitle.isChecked();
		time = checkTime.isChecked();
		date = checkDate.isChecked();
		systemTitle = checkSystemTitle.isChecked();
		battery = checkBattery.isChecked();
		temp = checkTemp.isChecked();
		//change = checkChange.isChecked();
		cpu = checkCpu.isChecked();
		uptime = checkUptime.isChecked();
		memoryTitle = checkMemoryTitle.isChecked();
		memory = checkMemory.isChecked();
		ram = checkRam.isChecked();
		networkTitle = checkNetworkTitle.isChecked();
		networkType = checkNetworkType.isChecked();
		networkUp = checkNetworkUp.isChecked();
		networkDown = checkNetworkDown.isChecked();
		
		
		//used shared preferences to transfer data
		SharedPreferences prefs = context.getSharedPreferences("MyPrefs", 0);
		Editor editor = prefs.edit();
		editor.putBoolean("TIMETITLE", timeTitle);
		editor.putBoolean("TIME", time);
		editor.putBoolean("DATE",date);
		editor.putBoolean("SYSTEMTITLE", systemTitle);
		editor.putBoolean("BATTERY", battery);
		editor.putBoolean("BATTERYTEMP", temp);
		//editor.putBoolean("BATTERYCHANGE", change);
		editor.putBoolean("CPU", cpu);
		editor.putBoolean("UPTIME", uptime);
		editor.putBoolean("MEMORYTITLE", memoryTitle);
		editor.putBoolean("MEMORY", memory);
		editor.putBoolean("RAM", ram);
		editor.putBoolean("NETWORKTITLE", networkTitle);
		editor.putBoolean("NETWORKTYPE", networkType);
		editor.putBoolean("NETWORKUP", networkUp);
		editor.putBoolean("NETWORKDOWN", networkDown);
		
		//boolean to tell the app to start updating again
		editor.putBoolean("UPDATE", true);
		editor.commit(); 
		//finishing up, and calling onupdate
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		appWidgetManager.updateAppWidget(widgetID, views);
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
		setResult(RESULT_OK, resultValue);
		Toast.makeText(this, "Widget Saved! Thank you for using Stats Monitor", Toast.LENGTH_LONG).show();
		finish();
				
		
	}
	
	public void TextBackSettings (View view)
	{
		Intent TextBackSettings = new Intent(ConfigureWidget.this, TextBackSettings.class);
		startActivity(TextBackSettings);
	}
	
	public void advancedSettings(View view)
	{
		Intent advanced = new Intent(ConfigureWidget.this, AdvancedSettings.class);
		startActivity(advanced);
	}
	
	@Override
	public void onBackPressed() 
	{
		//boolean to tell the app to start updating again
		SharedPreferences prefs = context.getSharedPreferences("MyPrefs", 0);
		Editor editor = prefs.edit();
				editor.putBoolean("UPDATE", true);
				editor.commit();
				
				finish();
	}
	
	

}
