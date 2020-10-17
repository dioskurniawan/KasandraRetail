package id.kasandra.retail;

import android.Manifest;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import id.kasandra.retail.bluetoothprint.*;
import id.kasandra.retail.bluetoothprint.Utils;
import mehdi.sakout.fancybuttons.FancyButton;

import static java.lang.Integer.parseInt;


/**
 * Created by Joko on 3/22/2017.
 */

public class TransactionActivity extends AppCompatActivity {

    private static String sURL = "https://kasandra.biz/appdata/getdata_dev2.php";
    private ArrayList<HashMap<String, String>> transactionlist = new ArrayList<HashMap<String, String>>();
    private ListView list;
    public static final String TAG_TRANSACTION = "transaction";
    public static final String TAG_LASTUPDATE = "lastupdate";
    public static final String TAG_NO = "trans_no";
    public static final String TAG_ID = "trans_id";
    public static final String TAG_REALNO = "trans_total";
    public static final String TAG_QTY = "qty";
    public static final String TAG_PRICE = "price";
    public static final String TAG_DATE = "updated_date";
    private static final int RESULT_SETTINGS = 2;

    public String sLastUpdate;
    public JSONArray transaction = null;
    public String sQuery = "";
    private static final int MY_PERMISSIONS_1 = 0;
    private static final int MY_PERMISSIONS_2 = 1;

    private int nClientId = 0;
    private static SessionManager session;
    private ProgressDialog pDialog;
    final Handler handler = new Handler();
    final Handler handler2 = new Handler();
    boolean isLoading2 = false;
    private int printType = 0;
    int n = 0;
    int count;
    TableLayout table_layout;
    ScrollView scroll;
    SQLiteHandler db;
    SQLiteDatabase write;
    SQLiteDatabase read;
    Typeface font1, font2;
    DecimalFormat df;
    TextView toolbar_title, trans_no, trans_date, trans_total, trans_total2, trans_total3, trans_total4, trans_payment;
    FloatingActionButton fab_a, fab_b, fab_c, fab_d;
    Cursor cursor;
    SimpleDateFormat formatter, sdf;
    private String[] sProduct, nPrice, nQty, nID, sPhoto;
    FancyButton btnPrint, btnFilter, btnAll;
    byte FONT_TYPE;
    private BluetoothSocket btsocket;
    private static OutputStream btoutputstream;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBluetoothDevice;
    CharSequence[] values;
    int[] currentChoice;
    AlertDialog alertDialog1;
    ProgressDialog mBluetoothPDialog;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private String btName;

    private ExpandableListView expList;
    private ExpandableListAdapter expListAdapter;
    ArrayList<TransactionItem> weekItems;
    ArrayList<TransactionItem> allItems;
    AlertDialog alertDialog;
    private EditText inputDate1, inputDate2;
    private String serverDate1 = "0";
    private String serverDate2 = "0";
    Calendar myCalendar;
    int dateNo;
    static boolean internetAvailable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText("Transaksi Penjualan");

        session = new SessionManager(this.getApplicationContext());

