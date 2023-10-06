package io.seventigers.gameslot.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import io.seventigers.gameslot.R;
import io.seventigers.gameslot.di.GlobalModule;

public class ConsentActivity extends AppCompatActivity {


    private static final int PERMISSION_CODE = 100;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editSharePref;
    private WebView wvDataPolicy;
    private Button btnAccept, btnDecline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);
        sharedPreferences = getSharedPreferences(GlobalModule.appCode, MODE_PRIVATE);
        editSharePref = sharedPreferences.edit();

        wvDataPolicy = findViewById(R.id.wvPolicyContent);
        btnAccept = findViewById(R.id.btnAcceptPolicy);
        btnDecline = findViewById(R.id.btnDeclinePolicy);

        showDataPolicy();
    }

    private void showDataPolicy() {
        wvDataPolicy.setWebViewClient(new WebViewClient());
        wvDataPolicy.loadUrl(GlobalModule.policyURL);
        btnAccept.setOnClickListener(btnAccepted -> {
            if (!checkPermission()) {
                requestPermission();
            } else {
                showUserConsent();
            }
        });

        btnDecline.setOnClickListener(btnDeclined -> {
            editSharePref.putBoolean("runOnce", false);
            editSharePref.apply();
            editSharePref.commit();
            finishAffinity();
        });
    }

    private boolean checkPermission() {
        int mediaPermission;
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mediaPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            mediaPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return locationPermission == PackageManager.PERMISSION_GRANTED
                && cameraPermission == PackageManager.PERMISSION_GRANTED
                && mediaPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
            }, PERMISSION_CODE);

        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, PERMISSION_CODE
            );
        }

        showUserConsent();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
                editSharePref.putBoolean("locationGranted", true);
                editSharePref.putBoolean("cameraGranted", true);
                editSharePref.putBoolean("mediaGranted", true);
                editSharePref.putBoolean("runOnce", true);
            }
        } else {

            editSharePref.putBoolean("locationGranted", false);
            editSharePref.putBoolean("cameraGranted", false);
            editSharePref.putBoolean("mediaGranted", false);
            editSharePref.putBoolean("runOnce", false);

        }
        editSharePref.apply();
        editSharePref.commit();

        showUserConsent();

    }

    private void showUserConsent() {
        if (!sharedPreferences.getBoolean("permitSendData", false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("User Data Consent");
            builder.setMessage(R.string.data_consent);
            builder.setPositiveButton("Accept", (acceptDialog, i) -> {
                editSharePref.putBoolean("permitSendData", true);
                editSharePref.apply();
                editSharePref.commit();
                acceptDialog.dismiss();
                startApp();

            });
            builder.setNegativeButton("Don't send Data", (rejectDialog, i) -> {
                editSharePref.putBoolean("permitSendData", false);
                editSharePref.apply();
                editSharePref.commit();
                rejectDialog.dismiss();
                startApp();
            });
            builder.show();
        }
        else{
            startApp();
        }
    }

    private void startApp(){
        Intent splashIntent = new Intent(this, SplashActivity.class);
        splashIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(splashIntent);
        finish();
    }
}