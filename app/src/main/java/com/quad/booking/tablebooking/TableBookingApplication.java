package com.quad.booking.tablebooking;

import android.app.Application;
import android.util.Log;

import com.quad.booking.tablebooking.network.NetworkHandler;

public class TableBookingApplication extends Application {
    private static final String TAG = "Application";
    private NetworkHandler networkHandlerInstance;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public NetworkHandler getNetworkHandlerInstance() {
        if (networkHandlerInstance == null) {
            synchronized (TableBookingApplication.class) {
                networkHandlerInstance = new NetworkHandler();
            }
        }
        return networkHandlerInstance;
    }
}
