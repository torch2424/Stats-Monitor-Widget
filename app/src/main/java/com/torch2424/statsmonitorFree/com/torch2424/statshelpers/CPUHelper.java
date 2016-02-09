package com.torch2424.statsmonitorFree.com.torch2424.statshelpers;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;

import com.torch2424.statsmonitorwidgetFree.R;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Pattern;

/**
 * Created by torch2424 on 1/25/16.
 */
public class CPUHelper {

    //Set our view
    RemoteViews views;

    //Our boolean for multiCore support
    boolean multiCore;

    //Our number of cores
    int numCores;

    //Our view status
    boolean viewCpu;

    public CPUHelper(RemoteViews parentView, SharedPreferences prefs) {

        //Set our view
        views = parentView;

        //Get our multicore option
        multiCore = prefs.getBoolean("MULTICPU", false);

        //Get the number of cpu cores that we have, if we want multi core support
        if(multiCore) {
            numCores = getNumCores();
        }

        //Get cpu view
        viewCpu = prefs.getBoolean("CPU", true);

        if(viewCpu) views.setViewVisibility(R.id.cpu, View.VISIBLE);
        else views.setViewVisibility(R.id.cpu, View.GONE);
    }

    /*
     * Gets the number of cores available in this device, across all processors.
     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     * @return The number of cores, or 1 if failed to get result
     * gotten from stack overflow: http://stackoverflow.com/questions/7962155/how-can-you-detect-a-dual-core-cpu-on-an-android-device-from-code
     */

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

    private int getNumCores()
    {

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


    public void getCpuUsage()
    {
        //if multi core cpu support is enabled
        if(!multiCore)
        {
            //SINGLE CPU

            //get out float from our cpu method, turn it into a percentage, and then set it to textview
            float f = readUsage();
            int percent = (int)(f * 100);
            String s = Integer.toString(percent);
            views.setTextViewText(R.id.cpu, "CPU: " + s + "%");
        }
        else
        {

            //MultiCore CPU
            String CPUString = "";
            float[] coreValues = new float[10];
            //get how many cores there are from function (Retrieved in constructor)
            //need to add 1 here and subtract one from second loop to get one extra and hopefully fix 4th core 100%
            //maybe the bulk/lack of sleep time to find core is what is making it always 100%
            for(byte i = 0; i < numCores; i++)
            {
                coreValues[i] = readCore(i);
                //sleep a little to get accurate readings
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //Simply assign our string now
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
                Thread.sleep(250);
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
                    Thread.sleep(50);
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

    //Return the status of the cpu section
    public boolean cpuStatus() {
        if(viewCpu) return true;
        else return false;
    }
}
