package com.torch2424.simplemonitor;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.RemoteViews;

import com.torch2424.statsmonitorwidget.R;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by torch2424 on 1/24/16.
 */
public class DiskSpace {

    //Declaring a constant I will be using for megabyte conversion
    final private long megs = 1048576L;

    //Our paths
    private File internalPath;
    private File externalPath;

    //Boolean to tell if we have a singular storage
    private boolean oneStorage;

    //Boolean to tell if we want out memory in Gigabytes or Megs
    boolean memoryGB;

    //Our deicimal format for gigabyte display
    final private DecimalFormat format = new DecimalFormat("0.00");

    //Our views
    RemoteViews views;

    public DiskSpace(RemoteViews theView, boolean memGB, String inputExternal) {

        //Get our paths

        //Internal
        internalPath = Environment.getDataDirectory();

        //External
        //False until proven otherwise
        oneStorage = false;

        //Check if we have an external string
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
                Environment.getExternalStorageDirectory() != Environment.getDataDirectory()) {
            externalPath = Environment.getExternalStorageDirectory();
        }
        //We actually did only have a single storage
        else {
            oneStorage = true;
        }

        //Set our view
        views = theView;

        //Our memory status
        memoryGB = memGB;
    }

    public void diskSpace ()
    {


        //declaring external and internal statfs
        StatFs externalStat = new StatFs(externalPath.getAbsolutePath());
        StatFs internalStat = new StatFs(internalPath.getAbsolutePath());

        //declaring get block sizes
        long blockSizeEx = externalStat.getBlockSizeLong();
        long blockSizeIn = internalStat.getBlockSizeLong();


        //available external storage in megabytes
        long availableBlocksEx = externalStat.getAvailableBlocksLong();
        long availableExternal = ((blockSizeEx) * (availableBlocksEx)) / megs;

        //total external storage in megabytes
        long totalBlocksEx = externalStat.getBlockCountLong();
        long totalExternal = ((blockSizeEx) * (totalBlocksEx)) / megs;

        //available internal storage in megs
        long availBlocksIn = internalStat.getAvailableBlocksLong();
        long availInternal = ((blockSizeIn) * (availBlocksIn)) / megs;

        //total blocks in internal storage in megs
        long totalBlocksIn = internalStat.getBlockCountLong();
        long totalInternal = ((blockSizeIn) * (totalBlocksIn)) / megs;

        //Gigabyte display settings
        if (memoryGB == true)
        {
            //use a decimal format to only show two places, and use .0 to get a decimal
            float usedFloatInternal =  ((totalInternal - availInternal)/1000.0f);
            float totalFloatInternal = (totalInternal/1000.0f);
            float usedFloatExternal =  ((totalExternal - availableExternal)/1000.0f);
            float totalFloatExternal = (totalExternal/1000.0f);
            DecimalFormat format = new DecimalFormat("0.00");
            views.setTextViewText(R.id.internalTitle, "Used Internal Storage:");
            views.setTextViewText(R.id.externalTitle, "Used External Storage:");
            views.setTextViewText(R.id.internal, format.format(usedFloatInternal) + "/" + format.format(totalFloatInternal) + " GB");
            views.setTextViewText(R.id.external, format.format(usedFloatExternal) + "/" + format.format(totalFloatExternal) + " GB");
        }
        else
        {
            //setting up text views
            views.setTextViewText(R.id.internalTitle, "Used Internal Storage:");
            views.setTextViewText(R.id.externalTitle, "Used External Storage:");
            views.setTextViewText(R.id.internal, Long.toString(totalInternal - availInternal) + "/" + Long.toString(totalInternal) + " MB");
            views.setTextViewText(R.id.external, Long.toString(totalExternal - availableExternal) + "/" + Long.toString(totalExternal) + " MB");
        }

    }

    //Function to set the internal storage of the device
    public void getInternalSpace() {

        //declaring internal statfs
        StatFs internalStat = new StatFs(internalPath.getAbsolutePath());

        //declaring get block sizes
        long blockSizeIn = internalStat.getBlockSizeLong();

        //available internal storage in megs
        long availBlocksIn = internalStat.getAvailableBlocksLong();
        long availInternal = ((blockSizeIn) * (availBlocksIn)) / megs;

        //total blocks in internal storage in megs
        long totalBlocksIn = internalStat.getBlockCountLong();
        long totalInternal = ((blockSizeIn) * (totalBlocksIn)) / megs;

        //Gigabyte display settings
        if (memoryGB == true)
        {
            //use a decimal format to only show two places, and use .0 to get a decimal
            float usedFloatInternal = getGigs(totalInternal - availInternal);
            float totalFloatInternal = getGigs(totalInternal);

            //Check if we only have one storage
            views.setTextViewText(R.id.internalTitle, "Used Internal Storage:");
            views.setTextViewText(R.id.internal, format.format(usedFloatInternal) + "/" + format.format(totalFloatInternal) + " GB");
        }
        else
        {
            //setting up text views
            views.setTextViewText(R.id.internalTitle, "Used Internal Storage:");
            views.setTextViewText(R.id.internal, Long.toString(totalInternal - availInternal) + "/" + Long.toString(totalInternal) + " MB");
        }
    }


    //Function to convert from megabytes to gigabytes
    private float getGigs(long megs) {
        return megs / 1000.0f;
    }
}
