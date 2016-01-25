package com.torch2424.statsmonitor;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.widget.RemoteViews;

import com.torch2424.statsmonitor.com.torch2424.statshelpers.BatteryHelper;
import com.torch2424.statsmonitor.com.torch2424.statshelpers.MemoryHelper;
import com.torch2424.statsmonitor.com.torch2424.statshelpers.TimeHelper;
import com.torch2424.statsmonitorwidget.R;


@SuppressLint("SimpleDateFormat")
public class SmAlarm extends BroadcastReceiver 
{
	//creating remote views out here for access anywhere in class
		RemoteViews views;
	//getting access to the config values
		ConfigureWidget config = new ConfigureWidget();

		//getting prefs and boolean values for which sections to display
		SharedPreferences prefs;
		boolean boolTimeTitle;
		boolean boolTime;
		boolean boolDate;
		boolean boolSystemTitle;
		boolean boolBattery;
		boolean boolTemp;
		boolean boolChange;
		boolean boolCpu;
		boolean boolUptime;
		boolean boolMemoryTitle;
        //Boolean for if there is an internal and external storage
        boolean isExternalStorage;
		boolean boolMemory;
		boolean boolRam;
		boolean boolNetworkTitle;
		boolean boolNetworkType;
		boolean boolNetworkUp;
		boolean boolNetworkDown;

		//advanced settings
		boolean tapConfig;
		boolean hourFormat;
		boolean rightBool;
		boolean centerBool;
		boolean CPUBool;
		boolean TitleBool;
		boolean shortBool;
		boolean kilobytesBool;
		boolean threeBool;
		boolean fiveBool;
		boolean degreesFBool;
		int secs; //for if people want a slower update interval


		//setting up colors
		int textColor;
		int backColor;


		//setting up text sizes
		int textSize;
		int textTitleSize;
	
		//for battery section
		float percent;
		
		
		//for config activity
		boolean updateBool;
		
		
		
