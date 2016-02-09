package com.torch2424.statsmonitor.com.torch2424.statshelpers;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.torch2424.statsmonitorwidget.R;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by torch2424 on 1/24/16.
 */
public class MemoryHelper {

    //Declaring a constant I will be using for megabyte conversion
    final private long megs = 1048576L;

    //Our paths
    private File internalPath;
    private File externalPath;

    //Boolean to tell if we have a singular storage
    private boolean oneStorage;

    //Memory manager for ram
    ActivityManager.MemoryInfo mi;

    //Boolean to tell if we want out memory in Gigabytes or Megs
    boolean memoryGB;
    boolean ramGB;

    //Boolean to tell if we want used or free memory
    boolean usedToFree;

    //Our decimal format for gigabyte display
    final private DecimalFormat format = new DecimalFormat("0.00");

    //Our views
    RemoteViews views;

    //Our view boolean
    boolean memoryView;
    boolean ramView;

    public MemoryHelper(RemoteViews parentView, SharedPreferences prefs) {


        //Set our view
        views = parentView;

        //Show and hide our views accordingly to preferecnes

        //Our memory status
        memoryGB = prefs.getBoolean("MEMORYGB", false);
        ramGB = prefs.getBoolean("RAMGB", false);
        usedToFree = prefs.getBoolean("USEDTOFREE", false);

        //Our external String
        String inputExternal = prefs.getString("EXTERNALPATH", "");

        //Get our paths

        //Internal
        internalPath = Environment.getDataDirectory();

        //External
        //False until proven otherwise
        oneStorage = false;

        //Check if we have an external string entered by the user
        if(inputExternal.equals("") == false && new File(inputExternal).exists())
        {
            externalPath = new File(inputExternal);
        }
        //Check if our Secondary storage exists
        else if(externalPath == null &&
                System.getenv("SECONDARY_STORAGE") != null) {

            //Save our secondary storage string
            String secStore = System.getenv("SECONDARY_STORAGE");

            //Check if it exists, and does not equal the internal storage
            if(new File(secStore).exists() &&
                    new File(secStore) != Environment.getDataDirectory()) {
                //Save our path
                externalPath = new File(secStore);
            }
        }
        //Check if our environment returns the correct path
        else if(externalPath == null &&
                Environment.getExternalStorageDirectory().exists() &&
                Environment.getExternalStorageDirectory().getTotalSpace() != Environment.getDataDirectory().getTotalSpace()) {

            externalPath = Environment.getExternalStorageDirectory();
        }
        //We actually did only have a single storage
        else {

            oneStorage = true;

            //Set external views to not visible
            views.setViewVisibility(R.id.external, View.GONE);
            views.setViewVisibility(R.id.externalTitle, View.GONE);
        }

        //Now check if the external storage has space, if it doesnt, we have one storage
        if(!oneStorage && !checkExternal()) {

            oneStorage = true;

            //Set external views to not visible
            views.setViewVisibility(R.id.external, View.GONE);
            views.setViewVisibility(R.id.externalTitle, View.GONE);
        }

        //Get Ram Stuff
        mi = new ActivityManager.MemoryInfo();

        //Finally get our views
        memoryView = prefs.getBoolean("MEMORY", true);
        ramView = prefs.getBoolean("RAM", true);
        if(memoryView)
        {
            //Set the views to visible
            views.setViewVisibility(R.id.internal, View.VISIBLE);
            views.setViewVisibility(R.id.internalTitle, View.VISIBLE);

            //Set the external views to visible if one storage
            if(!oneStorage) {
                views.setViewVisibility(R.id.external, View.VISIBLE);
                views.setViewVisibility(R.id.externalTitle, View.VISIBLE);
            }
        }
        else
        {
            //Set all memory views to false
            views.setViewVisibility(R.id.internal, View.GONE);
            views.setViewVisibility(R.id.internalTitle, View.GONE);
            views.setViewVisibility(R.id.external, View.GONE);
            views.setViewVisibility(R.id.externalTitle, View.GONE);
        }

        if(ramView) views.setViewVisibility(R.id.ram, View.VISIBLE);
        else views.setViewVisibility(R.id.ram, View.GONE);
    }

