package com.quad.booking.tablebooking.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

    public boolean isConnectedToNetwork(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null)
            return false;
        else
            return activeNetwork.isConnectedOrConnecting();
    }

    public boolean isStringNumeric(String str) {
        if (str == null)
            return false;
        char[] data = str.toCharArray();
        if (data.length <= 0)
            return false;
        int index = 0;
        if (data[0] == '-' && data.length > 1)
            index = 1;
        for (; index < data.length; index++) {
            if (data[index] < '0' || data[index] > '9')
                return false;
        }
        return true;
    }
}
