package com.quad.booking.tablebooking.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.quad.booking.tablebooking.R;
import com.quad.booking.tablebooking.adapters.CustomerListAdapter;
import com.quad.booking.tablebooking.model.Customer;
import com.quad.booking.tablebooking.ui.activity.TableBookingActivity;
import com.quad.booking.tablebooking.ui.controller.TableBookingWorkFlowController;
import com.quad.booking.tablebooking.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomerListFragment extends Fragment {
    private ProgressDialog progressDialog;
    private TableBookingWorkFlowController controller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.customer_list, container, false);
        final RecyclerView recyclerView = ((RecyclerView) ((LinearLayout) view).findViewById(R.id.recyclerView_customers_list));
        final Gson gson = new Gson();
        final CustomerListAdapter adapter = new CustomerListAdapter();
        String customersString = getArguments().getString(Constants.KEY_CUSTOMER_LIST);
        if (customersString.length() == 0) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Fetching Data");
            progressDialog.show();

            ArrayList<Customer>  customersList = new ArrayList<Customer>();
            customersList.add(new Customer("No Data", "", -1, -1, 0l));
            adapter.setData(customersList);

            TableBookingWorkFlowController.Result result = new TableBookingWorkFlowController.Result() {
                final TableBookingActivity activity = ((TableBookingActivity) CustomerListFragment.this.getActivity());

                @Override
                public void setSuccessResultData(final String message) {
                    activity.getHandler().post(new Thread() {
                        public void run() {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            List<Customer> customersList = Arrays.asList(gson.fromJson(message, Customer[].class));
                            if(customersList.size()==0){
                                customersList = new ArrayList<Customer>();
                                customersList.add(new Customer("No Data", "", -1, -1, 0l));
                            }
                            activity.setCustomers(customersList);
                            adapter.setData(customersList);
                            adapter.notifyItemChanged(0, customersList);
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
                            ArrayList<Customer> customersList = new ArrayList<Customer>();
                            customersList.add(new Customer("No Data", "", -1, -1, 0l));
                            adapter.setData(customersList);
                            adapter.notifyItemChanged(0, customersList);
                        }
                    });
                }
            };
            controller.fetchCustomerList(getActivity(), result/*, activity.getCustomers()*/);
        } else {
            List<Customer> customersList = Arrays.asList(gson.fromJson(customersString, Customer[].class));
            adapter.setData(customersList);
        }

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void setController(TableBookingWorkFlowController controller){
        this.controller=controller;
    }
}
