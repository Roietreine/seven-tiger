package io.seventigers.gameslot.di;

import android.app.Application;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class GlobalModule extends Application {
    private static Application appJS = new Application();
    private RequestQueue requestApi;
    
    private FirebaseRemoteConfig remoteConfig;

    public static final String appCode = "777T104"; // Client specific code for app
    public static String appFlyersId = "";
    public static String apiURL = "";
    public static String policyURL = "";
    public static String gameURL = "";
    public static String jsInterface = "";
    public static String appFlyerAPI = "";

    public static Boolean permitSendData;

    public SharedPreferences sharedPref;

    @Override
    public void onCreate() {
        super.onCreate();

        appJS = this;
        FirebaseApp.initializeApp(this);
        initRemoteConfig();
        sharedPref = getSharedPreferences(appCode,MODE_PRIVATE);
    }

    public static synchronized GlobalModule getInstance(){ return (GlobalModule) appJS; }

    private void initRemoteConfig(){
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(2800)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
    }

    public RequestQueue getRequest(){
        if(requestApi == null)
            requestApi = Volley.newRequestQueue(getApplicationContext());
        return requestApi;

    }

}
