package io.seventigers.gameslot.utilities;


import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyHelper

{

    private static RequestQueue mRequestQueue;
    private VolleyHelper() {}
    public static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }
    public static RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }
}