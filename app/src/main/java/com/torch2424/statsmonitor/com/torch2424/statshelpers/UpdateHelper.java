package com.torch2424.statsmonitor.com.torch2424.statshelpers;

import android.widget.RemoteViews;

/**
 * Created by aaron on 3/4/16.
 */

//Parent class for all the helper widget updater classes
public class UpdateHelper {

    //Set our view
    RemoteViews views;


    //Our constructor
    public UpdateHelper(RemoteViews parentView) {

        //Simply set the views
        views = parentView;

    }

    //Function to set views
    public void setViews(RemoteViews input) {
        views = input;
    }

}
