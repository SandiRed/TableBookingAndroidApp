package com.quad.booking.tablebooking.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.quad.booking.tablebooking.R;
import com.quad.booking.tablebooking.utils.Constants;

public class TablesListAdapter extends BaseAdapter {
    public Boolean[] tablesArray=new Boolean[60];
    private Context context;

    public TablesListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        if(tablesArray==null) tablesArray=new Boolean[60];
        for(Boolean bool:tablesArray)
        bool=true;
        return tablesArray.length;
    }

    @Override
    public Boolean getItem(int position) {
        return tablesArray[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            TextView textView = new TextView(context);
            textView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
            textView.setPadding(8, 8, 8, 8);
            textView.setGravity(Gravity.CENTER);
            convertView = textView;
        }
        setOnClickListener(position, convertView);
        String text="";
        if(tablesArray[position]!=null && tablesArray[position]){
            text=new StringBuffer(50).append(position).append(convertView.getResources().getString(R.string.table)).toString();
        }else{
            text=new StringBuffer(50).append(position).append(convertView.getResources().getString(R.string.table_available)).toString();
        }
        ((TextView) convertView).setText(text);
        if(tablesArray[position]!=null && tablesArray[position]){
            ((TextView) convertView).setTextColor(convertView.getResources().getColor(R.color.card_white));
            convertView.setBackgroundResource(R.drawable.customer_selector_2);
        }else{
            ((TextView) convertView).setTextColor(convertView.getResources().getColor(R.color.badge_black));
            convertView.setBackgroundResource(R.drawable.customer_selector);
        }
        return convertView;
    }

    private void signal2ShowCustomerList(View view,int tablePosition){
        Intent intent = new Intent(Constants.BROADCAST_ACTION);
        intent.putExtra(Constants.SHOW_CUSTOMER_LIST, tablePosition);
        LocalBroadcastManager.getInstance(view.getContext()).sendBroadcast(intent);
    }

    private void setOnClickListener(final int position, final View convertView) {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signal2ShowCustomerList(convertView,position);
                }
            };
        convertView.setOnClickListener(listener);
    }

    public Boolean[] getTablesArray() {
        return tablesArray;
    }

    public void setData(Boolean[] tablesArray) {
        this.tablesArray = tablesArray;
    }

}