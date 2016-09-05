package com.quad.booking.tablebooking.db;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.quad.booking.tablebooking.R;
import com.quad.booking.tablebooking.model.Customer;
import com.quad.booking.tablebooking.ui.activity.TableBookingActivity;
import com.quad.booking.tablebooking.ui.controller.TableBookingWorkFlowController;
import com.quad.booking.tablebooking.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DbUtils {
    private final String TAG = "DbUtils";

    private void signal2ReseTableReservations(Context context, int[]  positions) {
        Intent intent = new Intent(Constants.BROADCAST_ACTION);
        intent.putExtra(Constants.RESET_RESERVATIONS, positions);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void getCustomerListFromDb(final Context activity, final TableBookingWorkFlowController.Result result) {
        new Thread() {
            @Override
            public void run() {
                final Gson gson = new Gson();
                boolean success = false;
                SQLiteDatabase db = null;
                Cursor cursor = null;
                try {
                    DatabaseHelper databaseHelperInstance = new DatabaseHelper(activity.getApplicationContext());
                    db = databaseHelperInstance.getReadableDatabase();
                    cursor = db.rawQuery(new StringBuilder(50).append("select * from ").append(DatabaseHelper.CustomerTable.CUSTOMER_TABLE_NAME).toString(), null);
                    ArrayList<Integer> custIds = new ArrayList<Integer>();
                    ArrayList<Customer> customers = new ArrayList<Customer>();
                    if (cursor.moveToFirst()) {
                        do {
                            int customerId = cursor.getInt(DatabaseHelper.CustomerTable.INDEX__CUSTOMER_ID_COL);
                            String firstName = cursor.getString(DatabaseHelper.CustomerTable.INDEX_FIRST_NAME_COL);
                            String lastName = cursor.getString(DatabaseHelper.CustomerTable.INDEX_LAST_NAME_COL);
                            int tableNumber = cursor.getInt(DatabaseHelper.CustomerTable.INDEX_TABLE_NUMBER_COL);
                            long reservedTime = Long.parseLong(cursor.getString(DatabaseHelper.CustomerTable.INDEX__TABLE_BOOKED_TIME_COL));
                            customers.add((new Customer(firstName, lastName, customerId,tableNumber,reservedTime)));
                            if(tableNumber!=-1){
                                custIds.add(customerId);
                            }
                        } while (cursor.moveToNext());
                        success = true;
                    }
                    int[] ids=new int[custIds.size()];
                    int counter=-1;
                     for(Integer custId:custIds){
                         ids[++counter]=custIds.get(counter);
                     }
                    result.setSuccessResultData(gson.toJson(customers));
                    if(ids.length>0)
                    signal2ReseTableReservations(activity,ids);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null)
                        cursor.close();
                    if (db != null)
                        db.close();
                }
                if (!success) {
                    result.setFailureResultData(activity.getResources().getString(R.string.error));
                   /* activity.getHandler().post(new Thread() {
                        public void run() {
                            result.setFailureResultData(activity.getResources().getString(R.string.error));
                        }
                    });*/
                }
            }
        }.start();
    }

    public void storeCustomerList(final Context context, final Customer[] customersList,final boolean updateUi) {
        new Thread() {
            @Override
            public void run() {
                SQLiteDatabase db = null;
                try {
                    db = new DatabaseHelper(context).getWritableDatabase();
                    db.execSQL("delete from " + DatabaseHelper.CustomerTable.CUSTOMER_TABLE_NAME);
                    String sql = "insert into " + DatabaseHelper.CustomerTable.CUSTOMER_TABLE_NAME
                            + " values (?,?,?,?,?);";
                    SQLiteStatement statement = db.compileStatement(sql);
                    db.beginTransaction();
                    for (Customer customer : customersList) {
                        statement.clearBindings();
                        statement.bindString(1, customer.customerFirstName);
                        statement.bindString(2, customer.customerLastName);
                        statement.bindLong(3, customer.id);
                        statement.bindLong(4, customer.tableNumber);
                        statement.bindString(5, String.valueOf(customer.timeOfBooking));
                        statement.execute();
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                    sharedpreferences.edit().putBoolean(Constants.KEY_DATA_PRESENT, true).apply();
                    if(updateUi)
                    signal2UpdateUi(context);
                } catch (SQLiteException sql) {
                    sql.printStackTrace();
                } finally {
                    if (db != null) db.close();
                }
                Log.i(TAG, "----customer info saved in mobile device----");
            }
        }.start();
    }

    private void signal2UpdateUi(Context context) {
        Intent intent = new Intent(Constants.BROADCAST_ACTION);
        intent.putExtra(Constants.UPDATE_UI, true);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