    //Function to set the internal storage of the device
    public void getSpace() {

        //declaring internal statfs
        StatFs stats = new StatFs(internalPath.getAbsolutePath());

        //declaring get block sizes
        long blockSize = stats.getBlockSizeLong();

        //available internal storage in megs
        long availBlocks = stats.getAvailableBlocksLong();
        double availMB = ((blockSize) * (availBlocks)) / megs;

        //total blocks in internal storage in
        long totalBlocks = stats.getBlockCountLong();
        double totalMB = ((blockSize) * (totalBlocks)) / megs;

        //setting up text views
        //Check if we only have one storage
        if(usedToFree) {
            if(!oneStorage) views.setTextViewText(R.id.internalTitle, "Used Internal Storage:");
            else views.setTextViewText(R.id.internalTitle, "Used Storage:");
        }
        else {
            if(!oneStorage) views.setTextViewText(R.id.internalTitle, "Free Internal Storage:");
            else views.setTextViewText(R.id.internalTitle, "Free Storage:");
        }

        //Gigabyte display settings
        if (memoryGB == true)
        {
            //use a decimal format to only show two places, and use .0 to get a decimal
            if(usedToFree) {
                double availFloat = getGigs(availMB);
                double totalFloat = getGigs(totalMB);
                views.setTextViewText(R.id.internal, format.format(availFloat) + "/" + format.format(totalFloat) + " GB");
            }
            else {

                double usedFloat = getGigs(totalMB - availMB);
                double totalFloat = getGigs(totalMB);
                views.setTextViewText(R.id.internal, format.format(usedFloat) + "/" + format.format(totalFloat) + " GB");
            }

        }
        else
        {
            if(usedToFree) {
                views.setTextViewText(R.id.internal, fmt(availMB) + "/" + fmt(totalMB) + " MB");
            }
            else {

                views.setTextViewText(R.id.internal, fmt(totalMB - availMB) + "/" + fmt(totalMB) + " MB");
            }

        }

        //Now check for external storage, if we have it
        if(!oneStorage) {
            getExternalSpace();
        }
    }

    //Function to get the external space
    private void getExternalSpace() {

        //declaring external statfs
        StatFs stats = new StatFs(externalPath.getAbsolutePath());

        //declaring get block sizes
        long blockSize = stats.getBlockSizeLong();

        //available external storage in megs
        long availBlocks = stats.getAvailableBlocksLong();
        double availMB = ((blockSize) * (availBlocks)) / megs;

        //total blocks in external storage in megs
        long totalBlocks = stats.getBlockCountLong();
        double totalMB = ((blockSize) * (totalBlocks)) / megs;

        //setting up text views
        if(usedToFree) {
            views.setTextViewText(R.id.externalTitle, "Free External Storage:");
        }
        else {
            views.setTextViewText(R.id.externalTitle, "Used External Storage:");
        }

        //Gigabyte display settings
        if (memoryGB == true)
        {
            if(usedToFree) {
                //use a decimal format to only show two places, and use .0 to get a decimal
                double availFloat = getGigs(availMB);
                double totalFloat = getGigs(totalMB);
                views.setTextViewText(R.id.external, format.format(availFloat) + "/" + format.format(totalFloat) + " GB");
            }
            else {
                //use a decimal format to only show two places, and use .0 to get a decimal
                double usedFloat = getGigs(totalMB - availMB);
                double totalFloat = getGigs(totalMB);
                views.setTextViewText(R.id.external, format.format(usedFloat) + "/" + format.format(totalFloat) + " GB");
            }
        }
        else
        {
            if(usedToFree) {
                views.setTextViewText(R.id.external, fmt(availMB) + "/" + fmt(totalMB) + " MB");
            }
            else {
                views.setTextViewText(R.id.external, fmt(totalMB - availMB) + "/" + fmt(totalMB) + " MB");
            }
        }

    }

    //Function to get Ram
    public void getRam(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long avail = mi.availMem / 1048576L;
        long total = mi.totalMem / 1048576L;
        if(ramGB == true)
        {
            double usedFloat = getGigs(total - avail);
            double totalFloat = getGigs(total);
            views.setTextViewText(R.id.ram, "Used Ram: " + format.format(usedFloat) + "/" + format.format(totalFloat) + "GB");
        }
        else
        {
            views.setTextViewText(R.id.ram, "Used Ram: " + Long.toString(total - avail) + "/" + Long.toString(total) + "MB");
        }
    }

    //Function to convert from megabytes to gigabytes
    private double getGigs(double megs) {
        return megs / 1024.0;
    }

    //Function to check if the external storage has space
    private boolean checkExternal() {

        //declaring external statfs
        StatFs stats = new StatFs(externalPath.getAbsolutePath());

        //declaring get block sizes
        long blockSize = stats.getBlockSizeLong();

        //total blocks in external storage in megs
        long totalBlocks = stats.getBlockCountLong();
        long totalMB = ((blockSize) * (totalBlocks)) / megs;

        //Do a quick check if we have storage
        if(totalMB > 0) return true;
        else return false;
    }

    //Function to return formatted double without trailing zeroes
    //http://stackoverflow.com/questions/703396/how-to-nicely-format-floating-numbers-to-string-without-unnecessary-decimal-0
    public static String fmt(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }

    //Function to return view status
    public boolean memoryStatus() {
        if(memoryView) return true;
        else return false;
    }

    public boolean ramStatus() {
        if(ramView) return true;
        else return false;
    }
}
