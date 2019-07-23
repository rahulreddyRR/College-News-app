package com.rahul.newsdroid;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

public class SportsHunt extends Application {

    public static final String NIGHT_MODE = "NIGHT_MODE";
    private boolean isNightModeEnabled = false;
    private static SportsHunt singleton = null;

    public static SportsHunt getInstance() {

        if(singleton == null)
        {
            singleton = new SportsHunt();
        }
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseMessaging.getInstance().subscribeToTopic("post_push");

        //receivePushNotification();

        CustomFontUtils.setDefaultFont(this, "DEFAULT", "fonts/font_regular.ttf");
        CustomFontUtils.setDefaultFont(this, "MONOSPACE", "fonts/font_regular.ttf");
        CustomFontUtils.setDefaultFont(this, "SERIF", "fonts/font_regular.ttf");
        CustomFontUtils.setDefaultFont(this, "SANS_SERIF", "fonts/font_regular.ttf");
        singleton = this;
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.isNightModeEnabled = mPrefs.getBoolean(NIGHT_MODE, false);

    }

    public void receivePushNotification(){
        SharedPreferences sharedpreferences = getSharedPreferences("SHARE_SETTINGS", MODE_PRIVATE);
        boolean state = sharedpreferences.getBoolean("statePush", true);
        Log.d("MyNotificationState", "FCM Token: " + state);
        if (state){
            FirebaseMessaging.getInstance().subscribeToTopic("post_push");
        }else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("post_push");
        }
    }
}