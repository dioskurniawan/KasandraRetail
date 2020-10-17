package id.kasandra.retail;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = SQLiteHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 3;

	// Database Name
	private static final String DATABASE_NAME = "kasandra";

	// table name
	private static final String TABLE_USERS = "m_users";
	private static final String TABLE_CLIENTS = "m_clients";
	private static final String TABLE_CATEGORY = "m_category";
	private static final String TABLE_PRODUCTS = "m_products";
	private static final String TABLE_TRANS = "m_transactions";
	private static final String TABLE_TRANS_DETAIL = "m_transactions_detail";
	private static final String TABLE_TRANS_TEMP = "m_transactions_temp";
	private static final String TABLE_CUSTOMER_TEMP = "m_customers_temp";
    private static final String TABLE_TRANS_BILL = "m_transactions_bill";
    private static final String TABLE_TRANS_BILL_DETAIL = "m_transactions_bill_detail";

	public SQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "(user_id INTEGER PRIMARY KEY AUTOINCREMENT, user_name TEXT, user_fullname TEXT, user_email TEXT, user_active INTEGER, user_isadmin INTEGER, user_status INTEGER, client_id INTEGER);");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CLIENTS + "(client_id INTEGER PRIMARY KEY AUTOINCREMENT, client_username TEXT, client_fullname TEXT, client_status INTEGER);");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TRANS + "(trans_id INTEGER PRIMARY KEY AUTOINCREMENT, total_qty INTEGER, total_price DOUBLE, datetime TIMESTAMP, upload_status INTEGER, payment_type INTEGER, trans_tax INTEGER, trans_discount INTEGER, trans_no INTEGER, trans_serv_charge INTEGER);");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TRANS_DETAIL + "(trans_det_id INTEGER PRIMARY KEY AUTOINCREMENT, trans_id INTEGER, prod_id INTEGER, upload_status INTEGER);");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TRANS_TEMP + "(trans_temp_id INTEGER PRIMARY KEY AUTOINCREMENT, prod_id INTEGER, qty INTEGER, discount INTEGER, cust_temp_id INTEGER);");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORY + "(category_id INTEGER PRIMARY KEY, category_name TEXT, updated_date TIMESTAMP, category_photo TEXT, discount INTEGER);");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCTS + "(prod_id INTEGER PRIMARY KEY, prod_name TEXT, prod_code TEXT, prod_price_buy DOUBLE, prod_price_sell DOUBLE, prod_category_id INTEGER, updated_date TIMESTAMP, prod_photo TEXT);");
		//added in released v.0.7 database version 3
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CUSTOMER_TEMP + "(cust_temp_id INTEGER PRIMARY KEY AUTOINCREMENT, cust_name TEXT, cust_state INTEGER);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TRANS_BILL + "(trans_bill_id INTEGER PRIMARY KEY AUTOINCREMENT, total_qty INTEGER, total_price DOUBLE, datetime TIMESTAMP, upload_status INTEGER, payment_type INTEGER, trans_tax INTEGER, trans_discount INTEGER, trans_no INTEGER, trans_serv_charge INTEGER);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TRANS_BILL_DETAIL + "(trans_bill_det_id INTEGER PRIMARY KEY AUTOINCREMENT, trans_bill_id INTEGER, prod_id INTEGER, upload_status INTEGER);");

        Log.d(TAG, "Database tables created");
	}

	private static final String DATABASE_ALTER_TRANS = "ALTER TABLE "
			+ TABLE_TRANS + " ADD COLUMN trans_serv_charge INTEGER;"; // kasandra retail released v.0.6, database version 2
	private static final String DATABASE_ALTER_TRANS_TEMP = "ALTER TABLE "
			+ TABLE_TRANS_TEMP + " ADD COLUMN cust_temp_id INTEGER;"; // kasandra retail released v.0.7, database version 3

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER)
		// Create tables again
		//onCreate(db);
		//if(newVersion>oldVersion);
		/*if (oldVersion < 2) {
			db.execSQL(DATABASE_ALTER_TRANS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_TEMP);
		} else if (oldVersion < 3) {
		    db.execSQL(DATABASE_ALTER_TRANS_TEMP);
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CUSTOMER_TEMP + "(cust_temp_id INTEGER PRIMARY KEY AUTOINCREMENT, cust_name TEXT, cust_state INTEGER);");
        }*/
        switch(oldVersion) {
			case 1:
				db.execSQL(DATABASE_ALTER_TRANS);
				// we want both updates, so no break statement here...
			case 2:
				db.execSQL(DATABASE_ALTER_TRANS_TEMP);
				db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CUSTOMER_TEMP + "(cust_temp_id INTEGER PRIMARY KEY AUTOINCREMENT, cust_name TEXT, cust_state INTEGER);");
				db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TRANS_BILL + "(trans_bill_id INTEGER PRIMARY KEY AUTOINCREMENT, total_qty INTEGER, total_price DOUBLE, datetime TIMESTAMP, upload_status INTEGER, payment_type INTEGER, trans_tax INTEGER, trans_discount INTEGER, trans_no INTEGER, trans_serv_charge INTEGER);");
				db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TRANS_BILL_DETAIL + "(trans_bill_det_id INTEGER PRIMARY KEY AUTOINCREMENT, trans_bill_id INTEGER, prod_id INTEGER, upload_status INTEGER);");

        }
	}

}
