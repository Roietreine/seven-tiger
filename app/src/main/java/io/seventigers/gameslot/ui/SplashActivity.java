package io.seventigers.gameslot.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.remoteconfig.ConfigUpdate;
import com.google.firebase.remoteconfig.ConfigUpdateListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONException;
import org.json.JSONObject;


import io.seventigers.gameslot.R;
import io.seventigers.gameslot.utilities.VolleyHelper;
import io.seventigers.gameslot.di.GlobalModule;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    public static String splashActivityTAG = "FirebaseRemoteConfig";

    public static String apiResponse = "ApiResponse";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the title bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        // Enable fullscreen mode
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        setContentView(R.layout.activity_splash);

        splashScreenDelay();
        firebaseRemoteConfig();

    }

    private void splashScreenDelay(){
        new Handler().postDelayed(() -> {
        }, 3000);
    }

    public void firebaseRemoteConfig() {
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.setDefaultsAsync(R.xml.default_config);
        remoteConfig.fetchAndActivate().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                boolean updated = task.getResult();
                Toast.makeText(this, "Able to get the config", Toast.LENGTH_SHORT).show();
                Log.d(splashActivityTAG, "Remote Config updated:" + updated);
            } else {
                Toast.makeText(this, "Unable to get the config", Toast.LENGTH_SHORT).show();
                Log.d(splashActivityTAG, "Unable to get Config succesfully");
            }
            setConfig();
        });


        remoteConfig.addOnConfigUpdateListener(new ConfigUpdateListener() {
            @Override
            public void onUpdate(@NonNull ConfigUpdate configUpdate) {
                setConfig();
                Log.d(splashActivityTAG, "Updated Config:" + configUpdate);
            }

            @Override
            public void onError(FirebaseRemoteConfigException error) {
                setConfig();
                Log.d(splashActivityTAG, error.getLocalizedMessage());
            }
        });
    }

    private void setConfig() {
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        GlobalModule.appFlyersId = remoteConfig.getString("appFlyersId");
        GlobalModule.apiURL = remoteConfig.getString("apiURL");
        GlobalModule.jsInterface = remoteConfig.getString("jsInterface");
        GlobalModule.policyURL = remoteConfig.getString("policyURL");
        GlobalModule.appFlyerAPI = remoteConfig.getString("appFlyerAPI");
        networkCall();
    }

    private void networkCall() {
        VolleyHelper.init(this);
        RequestQueue requestQueue = VolleyHelper.getRequestQueue();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("appid", GlobalModule.apiURL);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String endPoint = GlobalModule.apiURL + "?appid=" + GlobalModule.appCode;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, endPoint, requestBody,
                response -> {
                    Log.d(apiResponse, response.toString());
                    try {
                        GlobalModule.gameURL = response.getString("gameURL");

                        SharedPreferences sharedPref = getSharedPreferences(GlobalModule.appCode, MODE_PRIVATE);

                        if (sharedPref.getBoolean("runOnce", false)) {

                            Intent webApp = new Intent(this, MainActivity.class);
                            webApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(webApp);
                            finish();

                        } else {

                            Intent dataPolicy = new Intent(this, ConsentActivity.class);
                            dataPolicy.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(dataPolicy);
                            finish();
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }, error -> {

            Log.d(apiResponse, error.getMessage());
        });

        requestQueue.add(jsonObjectRequest);

    }
}