		public void configPrefs(Context context)
		{
			//getting preferences, can't use intents, don't work with broadcast reciever
			prefs = context.getSharedPreferences("MyPrefs", 0);
			boolTimeTitle = prefs.getBoolean("TIMETITLE", true);
			boolTime = prefs.getBoolean("TIME", true);
			boolDate = prefs.getBoolean("DATE", true);
			boolSystemTitle = prefs.getBoolean("SYSTEMTITLE", true);
			boolBattery = prefs.getBoolean("BATTERY", true);
			boolTemp = prefs.getBoolean("BATTERYTEMP", true);
			boolChange = prefs.getBoolean("BATTERYCHANGE", true);
			boolCpu = prefs.getBoolean("CPU", true);
			boolUptime = prefs.getBoolean("UPTIME", true);
			boolMemoryTitle = prefs.getBoolean("MEMORYTITLE", true);
			boolMemory = prefs.getBoolean("MEMORY", true);
			boolRam = prefs.getBoolean("RAM", true);
			boolNetworkTitle = prefs.getBoolean("NETWORKTITLE", true);
			boolNetworkType = prefs.getBoolean("NETWORKTYPE", true);
			boolNetworkUp = prefs.getBoolean("NETWORKUP", true);
			boolNetworkDown = prefs.getBoolean("NETWORKDOWN", true);
			
			
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
			CPUBool = prefs.getBoolean("MULTICPU", false);
			TitleBool = prefs.getBoolean("NOCPUTITLE", false);
			kilobytesBool = prefs.getBoolean("KILOBYTE", false);
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
			if(centerBool == true)
			{
				views.setInt(R.id.widgetLayout, "setGravity", Gravity.CENTER);
			}
			else if (rightBool == true)
			{
				views.setInt(R.id.widgetLayout, "setGravity", Gravity.RIGHT);
			}
			else
			{
				views.setInt(R.id.widgetLayout, "setGravity", Gravity.LEFT);
			}
			//views.setfloat to set text sizes, but cant be set to zero!
			//if the preference from the checkbox is false, set the text visibility to gone, external sd check
			 if(boolTimeTitle == false)
			 {
				 views.setViewVisibility(R.id.timeTitle, View.GONE);
				 
			 }
			 else
			 {
				 views.setViewVisibility(R.id.timeTitle, View.VISIBLE);
				 
			 }
			 if (boolSystemTitle == false)
			 {
				 views.setViewVisibility(R.id.systemTitle, View.GONE); 
			 }
			 else
			 {
				 views.setViewVisibility(R.id.systemTitle, View.VISIBLE); 
			 }
			 if (boolMemoryTitle == false)
			 {
				 views.setViewVisibility(R.id.memoryTitle, View.GONE); 
			 }
			 else
			 {
				 views.setViewVisibility(R.id.memoryTitle, View.VISIBLE); 
			 }
			 if (boolTime == false)
			 {
				 views.setViewVisibility(R.id.time, View.GONE); 
			 }
			 else
			 {
				 views.setViewVisibility(R.id.time, View.VISIBLE); 
			 }
			 if (boolDate == false)
			 {
				 views.setViewVisibility(R.id.date, View.GONE); 
			 }
			 else
			 {
				 views.setViewVisibility(R.id.date, View.VISIBLE); 
			 }
			 if(boolBattery == false)
			 {
				 views.setViewVisibility(R.id.battery, View.GONE); 
			 }
			 else
			 {
				 views.setViewVisibility(R.id.battery, View.VISIBLE); 
			 }
			 if(boolTemp == false)
			 {
				 views.setViewVisibility(R.id.batteryTemp, View.GONE); 
			 }
			 else
			 {
				 views.setViewVisibility(R.id.batteryTemp, View.VISIBLE); 
			 }
			 if(boolCpu == false)
			   {
				   views.setViewVisibility(R.id.cpu, View.GONE); 
			   }
			 else
			   {
				   views.setViewVisibility(R.id.cpu, View.VISIBLE); 
			   }
			 if (boolUptime == false)
				{
					views.setViewVisibility(R.id.uptime, View.GONE); 
				}
			 else
				{
					views.setViewVisibility(R.id.uptime, View.VISIBLE); 
				}
			 if(boolMemory == false)
		        {

                    //Set all memory views to false
		        	views.setViewVisibility(R.id.internal, View.GONE); 
		        	views.setViewVisibility(R.id.internalTitle, View.GONE);
                    views.setViewVisibility(R.id.external, View.GONE);
                    views.setViewVisibility(R.id.externalTitle, View.GONE);
                }
			 else
		        {

                    //Set the views to visible
		        	views.setViewVisibility(R.id.internal, View.VISIBLE);
		        	views.setViewVisibility(R.id.internalTitle, View.VISIBLE);
                    //Set the views to visible
                    views.setViewVisibility(R.id.external, View.VISIBLE);
                    views.setViewVisibility(R.id.externalTitle, View.VISIBLE);
                }
		        if(boolRam == false)
		        {
		        	views.setViewVisibility(R.id.ram, View.GONE); 
		        }
		        else
		        {
		        	views.setViewVisibility(R.id.ram, View.VISIBLE); 
		        }
		        
		        if(boolNetworkTitle)
		        {
		        	views.setViewVisibility(R.id.networkTitle, View.VISIBLE); 
		        }
		        else
		        {
		        	views.setViewVisibility(R.id.networkTitle, View.GONE); 
		        }
		        
		        if(boolNetworkType)
		        {
		        	views.setViewVisibility(R.id.networkType, View.VISIBLE); 
		        }
		        else
		        {
		        	views.setViewVisibility(R.id.networkType, View.GONE); 
		        }
		        
		        if(boolNetworkUp)
		        {
		        	views.setViewVisibility(R.id.networkUp, View.VISIBLE); 
		        }
		        else
		        {
		        	views.setViewVisibility(R.id.networkUp, View.GONE); 
		        }
		        
		        if(boolNetworkDown)
		        {
		        	views.setViewVisibility(R.id.networkDown, View.VISIBLE); 
		        }
		        else
		        {
		        	views.setViewVisibility(R.id.networkDown, View.GONE); 
		        }
		        
		}
		
