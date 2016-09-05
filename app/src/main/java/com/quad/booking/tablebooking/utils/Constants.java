package com.quad.booking.tablebooking.utils;

public class Constants {
    public static final String TABLE_LIST_URL = "https://s3-eu-west-1.amazonaws.com/quandoo-assessment/table-map.json";
    public static final String CUSTOMER_LIST_URL = "https://s3-eu-west-1.amazonaws.com/quandoo-assessment/customer-list.json";

    public static final int DEFAULT_MAX_RETRIES = 2;
    public static final int TIME_2_RESET_RESERVATION_IN_MINUTES =1;//in minutes

    public static final String BROADCAST_ACTION = "com.quad.booking.tablebooking.show_view";
    public static final String SHOW_TABLE_LIST = "show_table_list";
    public static final String SHOW_CUSTOMER_LIST = "show_customer_list";
    public static final String UPDATE_UI = "update_ui";
    public static final String RESET_RESERVATIONS = "reset_reservations";

    public static final String SHARED_PREFERENCES="shared_prefs";
    public static final String KEY_TABLE_LIST="key_table_list";
    public static final String KEY_DATA_PRESENT="key_data_present";
    public static final String KEY_CUSTOMER_LIST="key_customer_list";

  }
