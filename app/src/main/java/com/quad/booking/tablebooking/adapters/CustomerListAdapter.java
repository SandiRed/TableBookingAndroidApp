package com.quad.booking.tablebooking.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quad.booking.tablebooking.R;
import com.quad.booking.tablebooking.model.Customer;
import com.quad.booking.tablebooking.utils.Constants;

import java.util.List;

public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.ViewHolder> {
    private final String TAG = "CustomerListAdapter";
    private List<Customer> customers;
    private ViewGroup parent;
    private int textColorWhenTableReserved;
    private int textColorWhenNoTableReserved;

    public void setData(List<Customer> customers){
        this.customers = customers;
    }

    private CustomerListAdapter.ViewHolder getViewHolder(int position) {
        if (parent == null)
            return null;
        else
            return ((CustomerListAdapter.ViewHolder) ((RecyclerView) parent).findViewHolderForAdapterPosition(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        this.parent = parent;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_info, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.getNameTextView().setText(customers.get(position).customerFirstName + " " + customers.get(position).customerLastName);
        viewHolder.getIdTextView().setText("Id:" + String.valueOf(customers.get(position).id));
         if(customers.get(position).tableNumber!=-1) {
            viewHolder.getTableNumberTextView().setText("Table:" + String.valueOf(customers.get(position).tableNumber));
            if(textColorWhenNoTableReserved==0)
                textColorWhenNoTableReserved=viewHolder.getTableNumberTextView().getResources().getColor(R.color.access_denied);
            viewHolder.getTableNumberTextView().setTextColor(textColorWhenNoTableReserved);
            viewHolder.getRootView().setBackgroundResource(R.drawable.customer_selector_2);
        }else {
            viewHolder.getTableNumberTextView().setText("Table: not reserved");
            if(textColorWhenTableReserved==0)
                textColorWhenTableReserved=viewHolder.getTableNumberTextView().getResources().getColor(R.color.badge_black);
            viewHolder.getTableNumberTextView().setTextColor(textColorWhenTableReserved);
            viewHolder.getRootView().setBackgroundResource(R.drawable.customer_selector);
        }
        setOnClickListener(viewHolder.getRootView(), position);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public Customer getItemAtPosition(int position) {
        return customers.get(position);
    }

    public void setOnClickListener(final View view, final int position) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signal2ShowTableList(view, position);
            }
        };
        view.findViewById(R.id.customer_name).setOnClickListener(listener);
        view.findViewById(R.id.customer_id).setOnClickListener(listener);
        view.findViewById(R.id.table_number).setOnClickListener(listener);
        view.findViewById(R.id.card_view).setOnClickListener(listener);
    }

    private void signal2ShowTableList(View view, int position) {
        Intent intent = new Intent(Constants.BROADCAST_ACTION);
        intent.putExtra(Constants.SHOW_TABLE_LIST, position);
        LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView id;
        private final TextView tableNumber;
        private final View view;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.customer_name);
            id = (TextView) view.findViewById(R.id.customer_id);
            tableNumber = (TextView) view.findViewById(R.id.table_number);
            this.view = view;
        }

        public TextView getNameTextView() {
            return name;
        }

        public TextView getIdTextView() {
            return id;
        }

        public TextView getTableNumberTextView() {
            return tableNumber;
        }

        public View getRootView() {
            return view;
        }
    }

}
