package com.quad.booking.tablebooking.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.quad.booking.tablebooking.R;
import com.quad.booking.tablebooking.db.DbUtils;
import com.quad.booking.tablebooking.model.Customer;
import com.quad.booking.tablebooking.ui.controller.TableBookingWorkFlowController;
import com.quad.booking.tablebooking.ui.fragment.CustomerListFragment;
import com.quad.booking.tablebooking.ui.fragment.TablesListFragment;
import com.quad.booking.tablebooking.utils.Constants;

import java.util.Calendar;
import java.util.List;

public class TableBookingActivity extends AppCompatActivity {
    private final String TAG = "TableBookingActivity";
    private final String CUSTOMER_LIST = "customer_list";
    private TableBookingWorkFlowController controller;
    private Handler handler;
    private List<Customer> customers;
    private Customer curentCustomer;
    private Boolean[] tableList;
    private Fragment visibleFragment;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (customers != null) {
                if (intent.hasExtra(Constants.SHOW_TABLE_LIST)) {
                    int customerId = intent.getExtras().getInt(Constants.SHOW_TABLE_LIST);
                    curentCustomer = customers.get(customerId);
                    showFragment(TablesListFragment.class.getName());
                    Log.i(TAG, "Got message:" + Constants.SHOW_TABLE_LIST);
                } else if (intent.hasExtra(Constants.SHOW_CUSTOMER_LIST)) {
                    int tableSelected = intent.getExtras().getInt(Constants.SHOW_CUSTOMER_LIST);
                    if (curentCustomer.id != -1 && !tableList[tableSelected]) {
                        if (curentCustomer.tableNumber != -1)
                            tableList[curentCustomer.tableNumber] = false;
                        tableList[tableSelected] = true;
                        curentCustomer.tableNumber = tableSelected;
                        curentCustomer.timeOfBooking = System.currentTimeMillis();
                        storeData(context, false);
                        resetReservedTableAndSaveData(new int[]{curentCustomer.id});
                    }
                    showFragment(CustomerListFragment.class.getName());
                    Log.i(TAG, "Got message:" + Constants.SHOW_CUSTOMER_LIST);
                } else if (intent.hasExtra(Constants.UPDATE_UI)) {
                    if (visibleFragment instanceof TablesListFragment) {
                        showFragment(TablesListFragment.class.getName());
                    } else {
                        showFragment(CustomerListFragment.class.getName());
                    }
                    Log.i(TAG, "Got message:" + Constants.UPDATE_UI);
                } else if (intent.hasExtra(Constants.RESET_RESERVATIONS)) {
                    int[] customerId = intent.getExtras().getIntArray(Constants.RESET_RESERVATIONS);
                    resetReservedTableAndSaveData(customerId);
                    Log.i(TAG, "Got message:" + Constants.RESET_RESERVATIONS);
                }
            }
        }
    };

    private void storeData(final Context context, boolean updateUI) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        sharedpreferences.edit().putString(Constants.KEY_TABLE_LIST, new Gson().toJson(tableList)).commit();
        try {
            new DbUtils().storeCustomerList(context, (Customer[]) customers.toArray(), updateUI);
        } catch (JsonSyntaxException j) {
            j.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        controller = new TableBookingWorkFlowController();
        setContentView(R.layout.activity_main);
        addCustomerListFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    public TableBookingWorkFlowController getController() {
        return controller;
    }

    public Handler getHandler() {
        return handler;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public Boolean[] getTableList() {
        SharedPreferences sharedpreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(Constants.KEY_TABLE_LIST)) {
            String tableListStr = sharedpreferences.getString(Constants.KEY_TABLE_LIST, "");
            if (tableListStr.length() > 0) {
                tableList = new Gson().fromJson(tableListStr, Boolean[].class);
            }
        }
        return tableList;
    }

    public void setTableList(Boolean[] tableList) {
        this.tableList = tableList;
    }

    public Dialog showDialog(String message, boolean cancellable, String positiveButtonString, String negativeButtonString) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);
        builder1.setCancelable(cancellable);
        if (positiveButtonString.length() > 0)
            builder1.setPositiveButton(
                    positiveButtonString,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
        if (negativeButtonString.length() > 0)
            builder1.setNegativeButton(
                    positiveButtonString,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
        return builder1.create();
    }

    public ProgressDialog showProgressDialog(String message) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(message);
        return pd;
    }

    private void resetReservedTableAndSaveData(final int[] customerIds) {
        Thread runnable = new Thread() {
            public void run() {
                for (Integer customerId : customerIds) {
                    Customer customer = customers.get(customerId);
                    if (tableList == null) {
                        tableList = getTableList();
                    }
                    if (customer.tableNumber != -1)
                        tableList[customer.tableNumber] = false;
                    customer.timeOfBooking = 0;
                    customer.tableNumber = -1;
                }
                storeData(TableBookingActivity.this, true);
            }
        };
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        for (Integer customerId : customerIds) {
            calendar2.setTimeInMillis(customers.get(customerId).timeOfBooking);
            if (calendar.compareTo(calendar2) > 0) {
                calendar2.add(Calendar.MINUTE, Constants.TIME_2_RESET_RESERVATION_IN_MINUTES);
                long diff = calendar2.getTimeInMillis() - calendar.getTimeInMillis();
                if (diff < 0) diff = 0;
                Log.d(TAG, "reset after " + diff);
                handler.postDelayed(runnable, diff);
            } else if (calendar.compareTo(calendar2) == 0) {
                Log.d(TAG, "reset after " + Constants.TIME_2_RESET_RESERVATION_IN_MINUTES * 60 * 1000);
                handler.postDelayed(runnable, Constants.TIME_2_RESET_RESERVATION_IN_MINUTES * 60 * 1000);
            } else {
                Log.d(TAG, "reset after 0 ms");
                handler.post(runnable);
            }
        }
    }

    private void addCustomerListFragment() {
        getSupportActionBar().setTitle(getResources().getString(R.string.customer_list));
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_CUSTOMER_LIST, new Gson().toJson(customers));
        CustomerListFragment fragment = new CustomerListFragment();
        fragment.setController(controller);
        fragment.setArguments(bundle);
        visibleFragment = fragment;
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, visibleFragment, CUSTOMER_LIST).commit();
    }

    public void showFragment(String fragmentName) {
        Bundle bundle = new Bundle();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragmentName.equals(TablesListFragment.class.getName())) {
            getSupportActionBar().setTitle(getResources().getString(R.string.table_list));
            bundle.putString(Constants.KEY_TABLE_LIST, new Gson().toJson(tableList));
            TablesListFragment fragment = new TablesListFragment();
            fragment.setController(controller);
            fragment.setArguments(bundle);
            visibleFragment = fragment;
            transaction.replace(R.id.fragment_container, visibleFragment).commit();
        } else if (fragmentName.equals(CustomerListFragment.class.getName())) {
            getSupportActionBar().setTitle(getResources().getString(R.string.customer_list));
            bundle.putString(Constants.KEY_CUSTOMER_LIST, new Gson().toJson(customers));
            CustomerListFragment fragment = new CustomerListFragment();
            fragment.setController(controller);
            fragment.setArguments(bundle);
            visibleFragment = fragment;
            transaction.replace(R.id.fragment_container, visibleFragment).commit();
        }
    }

    public Fragment getVisibleFragment() {
        return visibleFragment;
    }


    public BroadcastReceiver getmMessageReceiver() {
        return mMessageReceiver;
    }

    public Customer getCurentCustomer() {
        return curentCustomer;
    }


}
