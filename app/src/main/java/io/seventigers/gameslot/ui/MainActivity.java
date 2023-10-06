package io.seventigers.gameslot.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import io.seventigers.gameslot.R;
import io.seventigers.gameslot.di.GlobalModule;
import io.seventigers.gameslot.utilities.JsInterface;

public class MainActivity extends AppCompatActivity {

    private boolean bckExit = false;
    WebView webView;

    SharedPreferences appSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.wvApiContent);
        showWebview();

        appSharedPref = getSharedPreferences(GlobalModule.appCode, MODE_PRIVATE);
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void showWebview() {

        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.addJavascriptInterface(new JsInterface(this), GlobalModule.jsInterface);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.loadUrl(GlobalModule.gameURL);
        AppsFlyerLib.getInstance().setCollectIMEI(false);
        AppsFlyerLib.getInstance().setCollectAndroidID(false); AppsFlyerLib.getInstance().setAppId(GlobalModule.appFlyersId);
        AppsFlyerLib.getInstance().anonymizeUser(true);

        AppsFlyerLib.getInstance().init(GlobalModule.appFlyersId,null,this);
        AppsFlyerLib.getInstance().start(this);
    }


    @Override
    public void onBackPressed() {
        if (bckExit) {
            super.finishAffinity();
            return;
        }
        this.bckExit = true;
        Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> bckExit = false, 2000);
    }
}