        internetAvailable = session.isInternetAvailable();

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
                    Toast.makeText(TransactionActivity.this, "Device Not Support", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(TransactionActivity.this,
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

        db = new SQLiteHandler(getApplicationContext());
        write = db.getWritableDatabase();
        read = db.getReadableDatabase();

        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        trans_no = (TextView) findViewById(R.id.trans_no);
        trans_date = (TextView) findViewById(R.id.trans_date2);
        trans_total = (TextView) findViewById(R.id.trans_total2);
        trans_total2 = (TextView) findViewById(R.id.trans_total3);
        trans_total3 = (TextView) findViewById(R.id.trans_total4);
        trans_total4 = (TextView) findViewById(R.id.trans_total5);
        trans_payment = (TextView) findViewById(R.id.trans_payment2);
        TextView trans_date1 = (TextView) findViewById(R.id.trans_date1);
        TextView trans_total1 = (TextView) findViewById(R.id.trans_total1);
        TextView trans_payment1 = (TextView) findViewById(R.id.trans_payment1);
        TextView trans_prod1 = (TextView) findViewById(R.id.trans_prod1);
        btnPrint = (FancyButton) findViewById(R.id.btn_print);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)) {

                        if ((ActivityCompat.shouldShowRequestPermissionRationale(TransactionActivity.this, Manifest.permission.BLUETOOTH)) && (ActivityCompat.shouldShowRequestPermissionRationale(TransactionActivity.this, Manifest.permission.BLUETOOTH_ADMIN))) {
                        } else {
                            ActivityCompat.requestPermissions(TransactionActivity.this,
                                    new String[]{Manifest.permission.BLUETOOTH}, MY_PERMISSIONS_2);
                            ActivityCompat.requestPermissions(TransactionActivity.this,
                                    new String[]{Manifest.permission.BLUETOOTH_ADMIN}, MY_PERMISSIONS_2);
                        }
                    } else {
                        //connect();
                        callPrinter();
                    }
                } else {
                    //connect();
                    callPrinter();
                }
            }
        });
        btnFilter = (FancyButton) findViewById(R.id.btn_filter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterByDate();
            }
        });
        btnAll = (FancyButton) findViewById(R.id.btn_all);
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverDate1 = "0";
                serverDate2 = "0";
                new populateTransList().execute(serverDate1+"!"+serverDate2);
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showDetail();
                    }
                }, 3000);
            }
        });

        if(session.screenSize() < 7.9) {
            toolbar_title.setTextSize(20);
            trans_no.setTextSize(20);
            trans_date.setTextSize(16);
            trans_total.setTextSize(16);
            trans_total2.setTextSize(13);
            trans_total3.setTextSize(13);
            trans_total4.setTextSize(13);
            trans_payment.setTextSize(16);
            trans_date1.setTextSize(16);
            trans_total1.setTextSize(16);
            trans_payment1.setTextSize(16);
            trans_prod1.setTextSize(16);
        } else {
            toolbar_title.setTextSize(25);
            trans_no.setTextSize(25);
            trans_date.setTextSize(20);
            trans_total.setTextSize(20);
            trans_total2.setTextSize(16);
            trans_total3.setTextSize(16);
            trans_total4.setTextSize(16);
            trans_payment.setTextSize(20);
            trans_date1.setTextSize(20);
            trans_total1.setTextSize(20);
            trans_payment1.setTextSize(20);
            trans_prod1.setTextSize(20);
        }

        table_layout = (TableLayout) findViewById(R.id.tableLaporan);

        font1 = Typeface.createFromAsset(getAssets(), "quattrocentobold.ttf");
        font2 = Typeface.createFromAsset(getAssets(), "GenBasR.ttf");

        formatter = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');
        df = new DecimalFormat("#,###", otherSymbols);

        pDialog = new ProgressDialog(new ContextThemeWrapper(TransactionActivity.this, android.R.style.Theme_Holo_Light_Dialog));
        session = new SessionManager(this.getApplicationContext());

        if (nClientId == 0) {
            nClientId = session.nClient_ID();
        }
        // check nClientId from persistence store
        if (session.nClient_ID() != -999) {
            nClientId = session.nClient_ID();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_1);
                }
            } else {
                new populateTransList().execute(serverDate1 + "!" + serverDate2);
                //Toast.makeText(getApplicationContext(), "Populate 1", Toast.LENGTH_LONG).show();
                //isLoading2 = true;
                //new Thread(handlerTask2).start();
                //handlerTask2.run();
                //showDetail();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showDetail();
                        //Toast.makeText(getApplicationContext(), "Call Find-P(5)", Toast.LENGTH_LONG).show();
                    }
                }, 3000);
            }
        } else {
            new populateTransList().execute(serverDate1 + "!" + serverDate2);
            //Toast.makeText(getApplicationContext(), "Populate 2", Toast.LENGTH_LONG).show();
            //isLoading2 = true;
            //new Thread(handlerTask2).start();
            //handlerTask2.run();
            //showDetail();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showDetail();
                    //Toast.makeText(getApplicationContext(), "Call Find-P(5)", Toast.LENGTH_LONG).show();
                }
            }, 3000);
        }

        currentChoice = new int[]{0};
        values = new CharSequence[]{"Pilih Printer", "Nonaktifkan Printer", "Cetak Bill"};

        expList = (ExpandableListView) findViewById(R.id.expandableList);
        if (session.screenSize() < 6.5) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    385, LinearLayout.LayoutParams.MATCH_PARENT);
            //expList.setLayoutParams(new TableRow.LayoutParams(385, TableRow.LayoutParams.MATCH_PARENT));
            layoutParams.setMargins(0,0,0,80);
            expList.setLayoutParams(layoutParams);
        }
        expListAdapter = new ExpandableListAdapter(TransactionActivity.this, getApplicationContext());
        expList.setGroupIndicator(null);

        weekItems = new ArrayList<TransactionItem>();
        allItems = new ArrayList<TransactionItem>();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
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

    private class populateTransList extends AsyncTask<String, Integer, JSONObject> {
        //private TransListAdapter adapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //transactionlist.clear();
            try {
                pDialog.setMessage("Sedang mengambil data...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
                //new Thread(handlerTask).start();
                /*handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(pDialog != null || pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                        Toast.makeText(getApplicationContext(), "Silakan muat ulang halaman ini", Toast.LENGTH_SHORT).show();
                    }
                }, 20000);*/
                //handlerTask.run();
            } catch (Exception e) {

            }
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String extractString = args[0];
            String[] result = extractString.split("!");

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("trans_user_id", session.nOutlet_ID());
                jsonObject.put("start_date", result[0]);
                jsonObject.put("end_date", result[1]);

                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl(sURL, params);
                Log.d("kasandra", "kasandra debug : jsonObjectres=" + json.toString());

                return json;
            } catch (Exception e) {
                Log.d("kasandra", "kasandra debug err3: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                    expList.setAdapter((BaseExpandableListAdapter)null);
                    weekItems.clear();
                    allItems.clear();

                    //Log.d("zzzz", "zzzz: " + params.toString());
                    if(serverDate1.equals("0")) {
                        Cursor cLast = read.rawQuery("SELECT MAX(datetime) max_ts FROM m_transactions;", null);
                        if(cLast.getCount() > 0) {
                            cLast.moveToFirst();
                            Cursor cTrans1 = read.rawQuery("SELECT * FROM m_transactions WHERE DATE(datetime) > (select DISTINCT DATE('"+cLast.getString(0)+"','-7 days')) ORDER BY trans_id desc;", null);
                            //WHERE DATE(datetime) > (select DISTINCT DATE('"+cLast.getString(0)+"','-7 days'))
                            int rowTrans1 = cTrans1.getCount();
                            int n1 = 0;
                            int[] idtrans1, notrans1, transrealno1;
                            idtrans1 = new int[rowTrans1];
                            notrans1 = new int[rowTrans1];
                            transrealno1 = new int[rowTrans1];

                            Cursor cTrans = read.rawQuery("SELECT * FROM m_transactions WHERE trans_id NOT IN (SELECT trans_id FROM m_transactions WHERE DATE(datetime) > (select DISTINCT DATE('"+cLast.getString(0)+"','-7 days'))) ORDER BY trans_id desc LIMIT "+(100-rowTrans1)+";", null);
                            int rowTrans = cTrans.getCount();
                            int n = 0;
                            int[] idtrans, notrans, transrealno;
                            idtrans = new int[rowTrans];
                            notrans = new int[rowTrans];
                            transrealno = new int[rowTrans];

                            //if (rowTrans > 0 && rowTrans1 > 0) {
                            if (rowTrans1 > 0) {
                                n1 = rowTrans1;
                                cTrans1.moveToFirst();
                                if (rowTrans > 0) {
                                    n = rowTrans;
                                    cTrans.moveToFirst();
                                }
                                if (pDialog != null || pDialog.isShowing()) {
                                    pDialog.dismiss();
                                }

                                int nNo, nNo1;
                                for (int x = 0; x < rowTrans1; x++) {
                                    String updated_date = cTrans1.getString(3);
                                    SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    Date testDate_1 = null;
                                    try {
                                        testDate_1 = sdf_1.parse(updated_date);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    SimpleDateFormat formatter_1 = new SimpleDateFormat("dd MMM yy HH:mm");

                                    nNo1 = n+n1;
                                    String sName = cTrans1.getString(0);
                                    String sTS = formatter_1.format(testDate_1);
                                    Double sPrice = cTrans1.getDouble(2);
                                    Double nTax = cTrans1.getDouble(6);
                                    Double nDisc = cTrans1.getDouble(7);
                                    Double nSC = cTrans1.getDouble(9);
                                    int nRealNo = cTrans1.getInt(8);
                                    idtrans1[x] = Integer.parseInt(sName);
                                    notrans1[x] = nNo1;
                                    transrealno1[x] = nRealNo;
                                    TransactionItem week = new TransactionItem();
                                    week.setID(cTrans1.getString(0));
                                    week.setTitle(String.valueOf(nRealNo));
                                    week.setPrice("Rp"+df.format((sPrice - nDisc) + nTax + nSC));
                                    week.setDate(sTS);

                                    weekItems.add(week);
                                    cTrans1.moveToNext();
                                    n1 = n1 - 1;
                                }
                                cTrans1.close();
                                for (int y = 0; y < rowTrans; y++) {
                                    String updated_date = cTrans.getString(3);
                                    SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    Date testDate_1 = null;
                                    try {
                                        testDate_1 = sdf_1.parse(updated_date);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    SimpleDateFormat formatter_1 = new SimpleDateFormat("dd MMM yy HH:mm");
                                    nNo = n;
                                    String sName = cTrans.getString(0);
                                    String sTS = formatter_1.format(testDate_1);
                                    Double sPrice = cTrans.getDouble(2);
                                    Double nTax = cTrans.getDouble(6);
                                    Double nDisc = cTrans.getDouble(7);
                                    Double nSC = cTrans.getDouble(9);
                                    int nRealNo = cTrans.getInt(8);
                                    idtrans[y] = Integer.parseInt(sName);
                                    notrans[y] = nNo;
                                    transrealno[y] = nRealNo;
                                    TransactionItem all = new TransactionItem();
                                    all.setID(sName);
                                    //all.setTitle(String.valueOf(nNo));
                                    all.setTitle(String.valueOf(nRealNo));
                                    all.setPrice("Rp"+df.format((sPrice - nDisc) + nTax + nSC));
                                    all.setDate(sTS);

                                    allItems.add(all);
                                    cTrans.moveToNext();
                                    n = n - 1;
                                }
                                cTrans.close();

                                expListAdapter.setupTrips(allItems, weekItems);
                                expList.setAdapter(expListAdapter);

                                if (expListAdapter.getChildrenCount(0) >= 1) {
                                    expList.expandGroup(0);
                                }

                                expList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                                    @Override
                                    public boolean onGroupClick(ExpandableListView parent, View v,
                                                                int groupPosition, long id) {
                                        if (groupPosition == 0) {
                                            return true;
                                        } else {
                                            return false;
                                        }
                                    }
                                });
                                session.setTransID(idtrans1[0]);
                                session.setTransNO(notrans1[0]);
                                session.setTransRealNo(transrealno1[0]);
                            }
                        }
                        cLast.close();
                        if(pDialog != null || pDialog.isShowing()) {
                            pDialog.dismiss();
                        }
                    } else {
                        Cursor cTrans1_1 = read.rawQuery("SELECT * FROM m_transactions WHERE DATE(datetime) between '" + serverDate1 + "' and '" + serverDate2 + "' ORDER BY trans_id desc;", null);
                        //Cursor cLast = read.rawQuery("SELECT MAX(datetime) max_ts FROM m_transactions;", null);
                        if (cTrans1_1.getCount() > 0) {
                            //cLast.moveToFirst();
                            Cursor cTrans1 = read.rawQuery("SELECT * FROM m_transactions WHERE DATE(datetime) between '" + serverDate1 + "' and '" + serverDate2 + "' ORDER BY trans_id desc;", null);
                            //WHERE DATE(datetime) > (select DISTINCT DATE('"+cLast.getString(0)+"','-7 days'))

                            int rowTrans1 = cTrans1.getCount(); // Retrieve data from sqlite
                            int n1 = 0;
                            int nNo, nNo1;
                            int[] idtrans1, notrans1, transrealno1;
                            idtrans1 = new int[rowTrans1];
                            notrans1 = new int[rowTrans1];
                            transrealno1 = new int[rowTrans1];
                            if (rowTrans1 > 0) {
                                //n1 = rowTrans1;
                                cTrans1.moveToFirst();
                                if (pDialog != null || pDialog.isShowing()) {
                                    pDialog.dismiss();
                                }

                                for (int i = 0; i < rowTrans1; i++) {
                                    String updated_date = cTrans1.getString(3);
                                    SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    Date testDate_1 = null;
                                    try {
                                        testDate_1 = sdf_1.parse(updated_date);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    SimpleDateFormat formatter_1 = new SimpleDateFormat("dd MMM yy HH:mm");

                                    nNo1 = n1;
                                    String sName = cTrans1.getString(0);
                                    String sTS = formatter_1.format(testDate_1);
                                    Double sPrice = cTrans1.getDouble(2);
                                    Double nTax = cTrans1.getDouble(6);
                                    Double nDisc = cTrans1.getDouble(7);
                                    Double nSC = cTrans1.getDouble(9);
                                    int nRealNo = cTrans1.getInt(8);
                                    idtrans1[i] = Integer.parseInt(sName);
                                    notrans1[i] = nNo1;
                                    transrealno1[i] = nRealNo;
                                    TransactionItem all = new TransactionItem();
                                    all.setID(sName);
                                    //all.setTitle(String.valueOf(nNo));
                                    all.setTitle(String.valueOf(nRealNo));
                                    all.setPrice("Rp" + df.format((sPrice - nDisc) + nTax + nSC));
                                    all.setDate(sTS);

                                    allItems.add(all);
                                    cTrans1.moveToNext();
                                    n1 = n1 - 1;
                                }
                                cTrans1.close();
                                expListAdapter.getGroupId(1);
                                expListAdapter.setupTrips2(allItems);
                                expList.setAdapter(expListAdapter);

                                if (expListAdapter.getChildrenCount(0) >= 1) {
                                    expList.expandGroup(0);
                                }

                                expList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                                    @Override
                                    public boolean onGroupClick(ExpandableListView parent, View v,
                                                                int groupPosition, long id) {
                                        if (groupPosition == 0) {
                                            return true;
                                        } else {
                                            return false;
                                        }
                                    }
                                });
                                session.setTransID(idtrans1[0]);
                                session.setTransNO(notrans1[0]);
                                session.setTransRealNo(transrealno1[0]);
                            }
                            cTrans1_1.close();
                            if (pDialog != null || pDialog.isShowing()) {
                                pDialog.dismiss();
                            }
                        } else {

                            new isOnline().execute("");
                            if (!internetAvailable) {
                                Toast.makeText(getApplicationContext(), R.string.no_internet3, Toast.LENGTH_LONG).show();
                                if (pDialog != null || pDialog.isShowing()) {
                                    pDialog.dismiss();
                                }
                            } else {
                                boolean error = json.getBoolean("error");

                                if (!error) {
                                    //String id = json.getString("trans_id");
                                    String returnmessage = json.getString("msg");
                                    JSONArray params = json.getJSONArray("transaction");
                                /*if (params.length() > 0) {
                                    write.execSQL("DELETE FROM m_clients");
                                    write.execSQL("DELETE FROM m_users");
                                }*/
                                    int rowTrans1 = params.length(); // Retrieve data from server
                                    int n1 = 0;
                                    int nNo, nNo1;
                                    int[] idtrans1, notrans1, transrealno1;
                                    idtrans1 = new int[rowTrans1];
                                    notrans1 = new int[rowTrans1];
                                    transrealno1 = new int[rowTrans1];

                                    for (int i = 0; i < params.length(); i++) {
                                        JSONObject obj = params.getJSONObject(i);

                                        String updated_date = obj.getString("updated_date");
                                        SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                        Date testDate_1 = null;
                                        try {
                                            testDate_1 = sdf_1.parse(updated_date);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                        SimpleDateFormat formatter_1 = new SimpleDateFormat("dd MMM yy HH:mm");
                                        nNo1 = n1;
                                        String sName = obj.getString("trans_id");
                                        String sTS = formatter_1.format(testDate_1);
                                        Double sPrice = Double.valueOf(obj.getString("price"));
                                        Double nTax = Double.valueOf(obj.getString("tax"));
                                        Double nDisc = Double.valueOf(obj.getString("disc"));
                                        Double nSC = Double.valueOf(obj.getString("sc"));
                                        int nRealNo = Integer.parseInt(obj.getString("trans_id"));
                                        idtrans1[i] = Integer.parseInt(sName);
                                        notrans1[i] = nNo1;
                                        transrealno1[i] = nRealNo;
                                        TransactionItem all = new TransactionItem();
                                        all.setID(sName);
                                        //all.setTitle(String.valueOf(nNo));
                                        all.setTitle(String.valueOf(nRealNo));
                                        all.setPrice("Rp" + df.format((sPrice - nDisc) + nTax + nSC));
                                        all.setDate(sTS);

                                        allItems.add(all);
                                        n1 = n1 - 1;
                                    }
                                    expListAdapter.getGroupId(1);
                                    expListAdapter.setupTrips2(allItems);
                                    expList.setAdapter(expListAdapter);

                                    if (expListAdapter.getChildrenCount(0) >= 1) {
                                        expList.expandGroup(0);
                                    }

                                    expList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                                        @Override
                                        public boolean onGroupClick(ExpandableListView parent, View v,
                                                                    int groupPosition, long id) {
                                            if (groupPosition == 0) {
                                                return true;
                                            } else {
                                                return false;
                                            }
                                        }
                                    });
                                    session.setTransID(idtrans1[0]);
                                    session.setTransNO(notrans1[0]);
                                    session.setTransRealNo(transrealno1[0]);
                                    if (pDialog != null || pDialog.isShowing()) {
                                        pDialog.dismiss();
                                    }
                                }
                            }
                        }
                    }

                /*} else {
                    if(pDialog != null || pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                    String errorMsg = json.getString("error_msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }*/
            } catch (Exception e) {
                if(pDialog != null || pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                e.printStackTrace();
            }
            /*Cursor cLast = read.rawQuery("SELECT MAX(datetime) max_ts FROM m_transactions;", null);
            if(cLast.getCount() > 0) {
                cLast.moveToFirst();
                Cursor cTrans1 = read.rawQuery("SELECT * FROM m_transactions WHERE DATE(datetime) > (select DISTINCT DATE('"+cLast.getString(0)+"','-7 days')) ORDER BY trans_id desc;", null);
                //WHERE DATE(datetime) > (select DISTINCT DATE('"+cLast.getString(0)+"','-7 days'))
                int rowTrans1 = cTrans1.getCount();
                int n1 = 0;
                int[] idtrans1, notrans1, transrealno1;
                idtrans1 = new int[rowTrans1];
                notrans1 = new int[rowTrans1];
                transrealno1 = new int[rowTrans1];

                Cursor cTrans = read.rawQuery("SELECT * FROM m_transactions WHERE trans_id NOT IN (SELECT trans_id FROM m_transactions WHERE DATE(datetime) > (select DISTINCT DATE('"+cLast.getString(0)+"','-7 days'))) ORDER BY trans_id desc;", null);
                int rowTrans = cTrans.getCount();
                int n = 0;
                int[] idtrans, notrans, transrealno;
                idtrans = new int[rowTrans];
                notrans = new int[rowTrans];
                transrealno = new int[rowTrans];

                //if (rowTrans > 0 && rowTrans1 > 0) {
                if (rowTrans1 > 0) {
                    n1 = rowTrans1;
                    cTrans1.moveToFirst();
                    if (rowTrans > 0) {
                        n = rowTrans;
                        cTrans.moveToFirst();
                    }
                    if (pDialog != null || pDialog.isShowing()) {
                        pDialog.dismiss();
                    }

                    int nNo, nNo1;
                    for (int x = 0; x < rowTrans1; x++) {
                        String updated_date = cTrans1.getString(3);
                        SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date testDate_1 = null;
                        try {
                            testDate_1 = sdf_1.parse(updated_date);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        SimpleDateFormat formatter_1 = new SimpleDateFormat("dd MMM yy HH:mm");

                        nNo1 = n+n1;
                        String sName = cTrans1.getString(0);
                        String sTS = formatter_1.format(testDate_1);
                        Double sPrice = cTrans1.getDouble(2);
                        Double nTax = cTrans1.getDouble(6);
                        Double nDisc = cTrans1.getDouble(7);
                        Double nSC = cTrans1.getDouble(9);
                        int nRealNo = cTrans1.getInt(8);
                        idtrans1[x] = Integer.parseInt(sName);
                        notrans1[x] = nNo1;
                        transrealno1[x] = nRealNo;
                        TransactionItem week = new TransactionItem();
                        week.setID(cTrans1.getString(0));
                        week.setTitle(String.valueOf(nRealNo));
                        week.setPrice("Rp"+df.format((sPrice - nDisc) + nTax + nSC));
                        week.setDate(sTS);

                        weekItems.add(week);
                        cTrans1.moveToNext();
                        n1 = n1 - 1;
                    }
                    for (int y = 0; y < rowTrans; y++) {
                        String updated_date = cTrans.getString(3);
                        SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date testDate_1 = null;
                        try {
                            testDate_1 = sdf_1.parse(updated_date);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        SimpleDateFormat formatter_1 = new SimpleDateFormat("dd MMM yy HH:mm");
                        nNo = n;
                        String sName = cTrans.getString(0);
                        String sTS = formatter_1.format(testDate_1);
                        Double sPrice = cTrans.getDouble(2);
                        Double nTax = cTrans.getDouble(6);
                        Double nDisc = cTrans.getDouble(7);
                        Double nSC = cTrans.getDouble(9);
                        int nRealNo = cTrans.getInt(8);
                        idtrans[y] = Integer.parseInt(sName);
                        notrans[y] = nNo;
                        transrealno[y] = nRealNo;
                        TransactionItem all = new TransactionItem();
                        all.setID(sName);
                        //all.setTitle(String.valueOf(nNo));
                        all.setTitle(String.valueOf(nRealNo));
                        all.setPrice("Rp"+df.format((sPrice - nDisc) + nTax + nSC));
                        all.setDate(sTS);

                        allItems.add(all);
                        cTrans.moveToNext();
                        n = n - 1;
                    }

                    expListAdapter.setupTrips(allItems, weekItems);
                    expList.setAdapter(expListAdapter);

                    if (expListAdapter.getChildrenCount(0) >= 1) {
                        expList.expandGroup(0);
                    }

                    expList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v,
                                                    int groupPosition, long id) {
                            if (groupPosition == 0) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });
                    session.setTransID(idtrans1[0]);
                    session.setTransNO(notrans1[0]);
                    session.setTransRealNo(transrealno1[0]);
                }
            }*/
        }
    }

    public void showDetail() {

        //trans_no.setText("Nomor Transaksi #"+session.nTransNO());
        Cursor getTrans = read.rawQuery("SELECT * FROM m_transactions WHERE trans_id="+session.nTransID(), null);
        int rowTrans = getTrans.getCount();
        if(rowTrans > 0) {
            trans_no.setText("Nomor Transaksi #"+session.nTransRealNo());
            getTrans.moveToFirst();

            String date = getTrans.getString(3);
            Date testDate = null;
            try {
                testDate = sdf.parse(date);
            }catch(Exception ex){
                ex.printStackTrace();
            }
            trans_date.setText(formatter.format(testDate));
            trans_total.setText("Rp"+df.format(getTrans.getDouble(2) - getTrans.getDouble(7) + getTrans.getDouble(6) + getTrans.getDouble(9))+" ("+df.format(getTrans.getInt(1))+" item)");
            trans_total2.setText("(diskon Rp"+df.format(getTrans.getDouble(7))+")");
            trans_total3.setText("(service charge Rp"+df.format(getTrans.getDouble(9))+")");
            trans_total4.setText("(pajak Rp"+df.format(getTrans.getDouble(6))+")");
            String payment_type;
            if(getTrans.getInt(5) == 1){
                payment_type = "Cash";
            } else if(getTrans.getInt(5) == 2){
                payment_type = "Credit Card";
            } else if(getTrans.getInt(5) == 3){
                payment_type = "Debit Card";
            } else{
                payment_type = "Lainnya";
            }
            trans_payment.setText(payment_type);
            getTrans.close();
            table_layout.removeAllViews();
            cursor = read.rawQuery("select a.trans_det_id, count(a.prod_id) as qty, c.prod_name, c.prod_price_sell, c.prod_photo from m_transactions_detail a, m_products c where a.prod_id=c.prod_id and a.trans_id="+session.nTransID()+" GROUP BY a.prod_id ORDER BY a.trans_det_id ASC;", null);
            int rows = cursor.getCount();
            int cols = cursor.getColumnCount();

            //Toast.makeText(mContext, String.valueOf(rows), Toast.LENGTH_SHORT).show();
            if(rows == 0){

                TableRow row = new TableRow(getApplicationContext());
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 100));
                TextView tv = new TextView(getApplicationContext());
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 100));
                tv.setGravity(Gravity.CENTER);
                tv.setTypeface(font2, Typeface.ITALIC);
                tv.setTextColor(Color.RED);
                tv.setTextSize(15);
                tv.setPadding(20, 0, 0, 0);
                tv.setText("Silakan lakukan sinkronisasi data kembali");
                row.addView(tv);

                //table_layout.removeAllViews();
                table_layout.addView(row);
            } else {
                cursor.moveToFirst();
                // outer for loop
                count = 0;
                sPhoto = new String[cursor.getCount()];
                sProduct = new String[cursor.getCount()];
                nQty = new String[cursor.getCount()];
                nPrice = new String[cursor.getCount()];
                nID = new String[cursor.getCount()];
                // outer for loop
                for (int i = 0; i < rows; i++) {

                    sPhoto[i] = cursor.getString(4);
                    sProduct[i] = cursor.getString(2);
                    nQty[i] = cursor.getString(1);
                    nPrice[i] = cursor.getString(3);
                    nID[i] = cursor.getString(0);

                    TableRow row = new TableRow(getApplicationContext());
                    row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    if(i %2 == 0) {
                        row.setBackgroundColor(getResources().getColor(R.color.lightgreen2));
                    } else {
                        row.setBackgroundColor(getResources().getColor(R.color.white));
                    }

                    count++;
                    TextView tv = new TextView(getApplicationContext());
                    tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    tv.setGravity(Gravity.CENTER_VERTICAL);
                    tv.setTypeface(font1);
                    //tv.setTextSize(18);
                    tv.setText(String.valueOf(count));
                    tv.setTextColor(Color.BLACK);
                    row.addView(tv);

                    ImageView imgView = new ImageView(getApplicationContext());
                    imgView.setId(count+1);
                    imgView.setPadding(5,5,5,5);
                    if(i %2 == 0) {
                        imgView.setBackgroundColor(getResources().getColor(R.color.lightgreen2));
                    } else {
                        imgView.setBackgroundColor(Color.WHITE);
                    }
                    //new DownloadImageTask(imgView).execute("https://kasandra.biz/images/"+cursor.getString(4));

                    try {
                        //if (isConnected()) {
                        //    Glide.with(getApplicationContext()).load("https://kasandra.biz/images/" + cursor.getString(4)).into(imgView);
                        //} else {
                        File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                File.separator + "kasandra" + File.separator + "product" + File.separator + cursor.getString(4)); //+ "." + mFormat.name().toLowerCase());
                        Bitmap image = BitmapFactory.decodeFile(myImageFile.getAbsolutePath());
                        imgView.setImageBitmap(image);
                        imgView.startAnimation(AnimationUtils.loadAnimation(TransactionActivity.this, android.R.anim.fade_in));
                        //}
                        //} catch (InterruptedException e) {
                        //    e.printStackTrace();
                        //} catch (IOException e) {
                    } catch (Exception e) {
                        Glide.with(getApplicationContext()).load("https://kasandra.biz/images/" + cursor.getString(4)).into(imgView);
                    }
                /*if(isInternetOn()) {
                    Glide.with(getApplicationContext()).load("https://kasandra.biz/images/" + cursor.getString(4)).into(imgView);
                } else {
                    File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            File.separator + "kasandra" + File.separator + "product" + File.separator + cursor.getString(4)); //+ "." + mFormat.name().toLowerCase());
                    Bitmap image = BitmapFactory.decodeFile(myImageFile.getAbsolutePath());
                    imgView.setImageBitmap(image);
                    imgView.startAnimation(AnimationUtils.loadAnimation(TransactionActivity.this, android.R.anim.fade_in));
                }*/
                    //imgView.setImageDrawable(getResources().getDrawable(R.drawable.imgview_bg2));
                    //BitmapDrawable ob = new BitmapDrawable(getResources(), String.valueOf(R.drawable.imgview_bg));
                    //Bitmap bitmap = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
                    //imgView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(Bitmap.createBitmap(bitmap), 50));
                    row.addView(imgView);

                    TextView tv2 = new TextView(getApplicationContext());
                    tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    tv2.setGravity(Gravity.CENTER_VERTICAL);
                    tv2.setTypeface(font2, Typeface.BOLD);
                    //tv2.setTextSize(18);
                    tv2.setText(cursor.getString(2));
                    tv2.setTextColor(Color.BLACK);
                    row.addView(tv2);

                    TextView tv4 = new TextView(getApplicationContext());
                    tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    tv4.setGravity(Gravity.CENTER_VERTICAL);
                    tv4.setTypeface(font2, Typeface.BOLD);
                    //tv4.setTextSize(18);
                    tv4.setText("x"+cursor.getString(1));
                    tv4.setTextColor(Color.BLACK);
                    row.addView(tv4);

                    TextView tv3 = new TextView(getApplicationContext());
                    tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    tv3.setGravity(Gravity.CENTER_VERTICAL);
                    tv3.setTypeface(font2, Typeface.BOLD);
                    //tv3.setTextSize(18);
                    tv3.setText(df.format(cursor.getDouble(3)));
                    tv3.setTextColor(Color.BLACK);
                    row.addView(tv3);

                /*Button btn1 = new Button(getApplicationContext());
                btn1.setId(count+1);
                //btn1.setLayoutParams(new TableRow.LayoutParams(48, 48));
                btn1.setGravity(Gravity.CENTER);
                btn1.setTextSize(20);
                btn1.setTextColor(Color.RED);
                if(i %2 == 0) {
                    btn1.setBackgroundColor(getResources().getColor(R.color.lightblue));
                } else {
                    btn1.setBackgroundColor(Color.WHITE);
                }
                //btn1.setBackgroundResource(R.mipmap.del);
                //btn1.setBackgroundDrawable(getResources().getDrawable(R.drawable.cancel_icon));
                btn1.setText("X");

                row.addView(btn1);
                final int finalI = i;
                final int finalI1 = i;
                final int finalI2 = i;
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //write.execSQL("DELETE FROM m_transactions where prod_id="+nID[finalI1]+" LIMIT 1");
                        write.execSQL("delete from m_transactions_temp where trans_temp_id in (select trans_temp_id from m_transactions_temp where prod_id="+nID[finalI1]+" LIMIT 1)");
                        //Log.d("kasandra debug","DELETE FROM m_transactions where prod_id='"+nID[finalI1]+"' LIMIT 1");
                        Snackbar.make(view, sProduct[finalI]+" Dihapus", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        /-*-double totalp = session.nSubTotal()-Double.parseDouble(nPrice[finalI2]);
                        //prices = Double.parseDouble(cursor.getString(2));
                        subtotal.setText(": Rp. "+String.valueOf(totalp));
                        total.setText(": Rp. "+String.valueOf(totalp + (totalp * 10/100)));-*-/
                        showItem();
                        //table_layout.removeViewAt(finalI);
                    }
                });*/
                    if(session.screenSize() < 7.9) {
                        tv.setTextSize(13);
                        tv2.setTextSize(13);
                        tv3.setTextSize(13);
                        tv4.setTextSize(13);
                        imgView.setLayoutParams(new TableRow.LayoutParams(65, 65));
                    } else {
                        tv.setTextSize(18);
                        tv2.setTextSize(18);
                        tv3.setTextSize(18);
                        tv4.setTextSize(18);
                        imgView.setLayoutParams(new TableRow.LayoutParams(100, 100));
                    }
                    cursor.moveToNext();
                    table_layout.removeView(row);
                    table_layout.addView(row);
                }
                cursor.close();
            }

        } else {
            new isOnline().execute("");
            if (internetAvailable) {
                gettransdet();
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_internet3, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void gettransdet() {
        trans_no.setText("Nomor Transaksi #"+session.nTransRealNo());
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(sURL+"?get_trans_id=" + session.nTransID());

        if (entry2 != null) {
            try {
                String data = new String(entry2.data, "UTF-8");
                try {
                    parseJsonTransDet(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            JsonObjectRequest jsonReq2 = new JsonObjectRequest(Request.Method.GET,
                    sURL+"?get_trans_id=" + session.nTransID(), null, new Response.Listener<JSONObject>() {
                //"https://kasandra.biz/wbagenpass.json", null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    parseJsonTransDet(response);
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

    private void parseJsonTransDet(JSONObject response) {
        try {
            boolean error = response.getBoolean("error");
            if (!error) {
                String returnmessage = response.getString("msg");
                String datetime = response.getString("datetime");
                int total_qty = response.getInt("total_qty");
                int total_price = response.getInt("total_price");
                int tax = response.getInt("tax");
                int disc = response.getInt("disc");
                int sc = response.getInt("sc");
                int payment = response.getInt("payment_type");
                JSONArray params = response.getJSONArray("transdetails");
                if (params.length() > 0) {
                    String date = datetime;
                    Date testDate = null;
                    try {
                        testDate = sdf.parse(date);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    trans_date.setText(formatter.format(testDate));
                    trans_total.setText("Rp" + df.format(total_price - disc + tax + sc) + " (" + df.format(total_qty) + " item)");
                    trans_total2.setText("(diskon Rp" + df.format(disc) + ")");
                    trans_total3.setText("(service charge Rp" + df.format(sc) + ")");
                    trans_total4.setText("(pajak Rp" + df.format(tax) + ")");
                    String payment_type;
                    if (payment == 1) {
                        payment_type = "Cash";
                    } else if (payment == 2) {
                        payment_type = "Credit Card";
                    } else if (payment == 3) {
                        payment_type = "Debit Card";
                    } else {
                        payment_type = "Lainnya";
                    }
                    trans_payment.setText(payment_type);
                    table_layout.removeAllViews();
                /*cursor = read.rawQuery("select a.trans_det_id, count(a.prod_id) as qty, c.prod_name, c.prod_price_sell, c.prod_photo from m_transactions_detail a, m_products c where a.prod_id=c.prod_id and a.trans_id="+session.nTransID()+" GROUP BY a.prod_id ORDER BY a.trans_det_id ASC;", null);
                int rows = cursor.getCount();
                int cols = cursor.getColumnCount();

                if(rows == 0){

                    TableRow row = new TableRow(getApplicationContext());
                    row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 100));
                    TextView tv = new TextView(getApplicationContext());
                    tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 100));
                    tv.setGravity(Gravity.CENTER);
                    tv.setTypeface(font2, Typeface.ITALIC);
                    tv.setTextColor(Color.RED);
                    tv.setTextSize(15);
                    tv.setPadding(20, 0, 0, 0);
                    tv.setText("Silakan lakukan sinkronisasi data kembali");
                    row.addView(tv);

                    //table_layout.removeAllViews();
                    table_layout.addView(row);
                } else {*/
                    //cursor.moveToFirst();
                    // outer for loop
                    count = 0;
                    sPhoto = new String[params.length()];
                    sProduct = new String[params.length()];
                    nQty = new String[params.length()];
                    nPrice = new String[params.length()];
                    nID = new String[params.length()];

                    // outer for loop
                    for (int i = 0; i < params.length(); i++) {

                        JSONObject obj = params.getJSONObject(i);
                        //final int id = parseInt(obj.getString("outlet_id"));

                        sPhoto[i] = obj.getString("photo");
                        sProduct[i] = obj.getString("product");
                        nQty[i] = obj.getString("qty");
                        nPrice[i] = obj.getString("price");
                        nID[i] = obj.getString("trans_det_id");

                        TableRow row = new TableRow(getApplicationContext());
                        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                        if (i % 2 == 0) {
                            row.setBackgroundColor(getResources().getColor(R.color.lightgreen2));
                        } else {
                            row.setBackgroundColor(getResources().getColor(R.color.white));
                        }

                        count++;
                        TextView tv = new TextView(getApplicationContext());
                        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tv.setGravity(Gravity.CENTER_VERTICAL);
                        tv.setTypeface(font1);
                        //tv.setTextSize(18);
                        tv.setText(String.valueOf(count));
                        tv.setTextColor(Color.BLACK);
                        row.addView(tv);

                        ImageView imgView = new ImageView(getApplicationContext());
                        imgView.setId(count + 1);
                        imgView.setPadding(5, 5, 5, 5);
                        if (i % 2 == 0) {
                            imgView.setBackgroundColor(getResources().getColor(R.color.lightgreen2));
                        } else {
                            imgView.setBackgroundColor(Color.WHITE);
                        }
                        //new DownloadImageTask(imgView).execute("https://kasandra.biz/images/"+cursor.getString(4));

                        try {
                            //if (isConnected()) {
                            //    Glide.with(getApplicationContext()).load("https://kasandra.biz/images/" + cursor.getString(4)).into(imgView);
                            //} else {
                            File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                    File.separator + "kasandra" + File.separator + "product" + File.separator + obj.getString("photo")); //+ "." + mFormat.name().toLowerCase());
                            Bitmap image = BitmapFactory.decodeFile(myImageFile.getAbsolutePath());
                            imgView.setImageBitmap(image);
                            imgView.startAnimation(AnimationUtils.loadAnimation(TransactionActivity.this, android.R.anim.fade_in));
                            //}
                            //} catch (InterruptedException e) {
                            //    e.printStackTrace();
                            //} catch (IOException e) {
                        } catch (Exception e) {
                            Glide.with(getApplicationContext()).load("https://kasandra.biz/images/" + obj.getString("photo")).into(imgView);
                        }
                        row.addView(imgView);

                        TextView tv2 = new TextView(getApplicationContext());
                        tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tv2.setGravity(Gravity.CENTER_VERTICAL);
                        tv2.setTypeface(font2, Typeface.BOLD);
                        //tv2.setTextSize(18);
                        tv2.setText(obj.getString("product"));
                        tv2.setTextColor(Color.BLACK);
                        row.addView(tv2);

                        TextView tv4 = new TextView(getApplicationContext());
                        tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tv4.setGravity(Gravity.CENTER_VERTICAL);
                        tv4.setTypeface(font2, Typeface.BOLD);
                        //tv4.setTextSize(18);
                        tv4.setText("x" + obj.getString("qty"));
                        tv4.setTextColor(Color.BLACK);
                        row.addView(tv4);

                        TextView tv3 = new TextView(getApplicationContext());
                        tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tv3.setGravity(Gravity.CENTER_VERTICAL);
                        tv3.setTypeface(font2, Typeface.BOLD);
                        //tv3.setTextSize(18);
                        tv3.setText(df.format(obj.getInt("price")));
                        tv3.setTextColor(Color.BLACK);
                        row.addView(tv3);

                        if (session.screenSize() < 7.9) {
                            tv.setTextSize(13);
                            tv2.setTextSize(13);
                            tv3.setTextSize(13);
                            tv4.setTextSize(13);
                            imgView.setLayoutParams(new TableRow.LayoutParams(65, 65));
                        } else {
                            tv.setTextSize(18);
                            tv2.setTextSize(18);
                            tv3.setTextSize(18);
                            tv4.setTextSize(18);
                            imgView.setLayoutParams(new TableRow.LayoutParams(100, 100));
                        }
                        //cursor.moveToNext();
                        table_layout.removeView(row);
                        table_layout.addView(row);
                    }
                    //cursor.close();
                    //}
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

    private void filterByDate() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TransactionActivity.this, R.style.CustomDialogTheme);
        dialogBuilder.setIcon(getResources().getDrawable(R.mipmap.calendar));
        dialogBuilder.setTitle("Filter Pencarian");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new populateTransList().execute(serverDate1 + "!" + serverDate2);
                        handler2.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showDetail();
                                //Toast.makeText(getApplicationContext(), "Call Find-P(5)", Toast.LENGTH_LONG).show();
                            }
                        }, 3000);
                        //dialog.cancel();
                    }
                }
        );
        dialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.cancel();
                    }
                }

        );

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_filter, null);
        dialogBuilder.setView(dialogView);

        inputDate1 = (EditText) dialogView.findViewById(R.id.input_date1);
        inputDate2 = (EditText) dialogView.findViewById(R.id.input_date2);

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
                DatePickerDialog datePickerDialog = new DatePickerDialog(TransactionActivity.this,  date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
                dateNo = 1;
            }
        });
        inputDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog datePickerDialog = new DatePickerDialog(TransactionActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis() - 1000);
                datePickerDialog.show();
                dateNo = 2;
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void updateLabel(final int numDate) {
        DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(numDate == 1) {
            inputDate1.setText(sdf.format(myCalendar.getTime()));
            serverDate1 = newFormat.format(myCalendar.getTime());
            inputDate2.setText(sdf.format(myCalendar.getTime())); // added june, 20
            serverDate2 = newFormat.format(myCalendar.getTime());
        } else {
            inputDate2.setText(sdf.format(myCalendar.getTime()));
            serverDate2 = newFormat.format(myCalendar.getTime());
        }
    }

    private double getServCharge(double price, double disc, double sc) {
        return ((price - disc) * sc/100);
    }
    /*final Runnable handlerTask = new Runnable() {

        @Override
        public void run() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            n++;
            if (n >= 30) { // upload data every 30 seconds
                //Toast.makeText(TransactionActivity.this, "Tidak dapat menampilkan data, silakan muat ulang halaman ini", Toast.LENGTH_LONG).show();
                if(pDialog != null || pDialog.isShowing()) {
                    pDialog.dismiss();
                    n = 0;
                    // removed handler.removeCallbacks(handlerTask);
                    Thread.currentThread().interrupt();
                }
            }
            handler.postDelayed(handlerTask, 1000);
        }
    };
    final Runnable handlerTask2 = new Runnable() {

        @Override
        public void run() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            n++;
            if (n >= 3) {
                if (isLoading2) {
                    showDetail();
                    // removed handler2.removeCallbacks(handlerTask2);
                    isLoading2 = false;
                    n = 0;
                    Thread.currentThread().interrupt();
                }
            }
            handler2.postDelayed(handlerTask2, 1000);
        }
    };*/

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new populateTransList().execute(serverDate1 + "!" + serverDate2);
                    //Toast.makeText(getApplicationContext(), "Populate 3", Toast.LENGTH_LONG).show();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case MY_PERMISSIONS_2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //connect();
                    callPrinter();
                } else {
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

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
            Toast toast = Toast.makeText(TransactionActivity.this, "Gagal menyambungkan printer", Toast.LENGTH_SHORT);
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
            Toast.makeText(TransactionActivity.this, "Telah terhubung dengan printer, silakan Cetak Bill", Toast.LENGTH_LONG).show();
            printType = 2;
        }
    };

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v("IDPOS", "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);

                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    btName = mBluetoothDevice.getName();
                    //Thread mBlutoothConnectThread = new Thread((Runnable) this);
                    //mBlutoothConnectThread.start();
                    connectResult();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(TransactionActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(TransactionActivity.this, "Bluetooth activate denied", Toast.LENGTH_SHORT).show();
                }
                break;
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private void callPrinter() {
        if(printType == 2) {
            currentChoice = new int[]{2};
        } else {
            currentChoice = new int[]{printType};
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(TransactionActivity.this);
        builder.setTitle("Sambungkan ke Printer");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(printType == 0) {
                            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            if (mBluetoothAdapter == null) {
                                Toast.makeText(TransactionActivity.this, "Device Not Support", Toast.LENGTH_SHORT).show();
                            } else {
                                if (!mBluetoothAdapter.isEnabled()) {
                                    Intent enableBtIntent = new Intent(
                                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBtIntent,
                                            REQUEST_ENABLE_BT);
                                } else {
                                    ListPairedDevices();
                                    Intent connectIntent = new Intent(TransactionActivity.this,
                                            DeviceListActivity.class);
                                    startActivityForResult(connectIntent,
                                            REQUEST_CONNECT_DEVICE);

                                }
                            }
                        } else if(printType == 1){
                            if (mBluetoothAdapter != null)
                                mBluetoothAdapter.disable();
                            alertDialog1.dismiss();
                        } else if(printType == 2){
                            if (btsocket == null) {
                                printType = 0;
                                Toast.makeText(getApplication(), "Silakan hubungkan printer terlebih dahulu", Toast.LENGTH_LONG).show();
                                callPrinter();
                            } else {
                                printReceipt();
                            }
                        }

                    }
                }
        );

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }

        );
        builder.setSingleChoiceItems(values, currentChoice[0], new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch(item)
                {
                    case 0:
                        printType = 0;
                        break;
                    case 1:
                        printType = 1;
                        break;
                    case 2:
                        printType = 2;
                        break;
                }
                //alertDialog.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.setCanceledOnTouchOutside(false);
        alertDialog1.show();
    }

    private void printReceipt () {
        //if (btsocket == null) {
        //    Toast.makeText(getApplication(), "Silakan pilih printer terlebih dahulu", Toast.LENGTH_SHORT).show();
            //drawer.openDrawer(Gravity.LEFT);
        //}
        try {
            btoutputstream = btsocket
                    .getOutputStream();
            Calendar cal_ = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
            Date currentLocalTime_ = cal_.getTime();
            DateFormat dateCounter_ = new SimpleDateFormat("dd MMM yyyy HH:mm");

            dateCounter_.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
            String localTimeCounter = dateCounter_.format(currentLocalTime_);
            String client_name = session.sClientFullName();
            String outlet_name = session.sOutlet();
            String user_name = session.sUserName();

            resetPrint();
            byte[] printformat = {0x1B, 0x21, FONT_TYPE};
            btoutputstream.write(printformat);

            //if(btName.startsWith("RP58")){
            if (session.nPrinterChars() == 32) {
                Cursor cReadTrans = read.rawQuery("select datetime from m_transactions where trans_id="+session.nTransID(), null);
                int rowtrans = cReadTrans.getCount();
                if(rowtrans > 0) {
                    printTitle(outlet_name);
                    printNewLine();
                    printUnicodeforReset();
                    cReadTrans.moveToFirst();
                    String date = cReadTrans.getString(0);
                    Date transdate = null;
                    try {
                        transdate = sdf.parse(date);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    //printText1("Receipt : "+session.nTransNO());printText("\n");
                    printText1("Receipt : " + session.nTransRealNo());
                    printText("\n");
                    printText1("Tanggal : " + dateCounter_.format(transdate));
                    printText("\n");
                    printText1("Kasir   : " + user_name);
                    printText("\n");
                    printUnicode3();
                    printText1("\n");
                    printText1("Produk      Harga    Sub Total");
                    printText1("\n");
                    printUnicode3();
                    printText1("\n");
                    int count = 0;
                    double subtotal = 0;
                    //Cursor cRead = read.rawQuery("SELECT b.prod_name, b.prod_price_sell from m_transactions_temp a, m_products b WHERE a.prod_id=b.prod_id ORDER BY a.trans_temp_id ASC;", null);
                    Cursor cRead = read.rawQuery("select a.trans_det_id, count(a.prod_id) as qty_, count(a.prod_id) * b.prod_price_sell as totalprice, b.* from m_transactions_detail a, m_products b where a.prod_id=b.prod_id AND a.trans_id=" + session.nTransID() + " GROUP BY a.prod_id ORDER BY a.trans_det_id ASC;", null);
                    int rows = cRead.getCount();
                    Cursor cTax = read.rawQuery("select sum(trans_tax) from m_transactions where trans_id=" + session.nTransID() + ";", null);
                    //int rowsTax = cTax.getCount();
                    Cursor cDisc = read.rawQuery("select sum(trans_discount) from m_transactions where trans_id=" + session.nTransID() + ";", null);
                    //int rowsDisc = cDisc.getCount();
                    Cursor cSC = read.rawQuery("select sum(trans_serv_charge) from m_transactions where trans_id=" + session.nTransID() + ";", null);
                    if (rows > 0) {
                        cRead.moveToFirst();
                        cTax.moveToFirst();
                        cDisc.moveToFirst();
                        cSC.moveToFirst();
                        for (int i = 0; i < rows; i++) {
                            if (cRead.getString(4).length() >= 12) {
                                String lastWord = cRead.getString(4).substring(cRead.getString(4).lastIndexOf(" ") + 1);
                                printText1(cRead.getString(4).replace(" " + lastWord, ""));
                                printText1("\n");
                                printText1(lastWord);
                                resetNoSpace();
                                btoutputstream.write(PrinterCommands.BS);
                                resetNoSpace();
                                //printText(Utils.SPACE_15);
                                int lWLength = lastWord.length();
                                for (int j = 0; j < (9 - lWLength); j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            } else {
                                int length = cRead.getString(4).length();
                                printText1(cRead.getString(4));
                                for (int j = 0; j < (12 - length); j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            }
                            String price = "Rp" + df.format(cRead.getDouble(7));
                            int pLength = price.length();
                            printText1(price);
                            if (pLength >= 9) {
                                printText(Utils.ONE_SPACE);
                            } else if (pLength == 8) {
                                for (int j = 0; j < 2; j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            } else if (pLength == 7) {
                                for (int j = 0; j < 3; j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            }
                            printText1("\n");
                            for (int j = 0; j < 17; j++) {
                                printText(Utils.ONE_SPACE);
                            }
                            String quantity = "x" + cRead.getString(1);
                            int qLength = quantity.length();
                            printText1(quantity);
                            if (qLength >= 3) {
                                printText(Utils.ONE_SPACE);
                            } else if (qLength == 2) {
                                for (int j = 0; j < 2; j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            }
                            printText1("Rp" + df.format(cRead.getDouble(2)));
                            printText1("\n");
                            count = count + cRead.getInt(1);
                            subtotal = subtotal + cRead.getDouble(2);
                            cRead.moveToNext();
                        }

                        printUnicode4();
                        printText("\n");
                        printText1("Item               : ");
                        printText1(String.valueOf(count));
                        printText("\n");

                        printText1("Subtotal           : ");
                        printText1("Rp" + String.valueOf(df.format(subtotal)));
                        printText("\n");

                        printText1("Diskon           : ");
                        printText1("Rp" + String.valueOf(df.format(cDisc.getInt(0))));
                        printText("\n");

                        printText1("Service charge   : ");
                        printText1("Rp" + String.valueOf(df.format(cSC.getInt(0))));
                        printText1("\n");

                        printText1("Pajak            : ");
                        printText1("Rp" + String.valueOf(df.format(cTax.getInt(0))));
                        printText("\n");

                        printText1("Total              : ");
                        printText1("Rp" + String.valueOf(df.format(subtotal + cTax.getInt(0) + cSC.getInt(0) - cDisc.getInt(0))));
                        printText("\n");
                        printUnicode4();
                        printText("\n");
                        printText1("Terima kasih atas kunjungan Anda"); // total 42 char in a single line
                        printText("\n");
                        printNewLine();
                        printText("\n");
                        printText1("\n");
                    }
                    cReadTrans.close();
                } else {
                    print32();
                }
            } else {
                Cursor cReadTrans = read.rawQuery("select datetime from m_transactions where trans_id="+session.nTransID(), null);
                int rowtrans = cReadTrans.getCount();
                if(rowtrans > 0) {
                    printTitle(outlet_name);
                    printNewLine();
                    printUnicodeforReset();
                    cReadTrans.moveToFirst();
                    String date = cReadTrans.getString(0);
                    Date transdate = null;
                    try {
                        transdate = sdf.parse(date);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    //printText1("Receipt : "+session.nTransNO());printText("\n");
                    printText1("Receipt : " + session.nTransRealNo());
                    printText("\n");
                    printText1("Tanggal : " + dateCounter_.format(transdate));
                    printText("\n");
                    printText1("Kasir   : " + user_name);
                    printText("\n");
                    printUnicode();
                    printText1("\n");
                    printText1("Produk           Harga         Sub Total");
                    printText1("\n");
                    printUnicode();
                    printText1("\n");
                    int count = 0;
                    double subtotal = 0;
                    //Cursor cRead = read.rawQuery("SELECT b.prod_name, b.prod_price_sell from m_transactions_temp a, m_products b WHERE a.prod_id=b.prod_id ORDER BY a.trans_temp_id ASC;", null);
                    Cursor cRead = read.rawQuery("select a.trans_det_id, count(a.prod_id) as qty_, count(a.prod_id) * b.prod_price_sell as totalprice, b.* from m_transactions_detail a, m_products b where a.prod_id=b.prod_id AND a.trans_id=" + session.nTransID() + " GROUP BY a.prod_id ORDER BY a.trans_det_id ASC;", null);
                    int rows = cRead.getCount();
                    Cursor cTax = read.rawQuery("select sum(trans_tax) from m_transactions where trans_id=" + session.nTransID() + ";", null);
                    //int rowsTax = cTax.getCount();
                    Cursor cDisc = read.rawQuery("select sum(trans_discount) from m_transactions where trans_id=" + session.nTransID() + ";", null);
                    //int rowsDisc = cDisc.getCount();
                    Cursor cSC = read.rawQuery("select sum(trans_serv_charge) from m_transactions where trans_id=" + session.nTransID() + ";", null);
                    if (rows > 0) {
                        cRead.moveToFirst();
                        cTax.moveToFirst();
                        cDisc.moveToFirst();
                        cSC.moveToFirst();
                        for (int i = 0; i < rows; i++) {
                            if (cRead.getString(4).length() > 16) {
                                String lastWord = cRead.getString(4).substring(cRead.getString(4).lastIndexOf(" ") + 1);
                                printText1(cRead.getString(4).replace(" " + lastWord, ""));
                                printText1("\n");
                                printText1(lastWord);
                                resetNoSpace();
                                btoutputstream.write(PrinterCommands.BS);
                                resetNoSpace();
                                //printText(Utils.SPACE_15);
                                int lWLength = lastWord.length();
                                for (int j = 0; j < (17 - lWLength); j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            } else {
                                int length = cRead.getString(4).length();
                                printText1(cRead.getString(4));
                                for (int j = 0; j < (17 - length); j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            }
                            String price = "Rp" + df.format(cRead.getDouble(7));
                            int pLength = price.length();
                            printText1(price);
                            if (pLength >= 9) {
                                printText(Utils.ONE_SPACE);
                            } else if (pLength == 8) {
                                for (int j = 0; j < 2; j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            } else if (pLength == 7) {
                                for (int j = 0; j < 3; j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            }
                            String quantity = "x" + cRead.getString(1);
                            int qLength = quantity.length();
                            printText1(quantity);
                            if (qLength >= 3) {
                                printText(Utils.ONE_SPACE);
                            } else if (qLength == 2) {
                                for (int j = 0; j < 2; j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            }
                            printText1("Rp" + df.format(cRead.getDouble(2)));
                            printText1("\n");
                            count = count + cRead.getInt(1);
                            subtotal = subtotal + cRead.getDouble(2);
                            cRead.moveToNext();
                        }

                        //cursor.getString(4), Rp+df.format(cursor.getDouble(7)), "x"+cursor.getString(1), Rp+df.format(cursor.getDouble(2))
                        //subtotal.setText(": Rp. "+String.valueOf(df.format(session.nSubTotal())));
                        //tax2.setText(": Rp. "+String.valueOf(df.format(session.nSubTotal() * 10/100)));
                        //total.setText(": Rp. "+String.valueOf(df.format(session.nSubTotal() + (session.nSubTotal() * 10/100))));

                        printUnicode1();
                        printText("\n");
                        printText1("Item                         : ");
                        printText1(String.valueOf(count));
                        printText("\n");

                        printText1("Subtotal                     : ");
                        printText1("Rp" + String.valueOf(df.format(subtotal)));
                        printText("\n");

                        printText1("Diskon                       : ");
                        printText1("Rp" + String.valueOf(df.format(cDisc.getInt(0))));
                        printText("\n");

                        printText1("Service charge               : ");
                        printText1("Rp" + String.valueOf(df.format(cSC.getInt(0))));
                        printText1("\n");

                        printText1("Pajak                        : ");
                        printText1("Rp" + String.valueOf(df.format(cTax.getInt(0))));
                        printText("\n");

                        printText1("Total                        : ");
                        printText1("Rp" + String.valueOf(df.format(subtotal + cTax.getInt(0) + cSC.getInt(0) - cDisc.getInt(0))));
                        printText("\n");

                    /*printText1("Bayar                        : ");
                    printText1("Rp150.000");
                    printText("\n");

                    printText1("Kembali                      : ");
                    printText1("Rp20.000");
                    printText("\n");*/
                        printUnicode1();
                        printText1("\n");
                        //byte[] printformat1 = { 0x1B, 0x21, FONT_TYPE };
                        //btoutputstream.write(printformat1);
                        printText1(">>>  Terima kasih atas kunjungan Anda  <<<"); // total 42 char in a single line
                        printText("\n");
                        printNewLine();
                        printText("\n");
                        printText1("\n");
                    }
                    cReadTrans.close();
                } else {
                    print42();
                }
            }
            //This is printer specific code you can comment ==== > Start

            //printer specific code you can comment ==== > End
        } catch (Exception e) {
            Log.e("Main", "Exe ", e);
        }
    }

    private void print32() {
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(sURL+"?get_trans_id=" + session.nTransID());

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
                    sURL+"?get_trans_id=" + session.nTransID(), null, new Response.Listener<JSONObject>() {
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
            DateFormat dateCounter_ = new SimpleDateFormat("dd MMM yyyy HH:mm");
            boolean error = response.getBoolean("error");
            if (!error) {
                String returnmessage = response.getString("msg");
                String datetime = response.getString("datetime");
                int total_qty = response.getInt("total_qty");
                int total_price = response.getInt("total_price");
                int tax = response.getInt("tax");
                int disc = response.getInt("disc");
                int sc = response.getInt("sc");
                int payment = response.getInt("payment_type");
                JSONArray params = response.getJSONArray("transdetails");
                if (params.length() > 0) {
                    printTitle(session.sOutlet());
                    printNewLine();
                    printUnicodeforReset();
                    String date = datetime;
                    Date transdate = null;
                    try {
                        transdate = sdf.parse(date);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    //printText1("Receipt : "+session.nTransNO());printText("\n");
                    printText1("Receipt : " + session.nTransRealNo());
                    printText("\n");
                    printText1("Tanggal : " + dateCounter_.format(transdate));
                    printText("\n");
                    printText1("Kasir   : " + session.sUserName());
                    printText("\n");
                    printUnicode3();
                    printText1("\n");
                    printText1("Produk      Harga    Sub Total");
                    printText1("\n");
                    printUnicode3();
                    printText1("\n");
                    int count = 0;
                    double subtotal = 0;

                        for (int i = 0; i < params.length(); i++) {
                            JSONObject obj = params.getJSONObject(i);
                            if (obj.getString("product").length() >= 12) {
                                String lastWord = obj.getString("product").substring(obj.getString("product").lastIndexOf(" ") + 1);
                                printText1(obj.getString("product").replace(" " + lastWord, ""));
                                printText1("\n");
                                printText1(lastWord);
                                resetNoSpace();
                                try {
                                    btoutputstream.write(PrinterCommands.BS);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                resetNoSpace();
                                //printText(Utils.SPACE_15);
                                int lWLength = lastWord.length();
                                for (int j = 0; j < (9 - lWLength); j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            } else {
                                int length = obj.getString("product").length();
                                printText1(obj.getString("product"));
                                for (int j = 0; j < (12 - length); j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            }
                            String price = "Rp" + df.format(obj.getInt("price"));
                            int pLength = price.length();
                            printText1(price);
                            if (pLength >= 9) {
                                printText(Utils.ONE_SPACE);
                            } else if (pLength == 8) {
                                for (int j = 0; j < 2; j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            } else if (pLength == 7) {
                                for (int j = 0; j < 3; j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            }
                            printText1("\n");
                            for (int j = 0; j < 17; j++) {
                                printText(Utils.ONE_SPACE);
                            }
                            String quantity = "x" + obj.getInt("qty");
                            int qLength = quantity.length();
                            printText1(quantity);
                            if (qLength >= 3) {
                                printText(Utils.ONE_SPACE);
                            } else if (qLength == 2) {
                                for (int j = 0; j < 2; j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            }
                            printText1("Rp" + df.format(obj.getInt("total") * obj.getInt("qty")));
                            printText1("\n");
                            count = count + obj.getInt("qty");
                            subtotal = subtotal + obj.getInt("total");
                        }

                        printUnicode4();
                        printText("\n");
                        printText1("Item               : ");
                        printText1(String.valueOf(count));
                        printText("\n");

                        printText1("Subtotal           : ");
                        printText1("Rp" + String.valueOf(df.format(total_price)));
                        printText("\n");

                        printText1("Diskon           : ");
                        printText1("Rp" + String.valueOf(df.format(disc)));
                        printText("\n");

                        printText1("Service charge   : ");
                        printText1("Rp" + String.valueOf(df.format(sc)));
                        printText1("\n");

                        printText1("Pajak            : ");
                        printText1("Rp" + String.valueOf(df.format(tax)));
                        printText("\n");

                        printText1("Total              : ");
                        printText1("Rp" + String.valueOf(df.format(total_price + tax + sc - disc)));
                        printText("\n");
                        printUnicode4();
                        printText("\n");
                        printText1("Terima kasih atas kunjungan Anda"); // total 42 char in a single line
                        printText("\n");
                        printNewLine();
                        printText("\n");
                        printText1("\n");
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
        Cache.Entry entry2 = cache2.get(sURL+"?get_trans_id=" + session.nTransID());

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
                    sURL+"?get_trans_id=" + session.nTransID(), null, new Response.Listener<JSONObject>() {
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
            DateFormat dateCounter_ = new SimpleDateFormat("dd MMM yyyy HH:mm");
            boolean error = response.getBoolean("error");
            if (!error) {
                String returnmessage = response.getString("msg");
                String datetime = response.getString("datetime");
                int total_qty = response.getInt("total_qty");
                int total_price = response.getInt("total_price");
                int tax = response.getInt("tax");
                int disc = response.getInt("disc");
                int sc = response.getInt("sc");
                int payment = response.getInt("payment_type");
                JSONArray params = response.getJSONArray("transdetails");
                if (params.length() > 0) {
                    printTitle(session.sOutlet());
                    printNewLine();
                    printUnicodeforReset();
                    String date = datetime;
                    Date transdate = null;
                    try {
                        transdate = sdf.parse(date);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    printText1("Receipt : " + session.nTransRealNo());
                    printText("\n");
                    printText1("Tanggal : " + dateCounter_.format(transdate));
                    printText("\n");
                    printText1("Kasir   : " + session.sUserName());
                    printText("\n");
                    printUnicode();
                    printText1("\n");
                    printText1("Produk           Harga         Sub Total");
                    printText1("\n");
                    printUnicode();
                    printText1("\n");
                    int count = 0;
                    double subtotal = 0;
                    /*Cursor cRead = read.rawQuery("select a.trans_det_id, count(a.prod_id) as qty_, count(a.prod_id) * b.prod_price_sell as totalprice, b.* from m_transactions_detail a, m_products b where a.prod_id=b.prod_id AND a.trans_id=" + session.nTransID() + " GROUP BY a.prod_id ORDER BY a.trans_det_id ASC;", null);
                    int rows = cRead.getCount();
                    Cursor cTax = read.rawQuery("select sum(trans_tax) from m_transactions where trans_id=" + session.nTransID() + ";", null);
                    Cursor cDisc = read.rawQuery("select sum(trans_discount) from m_transactions where trans_id=" + session.nTransID() + ";", null);
                    Cursor cSC = read.rawQuery("select sum(trans_serv_charge) from m_transactions where trans_id=" + session.nTransID() + ";", null);
                    if (rows > 0) {
                        cRead.moveToFirst();
                        cTax.moveToFirst();
                        cDisc.moveToFirst();
                        cSC.moveToFirst();*/
                        for (int i = 0; i < params.length(); i++) {
                            JSONObject obj = params.getJSONObject(i);
                            if (obj.getString("product").length() > 16) {
                                String lastWord = obj.getString("product").substring(obj.getString("product").lastIndexOf(" ") + 1);
                                printText1(obj.getString("product").replace(" " + lastWord, ""));
                                printText1("\n");
                                printText1(lastWord);
                                resetNoSpace();
                                try {
                                    btoutputstream.write(PrinterCommands.BS);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                resetNoSpace();
                                //printText(Utils.SPACE_15);
                                int lWLength = lastWord.length();
                                for (int j = 0; j < (17 - lWLength); j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            } else {
                                int length = obj.getString("product").length();
                                printText1(obj.getString("product"));
                                for (int j = 0; j < (17 - length); j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            }
                            String price = "Rp" + df.format(obj.getInt("price"));
                            int pLength = price.length();
                            printText1(price);
                            if (pLength >= 9) {
                                printText(Utils.ONE_SPACE);
                            } else if (pLength == 8) {
                                for (int j = 0; j < 2; j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            } else if (pLength == 7) {
                                for (int j = 0; j < 3; j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            }
                            String quantity = "x" + obj.getString("qty");
                            int qLength = quantity.length();
                            printText1(quantity);
                            if (qLength >= 3) {
                                printText(Utils.ONE_SPACE);
                            } else if (qLength == 2) {
                                for (int j = 0; j < 2; j++) {
                                    printText(Utils.ONE_SPACE);
                                }
                            }
                            printText1("Rp" + df.format(obj.getInt("price") * obj.getInt("qty")));
                            printText1("\n");
                            count = count + obj.getInt("qty");
                            subtotal = subtotal + obj.getInt("price");
                        }

                        printUnicode1();
                        printText("\n");
                        printText1("Item                         : ");
                        printText1(String.valueOf(count));
                        printText("\n");

                        printText1("Subtotal                     : ");
                        printText1("Rp" + String.valueOf(df.format(total_price)));
                        printText("\n");

                        printText1("Diskon                       : ");
                        printText1("Rp" + String.valueOf(df.format(disc)));
                        printText("\n");

                        printText1("Service charge               : ");
                        printText1("Rp" + String.valueOf(df.format(sc)));
                        printText1("\n");

                        printText1("Pajak                        : ");
                        printText1("Rp" + String.valueOf(df.format(tax)));
                        printText("\n");

                        printText1("Total                        : ");
                        printText1("Rp" + String.valueOf(df.format(total_price + tax + sc - disc)));
                        printText("\n");

                        printUnicode1();
                        printText1("\n");

                        printText1(">>>  Terima kasih atas kunjungan Anda  <<<"); // total 42 char in a single line
                        printText("\n");
                        printNewLine();
                        printText("\n");
                        printText1("\n");
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

    protected void connect() {
        if(btsocket == null){
            Intent BTIntent = new Intent(getApplicationContext(), DeviceList.class);
            this.startActivityForResult(BTIntent, DeviceList.REQUEST_CONNECT_BT);
        }
        else{
            OutputStream opstream = null;
            try {
                opstream = btsocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            btoutputstream = opstream;

            //print command
            try {
                Calendar cal_ = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
                Date currentLocalTime_ = cal_.getTime();
                DateFormat dateCounter_ = new SimpleDateFormat("dd MMM yyyy HH:mm");

                dateCounter_.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
                String localTimeCounter = dateCounter_.format(currentLocalTime_);
                String client_name = session.sClientFullName();
                String outlet_name = session.sOutlet();
                String user_name = session.sUserName();
                /*Cursor cAcc = read.rawQuery("SELECT a.client_fullname, b.user_fullname from m_clients a, m_users b WHERE b.client_id=a.client_id;", null);
                int rowsAcc = cAcc.getCount();
                if(rowsAcc > 0) {
                    cAcc.moveToFirst();
                    client_name = cAcc.getString(0);
                    user_name = cAcc.getString(1);
                }*/

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                btoutputstream = btsocket.getOutputStream();
                btoutputstream.flush();
                resetPrint();

                byte[] printformat = { 0x1B, 0x21, FONT_TYPE };
                btoutputstream.write(printformat);

                //print title
                //printUnicode();
                //print normal text
                //printPhoto();
                printTitle(outlet_name);
                printNewLine();
                printUnicodeforReset();
                Cursor cReadTrans = read.rawQuery("select datetime from m_transactions where trans_id="+session.nTransID(), null);
                int rowtrans = cReadTrans.getCount();
                cReadTrans.moveToFirst();
                String date = cReadTrans.getString(0);
                Date transdate = null;
                try {
                    transdate = sdf.parse(date);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                //printText1("Receipt : "+session.nTransNO());printText("\n");
                printText1("Receipt : "+session.nTransRealNo());printText("\n");
                printText1("Tanggal : "+dateCounter_.format(transdate));
                printText("\n");
                printText1("Kasir   : "+user_name);printText("\n");
                printUnicode();
                printText1("\n");
                printText1("Produk           Harga         Sub Total");
                printText1("\n");
                printUnicode();
                printText1("\n");
                int count = 0;
                double subtotal = 0;
                //Cursor cRead = read.rawQuery("SELECT b.prod_name, b.prod_price_sell from m_transactions_temp a, m_products b WHERE a.prod_id=b.prod_id ORDER BY a.trans_temp_id ASC;", null);
                Cursor cRead = read.rawQuery("select a.trans_det_id, count(a.prod_id) as qty_, count(a.prod_id) * b.prod_price_sell as totalprice, b.* from m_transactions_detail a, m_products b where a.prod_id=b.prod_id AND a.trans_id="+session.nTransID()+" GROUP BY a.prod_id ORDER BY a.trans_det_id ASC;", null);
                int rows = cRead.getCount();
                if(rows > 0) {
                    cRead.moveToFirst();
                    for (int i = 0; i < rows; i++) {
                        if(cRead.getString(4).length() > 16) {
                            String lastWord = cRead.getString(4).substring(cRead.getString(4).lastIndexOf(" ")+1);
                            printText1(cRead.getString(4).replace(" "+lastWord,""));
                            printText1("\n");
                            printText1(lastWord);
                            resetNoSpace();
                            btoutputstream.write(PrinterCommands.BS);
                            resetNoSpace();
                            //printText(Utils.SPACE_15);
                            int lWLength = lastWord.length();
                            for (int j = 0; j < (17 - lWLength); j++) {
                                printText(Utils.ONE_SPACE);
                            }
                        } else {
                            int length = cRead.getString(4).length();
                            printText1(cRead.getString(4));
                            for (int j = 0; j < (17 - length); j++) {
                                printText(Utils.ONE_SPACE);
                            }
                        }
                        String price = "Rp"+df.format(cRead.getDouble(7));
                        int pLength = price.length();
                        printText1(price);
                        if(pLength >= 9){
                            printText(Utils.ONE_SPACE);
                        } else if(pLength == 8){
                            for (int j = 0; j < 2; j++) {
                                printText(Utils.ONE_SPACE);
                            }
                        } else if(pLength == 7){
                            for (int j = 0; j < 3; j++) {
                                printText(Utils.ONE_SPACE);
                            }
                        }
                        String quantity = "x"+cRead.getString(1);
                        int qLength = quantity.length();
                        printText1(quantity);
                        if(qLength >= 3){
                            printText(Utils.ONE_SPACE);
                        } else if(qLength == 2){
                            for (int j = 0; j < 2; j++) {
                                printText(Utils.ONE_SPACE);
                            }
                        }
                        printText1("Rp"+df.format(cRead.getDouble(2)));
                        printText1("\n");
                        count = count + cRead.getInt(1);
                        subtotal = subtotal + cRead.getDouble(2);
                        cRead.moveToNext();
                    }

                    //cursor.getString(4), Rp+df.format(cursor.getDouble(7)), "x"+cursor.getString(1), Rp+df.format(cursor.getDouble(2))
                    //subtotal.setText(": Rp. "+String.valueOf(df.format(session.nSubTotal())));
                    //tax2.setText(": Rp. "+String.valueOf(df.format(session.nSubTotal() * 10/100)));
                    //total.setText(": Rp. "+String.valueOf(df.format(session.nSubTotal() + (session.nSubTotal() * 10/100))));

                    printUnicode1();
                    printText("\n");
                    printText1("Item                         : ");
                    printText1(String.valueOf(count));
                    printText("\n");

                    printText1("Pajak                      : ");
                    printText1("Rp"+ String.valueOf(df.format(subtotal * session.nTaxValue()/100)));
                    printText("\n");

                    printText1("Total                        : ");
                    printText1("Rp"+ String.valueOf(df.format(subtotal + (subtotal * session.nTaxValue()/100))));
                    printText("\n");

                    /*printText1("Bayar                        : ");
                    printText1("Rp150.000");
                    printText("\n");

                    printText1("Kembali                      : ");
                    printText1("Rp20.000");
                    printText("\n");*/
                    printUnicode1();
                    printText1("\n");
                    //byte[] printformat1 = { 0x1B, 0x21, FONT_TYPE };
                    //btoutputstream.write(printformat1);
                    printText1(">>>  Terima kasih atas kunjungan Anda  <<<"); // total 42 char in a single line
                    printText("\n");
                    printNewLine();
                    printText("\n");
                    printText1("\n");
                    //printUnicode();
                    //printNewLine();

                    btoutputstream.flush();
                    cRead.close();
                } else {
                    //Snackbar.make(view, "Belum ada transaksi yang dipilih", Snackbar.LENGTH_LONG);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //print Title
    public static void printTitle(String msg) {
        try {
            //Print config
            byte[] bb = new byte[]{0x1B,0x21,0x08};
            byte[] bb2 = new byte[]{0x1B,0x21,0x20};
            byte[] bb3 = new byte[]{0x1B,0x21,0x10};
            byte[] cc = new byte[]{0x1B,0x21,0x00};

            //btoutputstream.write(bb);
            //btoutputstream.write(bb2);
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

    //print photo
    public void printPhoto() {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                    R.drawable.idlogo);
            if(bmp!=null){
                byte[] command = Utils.decodeBitmap(bmp);
                printText(command);
            }else{
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
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

    //print unicode
    public static void printUnicode1(){
        try {
            btoutputstream.write(PrinterCommands.ESC_ALIGN_CENTER);
            printText(Utils.UNICODE_TEXT1);
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
            btoutputstream.write(PrinterCommands.ESC_FONT_COLOR_DEFAULT);
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

    private void print_bt() {
        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            btoutputstream = btsocket.getOutputStream();

            byte[] printformat = { 0x1B, 0x21, FONT_TYPE };
            btoutputstream.write(printformat);
            String msg = "PT Intelligence Dynamics\nDaftar Pesanan\n1. Burger   1   Rp 30.000\n2. Bakmi Godok   2   Rp 60.000";
            btoutputstream.write(msg.getBytes());
            btoutputstream.write(0x0D);
            btoutputstream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(getApplicationContext());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    private static class isOnline extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            try {
                int timeoutMs = 1500;
                Socket sock = new Socket();
                SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

                sock.connect(sockaddr, timeoutMs);
                sock.close();

                return true;
            } catch (IOException e) { return false; }
        }

        @Override
        protected void onPostExecute(Boolean online) {
            super.onPostExecute(online);
            internetAvailable = online;
            session.setInternetAvailable(online);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

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
}