		public void widgetCpu() 
		{
			//if multi core cpu support is enabled
			if(CPUBool == true)
			{
				String CPUString = "";
				float[] coreValues = new float[10];
				//get how many cores there are from function
				//need to add 1 here and subtract one from second loop to get one extra and hopefully fix 4th core 100%
				//maybe the bulk/lack of sleep time to find core is what is making it always 100%
				int numCores = getNumCores();
				for(byte i = 0; i < numCores; i++)
				{
					coreValues[i] = readCore(i);
					//sleep a little to get accurate readings
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				for(byte j = 0; j < numCores; j++)
				{
					int percent = (int) (coreValues[j] * 100);
					if (j == 0)
					{
						CPUString = CPUString + "Core " + Integer.toString(j + 1) + ": " + percent + "%";
					}
					else
					{
						CPUString = CPUString + "\nCore " + Integer.toString(j + 1) + ": " + percent + "%"; 
					}
				}
				
			
				//setting visibility done in update
				views.setTextViewText(R.id.cpu, CPUString);
			}
			else
			{
			//get out float from our cpu method, turn it into a percentage, and then set it to textview
		   float f = readUsage();
		   int percent = (int)(f * 100);
		   String s = Integer.toString(percent);
		   //done before update
		   //views.setViewVisibility(R.id.CPUTitle, View.GONE); 
		   views.setTextViewText(R.id.cpu, "CPU: " + s + "%");
			}
		}
		
		//for single cpu value
		private float readUsage() 
		{
			/*
			 * taken from stack overflow
			 * this function reads the bytes from a logging file in the android system
			 * then puts the line into a string
			 * then spilts up each individual bit into an array
			 * then(since he know which byte represnets what) he is able to determine each cpu idle and usage
			 * then combines it together to get a single float for overall cpu usage
			 */
		    try {
		        RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
		        String load = reader.readLine();
		        String[] toks = load.split(" ");

		        long idle1 = Long.parseLong(toks[5]);
		        long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
		              + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

		        try 
		        {
		            Thread.sleep(500);
		        } 
		        catch (Exception e) {}

		        reader.seek(0);
		        load = reader.readLine();
		        reader.close();
		        toks = load.split(" ");

		        long idle2 = Long.parseLong(toks[5]);
		        long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
		            + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

		        return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

		    } 
		    catch (IOException ex) 
		    {
		        ex.printStackTrace();
		    }

		    return 0;
		} 
		
		//for multi core value
		private float readCore(int i) 
		{
			/*
			 * how to calculate multicore
			 * this function reads the bytes from a logging file in the android system (/proc/stat for cpu values)
			 * then puts the line into a string
			 * then spilts up each individual part into an array
			 * then(since he know which part represents what) we are able to determine each cpu total and work
			 * then combine it together to get a single float for overall cpu usage
			 */
		    try {
		        RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
		        //skip to the line we need
		        for(int ii = 0; ii < i + 1; ++ii)
		        {
		        	reader.readLine();
		        }
		        String load = reader.readLine();
		        
		        //cores will eventually go offline, and if it does, then it is at 0% because it is not being
		        //used. so we need to do check if the line we got contains cpu, if not, then this core = 0
		        if(load.contains("cpu"))
		        {
		        String[] toks = load.split(" ");
		        
		        //we are recording the work being used by the user and system(work) and the total info
		        //of cpu stuff (total)
		        //http://stackoverflow.com/questions/3017162/how-to-get-total-cpu-usage-in-linux-c/3017438#3017438

		        long work1 = Long.parseLong(toks[1])+ Long.parseLong(toks[2]) + Long.parseLong(toks[3]);
		        long total1 = Long.parseLong(toks[1])+ Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + 
		        		Long.parseLong(toks[4]) + Long.parseLong(toks[5])
		              + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

		        try 
		        {
		        	//short sleep time = less accurate. But android devices typically don't have more than
		        	//4 cores, and I'n my app, I run this all in a second. So, I need it a bit shorter
		            Thread.sleep(200);
		        } 
		        catch (Exception e) {}

		        reader.seek(0);
		        //skip to the line we need
		        for(int ii = 0; ii < i + 1; ++ii)
		        {
		        	reader.readLine();
		        }
		        load = reader.readLine();
		      //cores will eventually go offline, and if it does, then it is at 0% because it is not being
		        //used. so we need to do check if the line we got contains cpu, if not, then this core = 0%
		        if(load.contains("cpu"))
		        {
		        	reader.close();
		        	toks = load.split(" ");

		        	long work2 = Long.parseLong(toks[1])+ Long.parseLong(toks[2]) + Long.parseLong(toks[3]);
			        long total2 = Long.parseLong(toks[1])+ Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + 
			        		Long.parseLong(toks[4]) + Long.parseLong(toks[5])
			              + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);


			        
			        //here we find the change in user work and total info, and divide by one another to get our total
			        //seems to be accurate need to test on quad core
			        //http://stackoverflow.com/questions/3017162/how-to-get-total-cpu-usage-in-linux-c/3017438#3017438
			        
		        	return (float)(work2 - work1) / ((total2 - total1));
		        }
		        else
		        {
		        	reader.close();
		        	return 0;
		        }
		        
		        }
		        else
		        {
		        	reader.close();
		        	return 0;
		        }

		    } 
		    catch (IOException ex) 
		    {
		        ex.printStackTrace();
		    }

		    return 0;
		} 
		
