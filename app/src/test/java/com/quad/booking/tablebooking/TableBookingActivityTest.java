package com.quad.booking.tablebooking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.gson.Gson;
import com.quad.booking.tablebooking.model.Customer;
import com.quad.booking.tablebooking.ui.activity.TableBookingActivity;
import com.quad.booking.tablebooking.ui.fragment.CustomerListFragment;
import com.quad.booking.tablebooking.ui.fragment.TablesListFragment;
import com.quad.booking.tablebooking.utils.Constants;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.ArrayList;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TableBookingActivityTest {
    private TableBookingActivity mActivity;
    private Context mContext;

    @Before
    public void setUp() {
        mContext = Mockito.mock(Context.class);
        mActivity = Robolectric.setupActivity(TableBookingActivity.class);
    }

    @Test
    public void shouldNotBeNull() {
        TableBookingActivity activity = Robolectric.buildActivity(TableBookingActivity.class)
                .create().start().resume().get();
        Assert.assertNotNull(activity);
        CustomerListFragment customerListFragment = new CustomerListFragment();
        ArrayList<Customer> customersList = new ArrayList<Customer>();
        customersList.add(new Customer("No Data", "", -1, -1, 0l));
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_CUSTOMER_LIST, new Gson().toJson(customersList));
        customerListFragment.setArguments(bundle);
        customerListFragment.setController(activity.getController());
        SupportFragmentTestUtil.startVisibleFragment(customerListFragment);
        Assert.assertNotNull((customerListFragment.getView()));

    }

    @Test
    public void shouldReceiveBroadcasts() {
        TableBookingActivity activity = Robolectric.buildActivity(TableBookingActivity.class)
                .create().start().resume().get();
        BroadcastReceiver receiver = activity.getmMessageReceiver();
        activity.registerReceiver(receiver, new IntentFilter(Constants.BROADCAST_ACTION));
        Intent intent = new Intent(Constants.BROADCAST_ACTION);
        intent.putExtra(Constants.SHOW_TABLE_LIST, 1);
        activity.sendBroadcast(intent);
        receiver.onReceive(activity, intent);
        System.out.println(activity.getVisibleFragment()+", "+activity.getCurentCustomer());
    }

    @Test
    public void shouldChangeFragment() {
        TableBookingActivity activity = Robolectric.buildActivity(TableBookingActivity.class)
                .create().start().resume().get();
        String name= TablesListFragment.class.getName();
        activity.showFragment(name);
        Assert.assertEquals(activity.getVisibleFragment().getClass().getName(), name);
        Assert.assertNotSame(activity.getVisibleFragment().getClass().getName(),CustomerListFragment.class.getName());
    }


}