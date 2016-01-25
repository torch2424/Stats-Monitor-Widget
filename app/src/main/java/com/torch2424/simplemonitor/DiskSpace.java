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

    //Our decimal format for gigabyte display
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

        //Now check if the external storage has space, if it doesnt, we have one storage
        if(!oneStorage && !checkExternal()) oneStorage = true;

        //Set our view
        views = theView;

        //Our memory status
        memoryGB = memGB;
    }

    //Function to set the internal storage of the device
    public void getSpace() {

        //declaring internal statfs
        StatFs stats = new StatFs(internalPath.getAbsolutePath());

        //declaring get block sizes
        long blockSize = stats.getBlockSizeLong();

        //available internal storage in megs
        long availBlocks = stats.getAvailableBlocksLong();
        long availMB = ((blockSize) * (availBlocks)) / megs;

        //total blocks in internal storage in megs
        long totalBlocks = stats.getBlockCountLong();
        long totalMB = ((blockSize) * (totalBlocks)) / megs;

        //setting up text views
        //Check if we only have one storage
        if(!oneStorage) views.setTextViewText(R.id.internalTitle, "Used Internal Storage:");
        else views.setTextViewText(R.id.internalTitle, "Used Storage:");

        //Gigabyte display settings
        if (memoryGB == true)
        {
            //use a decimal format to only show two places, and use .0 to get a decimal
            float usedFloat = getGigs(totalMB - availMB);
            float totalFloat = getGigs(totalMB);
            views.setTextViewText(R.id.internal, format.format(usedFloat) + "/" + format.format(totalFloat) + " GB");
        }
        else
        {
            views.setTextViewText(R.id.internal, Long.toString(totalMB - availMB) + "/" + Long.toString(totalMB) + " MB");
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
        long availMB = ((blockSize) * (availBlocks)) / megs;

        //total blocks in external storage in megs
        long totalBlocks = stats.getBlockCountLong();
        long totalMB = ((blockSize) * (totalBlocks)) / megs;

        //setting up text views
        views.setTextViewText(R.id.externalTitle, "Used External Storage:");

        //Gigabyte display settings
        if (memoryGB == true)
        {
            //use a decimal format to only show two places, and use .0 to get a decimal
            float usedFloat = getGigs(totalMB - availMB);
            float totalFloat = getGigs(totalMB);
            views.setTextViewText(R.id.external, format.format(usedFloat) + "/" + format.format(totalFloat) + " GB");
        }
        else
        {
            views.setTextViewText(R.id.external, Long.toString(totalMB - availMB) + "/" + Long.toString(totalMB) + " MB");
        }

    }

    //Function to convert from megabytes to gigabytes
    private float getGigs(long megs) {
        return megs / 1000.0f;
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
}
