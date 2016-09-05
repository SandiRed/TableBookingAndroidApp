package com.quad.booking.tablebooking.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "main_db";
    private static final int DATABASE_VERSION = 2;
    private static final String CREATE_CUSTOMER_TABLE = "CREATE TABLE " + CustomerTable.CUSTOMER_TABLE_NAME + "(" +
            CustomerTable.KEY_FIRST_NAME + " TEXT," + CustomerTable.KEY_LAST_NAME + " TEXT ," + CustomerTable.KEY_CUSTOMER_ID + " INTEGER,"
            + CustomerTable.KEY_TABLE_NUMBER + " INTEGER," + CustomerTable.KEY_TABLE_BOOKED_TIME + " TEXT" + ");";
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CUSTOMER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CustomerTable.CUSTOMER_TABLE_NAME);
        onCreate(db);
    }

    public static class CustomerTable {
        public static final String CUSTOMER_TABLE_NAME = "customer_table";
        public static final String KEY_FIRST_NAME = "first_name";
        public static final String KEY_LAST_NAME = "last_name";
        public static final String KEY_CUSTOMER_ID = "customer_id";
        public static final String KEY_TABLE_NUMBER = "table_number";
        public static final String KEY_TABLE_BOOKED_TIME = "table_booked_time";
        public static final int INDEX_FIRST_NAME_COL = 0;
        public static final int INDEX_LAST_NAME_COL = 1;
        public static final int INDEX__CUSTOMER_ID_COL = 2;
        public static final int INDEX_TABLE_NUMBER_COL = 3;
        public static final int INDEX__TABLE_BOOKED_TIME_COL = 4;
    }

}