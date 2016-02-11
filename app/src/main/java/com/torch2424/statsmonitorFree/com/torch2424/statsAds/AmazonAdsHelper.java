package com.torch2424.statsmonitorFree.com.torch2424.statsAds;

import android.app.Activity;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.DefaultAdListener;
import com.amazon.device.ads.InterstitialAd;
import com.torch2424.statsmonitorwidgetFree.R;

/**
 * Created by aaron on 2/9/16.
 */
public class AmazonAdsHelper {

    //Our application key
    //MUST ADD ACTUAL KEY BEFORE BUILDS
    private final String appKey = "0123456789ABCDEF0123456789ABCDEF";

    //Our Ad layout
    AdLayout adView;

    //Our Activity we are inserting ads
    Activity activity;

    //Our interestrial
    private InterstitialAd interstitialAd;

    //Our constructor
    public AmazonAdsHelper(Activity main) {

        //Set our activity
        activity = main;

        AdRegistration.setAppKey(appKey);

        // Programmatically set the AmazonAdLayout
        adView = (AdLayout) activity.findViewById(R.id.adview);

        //AdTargetingOptions adOptions = new AdTargetingOptions();
        // Optional: Set ad targeting options here.

        // Retrieves an ad on background thread
        adView.loadAd();



        //Start loading our interestrial
        // Create the interstitial.
        interstitialAd = new InterstitialAd(activity);

        // Set the listener to use the callbacks below.
        interstitialAd.setListener(new MyCustomAdListener());

        // Load the interstitial.
        interstitialAd.loadAd();


    }

    //Function to destory our ads onDestory
    public void destroyAds() {
        adView.destroy();
    }


    class MyCustomAdListener extends DefaultAdListener
    {
        @Override
        public void onAdLoaded(Ad ad, AdProperties adProperties)
        {
            if (ad == interstitialAd)
            {
                // Show the interstitial ad to the app's user.
                // Note: While this implementation results in the ad being shown
                // immediately after it has finished loading, for smoothest user
                // experience you will generally want the ad already loaded
                // before it’s time to show it. You can thus instead set a flag
                // here to indicate the ad is ready to show and then wait until
                // the best time to display the ad before calling showAd().
                interstitialAd.showAd();
            }
        }

        @Override
        public void onAdFailedToLoad(Ad ad, AdError error)
        {
            // Simply try to show again
            interstitialAd.loadAd();
        }

        @Override
        public void onAdDismissed(Ad ad)
        {
            //Do NOthing if dismissed
        }
    }

}