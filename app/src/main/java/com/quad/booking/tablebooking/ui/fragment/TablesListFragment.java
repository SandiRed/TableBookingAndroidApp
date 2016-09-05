package com.quad.booking.tablebooking.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.quad.booking.tablebooking.R;
import com.quad.booking.tablebooking.adapters.TablesListAdapter;
import com.quad.booking.tablebooking.ui.activity.TableBookingActivity;
import com.quad.booking.tablebooking.ui.controller.TableBookingWorkFlowController;
import com.quad.booking.tablebooking.utils.Constants;

public class TablesListFragment extends Fragment {
    private ProgressDialog progressDialog;
    private TablesListAdapter tablesListAdapter;
    private TableBookingWorkFlowController controller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tables_list, container, false);
        tablesListAdapter = new TablesListAdapter(getContext());
        tablesListAdapter.setData(new Boolean[]{false});
        final Gson gson = new Gson();
        String tablesString = getArguments().getString(Constants.KEY_TABLE_LIST);
        if (tablesString.length() == 0) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Fetching Data");
            progressDialog.show();
            TableBookingWorkFlowController.Result result = new TableBookingWorkFlowController.Result() {
                final TableBookingActivity activity = ((TableBookingActivity) TablesListFragment.this.getActivity());
                @Override
                public void setSuccessResultData(final String tablesString) {
                    activity.getHandler().post(new Thread() {
                        public void run() {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            try {
                                Boolean[] list = gson.fromJson(tablesString, Boolean[].class);
                                SharedPreferences sharedpreferences = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                                sharedpreferences.edit().putString(Constants.KEY_TABLE_LIST, tablesString).commit();
                                activity.setTableList(list);
                                tablesListAdapter.setData(list);
                                tablesListAdapter.notifyDataSetChanged();
                            } catch (JsonSyntaxException j) {
                                j.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void setFailureResultData(final String message) {
                    activity.getHandler().post(new Thread() {
                        public void run() {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            activity.showDialog(message, true, activity.getResources().getString(R.string.ok), "");
                        }
                    });
                }
            };
            controller.fetchTableList(getActivity(), result);
        }else{
            Boolean[] tableList = gson.fromJson(tablesString, Boolean[].class);
            tablesListAdapter.setData(tableList);
        }
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(tablesListAdapter);

        return view;
    }

    public void setController(TableBookingWorkFlowController controller){
        this.controller=controller;
    }
}