		/*
		 * Gets the number of cores available in this device, across all processors.
		 * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
		 * @return The number of cores, or 1 if failed to get result
		 * gotten from stack overflow: http://stackoverflow.com/questions/7962155/how-can-you-detect-a-dual-core-cpu-on-an-android-device-from-code
		 */
		private int getNumCores() 
		{
		    //Private Class to display only CPU devices in the directory listing
		    class CpuFilter implements FileFilter 
		    {

		        @Override
		        public boolean accept(File pathname) 
		        {
		            //Check if filename is "cpu", followed by a single digit number
		            if(Pattern.matches("cpu[0-9]+", pathname.getName())) 
		            {
		                return true;
		            }
		            return false;
		        }
		    }

		    try {
		        //Get directory containing CPU info
		        File dir = new File("/sys/devices/system/cpu/");
		        //Filter to only list the devices we care about
		        File[] files = dir.listFiles(new CpuFilter());
		        //Return the number of cores (virtual CPU devices)
		        return files.length;
		    } catch(Exception e) {
		        //Default to return 1 core
		        return 1;
		    }
		}
	
		//method to get network type
        public void networkType(Context context)
        {
            //gotten from stack
            //http://stackoverflow.com/questions/2919414/get-network-type
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //need to check for tablets to see if they have telephony
            boolean hasTelephony = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);

            if(hasTelephony)
            {

            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            //mobile
            State mobile = conMan.getNetworkInfo(0).getState();

            //if mobile dtata is conntected
            if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING)
            {

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


            //WIFI SECTION
            //for wifi name
            WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            //wifi
            State wifi = conMan.getNetworkInfo(1).getState();

            //if wifi is connected
            if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)
            {
                views.setTextViewText(R.id.networkType, wifiInfo.getSSID());
            }

        }
	
	//method to get network speed
	public void networkSpeeds()
	{
		//use traffic stats to get bytes since device boot
		//checking for traffic stats report
		
		//use our preferences as a quick and easy way to store the previous value
		Editor editor = prefs.edit();
		//get the current bytes
		long speedD = TrafficStats.getTotalRxBytes();
		long speedU = TrafficStats.getTotalTxBytes();
		long prevD;
		long prevU;
		double down = 0;
		double up = 0;
		int runCount;
		

		if(TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED || TrafficStats.getTotalTxBytes() ==
				TrafficStats.UNSUPPORTED)
		{
			views.setTextViewText(R.id.networkDown, "Sorry, but Traffic Stats is unsupported by your device");
			views.setTextViewText(R.id.networkUp, "Sorry, but Traffic Stats is unsupported by your device");
		}
		else
		{

			//use our preferences as a quick and easy way to store and get the previous value
			//and time
			prevD = prefs.getLong("PREVD", 0);
			prevU = prefs.getLong("PREVU", 0);
			runCount = prefs.getInt("RUNCOUNT", 0);
			if(prevD == 0 && prevU == 0 && runCount == 0)
			{
				//store the previous values without setting down or up
				editor.putLong("PREVD", speedD);
				editor.putLong("PREVU", speedU);
				editor.putInt("RUNCOUNT", 1);
				editor.commit();
			}
			else
			{
				if(runCount > 2)
				{
				//set the values while committing the new value
				//setting precision for values
				DecimalFormat format = new DecimalFormat("0.00");
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
				//saving our previous values
				editor.putLong("PREVD", 0);
				editor.putLong("PREVU", 0);
				editor.putInt("RUNCOUNT", 0);
				editor.commit();
				
				}
				else
				{
					runCount++;
					editor.putInt("RUNCOUNT", runCount);
					editor.commit();
				}
			}
		
		}
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

        //Create diskspace manager
        MemoryHelper diskMan = new MemoryHelper(views, prefs);

		//call time methods, not calling if unchecked
		if(boolTime || boolDate)
		{
            timeMan.getTime();
		}
		
		//call system methods
		if(boolBattery || boolTemp)
		{
			battMan.getBatteryPercent(context);
            battMan.getBatteryTemp(context);
		}
		
		if(boolCpu)
		{
			widgetCpu();
		}
		
		if(boolUptime)
		{
		    timeMan.getUptime();
		}
		
		//call memory methods
		if(boolMemory)
		{
            diskMan.getSpace();
		}
		
		if(boolRam)
		{
			diskMan.getRam(context);
		}
		
		//call netowork methods
		if(boolNetworkType)
		{
		    networkType(context);
		}
		
		if(boolNetworkUp || boolNetworkDown)
		{
		    networkSpeeds();
		}
		
		
		
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
