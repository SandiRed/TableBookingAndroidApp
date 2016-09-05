package com.quad.booking.tablebooking.ui.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.quad.booking.tablebooking.R;
import com.quad.booking.tablebooking.TableBookingApplication;
import com.quad.booking.tablebooking.db.DbUtils;
import com.quad.booking.tablebooking.model.Customer;
import com.quad.booking.tablebooking.network.NetworkHandler;
import com.quad.booking.tablebooking.ui.activity.TableBookingActivity;
import com.quad.booking.tablebooking.utils.Constants;
import com.quad.booking.tablebooking.utils.Utils;

import java.util.List;

public class TableBookingWorkFlowController {
    private final String TAG = "Controller";
    private Utils utils;
    private DbUtils dbUtils;

    public TableBookingWorkFlowController() {
        utils = new Utils();
        dbUtils = new DbUtils();
    }

    public void fetchCustomerList(final Activity context, final Result result) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if(sharedpreferences.getBoolean(Constants.KEY_DATA_PRESENT,false)){
            dbUtils.getCustomerListFromDb(context, result);
        }else if (utils.isConnectedToNetwork(context)) {
            NetworkHandler.NetworkResult networkResult = new NetworkHandler.NetworkResult() {
                public void setSuccessResultData(String jsonString) {
                    if (jsonString.length() > 0) {
                        result.setSuccessResultData(jsonString);
                        try {
                            dbUtils.storeCustomerList(context, new Gson().fromJson(jsonString, Customer[].class),false);
                        } catch (JsonSyntaxException j) {
                            j.printStackTrace();
                        }
                    }
                }

                public void setFailureResultData(String jsonString) {
                    String errorMsg = context.getResources().getString(R.string.error);
                    if (utils.isStringNumeric(jsonString)) {
                        int statusCode = Integer.parseInt(jsonString);
                        if (statusCode == 400) {
                            errorMsg = context.getResources().getString(R.string.error_400);
                            Log.d(TAG, "statusCode in DashBoardController:" + statusCode);
                        } else if (statusCode == 500) {
                            errorMsg = context.getResources().getString(R.string.error_500);
                            Log.d(TAG, "statusCode in DashBoardController:" + statusCode);
                        } else {
                            Log.d(TAG, "statusCode in DashBoardController:" + statusCode);
                        }
                    } else {
                        Log.d(TAG, "return jsonString in DashBoardController:" + jsonString);
                    }
                    result.setFailureResultData(errorMsg);
                }
            };
            ((TableBookingApplication) context.getApplication()).getNetworkHandlerInstance().makeGetRequestForJsonArray(Constants.CUSTOMER_LIST_URL,
                    "cancel", context, Constants.DEFAULT_MAX_RETRIES, networkResult);
        }
    }

    public void fetchTableList(final Activity context, final Result result) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
         if(sharedpreferences.getString(Constants.KEY_TABLE_LIST,"").length()>0){
            Boolean[] booleans = new Boolean[60];
            result.setSuccessResultData(sharedpreferences.getString(Constants.KEY_TABLE_LIST,new Gson().toJson(booleans)));
        } else if (utils.isConnectedToNetwork(context)) {
            NetworkHandler.NetworkResult networkResult = new NetworkHandler.NetworkResult() {
                public void setSuccessResultData(String jsonString) {
                    if (jsonString.length() > 0) {
                        result.setSuccessResultData(jsonString);
                    }
                }

                public void setFailureResultData(String jsonString) {
                    String errorMsg = context.getResources().getString(R.string.error);
                    if (utils.isStringNumeric(jsonString)) {
                        int statusCode = Integer.parseInt(jsonString);
                        if (statusCode == 400) {
                            errorMsg = context.getResources().getString(R.string.error_400);
                            Log.d(TAG, "statusCode in DashBoardController:" + statusCode);
                        } else if (statusCode == 500) {
                            errorMsg = context.getResources().getString(R.string.error_500);
                            Log.d(TAG, "statusCode in DashBoardController:" + statusCode);
                        } else {
                            Log.d(TAG, "statusCode in DashBoardController:" + statusCode);
                        }
                    } else {
                        Log.d(TAG, "return jsonString in DashBoardController:" + jsonString);
                    }
                }
            };
            ((TableBookingApplication) context.getApplication()).getNetworkHandlerInstance().makeGetRequestForJsonArray(Constants.TABLE_LIST_URL,
                    "cancel", context, Constants.DEFAULT_MAX_RETRIES, networkResult);
        }
    }


    public static interface Result {
        void setSuccessResultData(String message);

        void setFailureResultData(String message);
    }
}
