package io.seventigers.gameslot.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.appsflyer.AppsFlyerLib;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import io.seventigers.gameslot.di.GlobalModule;

public class JsInterface {

    Context context;


    public JsInterface(Context context) {
        this.context = context;

    }


    public void postMessage(String name, String data) {
        Log.d("JsInterface:", "Event name:" + name + "Data:" + data);

        if("openWindow".equals(name)){
            try{
                JSONObject extLink = new JSONObject();
                Intent newWindow = new Intent(Intent.ACTION_VIEW);
                newWindow.setData(Uri.parse(extLink.getString("url")));
                newWindow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newWindow);
            } catch (JSONException e) {
                Log.d("OpenWindow", "OpenWindow Error: "+e.getMessage());
            }
        }

        if (GlobalModule.permitSendData) {

            RequestQueue sendAFRequest = GlobalModule.getInstance().getRequest();

            JSONObject requestBody = new JSONObject();

            try {
                requestBody.put("appsflyer_id", AppsFlyerLib.getInstance().getAppsFlyerUID(context));
                requestBody.put("eventName", name);
                requestBody.put("eventValue", data);
                requestBody.put("authentication", GlobalModule.appFlyersId);
                requestBody.put("endpoint", context.getPackageName());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String endPoint = GlobalModule.appFlyerAPI +
                    "?appsflyer_id" + AppsFlyerLib.getInstance().getAppsFlyerUID(context) +
                    "&eventName" + name +
                    "&eventValue=" + data +
                    "&authentication" + GlobalModule.appFlyersId +
                    "endpoint=" + context.getPackageName();

            JsonObjectRequest afRequest = new JsonObjectRequest(Request.Method.GET,
                    endPoint, requestBody, response -> {
                Log.d("AF:RESPONSE", "AppsFlyer Event Recieved: "+response.toString());
            }, error -> {
                Log.d("AF:RESPONSE", "AppsFlyer Event Error: "+error.getMessage());
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("accept", "application/json");
                    headers.put("content-type", "application/json");

                    return headers;
                }
            };
            sendAFRequest.add(afRequest);
        }
    }
}

