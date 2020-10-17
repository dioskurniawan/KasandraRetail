package id.kasandra.retail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import id.kasandra.retail.bluetoothprint.*;
import id.kasandra.retail.bluetoothprint.Utils;
import mehdi.sakout.fancybuttons.FancyButton;

import static java.lang.Integer.parseInt;

//import com.getbase.floatingactionbutton.FloatingActionButton;

/**
 * Created by Joko on 9/16/2017.
 */

public class ReportActivity extends AppCompatActivity {

    private int nClientId = 0;
    private SessionManager session;
    SQLiteHandler db;
    SQLiteDatabase write;
    SQLiteDatabase read;
    private ProgressDialog pDialog;
    final Handler handler = new Handler();
    int n = 0;
    int dateNo;
    public static String sURL = "https://kasandra.biz/appdata/getdata_dev2.php";
    TextView toolbar_title, txtTotal;
    FloatingActionButton fab_a, fab_b, fab_c, fab_d;
    private EditText inputDate1, inputDate2, inputTime1, inputTime2;
    private int mHour, mMinute;
    private FancyButton btnPrint;
    private String serverDate1, serverDate2, time1, time2;
    Calendar myCalendar;
    DrawerLayout drawer;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int RESULT_SETTINGS = 2;
    private static final int MY_PERMISSIONS_1 = 0;
    private static final int MY_PERMISSIONS_2 = 1;
    AlertDialog alertDialog1;
    private BluetoothSocket btsocket;
    private static OutputStream btoutputstream;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private String btName;
    private String mDeviceAddress;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBluetoothDevice;
    byte FONT_TYPE;
    DecimalFormat df;
    Calendar c;
    int h, m;
    final Handler handler4 = new Handler();
    int n4 = 0;
    boolean isLoading4 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Session manager
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());
        write = db.getWritableDatabase();
        read = db.getReadableDatabase();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText("Pelaporan "+session.sOutlet());

        Typeface font1 = Typeface.createFromAsset(getAssets(), "quattrocentobold.ttf");
        Typeface font2 = Typeface.createFromAsset(getAssets(), "GenBasR.ttf");
        /*drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        View header = navigationView.getHeaderView(0);
        TextView textView1 = (TextView) header.findViewById(R.id.client_acc);
        textView1.setText(session.sOutlet());
        textView1.setTypeface(font1);
        TextView textView2 = (TextView) header.findViewById(R.id.mail_acc);
        textView2.setText(session.sUserEmail());
        textView2.setTypeface(font2);*/

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');
        df = new DecimalFormat("#,###", otherSymbols);

        if(session.screenSize() < 7.9) {
            toolbar_title.setTextSize(20);
        } else {
            toolbar_title.setTextSize(25);
        }
        fab_a = (FloatingActionButton) findViewById(R.id.action_a);
        fab_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), TransactionActivity.class);
                startActivity(i);
                finish();
            }
        });
        /*fab_b = (FloatingActionButton) findViewById(R.id.action_b);
        fab_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("id_main", 1);
                startActivity(i);
                finish();
            }
        });*/
        fab_c = (FloatingActionButton) findViewById(R.id.action_c);
        fab_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(ReportActivity.this, "Device Not Support", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(ReportActivity.this,
                                DeviceListActivity.class);
                        startActivityForResult(connectIntent,
                                REQUEST_CONNECT_DEVICE);
                    }
                }
            }
        });
        fab_d = (FloatingActionButton) findViewById(R.id.action_d);
        fab_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ReportActivity.class);
                startActivity(i);
                finish();
            }
        });

        pDialog = new ProgressDialog(new ContextThemeWrapper(ReportActivity.this, android.R.style.Theme_Holo_Light_Dialog));

        if (nClientId == 0) {
            nClientId = session.nClient_ID();
        }
        // check nClientId from persistence store
        if (session.nClient_ID() != -999) {
            nClientId = session.nClient_ID();
        }

        txtTotal = (TextView) findViewById(R.id.total);
        TextView txtDate1 = (TextView) findViewById(R.id.date1);
        TextView txtDate2 = (TextView) findViewById(R.id.date2);
        txtDate1.setTypeface(font1);
        txtDate2.setTypeface(font1);
        inputDate1 = (EditText) findViewById(R.id.input_date1);
        inputDate2 = (EditText) findViewById(R.id.input_date2);
        inputTime1 = (EditText) findViewById(R.id.input_time1);
        inputTime2 = (EditText) findViewById(R.id.input_time2);
        inputTime1.setText("00:00");
        inputTime2.setText("23:59");
        inputTime1.setVisibility(View.INVISIBLE);
        inputTime2.setVisibility(View.INVISIBLE);
        time1 = "00:00";
        time2 = "23:59";
        btnPrint = (FancyButton) findViewById(R.id.btn_simpan);

        Calendar cald = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
        Date currentLocalTimes = cald.getTime();
        DateFormat dateF = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat dateF2 = new SimpleDateFormat("yyyy-MM-dd");
        dateF.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));

        inputDate1.setText(dateF.format(currentLocalTimes));
        inputDate2.setText(dateF.format(currentLocalTimes));
        serverDate1 = dateF2.format(currentLocalTimes);
        serverDate2 = dateF2.format(currentLocalTimes);

        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(dateNo);
            }
        };

        inputDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReportActivity.this, /*android.R.style.Theme_Holo_Light_Dialog,*/ date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
                dateNo = 1;
            }
        });
        inputDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReportActivity.this, /*android.R.style.Theme_Holo_Light_Dialog,*/ date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis() - 1000);
                datePickerDialog.show();
                dateNo = 2;
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if ((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)) {

                        if ((ActivityCompat.shouldShowRequestPermissionRationale(ReportActivity.this, android.Manifest.permission.BLUETOOTH)) && (ActivityCompat.shouldShowRequestPermissionRationale(ReportActivity.this, android.Manifest.permission.BLUETOOTH_ADMIN))) {
                        } else {
                            ActivityCompat.requestPermissions(ReportActivity.this,
                                    new String[]{android.Manifest.permission.BLUETOOTH}, MY_PERMISSIONS_2);
                            ActivityCompat.requestPermissions(ReportActivity.this,
                                    new String[]{android.Manifest.permission.BLUETOOTH_ADMIN}, MY_PERMISSIONS_2);
                        }
                    } else {
                        callPrinter();
                    }
                } else {
                    callPrinter();
                }
            }
        });

        //isLoading4=true;
        //n4=0;
        //new Thread(handlerTask4).start();

        /*Cursor cTax = read.rawQuery("select trans_tax from m_transactions where trans_id IN (select trans_id from m_transactions where datetime between '2018-03-20 00:00:01' and '2018-03-20 23:59:59');", null);
        cTax.moveToFirst();
        int rowtax = cTax.getCount();
        for (int i = 0; i < rowtax; i++) {
            Toast.makeText(ReportActivity.this, "Nilai Pajak = " + cTax.getLong(0), Toast.LENGTH_SHORT).show();
            cTax.moveToNext();
        }*/

        //Cursor cTotal = read.rawQuery("select SUM(total_price) from m_transactions WHERE datetime between date('now', 'start of day') and DATETIME('now');", null);
        //Cursor cTotal = read.rawQuery("select SUM(total_price + trans_tax) from m_transactions WHERE DATE(datetime) = DATE('now');", null);
        Cursor cTotal = read.rawQuery("select SUM(total_price - trans_discount + trans_tax + trans_serv_charge) from m_transactions WHERE DATE(datetime) = DATE('now');", null);
        int rowTotal = cTotal.getCount();
        cTotal.moveToFirst();
        if(rowTotal > 0){
            String totalPenjualan = "Rp. " + df.format(cTotal.getDouble(0));
            txtTotal.setText(totalPenjualan);
        }
    }

    /*final Runnable handlerTask4 = new Runnable() {

        @Override
        public void run() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            n4++;
            //Log.d("debug","n4="+n4);
            if (n4 >= 5) {
                if (isLoading4) {
                    new getTransactions().execute();
                    new getTransactionsDetail().execute();
                    isLoading4 = false;
                    n4 = 0;
                    Thread.currentThread().interrupt();
                }
            }
            handler4.postDelayed(handlerTask4, 1000);
        }
    };*/

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "quattrocentobold.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sync, menu);
        MenuItem offline = menu.findItem(R.id.action_offline);
        MenuItem sync = menu.findItem(R.id.action_sync);
        offline.setVisible(false);
        sync.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                //new getTransactions().execute();
                //new getTransactionsDetail().execute();
                return true;
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("id_main", 1);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*@SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_printer_on) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(ReportActivity.this, "Device Not Support", Toast.LENGTH_SHORT).show();
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent,
                            REQUEST_ENABLE_BT);
                } else {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(ReportActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent,
                            REQUEST_CONNECT_DEVICE);

                }
            }
        } else if (id == R.id.nav_printer_off) {
            if (mBluetoothAdapter != null)
                mBluetoothAdapter.disable();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(), Preferences.class);
            startActivityForResult(intent, RESULT_SETTINGS);
            return true;
        } else if (id == R.id.nav_logout) {
            exitApp();
        } else if (id == R.id.nav_exit) {
            finish();
        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }*/

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (btsocket != null)
                btsocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }*/

    public void connectResult() {
        try {
            btsocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            btsocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d("IDPOS", "CouldNotConnectToSocket", eConnectException);
            closeSocket(btsocket);
            mBluetoothConnectProgressDialog.cancel();
            Toast toast = Toast.makeText(ReportActivity.this, "Gagal menyambungkan printer", Toast.LENGTH_SHORT);
            View view = toast.getView();
            view.setBackgroundColor(Color.RED);
            //view.setBackgroundResource(R.drawable.custom_bkg);
            TextView text = (TextView) view.findViewById(android.R.id.message);
            text.setTextColor(Color.WHITE);
            toast.show();
            if (mBluetoothAdapter != null)
                mBluetoothAdapter.disable();
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d("ID POS Printer Connect", "SocketClosed");
        } catch (IOException ex) {
            Log.d("ID POS Printer Connect", "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();

            /*Toast toast = Toast.makeText(ReportActivity.this, "Telah terhubung dengan printer, silakan mencetak kembali", Toast.LENGTH_SHORT);
            View view = toast.getView();
            view.setBackgroundColor(getResources().getColor(R.color.darkgreen));
            //view.setBackgroundResource(R.drawable.custom_bkg);
            TextView text = (TextView) view.findViewById(android.R.id.message);
            text.setTextColor(Color.BLACK);
            toast.show();*/
            Toast.makeText(ReportActivity.this, "Telah terhubung dengan printer, silakan mencetak kembali", Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplication(), "OK "+btsocket.toString(), Toast.LENGTH_LONG).show();
        }
    };

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    mDeviceAddress = mExtra.getString("DeviceAddress");

                    Log.v("Kasandra", "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);

                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    btName = mBluetoothDevice.getName();
                    connectResult();
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(ReportActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(ReportActivity.this, "Bluetooth activate denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void updateLabel(final int numDate) {
        String startdate = null;
        String enddate = null;
        DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(numDate == 1) {
            inputDate1.setText(sdf.format(myCalendar.getTime()));
            serverDate1 = newFormat.format(myCalendar.getTime());
            startdate = newFormat.format(myCalendar.getTime());
            h = 0;
            m = 0;
            inputDate2.setText(sdf.format(myCalendar.getTime())); // added june, 20
            serverDate2 = newFormat.format(myCalendar.getTime());
        } else {
            inputDate2.setText(sdf.format(myCalendar.getTime()));
            serverDate2 = newFormat.format(myCalendar.getTime());
            enddate = newFormat.format(myCalendar.getTime());
            h = 23;
            m = 59;
        }
        // Get Current Time
        c = Calendar.getInstance();
        //mHour = c.set(Calendar.HOUR_OF_DAY, 00);
        //mMinute = c.set(Calendar.MINUTE, 00);
        Cursor cTotal = read.rawQuery("select SUM(total_price - trans_discount + trans_tax + trans_serv_charge) from m_transactions WHERE date(datetime) between '"+ serverDate1 +"' and '"+ serverDate2 +"';", null);
        int rowTotal = cTotal.getCount();
        cTotal.moveToFirst();
        if(cTotal.getInt(0) > 0){
            String totalPenjualan = "Rp. " + df.format(cTotal.getDouble(0));
            txtTotal.setText(totalPenjualan);
        } else {
            getTotalfrServer(serverDate1, serverDate2);
        }
        /*// Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String curTime = String.format("%02d:%02d", hourOfDay, minute);
                        if(numDate == 1) {
                            inputTime1.setText(curTime);
                            time1 = curTime+":00";
                        } else {
                            inputTime2.setText(curTime);
                            time2 = curTime+":59";
                        }
                        //Toast.makeText(ReportActivity.this, "Transaksi: between "+ serverDate1 +" and "+ serverDate2, Toast.LENGTH_SHORT).show();

                    }
                }, h, m, true);
        timePickerDialog.show();*/
    }

    private void getTotalfrServer(String date1, String date2) {
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(sURL+"?get_total=" + session.nOutlet_ID() + "&date1=" + date1 + "&date2=" + date2);

        if (entry2 != null) {
            try {
                String data = new String(entry2.data, "UTF-8");
                try {
                    parseJsonTotal(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            JsonObjectRequest jsonReq2 = new JsonObjectRequest(Request.Method.GET,
                    sURL+"?get_total=" + session.nOutlet_ID() + "&date1=" + date1 + "&date2=" + date2, null, new Response.Listener<JSONObject>() {
                //"https://kasandra.biz/wbagenpass.json", null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    parseJsonTotal(response);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("kasandra debug", "Error gettax: " + error.getMessage());
                }
            });
            jsonReq2.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            AppController.getInstance().addToRequestQueue(jsonReq2);
        }
    }

    private void parseJsonTotal(JSONObject response) {
        try {
            boolean error = response.getBoolean("error");
            if (!error) {
            String returnmessage = response.getString("msg");
            JSONArray params = response.getJSONArray("details");
            for (int i = 0; i < params.length(); i++) {
                JSONObject obj = params.getJSONObject(i);
                final int total = obj.getInt("total");
                String totalPenjualan = "Rp. " + df.format(total);
                txtTotal.setText(totalPenjualan);
            }
            } else {
                String errorMsg = response.getString("error_msg");
                Toast.makeText(getApplicationContext(),
                        errorMsg, Toast.LENGTH_LONG).show();
            }
            //pDialog.dismiss();
        } catch (JSONException e) {
            //pDialog.dismiss();
            e.printStackTrace();
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v("ID POS Printer Connect", "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPrinter();
                } else {
                }
                return;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    Intent i = new Intent(getApplicationContext(),
                            MainActivity.class);
                    i.putExtra("id_main", 1);
                    startActivity(i);
                    finish();
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec(command).waitFor() == 0);
    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            // if connected with internet
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            return false;
        }
        return false;
    }

    private class getTransactions extends AsyncTask<String, Integer, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("client_id", session.nOutlet_ID());

                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl(sURL, params);
                Log.d("kasandra", "kasandra debug : jsonObject=" + jsonObject.toString());

                return json;
            } catch (Exception e) {
                Log.d("kasandra", "kasandra debug err3: " + e.getMessage());
                return null;
            }
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            try {
                boolean error = json.getBoolean("error");
                if (!error) {
                    String returnmessage = json.getString("msg");
                    JSONArray params = json.getJSONArray("details");
                    if(params.length() > 0){
                        //write.execSQL("DELETE FROM m_transactions");
                    }

                    int trans_no;
                    for(int i=0; i<params.length(); i++) {
                        JSONObject obj = params.getJSONObject(i);

                        int id = Integer.parseInt(obj.getString("trans_id"));
                        String total_qty = obj.getString("total_qty");
                        String total_price = obj.getString("total_price");
                        String datetime = obj.getString("datetime");
                        String payment_type = obj.getString("payment_type");
                        String tax = obj.getString("trans_tax");
                        String disc = obj.getString("trans_disc");
                        String serv_charge = obj.getString("trans_serv_charge");
                        final int total_trans = obj.getInt("trans_total");
                        trans_no = total_trans-1;
                        final int finalTrans_no = trans_no;

                        if(total_qty.equals("") || total_qty.equals("null")){
                            total_qty = "0";
                        }
                        if(total_price.equals("") || total_price.equals("null")){
                            total_price = "0";
                        }
                        if(tax.equals("") || tax.equals("null")){
                            tax = "0";
                        }
                        if(disc.equals("") || disc.equals("null")){
                            disc = "0";
                        }
                        if(serv_charge.equals("") || serv_charge.equals("null")){
                            serv_charge = "0";
                        }
                        write.execSQL("INSERT INTO m_transactions VALUES(" + id + ", '" + total_qty + "', '" + total_price + "', '" + datetime + "', 1, " + payment_type +", " + tax +", " + disc +", " + finalTrans_no +", " + serv_charge +");");
                    }
                } else {
                    String errorMsg = json.getString("error_msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    private class getTransactionsDetail extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected void onPreExecute() {
            pDialog.setMessage("Sinkronisasi sedang berlangsung...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("client_id_detail", session.nOutlet_ID());

                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl(sURL, params);
                Log.d("kasandra", "kasandra debug : jsonObject=" + jsonObject.toString());

                return json;
            } catch (Exception e) {
                Log.d("kasandra", "kasandra debug err3: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

            try {
                boolean error = json.getBoolean("error");

                if (!error) {

                    String returnmessage = json.getString("msg");
                    JSONArray params = json.getJSONArray("details");

                    //Toast.makeText(getApplicationContext(), params.toString(), Toast.LENGTH_SHORT).show();

                    if(params.length() > 0){
                        //write.execSQL("DELETE FROM m_transactions_detail");
                    }

                    for(int i=0; i<params.length(); i++) {
                        JSONObject obj = params.getJSONObject(i);

                        int trans_det_id = Integer.parseInt(obj.getString("trans_det_id"));
                        int trans_id = Integer.parseInt(obj.getString("trans_id"));
                        int prod_id = Integer.parseInt(obj.getString("prod_id"));

                        write.execSQL("INSERT INTO m_transactions_detail VALUES(" + trans_det_id + ", " + trans_id + ", " + prod_id + ", 1);");
                    }

                    pDialog.dismiss();
                } else {
                    String errorMsg = json.getString("error_msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                }
            } catch (Exception e) {
                pDialog.dismiss();
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
    private void callPrinter() {
        //AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ReportActivity.this, android.R.style.Theme_Holo_Light_Dialog));
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        builder.setTitle("Cetak Transaksi Harian");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (btsocket == null) {
                            Toast.makeText(getApplication(), "Silakan hubungkan printer terlebih dahulu", Toast.LENGTH_LONG).show();
                            //drawer.openDrawer(Gravity.LEFT);
                        } else {
                            printReceipt();
                        }
                    }
                }
        );
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.cancel();
                        alertDialog1.dismiss();
                    }
                }

        );
        alertDialog1 = builder.create();
        alertDialog1.setCanceledOnTouchOutside(false);
        alertDialog1.show();
    }

    private void printReceipt() {
        try {
            btoutputstream = btsocket
                    .getOutputStream();
            Calendar cal_ = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
            Date currentLocalTime_ = cal_.getTime();
            DateFormat dateCounter_ = new SimpleDateFormat("dd MMM yyyy HH:mm");

            dateCounter_.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
            String localTimeCounter = dateCounter_.format(currentLocalTime_);
            String outlet = session.sOutlet();
            String user_name = session.sUserName();

            resetPrint();
            byte[] printformat = {0x1B, 0x21, FONT_TYPE};
            btoutputstream.write(printformat);

            //if (btName.startsWith("RP58")) {
            if (session.nPrinterChars() == 32) {
                printTitle("Pelaporan "+outlet);
                printNewLine();
                printUnicodeforReset();
                printText1("Tgl.Mulai : "+serverDate1+" "+time1);
                printText("\n");
                printText1("Tgl.Akhir : "+serverDate2+" "+time2);
                printText("\n");
                printUnicode3();
                printText1("\n");
                //printText1("  Produk             Penjualan");
                //printText1("\n");
                //printUnicode3();
                //printText1("\n");
                int count = 0;
                int count1 = 0;
                String fromDate = serverDate1+" "+time1;
                String toDate = serverDate2+" "+time2;
                String name;
                Cursor cRead = read.rawQuery("select a.trans_det_id, count(a.prod_id) as qty_, count(a.prod_id) * b.prod_price_sell as totalprice, b.prod_id, b.prod_code, b.prod_name from m_transactions_detail a, m_products b where a.prod_id=b.prod_id AND a.trans_id IN (select trans_id from m_transactions where datetime between '"+fromDate+"' and '"+toDate+"') GROUP BY a.prod_id ORDER BY b.prod_name ASC;", null);
                int rows = cRead.getCount();
                Cursor cTax = read.rawQuery("select sum(trans_tax) from m_transactions where trans_id IN (select trans_id from m_transactions where datetime between '"+fromDate+"' and '"+toDate+"');", null);
                //int rowsTax = cTax.getCount();
                Cursor cDisc = read.rawQuery("select sum(trans_discount) from m_transactions where trans_id IN (select trans_id from m_transactions where datetime between '"+fromDate+"' and '"+toDate+"');", null);
                //int rowsDisc = cDisc.getCount();
                Cursor cSC = read.rawQuery("select sum(trans_serv_charge) from m_transactions where trans_id IN (select trans_id from m_transactions where datetime between '"+fromDate+"' and '"+toDate+"');", null);
                if (rows > 0) {
                    cRead.moveToFirst();
                    cTax.moveToFirst();
                    cDisc.moveToFirst();
                    cSC.moveToFirst();
                    for (int i = 0; i < rows; i++) {
                        name = cRead.getString(4)+"-"+cRead.getString(5);
                        if (name.length() >= 29) {
                            String lastWord = name.substring(name.lastIndexOf(" ") + 1);
                            printText1(name.replace(" " + lastWord, ""));
                            printText1("\n");
                            printText1(lastWord);
                            resetNoSpace();
                            btoutputstream.write(PrinterCommands.BS);
                            resetNoSpace();
                            int lWLength = lastWord.length();
                            for (int j = 0; j < (29 - lWLength); j++) {
                                printText(Utils.ONE_SPACE);
                            }
                        } else {
                            int length = name.length();
                            printText1(name);
                            for (int j = 0; j < (29 - length); j++) {
                                printText(Utils.ONE_SPACE);
                            }
                        }
                        String quantity = cRead.getString(1);
                        int qLength = quantity.length();
                        if (qLength == 3) {
                        } else if (qLength == 2) {
                            printText(Utils.ONE_SPACE);
                        } else if (qLength == 1) {
                            printText(Utils.ONE_SPACE);
                            printText(Utils.ONE_SPACE);
                        }
                        printText1(quantity);
                        //String totalprice = cRead.getString(2);
                        printText1("Rp" + String.valueOf(df.format(cRead.getInt(2))));
                        printText1("\n");
                        count = count + cRead.getInt(1);
                        count1 = count1 + cRead.getInt(2);
                        cRead.moveToNext();
                    }

                    printUnicode4();
                    printText("\n");
                    printText1("Item           : ");
                    printText1(String.valueOf(count));
                    printText("\n");

                    printText1("Subtotal       : ");
                    printText1("Rp" + String.valueOf(df.format(count1)));
                    printText("\n");

                    printText1("Diskon         : ");
                    printText1("Rp" + String.valueOf(df.format(cDisc.getInt(0))));
                    printText("\n");

                    printText1("Service charge : ");
                    printText1("Rp"+ String.valueOf(df.format(cSC.getInt(0))));
                    printText1("\n");

                    printText1("Pajak          : ");
                    printText1("Rp" + String.valueOf(df.format(cTax.getInt(0))));
                    printText("\n");

                    printText1("Total          : ");
                    printText1("Rp" + String.valueOf(df.format(count1 + cTax.getInt(0) + cSC.getInt(0) - cDisc.getInt(0))));
                    printText("\n");

                    printUnicode4();
                    printText("\n");
                    printText1("Kasir          : " + user_name);
                    printText("\n");
                    printText1("Tgl.Print      : " + localTimeCounter);
                    printText("\n");
                    printText1("\n");
                    printText1("\n");
                } else {
                    print32();
                }
            } else {
                printTitle("Pelaporan "+outlet);
                printNewLine();
                printUnicodeforReset();
                printText1("Tgl.Mulai : "+serverDate1+" "+time1);
                printText("\n");
                printText1("Tgl.Akhir : "+serverDate2+" "+time2);
                printText("\n");
                printUnicode();
                printText1("\n");
                //printText1("  Produk                       Penjualan");
                //printText1("\n");
                //printUnicode();
                //printText1("\n");
                int count = 0;
                int count1 = 0;
                String fromDate = serverDate1+" "+time1;
                String toDate = serverDate2+" "+time2;
                String name;
                Cursor cRead = read.rawQuery("select a.trans_det_id, count(a.prod_id) as qty_, count(a.prod_id) * b.prod_price_sell as totalprice, b.prod_id, b.prod_code, b.prod_name from m_transactions_detail a, m_products b where a.prod_id=b.prod_id AND a.trans_id IN (select trans_id from m_transactions where datetime between '"+fromDate+"' and '"+toDate+"') GROUP BY a.prod_id ORDER BY b.prod_name ASC;", null);
                int rows = cRead.getCount();
                Cursor cTax = read.rawQuery("select sum(trans_tax) from m_transactions where trans_id IN (select trans_id from m_transactions where datetime between '"+fromDate+"' and '"+toDate+"');", null);
                //int rowsTax = cTax.getCount();
                Cursor cDisc = read.rawQuery("select sum(trans_discount) from m_transactions where trans_id IN (select trans_id from m_transactions where datetime between '"+fromDate+"' and '"+toDate+"');", null);
                //int rowsDisc = cDisc.getCount();
                Cursor cSC = read.rawQuery("select sum(trans_serv_charge) from m_transactions where trans_id IN (select trans_id from m_transactions where datetime between '"+fromDate+"' and '"+toDate+"');", null);
                if (rows > 0) {
                    cRead.moveToFirst();
                    cTax.moveToFirst();
                    cDisc.moveToFirst();
                    cSC.moveToFirst();
                    for (int i = 0; i < rows; i++) {
                        name = cRead.getString(4)+"-"+cRead.getString(5);
                        if (name.length() >= 39) {
                            String lastWord = name.substring(name.lastIndexOf(" ") + 1);
                            printText1(name.replace(" " + lastWord, ""));
                            printText1("\n");
                            printText1(lastWord);
                            resetNoSpace();
                            btoutputstream.write(PrinterCommands.BS);
                            resetNoSpace();
                            int lWLength = lastWord.length();
                            for (int j = 0; j < (39 - lWLength); j++) {
                                printText(Utils.ONE_SPACE);
                            }
                        } else {
                            int length = name.length();
                            printText1(name);
                            for (int j = 0; j < (39 - length); j++) {
                                printText(Utils.ONE_SPACE);
                            }
                        }
                        String quantity = cRead.getString(1);
                        int qLength = quantity.length();
                        if (qLength == 3) {
                        } else if (qLength == 2) {
                            printText(Utils.ONE_SPACE);
                        } else if (qLength == 1) {
                            printText(Utils.ONE_SPACE);
                            printText(Utils.ONE_SPACE);
                        }
                        printText1(quantity);
                        //String totalprice = cRead.getString(2);
                        printText1("Rp" + String.valueOf(df.format(cRead.getInt(2))));
                        printText1("\n");
                        count = count + cRead.getInt(1);
                        count1 = count1 + cRead.getInt(2);
                        cRead.moveToNext();
                    }

                    printUnicode4();
                    printText("\n");
                    printText1("Item                : ");
                    printText1(String.valueOf(count));
                    printText("\n");

                    printText1("Subtotal            : ");
                    printText1("Rp" + String.valueOf(df.format(count1)));
                    printText("\n");

                    printText1("Diskon              : ");
                    printText1("Rp" + String.valueOf(df.format(cDisc.getInt(0))));
                    printText("\n");

                    printText1("Service charge      : ");
                    printText1("Rp"+ String.valueOf(df.format(cSC.getInt(0))));
                    printText1("\n");

                    printText1("Pajak               : ");
                    printText1("Rp" + String.valueOf(df.format(cTax.getInt(0))));
                    printText("\n");

                    printText1("Total               : ");
                    printText1("Rp" + String.valueOf(df.format(count1 + cTax.getInt(0) + cSC.getInt(0) - cDisc.getInt(0))));
                    printText("\n");

                    printUnicode4();
                    printText("\n");
                    printText1("Kasir               : " + user_name);
                    printText("\n");
                    printText1("Tgl.Print           : " + localTimeCounter);
                    printText("\n");
                    printText1("\n");
                    printText1("\n");
                } else {
                    print42();
                }
            }
        } catch (Exception e) {
            Log.e("Main", "Exe ", e);
        }
    }

    private void print32() {
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(sURL+"?get_transactions=" + session.nOutlet_ID() + "&date1=" + serverDate1 + "&date2=" + serverDate2);

        if (entry2 != null) {
            try {
                String data = new String(entry2.data, "UTF-8");
                try {
                    parseJson32(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            JsonObjectRequest jsonReq2 = new JsonObjectRequest(Request.Method.GET,
                    sURL+"?get_transactions=" + session.nOutlet_ID() + "&date1=" + serverDate1 + "&date2=" + serverDate2, null, new Response.Listener<JSONObject>() {
                //"https://kasandra.biz/wbagenpass.json", null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    parseJson32(response);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("kasandra debug", "Error: " + error.getMessage());
                }
            });
            jsonReq2.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            AppController.getInstance().addToRequestQueue(jsonReq2);
        }
    }

    private void parseJson32(JSONObject response) {
        try {
            boolean error = response.getBoolean("error");
            DateFormat dateCounter_ = new SimpleDateFormat("dd MMM yyyy HH:mm");
            Calendar cal_ = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
            Date currentLocalTime_ = cal_.getTime();
            dateCounter_.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
            String localTimeCounter = dateCounter_.format(currentLocalTime_);
            if (!error) {
                String returnmessage = response.getString("msg");
                int total_qty = response.getInt("total_qty");
                int total_price = response.getInt("total_price");
                int tax = response.getInt("tax");
                int disc = response.getInt("disc");
                int sc = response.getInt("sc");
                JSONArray params = response.getJSONArray("transdetails");
                printTitle("Pelaporan "+session.sOutlet());
                printNewLine();
                printUnicodeforReset();
                printText1("Tgl.Mulai : "+serverDate1+" "+time1);
                printText("\n");
                printText1("Tgl.Akhir : "+serverDate2+" "+time2);
                printText("\n");
                printUnicode3();
                printText1("\n");
                String name;
                if (params.length() > 0) {
                    for (int i = 0; i < params.length(); i++) {
                        JSONObject obj = params.getJSONObject(i);
                        name = obj.getString("product_code")+"-"+obj.getString("product");
                        if (name.length() >= 29) {
                            String lastWord = name.substring(name.lastIndexOf(" ") + 1);
                            printText1(name.replace(" " + lastWord, ""));
                            printText1("\n");
                            printText1(lastWord);
                            resetNoSpace();
                            try {
                                btoutputstream.write(PrinterCommands.BS);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            resetNoSpace();
                            int lWLength = lastWord.length();
                            for (int j = 0; j < (29 - lWLength); j++) {
                                printText(Utils.ONE_SPACE);
                            }
                        } else {
                            int length = name.length();
                            printText1(name);
                            for (int j = 0; j < (29 - length); j++) {
                                printText(Utils.ONE_SPACE);
                            }
                        }
                        String quantity = obj.getString("qty");
                        int qLength = quantity.length();
                        if (qLength == 3) {
                        } else if (qLength == 2) {
                            printText(Utils.ONE_SPACE);
                        } else if (qLength == 1) {
                            printText(Utils.ONE_SPACE);
                            printText(Utils.ONE_SPACE);
                        }
                        printText1(quantity);
                        //String totalprice = cRead.getString(2);
                        printText1("Rp" + String.valueOf(df.format(obj.getInt("price"))));
                        printText1("\n");
                    }

                    printUnicode4();
                    printText("\n");
                    printText1("Item           : ");
                    printText1(String.valueOf(total_qty));
                    printText("\n");

                    printText1("Subtotal       : ");
                    printText1("Rp" + String.valueOf(df.format(total_price)));
                    printText("\n");

                    printText1("Diskon         : ");
                    printText1("Rp" + String.valueOf(df.format(disc)));
                    printText("\n");

                    printText1("Service charge : ");
                    printText1("Rp"+ String.valueOf(df.format(sc)));
                    printText1("\n");

                    printText1("Pajak          : ");
                    printText1("Rp" + String.valueOf(df.format(tax)));
                    printText("\n");

                    printText1("Total          : ");
                    printText1("Rp" + String.valueOf(df.format(total_price + tax + sc - disc)));
                    printText("\n");

                    printUnicode4();
                    printText("\n");
                    printText1("Kasir          : " + session.sFullName());
                    printText("\n");
                    printText1("Tgl.Print      : " + localTimeCounter);
                    printText("\n");
                    printText1("\n");
                    printText1("\n");
                } else {
                    String errorMsg = response.getString("msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            } else {
                String errorMsg = response.getString("error_msg");
                Toast.makeText(getApplicationContext(),
                        errorMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void print42() {
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(sURL+"?get_transactions=" + session.nOutlet_ID() + "&date1=" + serverDate1 + "&date2=" + serverDate2);

        if (entry2 != null) {
            try {
                String data = new String(entry2.data, "UTF-8");
                try {
                    parseJson42(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            JsonObjectRequest jsonReq2 = new JsonObjectRequest(Request.Method.GET,
                    sURL+"?get_transactions=" + session.nOutlet_ID() + "&date1=" + serverDate1 + "&date2=" + serverDate2, null, new Response.Listener<JSONObject>() {
                //"https://kasandra.biz/wbagenpass.json", null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    parseJson42(response);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("kasandra debug", "Error: " + error.getMessage());
                }
            });
            jsonReq2.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            AppController.getInstance().addToRequestQueue(jsonReq2);
        }
    }

    private void parseJson42(JSONObject response) {
        try {
            boolean error = response.getBoolean("error");
            DateFormat dateCounter_ = new SimpleDateFormat("dd MMM yyyy HH:mm");
            Calendar cal_ = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
            Date currentLocalTime_ = cal_.getTime();
            dateCounter_.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
            String localTimeCounter = dateCounter_.format(currentLocalTime_);
            if (!error) {
                String returnmessage = response.getString("msg");
                int total_qty = response.getInt("total_qty");
                int total_price = response.getInt("total_price");
                int tax = response.getInt("tax");
                int disc = response.getInt("disc");
                int sc = response.getInt("sc");
                JSONArray params = response.getJSONArray("transdetails");

                printTitle("Pelaporan "+session.sOutlet());
                printNewLine();
                printUnicodeforReset();
                printText1("Tgl.Mulai : "+serverDate1+" "+time1);
                printText("\n");
                printText1("Tgl.Akhir : "+serverDate2+" "+time2);
                printText("\n");
                printUnicode();
                printText1("\n");
                String name;
                if (params.length() > 0) {
                    for (int i = 0; i < params.length(); i++) {
                        JSONObject obj = params.getJSONObject(i);
                        name = obj.getString("product_code")+"-"+obj.getString("product");
                        if (name.length() >= 39) {
                            String lastWord = name.substring(name.lastIndexOf(" ") + 1);
                            printText1(name.replace(" " + lastWord, ""));
                            printText1("\n");
                            printText1(lastWord);
                            resetNoSpace();
                            try {
                                btoutputstream.write(PrinterCommands.BS);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            resetNoSpace();
                            int lWLength = lastWord.length();
                            for (int j = 0; j < (39 - lWLength); j++) {
                                printText(Utils.ONE_SPACE);
                            }
                        } else {
                            int length = name.length();
                            printText1(name);
                            for (int j = 0; j < (39 - length); j++) {
                                printText(Utils.ONE_SPACE);
                            }
                        }
                        String quantity = obj.getString("qty");
                        int qLength = quantity.length();
                        if (qLength == 3) {
                        } else if (qLength == 2) {
                            printText(Utils.ONE_SPACE);
                        } else if (qLength == 1) {
                            printText(Utils.ONE_SPACE);
                            printText(Utils.ONE_SPACE);
                        }
                        printText1(quantity);
                        //String totalprice = cRead.getString(2);
                        printText1("Rp" + String.valueOf(df.format(obj.getInt("price"))));
                        printText1("\n");
                    }

                    printUnicode4();
                    printText("\n");
                    printText1("Item                : ");
                    printText1(String.valueOf(total_qty));
                    printText("\n");

                    printText1("Subtotal            : ");
                    printText1("Rp" + String.valueOf(df.format(total_price)));
                    printText("\n");

                    printText1("Diskon              : ");
                    printText1("Rp" + String.valueOf(df.format(disc)));
                    printText("\n");

                    printText1("Service charge      : ");
                    printText1("Rp"+ String.valueOf(df.format(sc)));
                    printText1("\n");

                    printText1("Pajak               : ");
                    printText1("Rp" + String.valueOf(df.format(tax)));
                    printText("\n");

                    printText1("Total               : ");
                    printText1("Rp" + String.valueOf(df.format(total_price + tax + sc - disc)));
                    printText("\n");

                    printUnicode4();
                    printText("\n");
                    printText1("Kasir               : " + session.sFullName());
                    printText("\n");
                    printText1("Tgl.Print           : " + localTimeCounter);
                    printText("\n");
                    printText1("\n");
                    printText1("\n");
                } else {
                    String errorMsg = response.getString("msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            } else {
                String errorMsg = response.getString("error_msg");
                Toast.makeText(getApplicationContext(),
                        errorMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /*-------------------- printer setting --------------------*/
    //print Title
    public static void printTitle(String msg) {
        try {
            //Print config
            byte[] bb = new byte[]{0x1B,0x21,0x08};
            byte[] bb2 = new byte[]{0x1B,0x21,0x20};
            byte[] bb3 = new byte[]{0x1B,0x21,0x10};
            byte[] cc = new byte[]{0x1B,0x21,0x00};

            btoutputstream.write(bb3);

            //set text into center
            btoutputstream.write(PrinterCommands.ESC_ALIGN_CENTER);
            btoutputstream.write(msg.getBytes());
            btoutputstream.write(PrinterCommands.LF);
            btoutputstream.write(PrinterCommands.LF);
            btoutputstream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //print unicode
    public static void printUnicodeforReset(){
        try {
            btoutputstream.write(PrinterCommands.ESC_ALIGN_CENTER);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //print unicode
    public static void printUnicode(){
        try {
            btoutputstream.write(PrinterCommands.ESC_ALIGN_CENTER);
            printText(Utils.UNICODE_TEXT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void printUnicode3(){
        try {
            btoutputstream.write(PrinterCommands.ESC_ALIGN_CENTER);
            printText(Utils.UNICODE_TEXT3);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //print unicode
    public static void printUnicode4(){
        try {
            btoutputstream.write(PrinterCommands.ESC_ALIGN_CENTER);
            printText(Utils.UNICODE_TEXT4);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //print new line
    public static void printNewLine() {
        try {
            btoutputstream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void resetPrint() {
        try{
            //btoutputstream.write(PrinterCommands.ESC_FONT_COLOR_DEFAULT);
            btoutputstream.write(PrinterCommands.FS_FONT_ALIGN);
            btoutputstream.write(PrinterCommands.ESC_ALIGN_LEFT);
            btoutputstream.write(PrinterCommands.ESC_CANCEL_BOLD);
            btoutputstream.write(PrinterCommands.SELECT_FONT_A);
            btoutputstream.write(PrinterCommands.LF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void resetNoSpace() {
        try{
            btoutputstream.write(PrinterCommands.STX);
            //btoutputstream.write(PrinterCommands.ESC_FONT_COLOR_DEFAULT);
            btoutputstream.write(PrinterCommands.FS_FONT_ALIGN);
            btoutputstream.write(PrinterCommands.ESC_ALIGN_LEFT);
            btoutputstream.write(PrinterCommands.ESC_CANCEL_BOLD);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //print text
    public static void printText(String msg) {
        try {
            // Print normal text
            btoutputstream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //print text
    public static void printText1(String msg) {
        try {
            btoutputstream.write(PrinterCommands.ESC_ALIGN_LEFT);
            btoutputstream.write(PrinterCommands.FS_FONT_ALIGN);
            // Print normal text
            btoutputstream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //print byte[]
    public static void printText(byte[] msg) {
        try {
            // Print normal text
            btoutputstream.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /*-------------------- end printer setting --------------------*/
    public void showMessage(String title, String message)
    {
        //AlertDialog.Builder builder=new AlertDialog.Builder(new ContextThemeWrapper(ReportActivity.this, android.R.style.Theme_Holo_Light_Dialog));
        AlertDialog.Builder builder=new AlertDialog.Builder(ReportActivity.this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

}
