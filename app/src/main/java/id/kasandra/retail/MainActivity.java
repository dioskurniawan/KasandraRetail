package id.kasandra.retail;
/*
versi 0.7.2
*/
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.florent37.tutoshowcase.TutoShowcase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

import id.kasandra.retail.BasicImageDownloader.ImageError;
import id.kasandra.retail.BasicImageDownloader.OnImageLoaderListener;
import id.kasandra.retail.bluetoothprint.DeviceListActivity;
import id.kasandra.retail.bluetoothprint.PrinterCommands;
import id.kasandra.retail.bluetoothprint.UnicodeFormatter;
import mehdi.sakout.fancybuttons.FancyButton;
import id.kasandra.retail.bluetoothprint.Utils;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, OnSharedPreferenceChangeListener {

    private static String sURL = "https://kasandra.biz/appdata/getdata_dev2.php";
    private ArrayList<HashMap<String, String>> categorylist = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> customerlist = new ArrayList<HashMap<String, String>>();
    private ListView list, listCustomer;
    public static final String TAG_CATEGORY = "category";
    public static final String TAG_LASTUPDATE = "lastupdate";
    public static final String TAG_ID = "cat_id";
    public static final String TAG_NAME = "name";
    public static final String TAG_DISCOUNT = "discount";
    public static final String TAG_DATE = "updated_date";
    public static final String TAG_ICON = "picture_path";

    public String sLastUpdate;
    public JSONArray customer = null;
    public JSONArray category = null;
    public String sQuery = "";

    private int nClientId = 0;
    private static SessionManager session;
    private ProgressDialog pDialog, pDialog2;
    final Handler handler = new Handler();
    final Handler handler2 = new Handler();
    final Handler handler3 = new Handler();
    final Handler handler4 = new Handler();
    int id_main;
    int n = 0;
    int n2 = 0;
    int n3 = 0;
    int n4 = 0;
    int count;
    private FragmentTabHost mTabHost;
    GridLayout layout;
    TableLayout table_layout, table_layout1;
    ScrollView scroll;
    TextView subtotal, total, tax2, disc2;
    SQLiteHandler db;
    SQLiteDatabase write;
    SQLiteDatabase read;
    private String[] sProduct, nPrice, nQty, nID, nCategoryID;
    double tp, prices;
    int totalBayar = 0;
    int totalKembali = 0;
    Typeface font1, font2;
    DecimalFormat df;
    TextView title;
    FloatingActionButton fab_a, fab_b, fab_c, fab_d;
    private ImageLoader_ imageLoader;
    ScrollView midScrollView;
    LinearLayout linearLayout;

    String column1 = "Nama Item", column2 = "Harga", column3 = "", column4 = "Jumlah";
    Cursor cursor;
    /*String column_value1 = null, column_value2 = null, column_value3 = null;
    column1 = "Nama Item";
    column2 = "Harga Satuan";
    column3 = "Jumlah";
    */
    DrawerLayout drawer;
    byte FONT_TYPE;
    private BluetoothSocket btsocket;
    private static OutputStream btoutputstream;
    private static final int MY_PERMISSIONS_1 = 0;
    private static final int MY_PERMISSIONS_2 = 1;
    private static final int MY_PERMISSIONS_3 = 2;
    FancyButton btnPrint;
    //FancyButton btnSave;
    boolean isLoading = false;
    boolean isLoading2 = false;
    boolean isLoading4 = false;
    boolean isSavingTrans = false;
    boolean hasPrint = false;
    boolean hasSaved = true;
    boolean isPaymentChosen = false;
    int val;
    private GoogleApiClient mGoogleApiClient;
    private int printType = 0;
    private int paymentType = 1;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBluetoothDevice;
    BluetoothDevice mBluetoothDevice2;
    CharSequence[] values;
    CharSequence[] values2;
    int[] currentChoice;
    int[] currentChoice2;
    AlertDialog alertDialog1, alertDialog2, alertDialog3, alertDialog4, alertDialog5;
    ProgressDialog mBluetoothPDialog;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private String btName;
    private String mDeviceAddress;
    private static final int RESULT_SETTINGS = 2;
    private OnSharedPreferenceChangeListener listner;
    private String WiFi, sNetURL, sVoucher, sVoucher2, printerChars;
    private ImageView imgView;
    ActionBar bar;
    double screenInches;
    String wiboStatus, wiboMsg, wiboUser, wiboPass;
    int nDiscount=0;
    int nTax;
    int nSC;
    int discValue, discValue2;
    static boolean internetAvailable = true;
    LinearLayout searchLayout;
    EditText txtSearch;
    Button clearText;
    String msgNotFound;
    private double nLat, nLong;
    private int printTimes = 0;
    TextView txt_customer;
    private int customer_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        id_main = getIntent().getExtras().getInt("id_main");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bar = getSupportActionBar();

        new isOnline().execute("");

        session = new SessionManager(this.getApplicationContext());
        customer_id = session.nCustID();
        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int dens = dm.densityDpi;
        double wi = (double) width / (double) dens;
        double hi = (double) height / (double) dens;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        screenInches = Math.sqrt(x + y);
        session.setScreenSize(screenInches);
        //Toast.makeText(getApplicationContext(), "Ukuran Layar : "+String.valueOf(screenInches)+" - "+wi+" x "+hi, Toast.LENGTH_LONG).show();

        internetAvailable = session.isInternetAvailable();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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
                    Toast.makeText(MainActivity.this, "Device Not Support", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(MainActivity.this,
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

        imageLoader = new ImageLoader_(getApplicationContext());

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        db = new SQLiteHandler(getApplicationContext());
        write = db.getWritableDatabase();
        read = db.getReadableDatabase();

        title = (TextView) findViewById(R.id.toolbar_title);

        midScrollView = (ScrollView) findViewById(R.id.midScrollView);
        midScrollView.setLayoutParams(new TableRow.LayoutParams(630, TableRow.LayoutParams.MATCH_PARENT));
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        linearLayout.setLayoutParams(new TableRow.LayoutParams(630, TableRow.LayoutParams.MATCH_PARENT));
        ScrollView scrollOrder = (ScrollView) findViewById(R.id.scrollOrder);
        LinearLayout.LayoutParams LLParamsScroll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout layoutRight = (LinearLayout) findViewById(R.id.layout_total);
        layoutRight.setBackgroundColor(getResources().getColor(R.color.lightgreen1));
        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        searchLayout = (LinearLayout) findViewById(R.id.search);
        //searchLayout.setVisibility(View.GONE);

        font1 = Typeface.createFromAsset(getAssets(), "quattrocentobold.ttf");
        font2 = Typeface.createFromAsset(getAssets(), "GenBasR.ttf");

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
        textView2.setTypeface(font2);

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');
        df = new DecimalFormat("#,###", otherSymbols);

        txt_customer = (TextView) findViewById(R.id.txtcustomer);
        txt_customer.setTypeface(font1);
        TextView txt_category = (TextView) findViewById(R.id.txtcategory);
        txt_category.setTypeface(font1);
        //TextView txt_empty = (TextView) findViewById(R.id.txt_empty);
        //txt_empty.setText("Belum ada kategori produk di "+session.sClientFullName()+", silakan klik tombol di bawah ini untuk menambahkan");
        /*FancyButton fab = (FancyButton) findViewById(R.id.fab_link);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new isOnline().execute("");
                if (internetAvailable) {
                    Intent intent = new Intent(getApplicationContext(), CategoryDetailActivity.class);
                    intent.putExtra("cat_id", 0);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                }

            }
        });*/
        int order_id;
        Cursor cOrder = read.rawQuery("SELECT MAX(cust_temp_id) from m_customers_temp;", null);
        cOrder.moveToFirst();
        order_id = cOrder.getInt(0);
        if(order_id < 1) {
            write.execSQL("INSERT INTO m_customers_temp (cust_temp_id, cust_name, cust_state) VALUES(1, 1, 1);");
        }
        cOrder.close();
        FancyButton fab_order = (FancyButton) findViewById(R.id.fab_link_cust);
        fab_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //write.execSQL("DELETE FROM m_customers_temp;");
                int order_id, newId;
                Cursor cOrder = read.rawQuery("SELECT MAX(cust_temp_id) from m_customers_temp;", null);
                cOrder.moveToFirst();
                order_id = cOrder.getInt(0);
                newId = order_id + 1;
                cOrder.close();
                //showMessage("order max", String.valueOf(order_id));
                write.execSQL("INSERT INTO m_customers_temp (cust_temp_id, cust_name, cust_state) VALUES("+newId+", "+newId+", 1);");
                showCustomerList();
                changeCust(newId);

                /*Cursor cOrder1 = read.rawQuery("SELECT MAX(cust_temp_id) from m_customers_temp;", null);
                cOrder1.moveToFirst();
                changeCust(cOrder1.getInt(0));*/

                //showMessage("order new",cOrder1.getString(0));
            }
        });
        layout = (GridLayout) findViewById(R.id.grid_layout);
        //scroll = (ScrollView) findViewById(R.id.scroll);
        table_layout1 = (TableLayout) findViewById(R.id.tableLaporan1);
        table_layout = (TableLayout) findViewById(R.id.tableLaporan);
        TextView subtotal1 = (TextView) findViewById(R.id.subtotal1);
        subtotal1.setTypeface(font1);
        subtotal = (TextView) findViewById(R.id.subtotal2);
        subtotal.setTypeface(font1);
        TextView total1 = (TextView) findViewById(R.id.total1);
        total1.setTypeface(font1);
        total = (TextView) findViewById(R.id.total2);
        total.setTypeface(font1);
        TextView tax1 = (TextView) findViewById(R.id.tax1);
        tax1.setTypeface(font1);
        tax2 = (TextView) findViewById(R.id.tax2);
        tax2.setTypeface(font1);
        TextView disc1 = (TextView) findViewById(R.id.disc1);
        disc1.setTypeface(font1);
        disc2 = (TextView) findViewById(R.id.disc2);
        disc2.setTypeface(font1);

        /*btnSave = (FancyButton) findViewById(R.id.btn_simpan);
        btnSave.setVisibility(View.INVISIBLE);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog));
                builder.setTitle("Tombol ini hanya akan menyimpan transaksi tanpa mencetak. Anda yakin akan melanjutkan ?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int trans_id;
                                Cursor cRead = read.rawQuery("SELECT prod_id from m_transactions_temp ORDER BY trans_temp_id ASC;", null);
                                int rows = cRead.getCount();
                                Cursor cRead1 = read.rawQuery("SELECT SUM(a.qty) AS total_qty, SUM(b.prod_price_sell) AS total_price from m_transactions_temp a, m_products b WHERE a.prod_id=b.prod_id;", null);
                                if(rows > 0) {
                                    cRead1.moveToFirst();
                                    write.execSQL("INSERT INTO m_transactions (total_qty, total_price, datetime, upload_status) VALUES(" + cRead1.getInt(0) + ", " + cRead1.getInt(1) + ", datetime('now', 'localtime'), 0);");
                                    Cursor cRead2 = read.rawQuery("SELECT MAX(trans_id) FROM m_transactions;", null);
                                    cRead2.moveToFirst();
                                    trans_id = cRead2.getInt(0);
                                    cRead.moveToFirst();
                                    for (int i = 0; i < rows; i++) {
                                        write.execSQL("INSERT INTO m_transactions_detail (prod_id, trans_id, upload_status) VALUES(" + cRead.getInt(0) + ", " + trans_id + ", 0);");
                                        cRead.moveToNext();
                                    }
                                    Snackbar.make(view, "Transaksi berhasil disimpan", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    write.execSQL("DELETE FROM m_transactions_temp;");
                                    showItem();
                                    isLoading = true;
                                    n=0;
                                    handlerTask.run();
                                } else {
                                    Snackbar.make(view, "Belum ada transaksi yang dipilih", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }
                        }
                );

                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }

                );

                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
            }
        });*/

        btnPrint = (FancyButton) findViewById(R.id.btn_print);
        btnPrint.setVisibility(View.INVISIBLE);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)) {

                        if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.BLUETOOTH)) && (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN))) {
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.BLUETOOTH}, MY_PERMISSIONS_2);
                            ActivityCompat.requestPermissions(MainActivity.this,
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

        if (internetAvailable) {
            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        } else {
            bar.setBackgroundDrawable(new ColorDrawable(Color.RED));
        }
        if (session.screenSize() < 7.9 && session.screenSize() > 6.5) {
            title.setTextSize(20);
            txt_customer.setTextSize(17);
            txt_category.setTextSize(17);
            midScrollView.setLayoutParams(new TableRow.LayoutParams(390, TableRow.LayoutParams.MATCH_PARENT));
            linearLayout.setLayoutParams(new TableRow.LayoutParams(390, TableRow.LayoutParams.MATCH_PARENT));
            subtotal1.setTextSize(13);
            subtotal.setTextSize(13);
            tax1.setTextSize(13);
            tax2.setTextSize(13);
            disc1.setTextSize(13);
            disc2.setTextSize(13);
            total1.setTextSize(16);
            total.setTextSize(16);
            btnPrint.setLayoutParams(new TableRow.LayoutParams(0, 65, .60f));
            btnPrint.setFontIconSize(24);
            LLParams.topMargin = 0xffffff10;
            layoutRight.setLayoutParams(LLParams);
            LLParamsScroll.bottomMargin = 242;
            scrollOrder.setLayoutParams(LLParamsScroll);
            textView1.setTextSize(16);
            textView2.setTextSize(12);
        } else if (session.screenSize() < 6.5) {
            title.setTextSize(20);
            txt_customer.setTextSize(16);
            txt_category.setTextSize(16);
            midScrollView.setLayoutParams(new TableRow.LayoutParams(390, TableRow.LayoutParams.MATCH_PARENT));
            linearLayout.setLayoutParams(new TableRow.LayoutParams(390, TableRow.LayoutParams.MATCH_PARENT));
            subtotal1.setTextSize(11);
            subtotal.setTextSize(11);
            tax1.setTextSize(11);
            tax2.setTextSize(11);
            disc1.setTextSize(11);
            disc2.setTextSize(11);
            total1.setTextSize(14);
            total.setTextSize(14);
            btnPrint.setLayoutParams(new TableRow.LayoutParams(0, 65, .60f));
            btnPrint.setFontIconSize(22);
            LLParams.topMargin = 0xfffffec0;
            layoutRight.setLayoutParams(LLParams);
            LLParamsScroll.bottomMargin = 320;
            scrollOrder.setLayoutParams(LLParamsScroll);
            textView1.setTextSize(14);
            textView2.setTextSize(12);
        } else if (session.screenSize() > 9.0) {
            midScrollView.setLayoutParams(new TableRow.LayoutParams(860, TableRow.LayoutParams.MATCH_PARENT));
            linearLayout.setLayoutParams(new TableRow.LayoutParams(860, TableRow.LayoutParams.MATCH_PARENT));
        }/*else {
            title.setTextSize(25);
            txt_category.setTextSize(22);
            midScrollView.setLayoutParams(new TableRow.LayoutParams(750, TableRow.LayoutParams.MATCH_PARENT));
            subtotal1.setTextSize(18);
            subtotal.setTextSize(18);
            tax1.setTextSize(18);
            tax2.setTextSize(18);
            total1.setTextSize(22);
            total.setTextSize(22);
            btnPrint.setLayoutParams(new TableRow.LayoutParams(0,80,.77f));
            btnPrint.setFontIconSize(27);
            LLParams.topMargin = 0xfffffefc;
            layoutRight.setLayoutParams(LLParams);
            LLParamsScroll.bottomMargin = 262;
            scrollOrder.setLayoutParams(LLParamsScroll);
            textView1.setTextSize(20);
            textView2.setTextSize(16);
        }*/

        pDialog = new ProgressDialog(new ContextThemeWrapper(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog));

        session.setSubTotal(0);
        session.setDiscTotal(0);

        if (nClientId == 0) {
            nClientId = session.nClient_ID();
        }
        // check nClientId from persistence store
        if (session.nClient_ID() != -999) {
            nClientId = session.nClient_ID();
        }
        //Toast.makeText(getBaseContext(), "User ID = "+session.nUserID(), Toast.LENGTH_SHORT).show();

        //get the user settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        listner = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                //sNetURL = prefs.getString("token", "kasandra.biz").trim();
                /*sVoucher = prefs.getString("voucher", "58").trim();
                session.setVoucher(sVoucher);
                if (session.sVoucher().equals("50")) {
                    sVoucher2 = "30";
                } else if (session.sVoucher().equals("52")) {
                    sVoucher2 = "165MB";
                } else if (session.sVoucher().equals("53")) {
                    sVoucher2 = "350MB";
                } else if (session.sVoucher().equals("54")) {
                    sVoucher2 = "2 GB";
                } else if (session.sVoucher().equals("55")) {
                    sVoucher2 = "1 GB";
                } else if (session.sVoucher().equals("56")) {
                    sVoucher2 = "10 GB";
                } else if (session.sVoucher().equals("58")) {
                    sVoucher2 = "1 jam";
                }*/
                printerChars = prefs.getString("printer_chars", "42");
                session.setPrinterChars(parseInt(printerChars));
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listner);
        //sNetURL = prefs.getString("token", "kasandra.biz").trim();
        /*sVoucher = prefs.getString("voucher", "58").trim();
        session.setVoucher(sVoucher);
        if (session.sVoucher().equals("50")) {
            sVoucher2 = "30";
        } else if (session.sVoucher().equals("52")) {
            sVoucher2 = "165MB";
        } else if (session.sVoucher().equals("53")) {
            sVoucher2 = "350MB";
        } else if (session.sVoucher().equals("54")) {
            sVoucher2 = "2 GB";
        } else if (session.sVoucher().equals("55")) {
            sVoucher2 = "1 GB";
        } else if (session.sVoucher().equals("56")) {
            sVoucher2 = "10 GB";
        } else if (session.sVoucher().equals("58")) {
            sVoucher2 = "1 jam";
        }*/
        printerChars = prefs.getString("printer_chars", "42");
        session.setPrinterChars(parseInt(printerChars));

        /*for (int j=0; j<=12; j++){

            /=Button btnTag = new Button(this);
            btnTag.setLayoutParams(new TableRow.LayoutParams(250,250));
            btnTag.setPadding(3, 3, 3, 3);
            //btnTag.setBackgroundDrawable(getResources().getDrawable(R.drawable.salmon));
            btnTag.setBackgroundResource(R.drawable.salmon);
            btnTag.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_bg));
            btnTag.setGravity(Gravity.CENTER_VERTICAL | Gravity.FILL_VERTICAL);
            btnTag.requestLayout();
            //btnTag.setText("Button " + (j + 1));
            btnTag.setId(j + 1);=/

            ImageView imgView = new ImageView(this);
            imgView.setId(j+1);
            imgView.setLayoutParams(new TableRow.LayoutParams(230, 230));
            imgView.setPadding(5,5,5,5);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.imgview_bg));
            new DownloadImageTask(imgView).execute("https://kasandra.biz/images/1-sdb.png");

            layout.addView(imgView);

        }*/

        currentChoice = new int[]{0};
        values = new CharSequence[]{"Transaksi Selesai dan Cetak", "Transaksi Selesai", "Bill Sementara"};
        currentChoice2 = new int[]{0};
        values2 = new CharSequence[]{"Cash", "Credit Card", "Debit Card", "Mobile Payment"};
        sendtoServer();

        //new syncAccount().execute("select");
        new syncCategory().execute("select");
        if (id_main == 0) {
            new getToken().execute();
            new isOnline().execute("");
            if (internetAvailable) {
                new getTransactions().execute();
                new getTransactionsDetail().execute();
            }
            new syncProduct().execute("select");
            gettax();
            getsc();
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
                showCustomerList();
                new populateCatList().execute();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findp("");
                        //Toast.makeText(getApplicationContext(), "Call Find-P(1)", Toast.LENGTH_LONG).show();
                    }
                }, 2000);
                //isLoading2 = true;
                //n2=0;
                //new Thread(handlerTask2).start();
                //handlerTask2.run();
                //findp();
            }
        } else {
            showCustomerList();
            new populateCatList().execute();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    findp("");
                    //Toast.makeText(getApplicationContext(), "Call Find-P(2)", Toast.LENGTH_LONG).show();
                }
            }, 2000);
            //isLoading2 = true;
            //n2=0;
            //new Thread(handlerTask2).start();
            //handlerTask2.run();
            //findp();
        }

        /*try {
            if (!isConnected()) {
                Toast.makeText(getApplicationContext(), R.string.no_internet1 + " dan silakan coba kembali", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_internet1 + " sudah online", Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d("kasandra", "kasandra debug problem saving data:" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("kasandra", "kasandra debug problem saving data:" + e.getMessage());
        }*/

        /*if(session.sWiFi().equals("WiFi disconnected") || session.sWiFi().equals("WiFi disabled")) {
            //Toast.makeText(getApplication(), "Silakan pastikan pengaturan WiFi sudah terkoneksi sebelum mencetak. Jika WiFi tidak terhubung, maka informasi SSID WiFi tidak dapat dicetak", Toast.LENGTH_LONG).show();
            drawer.openDrawer(Gravity.LEFT);
            displayTuto();
        }*/

        if (!session.getShowcase()) {
            session.setShowcase(true);
            drawer.openDrawer(Gravity.LEFT);
            displayTuto();
        }
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    showItem();
                } catch (final Exception ex) {
                    Log.i("---", "Exception in thread 5");
                }
            }
        });
        //Toast.makeText(getApplicationContext(), "token : "+session.sJWT(), Toast.LENGTH_SHORT).show();

        txtSearch = (EditText) findViewById(R.id.txtSearch);
        clearText = (Button) findViewById(R.id.cleartext);
        txtSearch.addTextChangedListener(new MyTextWatcher(txtSearch));
        final FancyButton btnSearch = (FancyButton) findViewById(R.id.search_button);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String text = txtSearch.getText().toString();
                if (text.equals("")) {
                    Toast.makeText(getApplicationContext(), "Silakan inputkan SKU/PLU atau Nama Produk", Toast.LENGTH_SHORT).show();
                } else {
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findp(text);
                            //Toast.makeText(getApplicationContext(), "Call Find-P(2)", Toast.LENGTH_LONG).show();
                        }
                    }, 2000);
                }
            }
        });
        clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtSearch.setText("");
                findp("");
            }
        });
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

                if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) && (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))) {
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_3);
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_3);
                }
            } else {
                new getLatLng().execute("loc");
            }
        } else {
            new getLatLng().execute("loc");
        }
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();

        changeCust(1);
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            final String text = txtSearch.getText().toString();
            if(text.equals("")){
                //Toast.makeText(getApplicationContext(), "Silakan inputkan SKU/PLU atau Nama Produk", Toast.LENGTH_SHORT).show();
            } else {
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findp(text);
                    }
                }, 2000);
            }
        }

        public void afterTextChanged(Editable editable) {
        }
    }

    private void savePreferences() {
        try {
            //save persistent data
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            //editor.putString("token", sNetURL);
            //editor.putString("voucher", sVoucher);
            editor.putString("printer_chars", printerChars);
            session.setPrinterChars(parseInt(printerChars));
            editor.apply();

            /*session.setVoucher(sVoucher);
            if(session.sVoucher().equals("50")){
                sVoucher2 = "30";
            } else if(session.sVoucher().equals("52")){
                sVoucher2 = "165MB";
            } else if(session.sVoucher().equals("53")){
                sVoucher2 = "350MB";
            } else if(session.sVoucher().equals("54")){
                sVoucher2 = "2 GB";
            } else if(session.sVoucher().equals("55")){
                sVoucher2 = "1 GB";
            } else if(session.sVoucher().equals("56")){
                sVoucher2 = "10 GB";
            } else if(session.sVoucher().equals("58")){
                sVoucher2 = "1 jam";
            }*/

            //Toast.makeText(getApplicationContext(), "URL WiFi Hotspot : "+sNetURL, Toast.LENGTH_LONG).show();
        }
        catch (Exception e) { }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "quattrocentobold.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void showItem() {
        //write.execSQL("DELETE FROM m_transactions;");
        table_layout1.removeAllViews();

        TableRow row1 = new TableRow(getApplicationContext());
        row1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        //row1.setBackgroundColor(getResources().getColor(R.color.bg_login));

        TextView tv_ = new TextView(getApplicationContext());
        tv_.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv_.setGravity(Gravity.LEFT);
        tv_.setTypeface(font1, Typeface.BOLD);
        tv_.setTextColor(Color.WHITE);
        //tv_.setTextSize(18);
        tv_.setText("No.");
        row1.addView(tv_);

        /*View v = new View(getApplicationContext());
        v.setLayoutParams(new TableRow.LayoutParams(1,TableRow.LayoutParams.MATCH_PARENT));
        v.setBackgroundColor(getResources().getColor(R.color.white));
        row1.addView(v);*/

        TextView tv2_ = new TextView(getApplicationContext());
        tv2_.setLayoutParams(new TableRow.LayoutParams(200, TableRow.LayoutParams.WRAP_CONTENT));
        tv2_.setGravity(Gravity.LEFT);
        tv2_.setTypeface(font1, Typeface.BOLD);
        tv2_.setTextColor(Color.WHITE);
        //tv2_.setTextSize(18);
        tv2_.setText(column1);
        row1.addView(tv2_);

        TextView tv3_ = new TextView(getApplicationContext());
        tv3_.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv3_.setGravity(Gravity.RIGHT);
        tv3_.setTypeface(font1, Typeface.BOLD);
        tv3_.setTextColor(Color.WHITE);
        //tv3_.setTextSize(18);
        tv3_.setText(column2);
        row1.addView(tv3_);

        TextView tv4_ = new TextView(getApplicationContext());
        tv4_.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv4_.setGravity(Gravity.RIGHT);
        tv4_.setTypeface(font1, Typeface.BOLD);
        tv4_.setTextColor(Color.WHITE);
        //tv4_.setTextSize(18);
        tv4_.setText(column3);
        row1.addView(tv4_);

        TextView tv5_ = new TextView(getApplicationContext());
        tv5_.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv5_.setGravity(Gravity.CENTER);
        tv5_.setTypeface(font1, Typeface.BOLD);
        tv5_.setTextColor(Color.WHITE);
        //tv5_.setTextSize(18);
        tv5_.setText(column4);
        row1.addView(tv5_);

        if(session.screenSize() < 7.9 && session.screenSize() > 6.5) {
            tv_.setTextSize(12);
            tv2_.setTextSize(12);
            tv3_.setTextSize(12);
            tv4_.setTextSize(12);
            tv5_.setTextSize(12);
        } else if(session.screenSize() < 6.5) {
            tv_.setTextSize(12);
            tv2_.setTextSize(12);
            tv3_.setTextSize(12);
            tv4_.setTextSize(12);
            tv5_.setTextSize(12);
        } else {
            tv_.setTextSize(18);
            tv2_.setTextSize(18);
            tv3_.setTextSize(18);
            tv4_.setTextSize(18);
            tv5_.setTextSize(18);
        }

        table_layout1.addView(row1);

        table_layout.removeAllViews();
        //if(rows == 0) {
        //} else {
        //row.removeView(row);
        // inner for loop
        //int customer_id = session.nCustID();
        cursor = read.rawQuery("select a.trans_temp_id, count(a.qty) as qty_, count(a.qty) * b.prod_price_sell as totalprice, b.* from m_transactions_temp a, m_products b where a.prod_id=b.prod_id and a.cust_temp_id="+customer_id+" GROUP BY a.prod_id;", null);// ORDER BY a.trans_temp_id ASC;", null);
        //select a.* from fSnapshots a JOIN (SELECT MAX(b.snap_ts) 'max_snap_ts' FROM fSnapshots b where b.house_id = '$house_id' GROUP BY date(b.snap_ts)) c ON c.max_snap_ts = a.snap_ts order by a.snap_ts asc;
        int rows = cursor.getCount();
        //int cols = cursor.getColumnCount();

        //Toast.makeText(mContext, String.valueOf(rows), Toast.LENGTH_SHORT).show();
        if(rows == 0){

            TableRow row = new TableRow(getApplicationContext());
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT));
            TextView tv = new TextView(getApplicationContext());
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
            tv.setGravity(Gravity.CENTER);
            tv.setTypeface(font2, Typeface.ITALIC);
            tv.setTextColor(Color.RED);
            tv.setTextSize(15);
            tv.setPadding(20, 0, 0, 0);
            tv.setText("Belum ada transaksi");
            row.addView(tv);

            btnPrint.setVisibility(View.INVISIBLE);
            isSavingTrans = false;
            invalidateOptionsMenu();
            //btnSave.setVisibility(View.INVISIBLE);
            session.setSubTotal(0);
            session.setDiscTotal(0);
            subtotal.setText(": Rp "+ String.valueOf(df.format(session.nSubTotal())));
            tax2.setText(": Rp "+ String.valueOf(df.format((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100)) + " + " + String.valueOf(df.format(getServCharge())));
            disc2.setText(": Rp "+ String.valueOf(df.format(session.nDiscTotal())));
            total.setText(": Rp "+ String.valueOf(df.format((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge())));

            //table_layout.removeAllViews();
            table_layout.addView(row);
        } else {
            //final int finalI = i;
            /*final TableRow row = new TableRow(getApplicationContext());
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT));*/
                                /*if(finalI %2 == 0) {
                                    row.setBackgroundColor(getResources().getColor(R.color.light_grey));
                                } else {*/
            //}
            btnPrint.setVisibility(View.VISIBLE);
            //btnSave.setVisibility(View.VISIBLE);
            cursor.moveToFirst();

            // outer for loop
            count = 0;
            sProduct = new String[cursor.getCount()];
            nQty = new String[cursor.getCount()];
            nPrice = new String[cursor.getCount()];
            nID = new String[cursor.getCount()];
            nCategoryID = new String[cursor.getCount()];
            // outer for loop
            for (int i = 0; i < rows; i++) {
                sProduct[i] = cursor.getString(4);
                nQty[i] = cursor.getString(1);
                nPrice[i] = cursor.getString(7);
                nID[i] = cursor.getString(3);
                nCategoryID[i] = cursor.getString(8);
                Cursor cursorDisc = read.rawQuery("select discount from m_category where category_id="+nCategoryID[i], null);
                int rowDisc = cursorDisc.getCount();
                if(rowDisc > 0) {
                    cursorDisc.moveToFirst();
                    nDiscount = cursorDisc.getInt(0);
                }
                cursorDisc.close();

                TableRow row = new TableRow(getApplicationContext());
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
                if(i %2 == 0) {
                    row.setBackgroundColor(getResources().getColor(R.color.lightgreen2));
                } else {
                    row.setBackgroundColor(getResources().getColor(R.color.white));
                }
                TableRow row2 = new TableRow(getApplicationContext());
                row2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
                if(i %2 == 0) {
                    row2.setBackgroundColor(getResources().getColor(R.color.lightgreen2));
                } else {
                    row2.setBackgroundColor(getResources().getColor(R.color.white));
                }

                count++;
                TextView tv = new TextView(getApplicationContext());
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
                tv.setGravity(Gravity.CENTER);
                tv.setTypeface(font1);
                //tv.setTextSize(16);
                tv.setText(String.valueOf(count));
                tv.setTextColor(Color.BLACK);
                row.addView(tv);
                TextView tv_2 = new TextView(getApplicationContext());
                tv_2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
                tv_2.setGravity(Gravity.END);
                tv_2.setTypeface(font1);
                tv_2.setText("");
                tv_2.setTextColor(Color.BLACK);
                row2.addView(tv_2);

                TextView tv2 = new TextView(getApplicationContext());
                /*TableRow.LayoutParams tlparams = new TableRow.LayoutParams(
                        250,
                        60, Gravity.CENTER);
                tv2.setLayoutParams(tlparams);*/
                tv2.setLayoutParams(new TableRow.LayoutParams(255, 105, Gravity.CENTER));
                tv2.setGravity(Gravity.LEFT);
                tv2.setTypeface(font2, Typeface.BOLD);
                //tv2.setTextSize(15);
                tv2.setText(cursor.getString(4));
                tv2.setTextColor(Color.BLACK);
                row.addView(tv2);
                TextView tv2_2 = new TextView(getApplicationContext());
                tv2_2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
                tv2_2.setGravity(Gravity.END);
                tv2_2.setTypeface(font2, Typeface.BOLD);
                tv2_2.setText("Diskon");
                tv2_2.setTextColor(Color.BLUE);
                tv2_2.setVisibility(View.GONE);
                row2.addView(tv2_2);

                TextView tv3 = new TextView(getApplicationContext());
                tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
                tv3.setGravity(Gravity.RIGHT);
                tv3.setTypeface(font2, Typeface.BOLD);
                //tv3.setTextSize(15);
                tv3.setText(df.format(cursor.getDouble(7)));
                tv3.setTextColor(Color.BLACK);
                row.addView(tv3);
                TextView tv3_2 = new TextView(getApplicationContext());
                tv3_2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
                tv3_2.setGravity(Gravity.RIGHT);
                tv3_2.setTypeface(font2, Typeface.BOLD);
                tv3_2.setTextColor(Color.BLUE);
                tv3_2.setVisibility(View.GONE);
                if(nDiscount > 0) {
                    if(nDiscount > 100) {
                        discValue = nDiscount * parseInt(cursor.getString(1));
                    } else {
                        discValue = (cursor.getInt(7) * nDiscount / 100) * parseInt(cursor.getString(1));
                    }
                    tv2_2.setVisibility(View.VISIBLE);
                    tv3_2.setVisibility(View.VISIBLE);
                    tv3_2.setText(String.valueOf(discValue));
                }
                row2.addView(tv3_2);

                TextView tv4 = new TextView(getApplicationContext());
                tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
                tv4.setGravity(Gravity.RIGHT);
                tv4.setTypeface(font2, Typeface.BOLD);
                //tv4.setTextSize(15);
                tv4.setText("x"+cursor.getString(1));
                tv4.setTextColor(Color.BLACK);
                row.addView(tv4);
                TextView tv4_2 = new TextView(getApplicationContext());
                tv4_2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
                tv4_2.setGravity(Gravity.RIGHT);
                tv4_2.setTypeface(font2, Typeface.BOLD);
                tv4_2.setText("");
                tv4_2.setTextColor(Color.BLACK);
                row2.addView(tv4_2);
                TextView tv5_2 = new TextView(getApplicationContext());
                tv5_2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
                tv5_2.setGravity(Gravity.RIGHT);
                tv5_2.setTypeface(font2, Typeface.BOLD);
                tv5_2.setText("");
                tv5_2.setTextColor(Color.BLACK);
                row2.addView(tv5_2);
                TextView tv6_2 = new TextView(getApplicationContext());
                tv6_2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
                tv6_2.setGravity(Gravity.RIGHT);
                tv6_2.setTypeface(font2, Typeface.BOLD);
                tv6_2.setText("");
                tv6_2.setTextColor(Color.BLACK);
                row2.addView(tv6_2);

                Button btnAdd = new Button(getApplicationContext());
                btnAdd.setId(count+1);
                btnAdd.setLayoutParams(new TableRow.LayoutParams(40, TableLayout.LayoutParams.WRAP_CONTENT));
                btnAdd.setGravity(Gravity.END);
                btnAdd.setPadding(1,0,0,0);
                //btnAdd.setTextSize(30);
                //btnAdd.setTextColor(getResources().getColor(R.color.darkgreen));
                if(i %2 == 0) {
                    btnAdd.setBackgroundColor(getResources().getColor(R.color.lightgreen2));
                } else {
                    btnAdd.setBackgroundColor(Color.WHITE);
                }
                //btnAdd.setBackgroundResource(R.mipmap.del);
                //btnAdd.setBackgroundDrawable(getResources().getDrawable(R.drawable.cancel_icon));
                //btnAdd.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
                //btnAdd.setText("+");
                btnAdd.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btnadd, 0, 0, 0);
                row.addView(btnAdd);

                Button btnDel = new Button(getApplicationContext());
                btnDel.setId(count+1);
                btnDel.setLayoutParams(new TableRow.LayoutParams(40, TableLayout.LayoutParams.WRAP_CONTENT));
                //btnDel.setLayoutParams(new TableRow.LayoutParams(48, 48));
                btnDel.setGravity(Gravity.START);
                btnDel.setPadding(0,0,-2,0);
                //btnDel.setTextSize(30);
                //btnDel.setTextColor(Color.RED);
                if(i %2 == 0) {
                    btnDel.setBackgroundColor(getResources().getColor(R.color.lightgreen2));
                } else {
                    btnDel.setBackgroundColor(Color.WHITE);
                }
                //btnDel.setBackgroundResource(R.mipmap.del);
                //btnDel.setBackgroundDrawable(getResources().getDrawable(R.drawable.cancel_icon));
                //btnDel.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40);
                //btnDel.setText("-");
                btnDel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btnremove, 0, 0, 0);
                row.addView(btnDel);

                final int finalI = i;
                final int finalI1 = i;
                final int finalI2 = i;
                final int finalI3 = i;
                final int finalI4 = i;
                final int finalI5 = i;
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gettax();
                        //if(session.isSaved()) {
                            Cursor cursorDisc = read.rawQuery("select discount from m_category where category_id="+nCategoryID[finalI5], null);
                            int rowDisc = cursorDisc.getCount();
                            if(rowDisc > 0) {
                                cursorDisc.moveToFirst();
                                nDiscount = cursorDisc.getInt(0);
                            }
                            cursorDisc.close();
                            /*if(nDiscount > 0) {
                                if(nDiscount > 100) {
                                    discValue2 = nDiscount * Integer.parseInt(cursor.getString(1));
                                } else {
                                    discValue2 = (Integer.parseInt(nPrice[finalI5]) * nDiscount / 100) * Integer.parseInt(cursor.getString(1));
                                }
                            }*/
                            if(nDiscount > 0) {
                                if(nDiscount > 100) {
                                    discValue2 = nDiscount;
                                } else {
                                    discValue2 = (parseInt(nPrice[finalI5]) * nDiscount / 100);
                                }
                            } else {
                                discValue2 = 0;
                            }
                            write.execSQL("INSERT INTO m_transactions_temp (prod_id, qty, discount, cust_temp_id) VALUES(" + nID[finalI3] + ", 1, "+discValue2+", "+session.nCustID()+");");
                            //Snackbar.make(view, sProduct[finalI3] + " - Rp." + df.format(Double.parseDouble(nPrice[finalI3])), Snackbar.LENGTH_LONG)
                            //        .setAction("Action", null).show();
                            showItem();
                        //} else {
                        //    showMessage("PERINGATAN", "Transaksi tidak bisa diubah karena Bill Sementara sudah dicetak.\n\nSilakan pilih opsi Transaksi Selesai untuk membuat transaksi baru.");
                        //}
                    }
                });
                btnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!session.isSaved() && (session.nCustomerId() == customer_id)) {
                            showMessage("PERINGATAN", "Produk tidak bisa dihapus karena Bill Sementara sudah dicetak.\n\nSilakan pilih opsi Transaksi Selesai untuk membuat transaksi baru.");
                        } else {
                            //write.execSQL("DELETE FROM m_transactions where prod_id="+nID[finalI1]+" LIMIT 1");
                            write.execSQL("delete from m_transactions_temp where trans_temp_id in (select trans_temp_id from m_transactions_temp where prod_id=" + nID[finalI1] + " and cust_temp_id="+customer_id+" LIMIT 1)");
                            //Log.d("kasandra debug","DELETE FROM m_transactions where prod_id='"+nID[finalI1]+"' LIMIT 1");
                            //Snackbar.make(view, sProduct[finalI] + " Dihapus", Snackbar.LENGTH_LONG)
                            //        .setAction("Action", null).show();
                            showItem();
                        }
                        //table_layout.removeViewAt(finalI);
                    }
                });

                if(session.screenSize() < 7.9 && session.screenSize() > 6.5) {
                    tv.setTextSize(10);
                    tv2.setTextSize(10);
                    tv3.setTextSize(10);
                    tv4.setTextSize(10);
                    btnAdd.setTextSize(25);
                    btnDel.setTextSize(25);
                } else if(session.screenSize() < 6.5) {
                    tv.setTextSize(10);
                    tv2.setTextSize(10);
                    tv3.setTextSize(10);
                    tv4.setTextSize(10);
                    btnAdd.setTextSize(18);
                    btnDel.setTextSize(18);
                } else {
                    tv.setTextSize(15);
                    tv2.setTextSize(15);
                    tv3.setTextSize(15);
                    tv4.setTextSize(15);
                    btnAdd.setTextSize(30);
                    btnDel.setTextSize(30);
                }
                cursor.moveToNext();
                table_layout.removeView(row);
                table_layout.addView(row);
                table_layout.addView(row2);
            }
            cursor.close();

            Cursor cursor1 = read.rawQuery("select sum(b.prod_price_sell) as total_price, sum(a.discount) as totaldisc from m_transactions_temp a, m_products b where a.prod_id=b.prod_id and a.cust_temp_id="+customer_id+";", null);
            cursor1.moveToFirst();
            session.setSubTotal(cursor1.getDouble(0));
            session.setDiscTotal(cursor1.getInt(1));
            subtotal.setText(": Rp "+ String.valueOf(df.format(session.nSubTotal())));
            tax2.setText(": Rp "+ String.valueOf(df.format((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100)) + " + " + String.valueOf(df.format(getServCharge())));
            disc2.setText(": Rp "+ String.valueOf(df.format(session.nDiscTotal())));
            total.setText(": Rp "+ String.valueOf(df.format((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge())));
            cursor1.close();
        }

        /*Cursor getTrans = read.rawQuery("SELECT MAX(trans_no) FROM m_transactions;", null);
        int rowTrans = getTrans.getCount();
        //Toast.makeText(getApplicationContext(), "Jumlah trans "+nClientId+"-"+rowTrans, Toast.LENGTH_LONG).show();
        if(rowTrans > 0) {
            getTrans.moveToLast();
            val = getTrans.getInt(0) + 1;
            title.setText("Penjualan #" + val);
        } else {
            val = 1;
            title.setText("Penjualan #1");
        }*/
        showTitle();
    }

    public void showTitle() {
        Cursor getTrans = read.rawQuery("SELECT MAX(trans_no) FROM m_transactions;", null);
        int rowTrans = getTrans.getCount();
        //Toast.makeText(getApplicationContext(), "Jumlah trans "+nClientId+"-"+rowTrans, Toast.LENGTH_LONG).show();
        if(rowTrans > 0) {
            getTrans.moveToLast();
            val = getTrans.getInt(0) + 1;
            title.setText("Penjualan #" + val + " - Meja " + session.nCustID());
        } else {
            val = 1;
            title.setText("Penjualan #1" + session.nCustID());
        }
        getTrans.close();
    }

    private double getServCharge() {
        return ((session.nSubTotal() - session.nDiscTotal()) * session.nSCValue()/100);
    }

    private void callPrinter() {
        isPaymentChosen = false;
        update();
        printType = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog));
        builder.setTitle("Cetak Transaksi");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(printType == 0){
                            if (btsocket == null) {
                                Toast.makeText(getApplication(), "Silakan hubungkan printer terlebih dahulu", Toast.LENGTH_LONG).show();
                                drawer.openDrawer(Gravity.LEFT);
                            } else {
                                //if(session.sWiFi().equals("WiFi disconnected") || session.sWiFi().equals("WiFi disabled")) { /* disable feature 20190208 */
                                //    Toast.makeText(getApplication(), "Silakan pastikan pengaturan WiFi sudah terkoneksi sebelum mencetak. Jika WiFi tidak terhubung, maka informasi SSID WiFi tidak dapat dicetak", Toast.LENGTH_LONG).show();
                                    //drawer.openDrawer(Gravity.LEFT);
                                //}// else {
                                    if (!isPaymentChosen) {
                                        choosePaymentType();
                                    } else {
                                        getwibopass();
                                        showLoading();
                                        handler4.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                int copyPrintIs;
                                                if(session.getDoubleReceipt()){
                                                    copyPrintIs = 2;
                                                } else {
                                                    copyPrintIs = 1;
                                                }
                                                for (int x = 1; x <= copyPrintIs; x++) {
                                                    printReceipt(1);
                                                    SystemClock.sleep(4000);//This will pause every 4 seconds after printing once and the continue and pause again
                                                }
                                            }
                                        }, 5000);
                                        //isLoading4=true;
                                        //n4=0;
                                        //new Thread(handlerTask4).start();
                                        //handlerTask4.run();
                                        //printReceipt();
                                        /*if (hasPrint) {
                                            simpan();
                                            isLoading = true;
                                            n=0;
                                            handlerTask.run();
                                            hasPrint = false;
                                            printType = 0;
                                        }
                                        isPaymentChosen = false;*/
                                    }
                                //}
                            }
                        } else if (printType == 1){
                            if (btsocket == null) {
                                Toast.makeText(getApplication(), "Silakan hubungkan printer terlebih dahulu", Toast.LENGTH_LONG).show();
                                drawer.openDrawer(Gravity.LEFT);
                            } else {
                                /*if (!isPaymentChosen) {
                                    choosePaymentType();
                                } else {*/

                                //if(session.sWiFi().equals("WiFi disconnected") || session.sWiFi().equals("WiFi disabled")) { /* disable feature 20190208 */
                                //    Toast.makeText(getApplication(), "Silakan pastikan pengaturan WiFi sudah terkoneksi sebelum mencetak. Jika WiFi tidak terhubung, maka informasi SSID WiFi tidak dapat dicetak", Toast.LENGTH_LONG).show();
                                    //drawer.openDrawer(Gravity.LEFT);
                                //}// else {
                                getwibopass();
                                showLoading();
                                handler4.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        int copyPrintIs;
                                        if(session.getDoubleReceipt()){
                                            copyPrintIs = 2;
                                        } else {
                                            copyPrintIs = 1;
                                        }
                                        for (int x = 1; x <= copyPrintIs; x++) {
                                            printReceipt(0);
                                            SystemClock.sleep(4000);//This will pause every 4 seconds after printing once and the continue and pause again
                                        }
                                    }
                                }, 5000);
                                simpan_bill();
                                //isLoading4=true;
                                //n4=0;
                                //new Thread(handlerTask4).start();
                                //handlerTask4.run();
                                //printReceipt();
                                    hasSaved = false;
                                    session.setSavedTrue(hasSaved, customer_id);
                                /*    if (hasPrint) {
                                        simpan();
                                        isLoading = true;
                                        n=0;
                                        handlerTask.run();
                                        hasPrint = false;
                                        printType = 0;
                                    }
                                    isPaymentChosen = false;
                                }*/
                                //}
                            }
                        } else if (printType == 2) {
                            hasPrint = true;
                            if (!isPaymentChosen) {
                                choosePaymentType();
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            simpan();
                                            isSavingTrans = true;
                                            invalidateOptionsMenu();
                                        } catch (final Exception ex) {
                                            Log.i("---", "Exception in thread 6");
                                        }
                                    }
                                });
                            /*isLoading = true;
                            n=0;
                            new Thread(handlerTask).start();*/
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendtoServer();
                                        //}
                                        new isOnline().execute("");
                                        if (internetAvailable) {
                                            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                                        } else {
                                            bar.setBackgroundDrawable(new ColorDrawable(Color.RED));
                                        }
                                        showItem();
                                    }
                                }, 3000);
                                //handlerTask.run();
                                hasPrint = false;
                                printType = 0;
                                isPaymentChosen = false;
                            }
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
        builder.setSingleChoiceItems(values, currentChoice[0], new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch(item)
                {
                    case 0:
                        printType = 0;
                        if(!isPaymentChosen){
                            choosePaymentType();
                        }
                        //Toast.makeText(getApplication(), values[item], Toast.LENGTH_LONG).show();
                        //alertDialog1.dismiss();
                        break;
                    case 1:
                        printType = 2;
                        if(!isPaymentChosen){
                            choosePaymentType();
                        }
                        break;
                    case 2:
                        printType = 1;
                        break;
                }
                //alertDialog.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.setCanceledOnTouchOutside(false);
        alertDialog1.show();
    }

    private void printReceipt(int status) {
        try {
            btoutputstream = btsocket
                    .getOutputStream();
            Calendar cal_ = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
            Date currentLocalTime_ = cal_.getTime();
            DateFormat dateCounter_ = new SimpleDateFormat("dd MMM yyyy HH:mm");

            dateCounter_.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
            String localTimeCounter = dateCounter_.format(currentLocalTime_);
            String client_name = session.sClientFullName();
            String user_name = session.sUserName();
            String outlet_name = session.sOutlet();

            resetPrint();
            byte[] printformat = {0x1B, 0x21, FONT_TYPE};
            btoutputstream.write(printformat);

            //if (btName.startsWith("RP58")) {
            if (session.nPrinterChars() == 32) {
                printTitle(outlet_name);
                printNewLine();
                printUnicodeforReset();
                printText1("Receipt : " + String.format("%05d", session.nOutlet_ID())+'.'+val);
                                    printText1("\n");
                                    printText1("Tanggal : " + localTimeCounter);
                                    printText1("\n");
                                    printText1("Kasir   : " + session.sFullName());
                                    printText1("\n");
                                    printUnicode3();
                                    printText1("\n");
                                    printText1("Produk      Harga    Sub Total");
                                    printText1("\n");
                                    printUnicode3();
                                    printText1("\n");
                                    int count = 0;
                                    //Cursor cRead = read.rawQuery("SELECT b.prod_name, b.prod_price_sell from m_transactions_temp a, m_products b WHERE a.prod_id=b.prod_id ORDER BY a.trans_temp_id ASC;", null);
                                    Cursor cRead = read.rawQuery("select a.trans_temp_id, count(a.qty) as qty_, count(a.qty) * b.prod_price_sell as totalprice, b.* from m_transactions_temp a, m_products b where a.prod_id=b.prod_id and a.cust_temp_id="+customer_id+" GROUP BY a.prod_id ORDER BY a.trans_temp_id ASC;", null);
                                    int rows = cRead.getCount();
                                    if (rows > 0) {
                                        cRead.moveToFirst();
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
                                            cRead.moveToNext();
                                        }
                                        cRead.close();

                                        printUnicode4();
                                        printText1("\n");
                                        printText1("Subtotal sblm pajak: ");
                                        printText1("Rp" + String.valueOf(df.format((session.nSubTotal()))));
                                        printText1("\n");

                                        printText1("Diskon             : ");
                                        printText1("Rp" + String.valueOf(df.format(session.nDiscTotal())));
                                        printText1("\n");

                                        printText1("Service charge     : ");
                                        printText1("Rp"+ String.valueOf(df.format(getServCharge())));
                                        printText1("\n");

                                        printText1("Pajak              : ");
                                        //printText1("Rp" + String.valueOf(df.format((session.nSubTotal() - session.nDiscTotal()) * 10 / 100)));
                                        printText1("Rp" + String.valueOf(df.format((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100)));
                                        printText1("\n");

                                        printText1("Total              : ");
                                        //printText1("Rp" + String.valueOf(df.format(session.nSubTotal() - session.nDiscTotal() + ((session.nSubTotal() - session.nDiscTotal()) * 10 / 100))));
                                        printText1("Rp" + String.valueOf(df.format((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge())));
                                        printText1("\n");

                                        if(paymentType == 1 && printType == 0) {
                                            printText1("Uang Bayar         : ");
                                            printText1("Rp"+ String.valueOf(df.format(totalBayar)));
                                            printText1("\n");

                                            printText1("Kembali            : ");

                                            if(totalBayar < ((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal()) * session.nTaxValue()/100))){
                                                printText1("Rp0");
                                            } else {
                                                //printText1("Rp" + String.valueOf(df.format(totalBayar-((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal()) * session.nTaxValue()/100)))));
                                                printText1("Rp" + String.valueOf(df.format(totalBayar-((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge()))));
                                            }
                                            /*if(totalBayar < (session.nSubTotal() + (session.nSubTotal() * session.nTaxValue() / 100))) {
                                                printText1("Rp0");
                                            } else {
                                                printText1("Rp" + String.valueOf(df.format(totalBayar - (session.nSubTotal() + (session.nSubTotal() * session.nTaxValue() / 100)))));
                                            }*/
                                            printText1("\n");
                                        }

                                        printUnicode4();
                                        printText1("\n");
                                        if(status == 0) {
                                            printText1(">> B I L L  S E M E N T A R A <<"); // total 32 char in a single line
                                            printText1("\n");
                                            printNewLine();
                                            printText1("\n");
                                            printText1("\n");
                                        } else {
                                            printText1("Terima kasih atas kunjungan Anda"); // total 42 char in a single line
                                            printText1("\n");

                                            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                            try {
                                                String receipt_no = String.format(Locale.getDefault(), "%05d", session.nOutlet_ID()) + '.' + val;
                                                BitMatrix bitMatrix = multiFormatWriter.encode(receipt_no, BarcodeFormat.QR_CODE, 200, 200);
                                                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                                                printPhoto(bitmap);
                                                printNewLine();
                                                printText1("\n");
                                                printText1("\n");

                                            } catch (WriterException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        /*if(session.sWiFi().equals("WiFi disconnected") || session.sWiFi().equals("WiFi disabled")) {

                                        } else {
                                            if(!wiboStatus.equals("200")){
                                                //Toast.makeText(getApplication(), wiboMsg, Toast.LENGTH_LONG).show();
                                                showMessage("Status Cetak WiFi",wiboMsg);
                                            } else {
                                                printUnicode4();
                                                printText("\n");
                                                printText1("Wi-Fi SSID : ");
                                                printText1(session.sWiFi());
                                                printText("\n");
                                                printText1("User       : ");
                                                //printText1(session.sWiFiUser());
                                                printText1(wiboUser);
                                                printText("\n");
                                                printText1("Password   : ");
                                                //printText1(session.sWiFiPasswd());
                                                printText1(wiboPass);
                                                printText("\n");
                                                printText1("---    ( Berlaku " + sVoucher2 + " )     ---");
                                                printText("\n");
                                                //showMessage("Login WiFi","User :"+wiboUser+"\nPass :"+wiboPass);
                                            }
                                        }*/

                                    }
                // removed handler4.removeCallbacks(handlerTask4);
            } else {
                printTitle(outlet_name);
                printNewLine();
                printUnicodeforReset();
                printText1("Receipt : " + String.format(Locale.getDefault(),"%05d", session.nOutlet_ID())+'.'+val);
                printText1("\n");
                printText1("Tanggal : " + localTimeCounter);
                printText1("\n");
                printText1("Kasir   : " + session.sFullName());
                printText1("\n");
                printUnicode();
                printText1("\n");
                printText1("Produk           Harga         Sub Total");
                printText1("\n");
                printUnicode();
                printText1("\n");
                int count = 0;
                //Cursor cRead = read.rawQuery("SELECT b.prod_name, b.prod_price_sell from m_transactions_temp a, m_products b WHERE a.prod_id=b.prod_id ORDER BY a.trans_temp_id ASC;", null);
                Cursor cRead = read.rawQuery("select a.trans_temp_id, count(a.qty) as qty_, count(a.qty) * b.prod_price_sell as totalprice, b.* from m_transactions_temp a, m_products b where a.prod_id=b.prod_id and a.cust_temp_id="+customer_id+" GROUP BY a.prod_id ORDER BY a.trans_temp_id ASC;", null);
                int rows = cRead.getCount();
                if (rows > 0) {
                    cRead.moveToFirst();
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
                        cRead.moveToNext();
                    }
                    cRead.close();

                    printUnicode1();
                    printText1("\n");
                    printText1("Subtotal sebelum pajak       : ");
                    printText1("Rp" + String.valueOf(df.format((session.nSubTotal()))));
                    printText1("\n");

                    printText1("Diskon                       : ");
                    printText1("Rp" + String.valueOf(df.format(session.nDiscTotal())));
                    printText1("\n");

                    printText1("Service charge               : ");
                    printText1("Rp"+ String.valueOf(df.format(getServCharge())));
                    printText1("\n");

                    printText1("Pajak                        : ");
                    printText1("Rp" + String.valueOf(df.format((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100)));
                    printText1("\n");

                    printText1("Total                        : ");
                    printText1("Rp" + String.valueOf(df.format((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge())));
                    printText1("\n");

                    if(paymentType == 1 && printType == 0) {
                        printText1("Uang Bayar                   : ");
                        printText1("Rp"+ String.valueOf(df.format(totalBayar)));
                        printText1("\n");

                        printText1("Kembali                      : ");
                        if(totalBayar < ((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal()) * session.nTaxValue()/100))){
                            printText1("Rp0");
                        } else {
                            printText1("Rp" + String.valueOf(df.format(totalBayar-((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge()))));
                        }
                        /*if(totalBayar < (session.nSubTotal() + (session.nSubTotal() * session.nTaxValue() / 100))) {
                            printText1("Rp0");
                        } else {
                            printText1("Rp" + String.valueOf(df.format(totalBayar - (session.nSubTotal() + (session.nSubTotal() * session.nTaxValue() / 100)))));
                        }*/
                        printText1("\n");
                    }

                    printUnicode1();
                    printText1("\n");

                    if(status == 0) {
                        printText1(">>      B I L L  S E M E N T A R A      <<"); // total 42 char in a single line
                        printText1("\n");
                        printNewLine();
                        printText1("\n");
                        printText1("\n");
                    } else {
                        printText1(">>>  Terima kasih atas kunjungan Anda  <<<"); // total 42 char in a single line
                        printText1("\n");

                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                        try {
                            String receipt_no = String.format(Locale.getDefault(), "%05d", session.nOutlet_ID()) + '.' + val;
                            BitMatrix bitMatrix = multiFormatWriter.encode(receipt_no, BarcodeFormat.QR_CODE, 200, 200);
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                            printPhoto(bitmap);
                            printNewLine();
                            printText1("\n");
                            printText1("\n");

                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                    }
                    /*if(session.sWiFi().equals("WiFi disconnected") || session.sWiFi().equals("WiFi disabled")) {

                    } else {
                        if(!wiboStatus.equals("200")) {
                            //Toast.makeText(getApplication(), wiboMsg, Toast.LENGTH_LONG).show();
                            showMessage("Status Cetak WiFi",wiboMsg);
                        } else {
                            printUnicode1();
                            printText("\n");
                            printText1("Wi-Fi SSID  : ");
                            printText1(session.sWiFi());
                            printText("\n");
                            printText1("User        : ");
                            //printText1(session.sWiFiUser());
                            printText1(wiboUser);
                            printText("\n");
                            printText1("Password    : ");
                            //printText1(session.sWiFiPasswd());
                            printText1(wiboPass);
                            printText("\n");
                            printText1("-----       ( Berlaku " + sVoucher2 + " )        -----");
                            printText("\n");
                            //showMessage("Login WiFi","User :"+wiboUser+"\nPass :"+wiboPass);
                        }
                    }*/
                }
                // removed handler4.removeCallbacks(handlerTask4);
            }
            printTimes++;
            Log.d("printtimes", "print ok-"+printTimes);
            if(session.getDoubleReceipt()) {
                if(printTimes > 1) {
                    afterPrinting();
                    printTimes = 0;
                    hideLoading();
                }
            } else {
                afterPrinting();
                printTimes = 0;
                hideLoading();
            }
        } catch (Exception e) {
            hideLoading();
            Log.e("Main", "Exe ", e);
            // removed handler4.removeCallbacks(handlerTask4);
        }
    }

    private void afterPrinting(){
        hasPrint = true;
        if(printType == 0) {
            if (hasPrint) {
                runOnUiThread(new Runnable(){
                    public void run() {
                        try {
                            simpan();
                            isSavingTrans = true;
                            invalidateOptionsMenu();
                        } catch (final Exception ex) {
                            Log.i("---","Exception in thread 6");
                        }
                    }
                });
                        /*isLoading = true;
                        n = 0;
                        new Thread(handlerTask).start();*/
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendtoServer();
                        //}
                        new isOnline().execute("");
                        if(internetAvailable){
                            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                        } else {
                            bar.setBackgroundDrawable(new ColorDrawable(Color.RED));
                        }
                        showItem();
                    }
                }, 5000);
                //handlerTask.run();
                hasPrint = false;
                printType = 0;
            }
            isPaymentChosen = false;
        }
        resetwibopass();
    }

    private void choosePaymentType () {
        paymentType = 1;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog));
        builder1.setTitle("Pilih Jenis Pembayaran");
        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(paymentType == 1){
                            kembalian();
                            isPaymentChosen = true;
                        } else if (paymentType == 2){
                            cardtype();
                            isPaymentChosen = true;
                            /*if(printType == 0){
                                getwibopass();
                                handler4.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        printReceipt();
                                    }
                                }, 5000);*/
                                //isLoading4=true;
                                //n4=0;
                                //new Thread(handlerTask4).start();
                                //handlerTask4.run();
                                //printReceipt();
                                /*if(hasPrint){
                                    simpan();
                                    isLoading = true;
                                    n=0;
                                    handlerTask.run();
                                    hasPrint = false;
                                    printType = 0;
                                }
                                isPaymentChosen = false;*/
                            //}
                        } else if (paymentType == 3) {
                            isPaymentChosen = true;
                            if(printType == 0){
                                getwibopass();
                                showLoading();
                                handler4.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        int copyPrintIs;
                                        if(session.getDoubleReceipt()){
                                            copyPrintIs = 2;
                                        } else {
                                            copyPrintIs = 1;
                                        }
                                        for (int x = 1; x <= copyPrintIs; x++) {
                                            printReceipt(1);
                                            SystemClock.sleep(4000);//This will pause every 4 seconds after printing once and the continue and pause again
                                        }
                                    }
                                }, 5000);
                                //isLoading4=true;
                                //n4=0;
                                //new Thread(handlerTask4).start();
                                //handlerTask4.run();
                                //printReceipt();
                                /*if(hasPrint){
                                    simpan();
                                    isLoading = true;
                                    n=0;
                                    handlerTask.run();
                                    hasPrint = false;
                                    printType = 0;
                                }
                                isPaymentChosen = false;*/
                            } else if(printType == 2){
                                hasPrint = true;
                                if (!isPaymentChosen) {
                                    choosePaymentType();
                                } else {
                                    alertDialog1.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            try {
                                                simpan();
                                                isSavingTrans = true;
                                                invalidateOptionsMenu();
                                            } catch (final Exception ex) {
                                                Log.i("---", "Exception in thread 6");
                                            }
                                        }
                                    });
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            sendtoServer();
                                            //}
                                            new isOnline().execute("");
                                            if (internetAvailable) {
                                                bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                                            } else {
                                                bar.setBackgroundDrawable(new ColorDrawable(Color.RED));
                                            }
                                            showItem();
                                        }
                                    }, 3000);
                                    //handlerTask.run();
                                    hasPrint = false;
                                    printType = 0;
                                    isPaymentChosen = false;
                                }
                            }
                        } else {
                            mpaymenttype();
                            isPaymentChosen = true;
                            /*if(printType == 0){
                                getwibopass();
                                handler4.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        printReceipt();
                                    }
                                }, 5000);
                                //isLoading4=true;
                                //n4=0;
                                //new Thread(handlerTask4).start();
                                //handlerTask4.run();
                                //printReceipt();
                                /-*if(hasPrint){
                                    simpan();
                                    isLoading = true;
                                    n=0;
                                    handlerTask.run();
                                    hasPrint = false;
                                    printType = 0;
                                }
                                isPaymentChosen = false;*-/
                            }*/
                        }
                    }
                }
        );
        builder1.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.cancel();
                        isPaymentChosen = false;
                        alertDialog2.dismiss();
                    }
                }

        );
        builder1.setSingleChoiceItems(values2, currentChoice2[0], new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch(item)
                {
                    case 0:
                        paymentType = 1;
                        //Toast.makeText(getApplication(), values2[item]+" Dipilih", Toast.LENGTH_SHORT).show();
                        //alertDialog2.dismiss();
                        break;
                    case 1:
                        paymentType = 2;
                        //Toast.makeText(getApplication(), values2[item]+" Dipilih", Toast.LENGTH_SHORT).show();
                        //alertDialog2.dismiss();
                        break;
                    case 2:
                        paymentType = 3;
                        //Toast.makeText(getApplication(), values2[item]+" Dipilih", Toast.LENGTH_SHORT).show();
                        //alertDialog2.dismiss();
                        break;
                    case 3:
                        paymentType = 4;
                        //Toast.makeText(getApplication(), values2[item]+" Dipilih", Toast.LENGTH_SHORT).show();
                        //alertDialog2.dismiss();
                        break;
                }
                //alertDialog.dismiss();
            }
        });
        alertDialog2 = builder1.create();
        alertDialog2.setCanceledOnTouchOutside(false);
        alertDialog2.show();
    }

    private void exitApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Logout");
        builder.setMessage("Anda yakin ingin keluar dari akun ini ?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        write.execSQL("DELETE FROM m_clients");
                        write.execSQL("DELETE FROM m_category");
                        write.execSQL("DELETE FROM m_products");
                        write.execSQL("DELETE FROM m_transactions");
                        write.execSQL("DELETE FROM m_transactions_detail");
                        write.execSQL("DELETE FROM m_transactions_temp");
                        write.execSQL("DELETE FROM m_customers_temp");
                        session.clearAllData();
                        File dir1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                File.separator + "kasandra" + File.separator + "category" + File.separator);
                        if (dir1.isDirectory()) {
                            String[] children = dir1.list();
                            for (int i = 0; i < children.length; i++) {
                                new File(dir1, children[i]).delete();
                            }
                        }
                        File dir2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                File.separator + "kasandra" + File.separator + "product" + File.separator);
                        if (dir2.isDirectory()) {
                            String[] children = dir2.list();
                            for (int i = 0; i < children.length; i++) {
                                new File(dir2, children[i]).delete();
                            }
                        }
                        signOut();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }

        );
        AlertDialog alertdialog = builder.create();
        alertdialog.setCanceledOnTouchOutside(false);
        alertdialog.show();
    }

    private void kembalian() {
        totalBayar = 0;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog));
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(totalBayar < (session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal()) * session.nTaxValue()/100)){
                            isPaymentChosen = false;
                            alertDialog1.dismiss();
                            Toast.makeText(getApplicationContext(), "Uang yang dibayarkan masih kurang", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            if (printType == 0) {
                                getwibopass();
                                showLoading();
                                handler4.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        int copyPrintIs;
                                        if(session.getDoubleReceipt()){
                                            copyPrintIs = 2;
                                        } else {
                                            copyPrintIs = 1;
                                        }
                                        for (int x = 1; x <= copyPrintIs; x++) {
                                            printReceipt(1);
                                            SystemClock.sleep(4000);//This will pause every 4 seconds after printing once and the continue and pause again
                                        }
                                    }
                                }, 5000);
                                //isLoading4=true;
                                //n4 = 0;
                                //new Thread(handlerTask4).start();
                                //handlerTask4.run();
                                //printReceipt();
                            /*if(hasPrint){
                                simpan();
                                isLoading = true;
                                n=0;
                                handlerTask.run();
                                hasPrint = false;
                                printType = 0;
                            }
                            isPaymentChosen = false;*/
                            } else if(printType == 2){
                                hasPrint = true;
                                if (!isPaymentChosen) {
                                    choosePaymentType();
                                } else {
                                    alertDialog1.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            try {
                                                simpan();
                                                isSavingTrans = true;
                                                invalidateOptionsMenu();
                                            } catch (final Exception ex) {
                                                Log.i("---", "Exception in thread 6");
                                            }
                                        }
                                    });
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            sendtoServer();
                                            //}
                                            new isOnline().execute("");
                                            if (internetAvailable) {
                                                bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                                            } else {
                                                bar.setBackgroundDrawable(new ColorDrawable(Color.RED));
                                            }
                                            showItem();
                                        }
                                    }, 3000);
                                    //handlerTask.run();
                                    hasPrint = false;
                                    printType = 0;
                                    isPaymentChosen = false;
                                }
                            }
                        }
                        //dialog.cancel();
                    }
                }
        );
        dialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.cancel();
                        //isPaymentChosen = false;
                        alertDialog3.dismiss();
                        isPaymentChosen = false;
                        alertDialog2.dismiss();
                    }
                }

        );

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_money, null);
        dialogBuilder.setView(dialogView);

        GridLayout layout2 = (GridLayout) dialogView.findViewById(R.id.grid_layout2);
        ImageView imgView1 = (ImageView) layout2.findViewById(R.id.iv1);
        ImageView imgView2 = (ImageView) layout2.findViewById(R.id.iv2);
        ImageView imgView3 = (ImageView) layout2.findViewById(R.id.iv3);
        ImageView imgView4 = (ImageView) layout2.findViewById(R.id.iv4);
        ImageView imgView5 = (ImageView) layout2.findViewById(R.id.iv5);
        ImageView imgView6 = (ImageView) layout2.findViewById(R.id.iv6);
        ImageView imgView7 = (ImageView) layout2.findViewById(R.id.iv7);
        ImageView imgView8 = (ImageView) layout2.findViewById(R.id.iv8);
        final TextView txt1 = (TextView) dialogView.findViewById(R.id.bayar2);
        final TextView txt2 = (TextView) dialogView.findViewById(R.id.total2);
        final TextView txt3 = (TextView) dialogView.findViewById(R.id.kembali2);
        final TextView txt4 = (TextView) dialogView.findViewById(R.id.kembali1);
        txt2.setText(": Rp "+ String.valueOf(df.format((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge())));
        //txt2.setText(": Rp "+ String.valueOf(df.format((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal()) * session.nTaxValue()/100))));
        imgView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalBayar += 1000;
                txt1.setText(": Rp "+ String.valueOf(df.format(totalBayar)));
                if(totalBayar < ((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal()) * session.nTaxValue()/100))){
                    txt4.setTextSize(15);
                    txt4.setText("Sisa yang harus dibayar");
                    txt4.setTextColor(Color.RED);
                    //txt3.setText(": Rp "+ String.valueOf(df.format(((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal()) * session.nTaxValue()/100))-totalBayar)));
                    txt3.setText(": Rp "+ String.valueOf(df.format(((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge())-totalBayar)));
                } else {
                    txt4.setText("Kembali");
                    //txt3.setText(": Rp "+ String.valueOf(df.format(totalBayar-((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal()) * session.nTaxValue()/100)))));
                    txt3.setText(": Rp "+ String.valueOf(df.format(totalBayar-((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge()))));
                    txt4.setTextColor(getResources().getColor(R.color.darkgreen));
                }
            }
        });
        imgView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalBayar += 2000;
                txt1.setText(": Rp "+ String.valueOf(df.format(totalBayar)));
                if(totalBayar < ((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal()) * session.nTaxValue()/100))){
                    txt4.setTextSize(15);
                    txt4.setText("Sisa yang harus dibayar");
                    txt4.setTextColor(Color.RED);
                    txt3.setText(": Rp "+ String.valueOf(df.format(((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge())-totalBayar)));
                } else {
                    txt4.setText("Kembali");
                    txt3.setText(": Rp "+ String.valueOf(df.format(totalBayar-((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge()))));
                    txt4.setTextColor(getResources().getColor(R.color.darkgreen));
                }
            }
        });
        imgView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalBayar += 5000;
                txt1.setText(": Rp "+ String.valueOf(df.format(totalBayar)));
                if(totalBayar < ((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal()) * session.nTaxValue()/100))){
                    txt4.setTextSize(15);
                    txt4.setText("Sisa yang harus dibayar");
                    txt4.setTextColor(Color.RED);
                    txt3.setText(": Rp "+ String.valueOf(df.format(((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge())-totalBayar)));
                } else {
                    txt4.setText("Kembali");
                    txt3.setText(": Rp "+ String.valueOf(df.format(totalBayar-((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge()))));
                    txt4.setTextColor(getResources().getColor(R.color.darkgreen));
                }
            }
        });
        imgView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalBayar += 10000;
                txt1.setText(": Rp "+ String.valueOf(df.format(totalBayar)));
                if(totalBayar < ((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal()) * session.nTaxValue()/100))){
                    txt4.setTextSize(15);
                    txt4.setText("Sisa yang harus dibayar");
                    txt4.setTextColor(Color.RED);
                    txt3.setText(": Rp "+ String.valueOf(df.format(((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge())-totalBayar)));
                } else {
                    txt4.setText("Kembali");
                    txt3.setText(": Rp "+ String.valueOf(df.format(totalBayar-((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge()))));
                    txt4.setTextColor(getResources().getColor(R.color.darkgreen));
                }
            }
        });
        imgView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalBayar += 20000;
                txt1.setText(": Rp "+ String.valueOf(df.format(totalBayar)));
                if(totalBayar < ((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal()) * session.nTaxValue()/100))){
                    txt4.setTextSize(15);
                    txt4.setText("Sisa yang harus dibayar");
                    txt4.setTextColor(Color.RED);
                    txt3.setText(": Rp "+ String.valueOf(df.format(((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge())-totalBayar)));
                } else {
                    txt4.setText("Kembali");
                    txt3.setText(": Rp "+ String.valueOf(df.format(totalBayar-((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge()))));
                    txt4.setTextColor(getResources().getColor(R.color.darkgreen));
                }
            }
        });
        imgView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalBayar += 50000;
                txt1.setText(": Rp "+ String.valueOf(df.format(totalBayar)));
                if(totalBayar < ((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal()) * session.nTaxValue()/100))){
                    txt4.setTextSize(15);
                    txt4.setText("Sisa yang harus dibayar");
                    txt4.setTextColor(Color.RED);
                    txt3.setText(": Rp "+ String.valueOf(df.format(((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge())-totalBayar)));
                } else {
                    txt4.setText("Kembali");
                    txt3.setText(": Rp "+ String.valueOf(df.format(totalBayar-((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge()))));
                    txt4.setTextColor(getResources().getColor(R.color.darkgreen));
                }
            }
        });
        imgView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalBayar += 100000;
                txt1.setText(": Rp "+ String.valueOf(df.format(totalBayar)));
                if(totalBayar < ((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal()) * session.nTaxValue()/100))){
                    txt4.setTextSize(15);
                    txt4.setText("Sisa yang harus dibayar");
                    txt4.setTextColor(Color.RED);
                    txt3.setText(": Rp "+ String.valueOf(df.format(((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge())-totalBayar)));
                } else {
                    txt4.setText("Kembali");
                    txt3.setText(": Rp "+ String.valueOf(df.format(totalBayar-((session.nSubTotal() - session.nDiscTotal()) + ((session.nSubTotal() - session.nDiscTotal() + getServCharge()) * session.nTaxValue()/100) + getServCharge()))));
                    txt4.setTextColor(getResources().getColor(R.color.darkgreen));
                }
            }
        });
        imgView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalBayar = 0;
                txt1.setText(": Rp "+ String.valueOf(df.format(totalBayar)));
                txt4.setText("Kembali");
                txt4.setTextColor(Color.BLACK);
                txt3.setText(": Rp "+ String.valueOf(df.format(totalBayar)));
            }
        });
        alertDialog3 = dialogBuilder.create();
        alertDialog3.setCanceledOnTouchOutside(false);
        alertDialog3.show();
    }

    private void cardtype() {
        paymentType = parseInt(paymentType+"1");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog));
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            if (printType == 0) {
                                getwibopass();
                                showLoading();
                                handler4.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        int copyPrintIs;
                                        if(session.getDoubleReceipt()){
                                            copyPrintIs = 2;
                                        } else {
                                            copyPrintIs = 1;
                                        }
                                        for (int x = 1; x <= copyPrintIs; x++) {
                                            printReceipt(1);
                                            SystemClock.sleep(4000);//This will pause every 4 seconds after printing once and the continue and pause again
                                        }
                                    }
                                }, 5000);
                            } else if(printType == 2) {
                                hasPrint = true;
                                if (!isPaymentChosen) {
                                    choosePaymentType();
                                } else {
                                    alertDialog1.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            try {
                                                simpan();
                                                isSavingTrans = true;
                                                invalidateOptionsMenu();
                                            } catch (final Exception ex) {
                                                Log.i("---", "Exception in thread 6");
                                            }
                                        }
                                    });
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            sendtoServer();
                                            //}
                                            new isOnline().execute("");
                                            if (internetAvailable) {
                                                bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                                            } else {
                                                bar.setBackgroundDrawable(new ColorDrawable(Color.RED));
                                            }
                                            showItem();
                                        }
                                    }, 3000);
                                    //handlerTask.run();
                                    hasPrint = false;
                                    printType = 0;
                                    isPaymentChosen = false;
                                }
                            }
                    }
                }
        );
        dialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //isPaymentChosen = false;
                        alertDialog4.dismiss();
                        isPaymentChosen = false;
                        alertDialog2.dismiss();
                    }
                }

        );

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_creditcard, null);
        dialogBuilder.setView(dialogView);

        GridLayout layout3 = (GridLayout) dialogView.findViewById(R.id.grid_layout3);
        ImageView imgView1 = (ImageView) layout3.findViewById(R.id.iv1);
        ImageView imgView2 = (ImageView) layout3.findViewById(R.id.iv2);
        ImageView imgView3 = (ImageView) layout3.findViewById(R.id.iv3);
        final TextView txt3 = (TextView) dialogView.findViewById(R.id.cc2);
        final TextView txt4 = (TextView) dialogView.findViewById(R.id.cc1);
        imgView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentType = 2;
                paymentType = parseInt(paymentType+"1");
                txt3.setText(": VISA");
            }
        });
        imgView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentType = 2;
                paymentType = parseInt(paymentType+"2");
                txt3.setText(": MasterCard");
            }
        });
        imgView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentType = 2;
                paymentType = parseInt(paymentType+"3");
                txt3.setText(": Lainnya");
            }
        });
        alertDialog4 = dialogBuilder.create();
        alertDialog4.setCanceledOnTouchOutside(false);
        alertDialog4.show();
    }

    private void mpaymenttype() {
        paymentType = parseInt(paymentType+"1");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog));
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (printType == 0) {
                            getwibopass();
                            showLoading();
                            handler4.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    int copyPrintIs;
                                    if(session.getDoubleReceipt()){
                                        copyPrintIs = 2;
                                    } else {
                                        copyPrintIs = 1;
                                    }
                                    for (int x = 1; x <= copyPrintIs; x++) {
                                        printReceipt(1);
                                        SystemClock.sleep(4000);//This will pause every 4 seconds after printing once and the continue and pause again
                                    }
                                }
                            }, 5000);
                        } else if(printType == 2) {
                            hasPrint = true;
                            if (!isPaymentChosen) {
                                choosePaymentType();
                            } else {
                                alertDialog1.dismiss();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            simpan();
                                            isSavingTrans = true;
                                            invalidateOptionsMenu();
                                        } catch (final Exception ex) {
                                            Log.i("---", "Exception in thread 6");
                                        }
                                    }
                                });
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendtoServer();
                                        //}
                                        new isOnline().execute("");
                                        if (internetAvailable) {
                                            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                                        } else {
                                            bar.setBackgroundDrawable(new ColorDrawable(Color.RED));
                                        }
                                        showItem();
                                    }
                                }, 3000);
                                //handlerTask.run();
                                hasPrint = false;
                                printType = 0;
                                isPaymentChosen = false;
                            }
                        }
                    }
                }
        );
        dialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //isPaymentChosen = false;
                        alertDialog5.dismiss();
                        isPaymentChosen = false;
                        alertDialog2.dismiss();
                    }
                }

        );

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_mpayment, null);
        dialogBuilder.setView(dialogView);

        GridLayout layout3 = (GridLayout) dialogView.findViewById(R.id.grid_layout3);
        ImageView imgView1 = (ImageView) layout3.findViewById(R.id.iv1);
        ImageView imgView2 = (ImageView) layout3.findViewById(R.id.iv2);
        ImageView imgView3 = (ImageView) layout3.findViewById(R.id.iv3);
        final TextView txt3 = (TextView) dialogView.findViewById(R.id.cc2);
        final TextView txt4 = (TextView) dialogView.findViewById(R.id.cc1);
        imgView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentType = 4;
                paymentType = parseInt(paymentType+"1");
                txt3.setText(": Go-Pay");
            }
        });
        imgView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentType = 4;
                paymentType = parseInt(paymentType+"2");
                txt3.setText(": OVO");
            }
        });
        imgView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentType = 4;
                paymentType = parseInt(paymentType+"3");
                txt3.setText(": LinkAja");
            }
        });
        alertDialog5 = dialogBuilder.create();
        alertDialog5.setCanceledOnTouchOutside(false);
        alertDialog5.show();
    }

    private void resetwibopass() {
        wiboStatus = "";
        wiboMsg = "";
        wiboUser = "";
        wiboPass = "";
    }

    private void getwibopass() {
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get("https://kasandra.biz/wbagenpass.php?token=" + session.sToken() + "&profile=" + session.sVoucher());
        //Cache.Entry entry2 = cache2.get("https://kasandra.biz/wbagenpass.json");

        if (entry2 != null) {
            try {
                String data = new String(entry2.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            JsonObjectRequest jsonReq2 = new JsonObjectRequest(Request.Method.GET,
                    "https://kasandra.biz/wbagenpass.php?token=" + session.sToken() + "&profile=" + session.sVoucher(), null, new Response.Listener<JSONObject>() {
                    //"https://kasandra.biz/wbagenpass.json", null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    //VolleyLog.d("kasandra debug", "Response: " + response.toString());
                    parseJsonFeed(response);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("kasandra debug", "Error getwibopass: " + error.getMessage());
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

    private void parseJsonFeed(JSONObject response) {
        try {

            String status = response.getString("status");
            String message = response.getString("message");
            wiboStatus = status;
            wiboMsg = message;
            //Toast.makeText(getApplicationContext(), status+" - "+message, Toast.LENGTH_SHORT).show();
            String username = response.getString("username");
            String password = response.getString("password");
            wiboUser = username;
            wiboPass = password;
            //Toast.makeText(getApplicationContext(), "Username/Pass : "+username+" - "+password, Toast.LENGTH_LONG).show();

            //isLoading4 = true;
        } catch (JSONException e) {
            e.printStackTrace();
            //isLoading4 = true;
        }
    }

    private void gettax() {
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(sURL+"?tax=" + session.nOutlet_ID());

        if (entry2 != null) {
            try {
                String data = new String(entry2.data, "UTF-8");
                try {
                    parseJsonTax(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            JsonObjectRequest jsonReq2 = new JsonObjectRequest(Request.Method.GET,
                    sURL+"?tax=" + session.nOutlet_ID(), null, new Response.Listener<JSONObject>() {
                //"https://kasandra.biz/wbagenpass.json", null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    parseJsonTax(response);
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

    private void parseJsonTax(JSONObject response) {
        try {
            String returnmessage = response.getString("msg");
            JSONArray params = response.getJSONArray("details");
            for (int i = 0; i < params.length(); i++) {
                JSONObject obj = params.getJSONObject(i);
                final int id = parseInt(obj.getString("outlet_id"));
                final int tax = parseInt(obj.getString("outlet_tax"));

                nTax = tax;
                session.setTaxValue(nTax);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getsc() {
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(sURL+"?serv_charge=" + session.nClient_ID());

        if (entry2 != null) {
            try {
                String data = new String(entry2.data, "UTF-8");
                try {
                    parseJsonSC(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            JsonObjectRequest jsonReq2 = new JsonObjectRequest(Request.Method.GET,
                    sURL+"?serv_charge=" + session.nClient_ID(), null, new Response.Listener<JSONObject>() {
                //"https://kasandra.biz/wbagenpass.json", null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    parseJsonSC(response);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("kasandra debug", "Error getsc: " + error.getMessage());
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

    private void parseJsonSC(JSONObject response) {
        try {
            String returnmessage = response.getString("msg");
            JSONArray params = response.getJSONArray("details");
            for (int i = 0; i < params.length(); i++) {
                JSONObject obj = params.getJSONObject(i);
                final int id = parseInt(obj.getString("client_id"));
                final int sc = parseInt(obj.getString("client_sc"));

                nSC = sc;
                session.setSCValue(nSC);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void simpan() {
        float nilaiPajak = session.nTaxValue() / 100.0f;
        float nilaiServCharge = session.nSCValue() / 100.0f;
        int trans_id;
        Cursor cRead = read.rawQuery("SELECT prod_id, trans_temp_id from m_transactions_temp where cust_temp_id="+customer_id+" ORDER BY trans_temp_id ASC;", null);
        int rows = cRead.getCount();
        Cursor cRead1 = read.rawQuery("SELECT SUM(a.qty) AS total_qty, SUM(b.prod_price_sell) AS total_price, SUM(a.discount) as disctotal from m_transactions_temp a, m_products b WHERE a.prod_id=b.prod_id and a.cust_temp_id="+customer_id+";", null);
        //Toast.makeText(getApplicationContext(), "m_transactions_temp"+rows, Toast.LENGTH_LONG).show();
        if(rows > 0) {
            cRead1.moveToFirst();

            Cursor cTransMax = read.rawQuery("SELECT MAX(trans_no) FROM m_transactions;", null);
            int trans_no;
            if(cTransMax.getCount() > 0){
                cTransMax.moveToFirst();
                trans_no = cTransMax.getInt(0)+1;
            } else {
                trans_no = 0;
            }
            cTransMax.close();
            write.execSQL("INSERT INTO m_transactions (total_qty, total_price, datetime, upload_status, payment_type, trans_discount, trans_tax, trans_no, trans_serv_charge) VALUES(" + cRead1.getInt(0) + ", " + cRead1.getInt(1) + ", datetime('now', 'localtime'), 0," + paymentType + "," + cRead1.getInt(2) + "," + ((cRead1.getInt(1) - cRead1.getInt(2)) + ((cRead1.getInt(1) - cRead1.getInt(2)) * nilaiServCharge)) * nilaiPajak + "," + trans_no + "," + (cRead1.getInt(1) - cRead1.getInt(2)) * nilaiServCharge + ");");
            Cursor cRead2 = read.rawQuery("SELECT MAX(trans_id) FROM m_transactions;", null);
            cRead2.moveToFirst();
            trans_id = cRead2.getInt(0);
            cRead.moveToFirst();
            for (int i = 0; i < rows; i++) {
                write.execSQL("INSERT INTO m_transactions_detail (prod_id, trans_id, upload_status) VALUES(" + cRead.getInt(0) + ", " + trans_id + ", 0);");
                write.execSQL("DELETE FROM m_transactions_temp WHERE trans_temp_id="+cRead.getInt(1)+" and cust_temp_id="+customer_id+";");
                //Toast.makeText(getApplicationContext(),"DELETE FROM m_transactions_temp WHERE trans_temp_id="+cRead.getInt(1)+";", Toast.LENGTH_LONG).show();
                cRead.moveToNext();
            }
            cRead2.close();
            Snackbar.make(getWindow().getDecorView().getRootView(), "Transaksi berhasil disimpan", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            //write.execSQL("DELETE FROM m_transactions_temp;");

            /*Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();*/
            if(customer_id == 1){
                changeCust(customer_id);
            } else {
                deleteCust(customer_id);
            }
            hasSaved = true;
            session.setSavedTrue(hasSaved, customer_id);
        } else {
            Snackbar.make(getWindow().getDecorView().getRootView(), "Belum ada transaksi yang dipilih", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        cRead.close();
        cRead1.close();
    }

    private void simpan_bill() {
        float nilaiPajak = session.nTaxValue() / 100.0f;
        float nilaiServCharge = session.nSCValue() / 100.0f;
        int trans_bill_idval;
        Cursor cRead = read.rawQuery("SELECT prod_id, trans_temp_id from m_transactions_temp where cust_temp_id="+customer_id+" ORDER BY trans_temp_id ASC;", null);
        int rows = cRead.getCount();
        Cursor cRead1 = read.rawQuery("SELECT SUM(a.qty) AS total_qty, SUM(b.prod_price_sell) AS total_price, SUM(a.discount) as disctotal from m_transactions_temp a, m_products b WHERE a.prod_id=b.prod_id and a.cust_temp_id="+customer_id+";", null);
        //Toast.makeText(getApplicationContext(), "m_transactions_temp"+rows, Toast.LENGTH_LONG).show();
        if(rows > 0) {
            cRead1.moveToFirst();

            Cursor cTransMax = read.rawQuery("SELECT MAX(trans_no) FROM m_transactions_bill;", null);
            int trans_no;
            if(cTransMax.getCount() > 0){
                cTransMax.moveToFirst();
                trans_no = cTransMax.getInt(0)+1;
            } else {
                trans_no = 0;
            }
            cTransMax.close();
            write.execSQL("INSERT INTO m_transactions_bill (total_qty, total_price, datetime, upload_status, payment_type, trans_discount, trans_tax, trans_no, trans_serv_charge) VALUES(" + cRead1.getInt(0) + ", " + cRead1.getInt(1) + ", datetime('now', 'localtime'), 0," + paymentType + "," + cRead1.getInt(2) + "," + ((cRead1.getInt(1) - cRead1.getInt(2)) + ((cRead1.getInt(1) - cRead1.getInt(2)) * nilaiServCharge)) * nilaiPajak + "," + customer_id + "," + (cRead1.getInt(1) - cRead1.getInt(2)) * nilaiServCharge + ");");
            Cursor cRead2 = read.rawQuery("SELECT MAX(trans_bill_id) FROM m_transactions_bill;", null);
            cRead2.moveToFirst();
            trans_bill_idval = cRead2.getInt(0);
            cRead.moveToFirst();
            for (int i = 0; i < rows; i++) {
                write.execSQL("INSERT INTO m_transactions_bill_detail (prod_id, trans_bill_id, upload_status) VALUES(" + cRead.getInt(0) + ", " + trans_bill_idval + ", 0);");
                //write.execSQL("DELETE FROM m_transactions_temp WHERE trans_temp_id="+cRead.getInt(1)+" and cust_temp_id="+customer_id+";");
                //Toast.makeText(getApplicationContext(),"DELETE FROM m_transactions_temp WHERE trans_temp_id="+cRead.getInt(1)+";", Toast.LENGTH_LONG).show();
                cRead.moveToNext();
            }
            cRead2.close();
            //Snackbar.make(getWindow().getDecorView().getRootView(), "Transaksi berhasil disimpan", Snackbar.LENGTH_LONG)
            //       .setAction("Action", null).show();
            //write.execSQL("DELETE FROM m_transactions_temp;");

            /*Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();*/
            /*hasSaved = true;
            session.setSavedTrue(hasSaved);
            if(customer_id == 1){
                changeCust(customer_id);
            } else {
                deleteCust(customer_id);
            }*/
        } else {
            Snackbar.make(getWindow().getDecorView().getRootView(), "Belum ada transaksi yang dipilih", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        cRead.close();
        cRead1.close();
    }

    /*protected void connect() {
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = btsocket
                            .getOutputStream();
                    String BILL = "";

                    BILL = "\nInvoice No: ABCDEF28060000005" + "    "
                            + "04-08-2011\n" + "ALMAS MARIS\n";
                    BILL = BILL
                            + "------------------------------";
                    BILL = BILL + "\n\n";
                    BILL = BILL + "Total Qty:  " + "     " + "2.0\n";
                    BILL = BILL + "Total Value:" + "     "
                            + "17625.0\n";
                    BILL = BILL
                            + "------------------------------\n\n";
                    os.write(BILL.getBytes());
                    //This is printer specific code you can comment ==== > Start

                    // Setting height
                    int gs = 29;
                    os.write(intToByteArray(gs));
                    int h = 104;
                    os.write(intToByteArray(h));
                    int n = 162;
                    os.write(intToByteArray(n));

                    // Setting Width
                    int gs_width = 29;
                    os.write(intToByteArray(gs_width));
                    int w = 119;
                    os.write(intToByteArray(w));
                    int n_width = 2;
                    os.write(intToByteArray(n_width));

                    // Print BarCode
                    int gs1 = 29;
                    os.write(intToByteArray(gs1));
                    int k = 107;
                    os.write(intToByteArray(k));
                    int m = 73;
                    os.write(intToByteArray(m));

                    String barCodeVal = "ASDFC028060000005";// "HELLO12345678912345012";
                    System.out.println("Barcode Length : "
                            + barCodeVal.length());
                    int n1 = barCodeVal.length();
                    os.write(intToByteArray(n1));

                    for (int i = 0; i < barCodeVal.length(); i++) {
                        os.write((barCodeVal.charAt(i) + "").getBytes());
                    }
                    //printer specific code you can comment ==== > End
                } catch (Exception e) {
                    Log.e("Main", "Exe ", e);
                }
            }
        };
        t.start();
    }*/

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
    public void printPhoto(Bitmap bmp) {
        try {
            //Bitmap bmp = BitmapFactory.decodeResource(getResources(),
             //       R.drawable.idlogo);
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
            //btoutputstream.write(PrinterCommands.ESC_FONT_COLOR_DEFAULT);
            btoutputstream.write(PrinterCommands.FS_FONT_ALIGN);
            btoutputstream.write(PrinterCommands.ESC_ALIGN_LEFT);
            btoutputstream.write(PrinterCommands.ESC_CANCEL_BOLD);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }
    //print text
    public static void printTextStr(String msg) {
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

    private void sendtoServer(){
        //Toast.makeText(getApplicationContext(), "SendtoServer", Toast.LENGTH_LONG).show();
        // removed handler.removeCallbacks(handlerTask);
        Cursor cRead3 = read.rawQuery("SELECT * from m_transactions WHERE upload_status=0 ORDER BY trans_id DESC;", null);
        int rows1 = cRead3.getCount();
        //Toast.makeText(getApplicationContext(),"m_transactions="+rows1, Toast.LENGTH_LONG).show();
        cRead3.moveToFirst();
        for (int j = 0; j < rows1; j++) {
                        /*try {
                            if (!isConnected()) {
                                Toast.makeText(getApplicationContext(), R.string.no_internet1 + " dan silakan coba kembali", Toast.LENGTH_LONG).show();
                            } else {*/
            new saveTransaction().execute(cRead3.getString(0) + "!" + cRead3.getString(1) + "!" + cRead3.getString(2) + "!" + cRead3.getString(3) + "!" + cRead3.getString(4) + "!" + cRead3.getString(5) + "!" + cRead3.getString(7));
            //Log.d("posdebug",cRead3.getString(0) + "!" + cRead3.getString(1) + "!" + cRead3.getString(2) + "!" + cRead3.getString(3));
                            /*}
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.d("kasandra", "kasandra debug problem saving data:" + e.getMessage());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("kasandra", "kasandra debug problem saving data:" + e.getMessage());
                        }*/
            //Toast.makeText(getApplicationContext(), cRead3.getString(0) + "!" + cRead3.getString(1) + "!" + cRead3.getString(2) + "!" + cRead3.getString(3), Toast.LENGTH_LONG).show();
            cRead3.moveToNext();
        }
        cRead3.close();
        // save transaction_bill_temp //
        Cursor cRead3_bill = read.rawQuery("SELECT * from m_transactions_bill WHERE upload_status=0 ORDER BY trans_bill_id DESC;", null);
        int rows1_bill = cRead3_bill.getCount();
        cRead3_bill.moveToFirst();
        for (int j_bill = 0; j_bill < rows1_bill; j_bill++) {
            new saveTransactionBill().execute(cRead3_bill.getString(0) + "!" + cRead3_bill.getString(1) + "!" + cRead3_bill.getString(2) + "!" + cRead3_bill.getString(3) + "!" + cRead3_bill.getString(4) + "!" + cRead3_bill.getString(5) + "!" + cRead3_bill.getString(7));
            cRead3_bill.moveToNext();
        }
        cRead3_bill.close();
        // end //
        Cursor cRead4 = read.rawQuery("SELECT a.*, b.datetime from m_transactions_detail a, m_transactions b WHERE a.trans_id=b.trans_id AND a.upload_status=0 ORDER BY a.trans_id DESC;", null);
        int rows2 = cRead4.getCount();
        //Toast.makeText(getApplicationContext(),"m_transactions_detail="+rows2, Toast.LENGTH_LONG).show();
        cRead4.moveToFirst();
        for (int l = 0; l < rows2; l++) {
                        /*try {
                            if (!isConnected()) {
                                Toast.makeText(getApplicationContext(), R.string.no_internet1 + " dan silakan coba kembali", Toast.LENGTH_LONG).show();
                            } else {*/
                                new saveTransactionDetail().execute(cRead4.getString(0) + "!" + cRead4.getString(1) + "!" + cRead4.getString(2) + "!" + cRead4.getString(3) + "!" + cRead4.getString(4));
                                //Log.d("posdebug2",cRead4.getString(0) + "!" + cRead4.getString(1) + "!" + cRead4.getString(2));
                            /*}
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.d("kasandra", "kasandra debug problem saving data:" + e.getMessage());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("kasandra", "kasandra debug problem saving data:" + e.getMessage());
                        }*/
            //Toast.makeText(getApplicationContext(), cRead4.getString(0) + "!" + cRead4.getString(1) + "!" + cRead4.getString(2), Toast.LENGTH_LONG).show();
            cRead4.moveToNext();
        }
        cRead4.close();
        // save transaction_bill_temp_detail //
        Cursor cRead4_bill = read.rawQuery("SELECT a.*, b.datetime from m_transactions_bill_detail a, m_transactions_bill b WHERE a.trans_bill_id=b.trans_bill_id AND a.upload_status=0 ORDER BY a.trans_bill_id DESC;", null);
        int rows2_bill = cRead4_bill.getCount();
        cRead4_bill.moveToFirst();
        for (int l_bill = 0; l_bill < rows2_bill; l_bill++) {
            new saveTransactionBillDetail().execute(cRead4_bill.getString(0) + "!" + cRead4_bill.getString(1) + "!" + cRead4_bill.getString(2) + "!" + cRead4_bill.getString(3) + "!" + cRead4_bill.getString(4));
            cRead4_bill.moveToNext();
        }
        cRead4_bill.close();
        // end //
        isSavingTrans = false;
        invalidateOptionsMenu();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

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

    /*final Runnable handlerTask = new Runnable() {

        @Override
        public void run() {
                //Thread.sleep(1000);// sleeps 1 second
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                n++;
                //Toast.makeText(MainActivity.this, "n ="+n, Toast.LENGTH_SHORT).show();
                if (n >= 5) {
                    if (isLoading) {
                        sendtoServer();
                        //Toast.makeText(MainActivity.this, "Jumlah n ="+n, Toast.LENGTH_LONG).show();
                        //if(pDialog != null || pDialog.isShowing()) {
                        //    pDialog.dismiss();
                        //handler.removeCallbacks(handlerTask);
                        isLoading = false;
                        n = 0;
                        showItem();
                        //}
                        bar.invalidateOptionsMenu();
                        try {
                            if (!isConnected()) {
                                bar.setBackgroundDrawable(new ColorDrawable(Color.RED));
                            } else {
                                bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        /--*if(!isInternetOn()) {
                            bar.setBackgroundDrawable(new ColorDrawable(Color.RED));
                        } else {
                            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                        }*--/
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
            n2++;
            if (n2 >= 5) {
                if (isLoading2) {
                    findp();
                    // removed handler2.removeCallbacks(handlerTask2);
                    isLoading2 = false;
                    n2 = 0;
                    Thread.currentThread().interrupt();
                }
            }
            handler2.postDelayed(handlerTask2, 1000);
        }
    };
    final Runnable handlerTask3 = new Runnable() {

        @Override
        public void run() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            n3++;
            if (n3 >= 30) { // upload data every 30 seconds
                //Toast.makeText(MainActivity.this, "Tidak dapat menampilkan data, silakan muat ulang halaman ini", Toast.LENGTH_LONG).show();
                if(pDialog != null || pDialog.isShowing()) {
                    pDialog.dismiss();
                    n3 = 0;
                    // removed handler3.removeCallbacks(handlerTask3);
                    Thread.currentThread().interrupt();
                }
            }
            handler3.postDelayed(handlerTask3, 1000);
        }
    };
    final Runnable handlerTask4 = new Runnable() {

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
            if (n4 >= 10) {
                if (isLoading4) {
                    printReceipt();
                    //Log.d("debug2","done="+n4);
                    // removed handler4.removeCallbacks(handlerTask4);
                    isLoading4 = false;
                    n4 = 0;
                    Thread.currentThread().interrupt();
                }
            }
            handler4.postDelayed(handlerTask4, 1000);
        }
    };*/
    @Override
    public void onResume() {
        //Toast.makeText(getApplicationContext(), "CALL ONRESUME", Toast.LENGTH_LONG).show();
        super.onResume();
        //new populateList().execute();

        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
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
                new populateCatList().execute();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findp();
                        Toast.makeText(getApplicationContext(), "Call Find-P(3)", Toast.LENGTH_LONG).show();
                    }
                }, 5000);
                //isLoading2 = true;
                //n2=0;
                //new Thread(handlerTask2).start();
                //handlerTask2.run();
            }
        } else {
            new populateCatList().execute();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    findp();
                    Toast.makeText(getApplicationContext(), "Call Find-P(4)", Toast.LENGTH_LONG).show();
                }
            }, 5000);
            //isLoading2 = true;
            //n2=0;
            //new Thread(handlerTask2).start();
            //handlerTask2.run();
        }*/
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Toast.makeText(MainActivity.this, "CALL MENU", Toast.LENGTH_LONG).show();
        /* Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;*/
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sync, menu);

        MenuItem offline = menu.findItem(R.id.action_offline);
        MenuItem msync = menu.findItem(R.id.action_sync);

        if(isSavingTrans) {
            msync.setVisible(false);
        } else {
            msync.setVisible(true);
        }

        new isOnline().execute("");
        if(internetAvailable){
            offline.setVisible(false);
        } else {
            offline.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_sync:
                sendtoServer();
                isSavingTrans = true;
                invalidateOptionsMenu();
                /*if(!isInternetOn()) {
                    bar.setBackgroundDrawable(new ColorDrawable(Color.RED));
                } else {
                    bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                }
                if(!isInternetOn()) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                }*/
                new isOnline().execute("");
                if(internetAvailable){
                    bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                    bar.setBackgroundDrawable(new ColorDrawable(Color.RED));
                }
                /*try {
                    if (!isConnected()) {
                        Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                    } else {
                        //Toast.makeText(getApplicationContext(), "Sinkronisasi data sedang berlangsung", Toast.LENGTH_LONG).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("fao", "fao debug problem getting data:" + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("fao", "fao debug problem getting data:" + e.getMessage());
                }*/

                gettax();
                getsc();
                new getToken().execute();
                new isOnline().execute("");
                if(internetAvailable){
                    new getTransactions().execute();
                    new getTransactionsDetail().execute();
                }
                    //new syncAccount().execute("select");
                //Log.d("acc sync called","ok");
                new syncCategory().execute("select");
                //Log.d("all sync called","ok");
                new syncProduct().execute("select");
                //Log.d("product sync called","ok");
                return true;
            case R.id.action_offline:
                Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_printer_on) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(MainActivity.this, "Device Not Support", Toast.LENGTH_SHORT).show();
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent,
                            REQUEST_ENABLE_BT);
                } else {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(MainActivity.this,
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
            //session.setShowcase(false);
        } else if (id == R.id.nav_exit) {
            finish();
            //session.setShowcase(false);
        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class populateCatList extends AsyncTask<String, Integer, JSONObject> {
        private CatListAdapter adapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                /*pDialog.setMessage("Sedang mengambil data...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();*/
                /*isLoading = true;
                n=0;
                new Thread(handlerTask).start();*/
                //handlerTask.run();
            } catch (Exception e) {

            }
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                JSONParser jParser = new JSONParser();
                JSONObject json = jParser.getJSONFromUrl(sURL + "?user_id=" + nClientId + "&jwt=" + session.sJWT());
                Log.d("kasandra", "kasandra debug : populateCatList json=" + json.toString() + "Log :" + sURL + "?user_id=" + nClientId + "&jwt=" + session.sJWT());
                return json;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            //if(pDialog != null || pDialog.isShowing()) {
            //    pDialog.dismiss();
            //}
            //Toast.makeText(mContext, "Problem getting data:" + e.getMessage(), Toast.LENGTH_SHORT).show();

            // Getting JSON Array from URL
            //category = json.getJSONArray(TAG_CATEGORY);
            //sLastUpdate = json.getString(TAG_LASTUPDATE);
            Cursor cCat = read.rawQuery("SELECT * FROM m_category;", null);
            int rowCat = cCat.getCount();
            int rowCat1 = cCat.getCount();
            if(rowCat1 > 0) {
                // removed handler.removeCallbacks(handlerTask);
                categorylist.clear();
                //Toast.makeText(getApplicationContext(), "Jumlah trans "+nClientId+"-"+rowTrans, Toast.LENGTH_LONG).show();
                int[] cat_id;
                cat_id = new int[rowCat];
                if(rowCat > 0) {
                    cCat.moveToFirst();
                    for (int i = 0; i < rowCat; i++) {
                        cat_id[i] = parseInt(cCat.getString(0));
                        // Adding value HashMap key => value for displaying in a ListView
                        HashMap<String, String> map = new HashMap<String, String>();
                        //map.put(TAG_LASTUPDATE, sLastUpdate);
                        map.put(TAG_ID, cCat.getString(0));
                        map.put(TAG_NAME, cCat.getString(1));
                        map.put(TAG_DATE, cCat.getString(2));
                        map.put(TAG_ICON, cCat.getString(3));
                        map.put(TAG_DISCOUNT, cCat.getString(4));
                        categorylist.add(map);
                        cCat.moveToNext();
                    }
                    session.setCatID(cat_id[0]);
                }

                list=(ListView) findViewById(R.id.listCategory);
                adapter = new CatListAdapter(MainActivity.this, categorylist, MainActivity.this);
                adapter.clearAdapter();
                list.setEmptyView(findViewById(android.R.id.empty));
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        view.setSelected(true);
                        //mSelectedItem = position;
                        //mAdapter.notifyDataSetChanged();
                    }
                });
            } else {
                try {
                    //pDialog.dismiss();
                    // removed handler.removeCallbacks(handlerTask);
                    categorylist.clear();
                    // Getting JSON Array from URL
                    category = json.getJSONArray(TAG_CATEGORY);
                    sLastUpdate = json.getString(TAG_LASTUPDATE);

                    int[] cat_id;
                    String[] token;
                    cat_id = new int[category.length()];
                    token = new String[category.length()];
                    for (int i = 0; i < category.length(); i++) {
                        JSONObject c = category.getJSONObject(i);
                        // Storing  JSON item in a Variable
                        String nID = String.valueOf(c.getInt(TAG_ID));
                        String sName = c.getString(TAG_NAME);
                        String sTS = c.getString(TAG_DATE);
                        String sPicture = c.getString(TAG_ICON);
                        String sDiscount = c.getString(TAG_DISCOUNT);
                        cat_id[i] = parseInt(nID);
                        token[i] = c.getString("token");
                        // Adding value HashMap key => value for displaying in a ListView
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_LASTUPDATE, sLastUpdate);
                        map.put(TAG_ID, nID);
                        map.put(TAG_NAME, sName);
                        map.put(TAG_DATE, sTS);
                        map.put(TAG_ICON, sPicture);
                        map.put(TAG_DISCOUNT, sDiscount);
                        categorylist.add(map);

                    }
                    session.setCatID(cat_id[0]);
                    session.setJWT(token[0]);

                    list = (ListView) findViewById(R.id.listCategory);
                    adapter = new CatListAdapter(MainActivity.this, categorylist, MainActivity.this);
                    adapter.clearAdapter();
                    list.setEmptyView(findViewById(android.R.id.empty));
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            view.setSelected(true);
                            //mSelectedItem = position;
                            //mAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (Exception e) {
                    //if (pDialog != null || pDialog.isShowing()) {
                    //    pDialog.dismiss();
                    //}
                    //Toast.makeText(mContext, "Problem getting data:" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    // removed handler.removeCallbacks(handlerTask);
                    categorylist.clear();
                    // Getting JSON Array from URL
                    //category = json.getJSONArray(TAG_CATEGORY);
                    //sLastUpdate = json.getString(TAG_LASTUPDATE);
                    Cursor cCat2 = read.rawQuery("SELECT * FROM m_category;", null);
                    int rowCat2 = cCat2.getCount();
                    //Toast.makeText(getApplicationContext(), "Jumlah trans "+nClientId+"-"+rowTrans, Toast.LENGTH_LONG).show();
                    int[] cat_id;
                    cat_id = new int[rowCat2];
                    if (rowCat2 > 0) {
                        cCat2.moveToFirst();
                        for (int i = 0; i < rowCat2; i++) {
                            cat_id[i] = parseInt(cCat2.getString(0));
                            // Adding value HashMap key => value for displaying in a ListView
                            HashMap<String, String> map = new HashMap<String, String>();
                            //map.put(TAG_LASTUPDATE, sLastUpdate);
                            map.put(TAG_ID, cCat2.getString(0));
                            map.put(TAG_NAME, cCat2.getString(1));
                            map.put(TAG_DATE, cCat2.getString(2));
                            map.put(TAG_ICON, cCat2.getString(3));
                            map.put(TAG_DISCOUNT, cCat2.getString(4));
                            categorylist.add(map);
                            cCat2.moveToNext();
                        }
                        session.setCatID(cat_id[0]);
                    }
                    cCat2.close();

                    list = (ListView) findViewById(R.id.listCategory);
                    adapter = new CatListAdapter(MainActivity.this, categorylist, MainActivity.this);
                    adapter.clearAdapter();
                    list.setEmptyView(findViewById(android.R.id.empty));
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            view.setSelected(true);
                            //mSelectedItem = position;
                            //mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
            cCat.close();
            //Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
        }
    }

    public void selectCust(String keyword){
        customer_id = session.nCustID();
        txt_customer.setText("Meja "+String.valueOf(session.nCustID()));
        showItem();
    }

    public void deleteCust(int cust_id){
        write.execSQL("DELETE FROM m_transactions_temp WHERE cust_temp_id="+cust_id);
        write.execSQL("DELETE FROM m_customers_temp WHERE cust_temp_id="+cust_id);
        showCustomerList();
        changeCust(1);
        //Snackbar.make(getApplicationContext(), " deleted", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void changeCust(int cust_id){
        customer_id = cust_id;
        session.setCustID(customer_id);
        txt_customer.setText("Meja "+String.valueOf(session.nCustID()));
        showItem();
    }

    public void findp(String keyword){
        gettax();
        if(session.nCatID() < 0) {
            //Toast.makeText(getApplicationContext(), "Kategori = " + session.nCatID(), Toast.LENGTH_SHORT).show();
        } else {
            layout.removeAllViews();

            //runOnUiThread(new Runnable(){
            //    public void run() {
            //        try {
                        /*Cursor cProd = read.rawQuery("SELECT * FROM m_products WHERE prod_category_id="+session.nCatID()+" order by prod_name ASC LIMIT 1", null);
                        int rowProd = cProd.getCount();
                        if (rowProd > 0) {*/
            drawLayout(keyword);
                        /*} else {
                            new getProducts().execute();
                        }*/
            //        } catch (final Exception ex) {
            //            Log.i("---","Exception in thread 4");
            //        }
            //    }
            //});
        }
    }

    public void showCustomerList(){
        CustListAdapter adapter;

        Cursor cCust = read.rawQuery("SELECT * FROM m_customers_temp;", null);
        int rowCust = cCust.getCount();
        int rowCust1 = cCust.getCount();
        if(rowCust1 > 0) {
            // removed handler.removeCallbacks(handlerTask);
            customerlist.clear();
            int[] cust_id;
            cust_id = new int[rowCust];
            if (rowCust > 0) {
                cCust.moveToFirst();
                for (int i = 0; i < rowCust; i++) {
                    cust_id[i] = parseInt(cCust.getString(0));
                    // Adding value HashMap key => value for displaying in a ListView
                    HashMap<String, String> map = new HashMap<String, String>();
                    //map.put(TAG_LASTUPDATE, sLastUpdate);
                    map.put("cust_id", cCust.getString(0));
                    map.put("cust_name", cCust.getString(1));
                    customerlist.add(map);
                    //Toast.makeText(getApplicationContext(), "Nomor Meja "+cCust.getString(1), Toast.LENGTH_LONG).show();
                    cCust.moveToNext();
                }
            }

            listCustomer = (ListView) findViewById(R.id.listCustomer);
            adapter = new CustListAdapter(MainActivity.this, customerlist, MainActivity.this);
            adapter.clearAdapter();
            listCustomer.setEmptyView(findViewById(android.R.id.empty));
            listCustomer.setAdapter(adapter);
            listCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    view.setSelected(true);
                    //mSelectedItem = position;
                    //mAdapter.notifyDataSetChanged();
                    listCustomer.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    listCustomer.setItemChecked(position, true);
                    listCustomer.setSelector(R.drawable.selector);
                }
            });
        }
        cCust.close();
    }

    public void neworder(){
        if(session.nCatID() < 0) {
            //Toast.makeText(getApplicationContext(), "Kategori = " + session.nCatID(), Toast.LENGTH_SHORT).show();
        } else {
            layout.removeAllViews();
            //drawLayout();
        }
    }

    public void drawLayout(final String key){
        runOnUiThread(new Runnable(){
            public void run() {
                try {
                    Cursor cProd;
                    int rowProd, rowProd1;
                    if(!key.equals("")){
                        cProd = read.rawQuery("SELECT * FROM m_products WHERE prod_code like '%"+key+"%' OR prod_name like '%"+key+"%' order by prod_name ASC", null);
                        rowProd = cProd.getCount();
                        rowProd1 = cProd.getCount();
                        msgNotFound = "Produk dengan keyword tersebut tidak ditemukan";
                    } else {
                        txtSearch.setText("");
                        cProd = read.rawQuery("SELECT * FROM m_products WHERE prod_category_id="+session.nCatID()+" order by prod_name ASC", null);
                        rowProd = cProd.getCount();
                        rowProd1 = cProd.getCount();
                        msgNotFound = "Belum ada produk untuk kategori yang dipilih";
                    }
        if (rowProd > 0) {
            //Toast.makeText(getApplicationContext(),"Condition 4 - "+String.valueOf(rowProd), Toast.LENGTH_LONG).show();
            cProd.moveToFirst();
            for(int i=0; i<rowProd; i++) {
                final int id = parseInt(cProd.getString(0));
                final String name = cProd.getString(1);
                String code = cProd.getString(2);
                String price_buy = cProd.getString(3);
                final String price_sell = cProd.getString(4);
                final int category_id = parseInt(cProd.getString(5));
                final String updated_date = cProd.getString(6);
                final String photo = cProd.getString(7);

                LinearLayout LL = new LinearLayout(getApplicationContext());
                LL.setOrientation(LinearLayout.VERTICAL);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    LL.setBackground(getResources().getDrawable(R.drawable.img_border));
                }
                LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LL.setLayoutParams(LLParams);

                imgView = new ImageView(getApplicationContext());
                imgView.setId(i+1);
                imgView.setLayoutParams(new TableRow.LayoutParams(210, 210));
                imgView.setPadding(5,5,5,5);
                imgView.setImageDrawable(getResources().getDrawable(R.drawable.imgview_bg));

                File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "kasandra" + File.separator + "product" + File.separator + photo); //+ "." + mFormat.name().toLowerCase());
                Bitmap image = BitmapFactory.decodeFile(myImageFile.getAbsolutePath());
                imgView.setImageBitmap(image);
                imgView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));

                TextView txtName = new TextView(getApplicationContext());
                txtName.setId(i+1);
                txtName.setGravity(Gravity.CENTER);
                txtName.setTypeface(font2);
                txtName.setTextColor(Color.BLACK);
                TextView txtCode = new TextView(getApplicationContext());
                txtCode.setId(i+1);
                txtCode.setGravity(Gravity.CENTER);
                txtCode.setTypeface(font2);
                txtCode.setTextColor(Color.BLACK);
                RelativeLayout.LayoutParams txtParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                txtParams2.addRule(RelativeLayout.BELOW, imgView.getId());
                txtParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
                txtCode.setLayoutParams(txtParams2);
                txtCode.setSingleLine(false);
                txtCode.setMaxLines(2);
                txtCode.setText(code);
                if(session.screenSize() < 7.9 && session.screenSize() > 6.5) {
                    imgView.setLayoutParams(new TableRow.LayoutParams(130, 130));
                } else if(session.screenSize() < 6.5) {
                    //layout.setColumnCount(4);
                    imgView.setLayoutParams(new TableRow.LayoutParams(130, 130));
                    txtName.setTextSize(12);
                } else if(session.screenSize() > 9.0){
                    layout.setColumnCount(4);
                    imgView.setLayoutParams(new TableRow.LayoutParams(215, 215));
                }
                RelativeLayout.LayoutParams txtParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                txtParams.addRule(RelativeLayout.BELOW, imgView.getId());
                txtParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                txtName.setLayoutParams(txtParams);
                txtName.setSingleLine(false);
                txtName.setMaxLines(2);
                txtName.setText(name);

                LL.addView(imgView);
                LL.addView(txtName);
                LL.addView(txtCode);
                layout.addView(LL);

                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gettax();
                        //if(session.isSaved()) {
                            Cursor cursorDisc = read.rawQuery("select discount from m_category where category_id="+category_id, null);
                            int rowDisc = cursorDisc.getCount();
                            if(rowDisc > 0) {
                                cursorDisc.moveToFirst();
                                nDiscount = cursorDisc.getInt(0);
                            }
                            cursorDisc.close();
                            if(nDiscount > 0) {
                                if(nDiscount > 100) {
                                    discValue2 = nDiscount;
                                } else {
                                    discValue2 = (parseInt(price_sell) * nDiscount / 100);
                                }
                            } else {
                                discValue2 = 0;
                            }
                            //Snackbar.make(view, name + " - Rp." + df.format(Double.parseDouble(price_sell)), Snackbar.LENGTH_LONG)
                            //        .setAction("Action", null).show();
                            write.execSQL("INSERT INTO m_transactions_temp (prod_id, qty, discount, cust_temp_id) VALUES(" + id + ", 1, "+discValue2+", "+session.nCustID()+");");
                            showItem();
                        //} else {
                        //    showMessage("PERINGATAN", "Transaksi tidak bisa diubah karena bill sudah dicetak.\n\nSilakan SIMPAN transaksi ini terlebih dahulu untuk membuat transaksi baru.");
                        //}
                    }
                });
                cProd.moveToNext();
            }
            cProd.close();
            if(pDialog != null || pDialog.isShowing()) {
                pDialog.dismiss();
            }
            // removed handler3.removeCallbacks(handlerTask3);
            //new Thread(handlerTask3).interrupt();
        } else {
            //Toast.makeText(getApplicationContext(),"Condition 4", Toast.LENGTH_LONG).show();
            String errorMsg = "Data tidak ditemukan";
            //Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
            LinearLayout LL = new LinearLayout(getApplicationContext());
            LL.setOrientation(LinearLayout.VERTICAL);
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        LL.setBackground(getResources().getDrawable(R.drawable.img_border));
                    }*/
            LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            //LL.setWeightSum(6f);
            //LLParams.setMargins(50,10,10,10);
            LL.setLayoutParams(LLParams);
            TextView txtView = new TextView(getApplicationContext());
            txtView.setId(0);
            txtView.setLayoutParams(new TableRow.LayoutParams(630, TableRow.LayoutParams.WRAP_CONTENT));
            txtView.setTextSize(18);
            if(session.screenSize() < 7.9 && session.screenSize() > 6.5) {
                txtView.setLayoutParams(new TableRow.LayoutParams(390, TableRow.LayoutParams.WRAP_CONTENT));
                txtView.setTextSize(14);
            } else if(session.screenSize() < 6.5){
                txtView.setLayoutParams(new TableRow.LayoutParams(390, TableRow.LayoutParams.WRAP_CONTENT));
                txtView.setTextSize(12);
            } else if(session.screenSize() > 9.0){
                txtView.setLayoutParams(new TableRow.LayoutParams(860, TableRow.LayoutParams.WRAP_CONTENT));
            }
            txtView.setPadding(5,5,5,5);
            txtView.setGravity(Gravity.CENTER);
            txtView.setTypeface(font1);
            txtView.setTextColor(Color.BLACK);
            txtView.setText("\n"+msgNotFound);
            //txtView.setLayoutParams(LLParams);
            LL.addView(txtView);

            /*Button fab2 = new Button(MainActivity.this);
            fab2.setId(0);
            fab2.setGravity(Gravity.END);
            fab2.setBackgroundColor(Color.WHITE);
            fab2.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            fab2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            fab2.setText("Tambah >>  ");
                        /-*if(session.screenSize() < 7.9) {
                            fab2.setLayoutParams(new TableRow.LayoutParams(390, TableRow.LayoutParams.WRAP_CONTENT));
                        } else if(session.screenSize() > 9.0){
                            fab2.setLayoutParams(new TableRow.LayoutParams(860, TableRow.LayoutParams.WRAP_CONTENT));
                        } else {
                            fab2.setLayoutParams(new TableRow.LayoutParams(750, TableRow.LayoutParams.WRAP_CONTENT));
                        }*-/
            fab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                    intent.putExtra("prod_id", 0);
                    startActivity(intent);
                    finish();
                }
            });
            LL.addView(fab2);*/

            layout.addView(LL);
            if(pDialog != null || pDialog.isShowing()) {
                pDialog.dismiss();
            }
            // removed handler3.removeCallbacks(handlerTask3);
            //new Thread(handlerTask3).interrupt();
        }
                } catch (final Exception ex) {
                    Log.i("---","Exception in thread 4");
                }
            }
        });
    }

    /*private class getProducts extends AsyncTask<String, Integer, JSONObject> {
        //private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                pDialog.setMessage("Sedang mengambil data...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
                //n3=0;
                //new Thread(handlerTask3).start();
                //handlerTask3.run();
            } catch (Exception e) {

            }
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            //String values = arg0[0];
            //String extractString = values;
            //String[] result = extractString.split("!");

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cat_id", session.nCatID());

                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl(sURL, params);
                Log.d("kasandra", "kasandra debug : getProduct jsonObject=" + jsonObject.toString());

                return json;
            } catch (Exception e) {
                Log.d("kasandra", "kasandra debug getProduct err3: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            try {
                //error = false;
                //error = !isInternetOn() || json.getBoolean("error");
                //if (isInternetOn()) {
                //try {

                    //if(rowProd1 > 0) {
                    //Toast.makeText(getApplicationContext(),"SELECT * FROM m_products WHERE prod_category_id="+session.nCatID()+" order by prod_name ASC" + "\n"+rowProd, Toast.LENGTH_LONG).show();

                //} else {

                    //}
                /-*} catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*-/
            } catch (Exception e) {
                // removed handler3.removeCallbacks(handlerTask3);
                //new Thread(handlerTask3).interrupt();
                if(pDialog != null || pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                e.printStackTrace();
                new isOnline().execute("");
                if(internetAvailable){
                    try {
                        boolean error;
                        error = json.getBoolean("error");
                        /-*} else {
                            error = false;
                        }*-/

                        if (!error) {

                            //Toast.makeText(getApplicationContext(),"Call the 3rd", Toast.LENGTH_LONG).show();
                            //Toast.makeText(getApplicationContext(),"Condition 1", Toast.LENGTH_LONG).show();
                            //String id = json.getString("id");
                            String returnmessage = json.getString("msg");
                            JSONArray params = json.getJSONArray("details");
                            //if (params.length() > 0) {
                            //write.execSQL("DELETE FROM fHouses");
                            //}
                            //Toast.makeText(getActivity().getApplicationContext(),"jumlah json: " + params.length(), Toast.LENGTH_LONG).show();

                            for (int i = 0; i < params.length(); i++) {
                                JSONObject obj = params.getJSONObject(i);
                                final int id = Integer.parseInt(obj.getString("prod_id"));
                                final String name = obj.getString("prod_name");
                                String code = obj.getString("prod_code");
                                String price_buy = obj.getString("prod_price_buy");
                                final String price_sell = obj.getString("prod_price_sell");
                                final String updated_date = obj.getString("updated_date");
                                final int category_id = Integer.parseInt(obj.getString("category_id"));
                                final String photo = obj.getString("prod_photo");

                                if (code.equals("") || code.equals("null")) {
                                    code = "";
                                }
                                if (price_buy.equals("") || price_buy.equals("null")) {
                                    price_buy = "0";
                                }

                                //Log.d("kasandra debug", "INSERT INTO m_products VALUES(" + id + ", '" + name + "', '" + code + "', " + price_buy + ", " + price_sell + ", " + category_id + ", '" + updated_date + "', '" + photo + "');");
                                //Cursor c = read.rawQuery("SELECT prod_id FROM m_products WHERE prod_id="+id, null);
                                //int rows = c.getCount();
                                //c.moveToFirst();
                                //if(rows == 0) {
                                try {
                                    write.execSQL("INSERT INTO m_products VALUES(" + id + ", '" + name + "', '" + code + "', " + price_buy + ", " + price_sell + ", " + category_id + ", '" + updated_date + "', '" + photo + "');");
                                } catch (Exception ex) {
                                    Log.d("kasandra", "kasandra debug getProduct err3: " + e.getMessage());
                                }
                                //}

                                LinearLayout LL = new LinearLayout(getApplicationContext());
                                LL.setOrientation(LinearLayout.VERTICAL);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    LL.setBackground(getResources().getDrawable(R.drawable.img_border));
                                }
                                //LL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                //LL.setWeightSum(6f);
                                LL.setLayoutParams(LLParams);

                                imgView = new ImageView(getApplicationContext());
                                imgView.setId(i + 1);
                                imgView.setLayoutParams(new TableRow.LayoutParams(250, 250));
                                if (session.screenSize() < 7.9) {
                                    imgView.setLayoutParams(new TableRow.LayoutParams(130, 130));
                                } else if (session.screenSize() > 9.0) {
                                    layout.setColumnCount(4);
                                    imgView.setLayoutParams(new TableRow.LayoutParams(215, 215));
                                }
                                imgView.setPadding(5, 5, 5, 5);
                                imgView.setImageDrawable(getResources().getDrawable(R.drawable.imgview_bg));
                                //imageLoader.DisplayImage("https://kasandra.biz/images/"+obj.getString("prod_photo"), imgView);
                                //Toast.makeText(getApplicationContext(), "https://kasandra.biz/images/"+obj.getString("prod_photo"), Toast.LENGTH_SHORT).show();
                                //new DownloadImageTask(imgView).execute("https://kasandra.biz/images/"+obj.getString("prod_photo"));
                                Glide.with(getApplicationContext()).load("https://kasandra.biz/images/" + obj.getString("prod_photo")).into(imgView);
                        /-*View v = new View(getApplicationContext());
                        v.setLayoutParams(new LinearLayout.LayoutParams(200,3));
                        v.setBackgroundColor(getResources().getColor(R.color.lightblue));*-/
                                downloadImagetoDevice(obj.getString("prod_photo"));
                        /-*File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                File.separator + "kasandra" + File.separator + "product" + File.separator + obj.getString("prod_photo")); //+ "." + mFormat.name().toLowerCase());
                        Bitmap image = BitmapFactory.decodeFile(myImageFile.getAbsolutePath());
                        imgView.setImageBitmap(image);
                        imgView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));*-/

                                TextView txtName = new TextView(getApplicationContext());
                                txtName.setId(i + 1);
                                txtName.setGravity(Gravity.CENTER);
                                txtName.setTypeface(font2);
                                txtName.setTextColor(Color.BLACK);
                                //TableRow.LayoutParams txtParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                                //txtName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                                //txtName.setPadding(5,5,5,5);
                                //txtName.setPadding(0,100,0,0);
                                RelativeLayout.LayoutParams txtParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                txtParams.addRule(RelativeLayout.BELOW, imgView.getId());
                                txtParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                                txtName.setLayoutParams(txtParams);
                                txtName.setSingleLine(false);
                                txtName.setMaxLines(2);
                                //txtName.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                                txtName.setText(name);

                                LL.addView(imgView);
                                //LL.addView(v);
                                LL.addView(txtName);

                                layout.addView(LL);

                                imgView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (session.isSaved()) {
                                            Cursor cursorDisc = read.rawQuery("select discount from m_category where category_id="+category_id, null);
                                            int rowDisc = cursorDisc.getCount();
                                            if(rowDisc > 0) {
                                                cursorDisc.moveToFirst();
                                                nDiscount = cursorDisc.getInt(0);
                                            }
                                            if(nDiscount > 0) {
                                                if(nDiscount > 100) {
                                                    discValue2 = nDiscount;
                                                } else {
                                                    discValue2 = (Integer.parseInt(price_sell) * nDiscount / 100);
                                                }
                                            } else {
                                                discValue2 = 0;
                                            }
                                            Snackbar.make(view, name + " - Rp." + df.format(Double.parseDouble(price_sell)), Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            write.execSQL("INSERT INTO m_transactions_temp (prod_id, qty, discount) VALUES(" + id + ", 1, "+discValue2+");");
                                            showItem();
                                        } else {
                                            showMessage("PERINGATAN", "Transaksi tidak bisa diubah karena bill sudah dicetak.\n\nSilakan SIMPAN transaksi ini terlebih dahulu untuk membuat transaksi baru.");
                                            //Toast.makeText(getApplicationContext(), "Simpan transaksi terlebih dahulu, karena bill sudah dicetak", Toast.LENGTH_LONG).show();
                                        }

                                /-*double tp = session.nSubTotal();
                                if(tp > 0){
                                    tp = tp;
                                } else {
                                    tp = 0;
                                }
                                double prices = Double.parseDouble(price_sell);
                                tp += prices;
                                session.setSubTotal(tp);
                                subtotal.setText(": Rp. "+String.valueOf(session.nSubTotal()));
                                total.setText(": Rp. "+String.valueOf(session.nSubTotal() + (session.nSubTotal() * 10/100)));
                                *-/
                                        //table_layout.removeAllViews();
                                        //table_layout1.removeAllViews();
                                    }
                                });
                            }
                            pDialog.dismiss();
                        } else {
                            //Toast.makeText(getApplicationContext(),"Call the 4th", Toast.LENGTH_LONG).show();
                            //Toast.makeText(getApplicationContext(),"Condition 2", Toast.LENGTH_LONG).show();
                            String errorMsg = json.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                            LinearLayout LL = new LinearLayout(getApplicationContext());
                            LL.setOrientation(LinearLayout.VERTICAL);
                    /-*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        LL.setBackground(getResources().getDrawable(R.drawable.img_border));
                    }*-/
                            LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            //LL.setWeightSum(6f);
                            //LLParams.setMargins(50,10,10,10);
                            LL.setLayoutParams(LLParams);
                            TextView txtView = new TextView(getApplicationContext());
                            txtView.setId(0);
                            txtView.setLayoutParams(new TableRow.LayoutParams(750, TableRow.LayoutParams.MATCH_PARENT));
                            txtView.setTextSize(18);
                            if (session.screenSize() < 7.9) {
                                txtView.setLayoutParams(new TableRow.LayoutParams(390, TableRow.LayoutParams.MATCH_PARENT));
                                txtView.setTextSize(15);
                            } else if (session.screenSize() > 9.0) {
                                txtView.setLayoutParams(new TableRow.LayoutParams(860, TableRow.LayoutParams.MATCH_PARENT));
                            }
                            txtView.setPadding(5, 5, 5, 5);
                            txtView.setGravity(Gravity.CENTER);
                            txtView.setTypeface(font1);
                            txtView.setTextColor(Color.BLACK);
                            txtView.setText("\nBelum ada produk untuk kategori yang dipilih");
                            //txtView.setLayoutParams(LLParams);
                            Button fab2 = new Button (MainActivity.this);
                            fab2.setId(0);
                            fab2.setGravity(Gravity.END);
                            fab2.setBackgroundColor(Color.WHITE);
                            fab2.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            fab2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                            fab2.setText("Tambah >>  ");
                            /-*if(session.screenSize() < 7.9) {
                                fab2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                            } else if(session.screenSize() > 9.0){
                                fab2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                            } else {
                                fab2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                            }*-/
                            fab2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                                    intent.putExtra("prod_id", 0);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            LL.addView(fab2);

                            LL.addView(txtView);

                            layout.addView(LL);
                            pDialog.dismiss();
                        }
                    } catch (Exception e0) {
                        pDialog.dismiss();
                    }
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }*/

    private void downloadImageCattoDevice(final String filename) {
        final BasicImageDownloader downloader = new BasicImageDownloader(new BasicImageDownloader.OnImageLoaderListener() {
            @Override
            public void onError(BasicImageDownloader.ImageError error) {
                /*Toast.makeText(ImageActivity.this, "Error code " + error.getErrorCode() + ": " +
                        error.getMessage(), Toast.LENGTH_LONG).show();
                imgDisplay.setImageResource(RES_ERROR);
                tvPercent.setVisibility(View.GONE);
                pbLoading.setVisibility(View.GONE);*/
                error.printStackTrace();
            }

            @Override
            public void onProgressChange(int percent) {
                //pbLoading.setProgress(percent);
                //tvPercent.setText(percent + "%");
            }

            @Override
            public void onComplete(Bitmap result) {
                        /* save the image - I'm gonna use JPEG */
                final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(filename);
                    result.compress(Bitmap.CompressFormat.JPEG, 50, out); // bmp is your Bitmap instance
                } catch (Exception e) {
                    e.printStackTrace();
                }
                        /* don't forget to include the extension into the file name */
                final File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "kasandra" + File.separator + "category" + File.separator + filename); //+ "." + mFormat.name().toLowerCase());
                BasicImageDownloader.writeToDisk(myImageFile, result, new BasicImageDownloader.OnBitmapSaveListener() {
                    @Override
                    public void onBitmapSaved() {
                        //Toast.makeText(getApplicationContext(), "Image saved as: " + myImageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onBitmapSaveError(BasicImageDownloader.ImageError error) {
                        /*Toast.makeText(mContext, "Error code " + error.getErrorCode() + ": " +
                                error.getMessage(), Toast.LENGTH_LONG).show();*/
                        error.printStackTrace();
                    }
                }, mFormat, false);
                //imgView.setImageBitmap(result);
                //imgView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
            }
        });
        downloader.download("https://kasandra.biz/images/" + filename, true);
    }

    private void downloadImagetoDevice(final String filename) {
        final BasicImageDownloader downloader = new BasicImageDownloader(new OnImageLoaderListener() {
            @Override
            public void onError(ImageError error) {
                /*Toast.makeText(ImageActivity.this, "Error code " + error.getErrorCode() + ": " +
                        error.getMessage(), Toast.LENGTH_LONG).show();
                imgDisplay.setImageResource(RES_ERROR);
                tvPercent.setVisibility(View.GONE);
                pbLoading.setVisibility(View.GONE);*/
                error.printStackTrace();
            }

            @Override
            public void onProgressChange(int percent) {
                //pbLoading.setProgress(percent);
                //tvPercent.setText(percent + "%");
            }

            @Override
            public void onComplete(Bitmap result) {
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(filename);
                    result.compress(Bitmap.CompressFormat.JPEG, 50, out); // bmp is your Bitmap instance
                } catch (Exception e) {
                    e.printStackTrace();
                }
                        /* save the image - I'm gonna use JPEG */
                final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
                        /* don't forget to include the extension into the file name */
                final File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "kasandra" + File.separator + "product" + File.separator + filename); //+ "." + mFormat.name().toLowerCase());
                BasicImageDownloader.writeToDisk(myImageFile, result, new BasicImageDownloader.OnBitmapSaveListener() {
                    @Override
                    public void onBitmapSaved() {
                        //Toast.makeText(MainActivity.this, "Image saved as: " + myImageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onBitmapSaveError(ImageError error) {
                        /*Toast.makeText(MainActivity.this, "Error code " + error.getErrorCode() + ": " +
                                error.getMessage(), Toast.LENGTH_LONG).show();*/
                        error.printStackTrace();
                    }
                }, mFormat, false);
                //imgView.setImageBitmap(result);
                //imgView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
            }
        });
        downloader.download("https://kasandra.biz/images/" + filename, true);
    }

    private class getTransactions extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                //pDialog = new ProgressDialog(mContext);
                pDialog.setMessage("Sinkronisasi sedang berlangsung...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            } catch (Exception e) {

            }
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
                Log.d("kasandra", "kasandra debug : getTransaction jsonObject=" + jsonObject.toString());

                return json;
            } catch (Exception e) {
                Log.d("kasandra", "kasandra debug getTransaction err3: " + e.getMessage());
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
                        Cursor cRead3 = read.rawQuery("SELECT * from m_transactions WHERE upload_status=0;", null);
                        int rows1 = cRead3.getCount();
                        if(rows1==0){
                            write.execSQL("DELETE FROM m_transactions");
                        } //else {
                          //  Toast.makeText(MainActivity.this, "Ada offline data"+rows1, Toast.LENGTH_LONG).show();
                        //}
                        cRead3.close();
                    }
                    JSONObject objCount = params.getJSONObject(1);
                    int total_trans = objCount.getInt("trans_total");
                    int trans_no;
                    trans_no = total_trans;
                    for(int i=0; i<params.length(); i++) {
                        JSONObject obj = params.getJSONObject(i);

                        final int id = parseInt(obj.getString("trans_id"));
                        String total_qty = obj.getString("total_qty");
                        String total_price = obj.getString("total_price");
                        final String datetime = obj.getString("datetime");
                        final String payment_type = obj.getString("payment_type");
                        String tax = obj.getString("trans_tax");
                        String disc = obj.getString("trans_disc");
                        String serv_charge = obj.getString("trans_serv_charge");

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

                        //Cursor c = read.rawQuery("SELECT prod_id FROM m_products WHERE prod_id="+id, null);
                        //int rows = c.getCount();
                        //c.moveToFirst();
                        //if(rows == 0) {
                        final String finalTotal_qty = total_qty;
                        final String finalTotal_price = total_price;
                        final String finalTax = tax;
                        final String finalDisc = disc;
                        final String finalServCharge = serv_charge;
                        //final int trans_no = total_trans;
                        trans_no--;
                        //final int finalTrans_no = trans_no;
                        final int finalTrans_no = trans_no;
                        /*runOnUiThread(new Runnable(){
                            public void run() {
                                Cursor cReadTrans = null;
                                try {
                                    cReadTrans = read.rawQuery("select trans_id from m_transactions where trans_id="+id+";", null);
                                    int rowTrans = cReadTrans.getCount();
                                    if(rowTrans == 0){
                                        write.execSQL("INSERT INTO m_transactions VALUES(" + id + ", '" + finalTotal_qty + "', '" + finalTotal_price + "', '" + datetime + "', 1, " + payment_type +", " + finalTax +", " + finalDisc +", " + finalTrans_no +", " + finalServCharge +");");
                                        //Toast.makeText(getApplicationContext(), "Trans NO"+finalTrans_no, Toast.LENGTH_LONG).show();
                                    }
                                }  finally {
                                    cReadTrans.close();
                                }
                            }
                        });*/
                        //AsyncTask.execute(new Runnable() {
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                Cursor cReadTrans = null;
                                try {
                                    read.beginTransaction();
                                    cReadTrans = read.rawQuery("select trans_id from m_transactions where trans_id="+id+";", null);
                                    int rowTrans = cReadTrans.getCount();
                                    if(rowTrans == 0){
                                        write.execSQL("INSERT INTO m_transactions VALUES(" + id + ", '" + finalTotal_qty + "', '" + finalTotal_price + "', '" + datetime + "', 1, " + payment_type +", " + finalTax +", " + finalDisc +", " + finalTrans_no +", " + finalServCharge +");");
                                        //Toast.makeText(getApplicationContext(), "Trans NO"+finalTrans_no, Toast.LENGTH_LONG).show();
                                    }
                                    read.setTransactionSuccessful();
                                    cReadTrans.close();
                                } catch (Exception ignored) {
                                } finally {
                                    read.endTransaction();
                                }
                            }
                        });
                        //}
                        //Toast.makeText(getApplicationContext(), "INSERT INTO m_transactions VALUES(" + id + ", '" + total_qty + "', '" + total_price + "', '" + datetime + "');", Toast.LENGTH_SHORT).show();
                    }

                    showTitle();
                    /*Cursor getTrans = read.rawQuery("SELECT MAX(trans_no) FROM m_transactions;", null);
                    int rowTrans = getTrans.getCount();
                    //Toast.makeText(getApplicationContext(), "Jumlah trans "+nClientId+"-"+rowTrans, Toast.LENGTH_LONG).show();
                    if(rowTrans > 0) {
                        getTrans.moveToLast();
                        val = getTrans.getInt(0) + 1;
                        title.setText("Penjualan #" + val);
                    } else {
                        val = 1;
                        title.setText("Penjualan #1");
                    }
                    if(params.length() > 0) {
                        title.setText("Penjualan #"+String.valueOf(params.length() + 1));
                    }*/
                    pDialog.dismiss();

                } else {
                    pDialog.dismiss();
                    String errorMsg = json.getString("error_msg");
                    //Toast.makeText(getApplicationContext(),
                      //      errorMsg, Toast.LENGTH_LONG).show();
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

    private class getTransactionsDetail extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                //pDialog = new ProgressDialog(mContext);
                pDialog.setMessage("Sinkronisasi sedang berlangsung...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            } catch (Exception e) {

            }
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("client_id_detail", session.nOutlet_ID());
                //jsonObject.put("limit", "1000");

                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl(sURL, params);
                Log.d("kasandra", "kasandra debug : getTransactionDetail jsonObject=" + jsonObject.toString());

                return json;
            } catch (Exception e) {
                Log.d("kasandra", "kasandra debug getTransactionDetail err3: " + e.getMessage());
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
                    final JSONArray params = json.getJSONArray("details");

                    //Toast.makeText(getApplicationContext(), params.toString(), Toast.LENGTH_SHORT).show();

                    if(params.length() > 0){
                        Cursor cRead3 = read.rawQuery("SELECT * from m_transactions_detail WHERE upload_status=0;", null);
                        int rows1 = cRead3.getCount();
                        if(rows1==0){
                            write.execSQL("DELETE FROM m_transactions_detail");
                        }
                        cRead3.close();
                    }

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                        for(int i=0; i<params.length(); i++) {
                            try {
                            JSONObject obj = params.getJSONObject(i);

                            final int trans_det_id = parseInt(obj.getString("trans_det_id"));
                            final int trans_id = parseInt(obj.getString("trans_id"));
                            final int prod_id = parseInt(obj.getString("prod_id"));

                            /*runOnUiThread(new Runnable(){
                                public void run() {
                                    Cursor cReadTransDet = null;
                                    try {
                                        cReadTransDet = read.rawQuery("select trans_det_id from m_transactions_detail where trans_det_id="+trans_det_id+";", null);
                                        int rowTransDet = cReadTransDet.getCount();
                                        if(rowTransDet == 0){
                                            write.execSQL("INSERT INTO m_transactions_detail VALUES(" + trans_det_id + ", " + trans_id + ", " + prod_id + ", 1);");
                                        }
                                    } finally {
                                        cReadTransDet.close();
                                    }
                                }
                            });*/
                                    Cursor cReadTransDet = null;
                                    read.beginTransaction();
                                    cReadTransDet = read.rawQuery("select trans_det_id from m_transactions_detail where trans_det_id="+trans_det_id+";", null);
                                    int rowTransDet = cReadTransDet.getCount();
                                    if(rowTransDet == 0){
                                        write.execSQL("INSERT INTO m_transactions_detail VALUES(" + trans_det_id + ", " + trans_id + ", " + prod_id + ", 1);");
                                    }
                                    read.setTransactionSuccessful();
                                    cReadTransDet.close();
                            } catch (Exception ignored) {
                            } finally {
                                    read.endTransaction();
                                        //cReadTransDet.close();
                            }
                        }
                    }
                    });
                    pDialog.dismiss();

                } else {
                    pDialog.dismiss();
                    String errorMsg = json.getString("error_msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
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

    private class saveTransactionDetail extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Sinkronisasi sedang berlangsung...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            //Log.d("posdebug", "posdebug");

        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            String extractString = arg0[0];
            String[] result = extractString.split("!");

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("trans_detail_id", result[0]);
                jsonObject.put("trans_id", result[1]);
                jsonObject.put("prod_id", result[2]);
                jsonObject.put("upload_status", result[3]);
                jsonObject.put("datetime", result[4]);

                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl(sURL, params);
                Log.d("kasandra", "kasandra debug : saveTransactionDetail jsonObject=" + jsonObject.toString());

                return json;
            } catch (Exception e) {
                Log.d("kasandra", "kasandra debug saveTransactionDetail err3: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            try {
                pDialog.dismiss();
                boolean error = json.getBoolean("error");

                // Check for error node in json
                if (!error) {

                    final String id = json.getString("id");
                    String returnmessage = json.getString("msg");
                    JSONObject params = json.getJSONObject("param");
                    runOnUiThread(new Runnable(){
                        public void run() {
                            try {
                                write.execSQL("UPDATE m_transactions_detail SET upload_status=1 WHERE trans_det_id="+ id +";");
                            } catch (final Exception ex) {
                                Log.i("---","Exception in thread 3");
                            }
                        }
                    });
                    //Toast.makeText(getApplicationContext(),returnmessage, Toast.LENGTH_LONG).show();

                } else {
                    // Error in login. Get the error message
                    String errorMsg = json.getString("error_msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                pDialog.dismiss();
                // JSON error
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

    }

    private class saveTransactionBillDetail extends AsyncTask<String, Integer, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            String extractString = arg0[0];
            String[] result = extractString.split("!");
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("trans_bill_detail_id", result[0]);
                jsonObject.put("trans_bill_id", result[1]);
                jsonObject.put("prod_id", result[2]);
                jsonObject.put("upload_status", result[3]);
                jsonObject.put("datetime", result[4]);
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));
                JSONObject json = jsonParser.getJSONFromUrl(sURL, params);
                Log.d("kasandra", "kasandra debug : saveTransactionBillDetail jsonObject=" + jsonObject.toString());
                return json;
            } catch (Exception e) {
                Log.d("kasandra", "kasandra debug saveTransactionBillDetail err3: " + e.getMessage());
                return null;
            }
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            try {
                boolean error = json.getBoolean("error");
                if (!error) {
                    final String id = json.getString("id");
                    String returnmessage = json.getString("msg");
                    JSONObject params = json.getJSONObject("param");
                    runOnUiThread(new Runnable(){
                        public void run() {
                            try {
                                write.execSQL("UPDATE m_transactions_bill_detail SET upload_status=1 WHERE trans_bill_det_id="+ id +";");
                                write.execSQL("DELETE FROM m_transactions_bill_detail WHERE upload_status=1 AND trans_bill_det_id="+ id +";");
                            } catch (final Exception ex) {
                                Log.i("---","Exception in thread 3");
                            }
                        }
                    });
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

    private class saveTransaction extends AsyncTask<String, Integer, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d("posdebug", "posdebugX");
            //Toast.makeText(getApplicationContext(),"Upload to Server", Toast.LENGTH_LONG).show();
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            String extractString1 = arg0[0];
            String[] result = extractString1.split("!");
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("transaction_id", result[0]);
                jsonObject.put("qty", result[1]);
                jsonObject.put("price", result[2]);
                jsonObject.put("datetime", result[3]);
                jsonObject.put("client_id", nClientId);
                jsonObject.put("upload_status", result[4]);
                jsonObject.put("payment_type", result[5]);
                jsonObject.put("disc", result[6]);
                jsonObject.put("outlet_id", session.nOutlet_ID());

                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl(sURL, params);
                Log.d("kasandra", "kasandra debug : saveTransaction jsonObjectTrans=" + jsonObject.toString());

                return json;
            } catch (Exception e) {
                Log.d("kasandra", "kasandra debug saveTransaction err3: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            try {
                boolean error = json.getBoolean("error");

                // Check for error node in json
                if (!error) {

                    String id = json.getString("id");
                    String returnmessage = json.getString("msg");
                    JSONObject params = json.getJSONObject("param");

                    write.execSQL("UPDATE m_transactions SET upload_status=1 WHERE trans_id="+ id +";");
                    Toast.makeText(getApplicationContext(),returnmessage, Toast.LENGTH_LONG).show();

                } else {
                    // Error in login. Get the error message
                    String errorMsg = json.getString("error_msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                // JSON error
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

    }

    private class saveTransactionBill extends AsyncTask<String, Integer, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected JSONObject doInBackground(String... arg0) {
            String extractString1 = arg0[0];
            String[] result = extractString1.split("!");
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("transaction_bill_id", result[0]);
                jsonObject.put("qty", result[1]);
                jsonObject.put("price", result[2]);
                jsonObject.put("datetime", result[3]);
                jsonObject.put("client_id", nClientId);
                jsonObject.put("upload_status", result[4]);
                jsonObject.put("payment_type", result[5]);
                jsonObject.put("disc", result[6]);
                jsonObject.put("outlet_id", session.nOutlet_ID());
                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));
                JSONObject json = jsonParser.getJSONFromUrl(sURL, params);
                Log.d("kasandra", "kasandra debug : saveTransactionBill jsonObjectTrans=" + jsonObject.toString());
                return json;
            } catch (Exception e) {
                Log.d("kasandra", "kasandra debug saveTransactionBill err3: " + e.getMessage());
                return null;
            }
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            try {
                boolean error = json.getBoolean("error");
                if (!error) {
                    String id = json.getString("id");
                    String returnmessage = json.getString("msg");
                    JSONObject params = json.getJSONObject("param");
                    write.execSQL("UPDATE m_transactions_bill SET upload_status=1 WHERE trans_bill_id="+ id +";");
                    write.execSQL("DELETE FROM m_transactions_bill WHERE upload_status=1 AND trans_bill_id="+ id +";");
                    Toast.makeText(getApplicationContext(),returnmessage, Toast.LENGTH_LONG).show();
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

    @Override
    public void onPause() {
        super.onPause();
        savePreferences();
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
            Toast toast = Toast.makeText(MainActivity.this, "Gagal menyambungkan printer, silakan coba lagi", Toast.LENGTH_SHORT);
            View view = toast.getView();
            view.setBackgroundColor(Color.RED);
            //view.setBackgroundResource(R.drawable.custom_bkg);
            TextView text = (TextView) view.findViewById(android.R.id.message);
            text.setTextColor(Color.WHITE);
            toast.show();
            if (mBluetoothAdapter != null)
                mBluetoothAdapter.disable();
            drawer.openDrawer(Gravity.LEFT);
            //Toast.makeText(MainActivity.this, "Gagal menyambungkan printer", Toast.LENGTH_SHORT).show();
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

            /*Toast toast = Toast.makeText(MainActivity.this, "Telah terhubung dengan printer, silakan mencetak kembali", Toast.LENGTH_SHORT);
            View view = toast.getView();
            view.setBackgroundColor(getResources().getColor(R.color.darkgreen));
            //view.setBackgroundResource(R.drawable.custom_bkg);
            TextView text = (TextView) view.findViewById(android.R.id.message);
            text.setTextColor(Color.BLACK);
            toast.show();*/
            Toast.makeText(MainActivity.this, "Telah terhubung dengan printer, silakan mencetak kembali", Toast.LENGTH_LONG).show();
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
                    Intent connectIntent = new Intent(MainActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(MainActivity.this, "Bluetooth activate denied", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new populateCatList().execute();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findp("");
                            //Toast.makeText(getApplicationContext(), "Call Find-P(5)", Toast.LENGTH_LONG).show();
                        }
                    }, 2000);
                    //isLoading2 = true;
                    //n2=0;
                    //new Thread(handlerTask2).start();
                    //handlerTask2.run();
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

            case MY_PERMISSIONS_3: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    new getLatLng().execute("loc");
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public class syncAccount extends AsyncTask<String, Integer, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            String values = arg0[0];

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("sync_account", session.nUserID());

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

                    //String id = json.getString("id");
                    String returnmessage = json.getString("msg");
                    JSONArray params = json.getJSONArray("details");
                    if(params.length() > 0){
                        write.execSQL("DELETE FROM m_clients");
                        write.execSQL("DELETE FROM m_users");
                    }
                    //Toast.makeText(getActivity().getApplicationContext(),"jumlah json: " + params.length(), Toast.LENGTH_LONG).show();

                    for(int i=0; i<params.length(); i++) {

                        JSONObject obj = params.getJSONObject(i);
                        final int client_id = parseInt(obj.getString("client_id"));
                        final String client_username = obj.getString("client_username");
                        String client_fullname = obj.getString("client_fullname");
                        int client_status = parseInt(obj.getString("client_status"));
                        final int user_id = parseInt(obj.getString("user_id"));
                        final String user_name = obj.getString("user_name");
                        final String user_fullname = obj.getString("user_fullname");
                        final String user_email = obj.getString("user_email");
                        final int user_active = parseInt(obj.getString("user_active"));
                        final int user_isadmin = parseInt(obj.getString("user_isadmin"));
                        final int user_status = parseInt(obj.getString("user_status"));

                        try {
                            write.execSQL("INSERT INTO m_users VALUES(" + user_id + ", '" + user_name + "', '" + user_fullname + "', '" + user_email + "', " + user_active + ", " + user_isadmin + ", " + user_status + ", " + client_id + ");");
                            write.execSQL("INSERT INTO m_clients VALUES(" + client_id + ", '" + client_username + "', '" + client_fullname + "', " + client_status + ");");
                        } catch (Exception e) {
                            Log.d("kasandra", "kasandra debug err3: " + e.getMessage());
                        }
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

    private class syncProduct extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*try {
                //pDialog = new ProgressDialog(mContext);
                pDialog.setMessage("Sinkronisasi sedang berlangsung...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            } catch (Exception e) {

            }*/
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            String values = arg0[0];

            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("sync_product", nClientId);

                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl(sURL, params);
                Log.d("kasandra", "kasandra debug : syncProduct jsonObject=" + jsonObject.toString());

                return json;
            } catch (Exception e) {
                Log.d("kasandra", "kasandra debug syncProduct err3: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            try {
                boolean error = json.getBoolean("error");

                if (!error) {

                    //String id = json.getString("id");
                    String returnmessage = json.getString("msg");
                    JSONArray params = json.getJSONArray("details");
                    if(params.length() > 0){
                        /*if(isInternetOn()) {
                            write.execSQL("DELETE FROM m_products");
                        }*/
                        new isOnline().execute("");
                        if(internetAvailable){
                            write.execSQL("DELETE FROM m_products");
                        }
                    }
                    //Toast.makeText(getActivity().getApplicationContext(),"jumlah json: " + params.length(), Toast.LENGTH_LONG).show();

                    for(int i=0; i<params.length(); i++) {
                        JSONObject obj = params.getJSONObject(i);
                        final int id = parseInt(obj.getString("prod_id"));
                        final String name = obj.getString("prod_name");
                        String code = obj.getString("prod_code");
                        String price_buy = obj.getString("prod_price_buy");
                        final String price_sell = obj.getString("prod_price_sell");
                        final String updated_date = obj.getString("updated_date");
                        final int category_id = parseInt(obj.getString("category_id"));
                        final String photo = obj.getString("prod_photo");

                        if(code.equals("") || code.equals("null")){
                            code = "";
                        }
                        if(price_buy.equals("") || price_buy.equals("null")){
                            price_buy = "0";
                        }

                        final String finalCode = code;
                        final String finalPrice_buy = price_buy;
                        write.execSQL("INSERT INTO m_products VALUES(" + id + ", '" + name + "', '" + finalCode + "', " + finalPrice_buy + ", " + price_sell + ", " + category_id + ", '" + updated_date + "', '" + photo + "');");

                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                File.separator + "kasandra" + File.separator + "product" + File.separator, photo );
                        if (!file.exists()) {
                            downloadImagetoDevice(photo);
                        }
                    }
                    /*handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findp();
                            Toast.makeText(getApplicationContext(), "Call Find-P(6)", Toast.LENGTH_LONG).show();
                        }
                    }, 5000);*/
                    //pDialog.dismiss();
                } else {
                    //pDialog.dismiss();
                    String errorMsg = json.getString("error_msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                //pDialog.dismiss();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    private class syncCategory extends AsyncTask<String, Integer, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            String values = arg0[0];

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("sync_category", nClientId);

                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl(sURL, params);
                Log.d("kasandra", "kasandra debug : syncCategory jsonObject=" + jsonObject.toString());

                return json;
            } catch (Exception e) {
                Log.d("kasandra", "kasandra debug syncCategory err3: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);

            try {
                boolean error = json.getBoolean("error");

                if (!error) {

                    //String id = json.getString("id");
                    String returnmessage = json.getString("msg");
                    JSONArray params = json.getJSONArray("category");
                    if(params.length() > 0){
                        write.execSQL("DELETE FROM m_category");
                    }
                    //Toast.makeText(getActivity().getApplicationContext(),"jumlah json: " + params.length(), Toast.LENGTH_LONG).show();

                    for(int i=0; i<params.length(); i++) {
                        JSONObject obj = params.getJSONObject(i);
                        final int id = parseInt(obj.getString("cat_id"));
                        final String name = obj.getString("name");
                        final String updated_date = obj.getString("updated_date");
                        final String photo = obj.getString("picture_path");
                        String discount = obj.getString("discount");
                        if(discount.equals("") || discount.equals("null")){
                            discount = "0";
                        }

                        try {
                            write.execSQL("INSERT INTO m_category VALUES(" + id + ", '" + name + "', '" + updated_date + "', '" + photo + "', '" + discount + "');");
                            //Glide.with(getApplicationContext()).load("https://kasandra.biz/images/" + photo).into(pImageView);
                            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                    File.separator + "kasandra" + File.separator + "category" + File.separator, photo );
                            if (!file.exists()) {
                                downloadImageCattoDevice(photo);
                            }
                        } catch (Exception e) {
                            Log.d("kasandra", "kasandra debug syncCategory err3: " + e.getMessage());
                        }

                    }
                    new populateCatList().execute();
                    /*handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findp();
                            Toast.makeText(getApplicationContext(), "Call Find-P(7)", Toast.LENGTH_LONG).show();
                        }
                    }, 5000);*/
                    //isLoading2 = true;
                    //n2=0;
                    //new Thread(handlerTask2).start();
                    //handlerTask2.run();

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

    private class getToken extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                JSONParser jParser = new JSONParser();
                JSONObject json = jParser.getJSONFromUrl(sURL + "?outlet_id="+session.nOutlet_ID());
                //JSONObject json = jParser.getJSONFromUrl(sURL + "?outlet_id=" + nOutletId);
                return json;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                // Getting JSON Array from URL
                String token = json.getString("token");
                session.setToken(token);
            } catch (Exception e) {

            }
        }
    }

    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog));
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec(command).waitFor() == 0);
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

    protected void displayTuto() {
        TutoShowcase.from(this)
                .setListener(new TutoShowcase.Listener() {
                    @Override
                    public void onDismissed() {
                        update();
                        drawer.closeDrawer(GravityCompat.START);
                        //Toast.makeText(MainActivity.this, "Tutorial dismissed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setContentView(R.layout.showcase)
                .setFitsSystemWindows(true)
                .on(R.id.listCategory)
                .addCircle()
                .withBorder()
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (mBluetoothAdapter == null) {
                            Toast.makeText(MainActivity.this, "Device Not Support", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!mBluetoothAdapter.isEnabled()) {
                                Intent enableBtIntent = new Intent(
                                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent,
                                        REQUEST_ENABLE_BT);
                            } else {
                                ListPairedDevices();
                                Intent connectIntent = new Intent(MainActivity.this,
                                        DeviceListActivity.class);
                                startActivityForResult(connectIntent,
                                        REQUEST_CONNECT_DEVICE);

                            }
                        }
                        drawer.closeDrawer(GravityCompat.START);
                    }
                })

                //.on(R.id.swipable)
                //.displaySwipableLeft()
                //.delayed(399)
                //.animated(true)

                .show();
    }

    public void update() {
        WifiManager wm = (WifiManager) getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        boolean connected = ((ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        if (wm.isWifiEnabled()) {
            if (!connected) {
                session.setWiFi("WiFi disconnected");
                //Toast.makeText(getApplicationContext(), "WiFi = "+session.sWiFi(), Toast.LENGTH_LONG).show(); /*==by joko feb, 6==*/
            } else {
                WifiInfo wi = wm.getConnectionInfo();
                int ip = wi.getIpAddress();
                session.setWiFi(wi.getSSID());
                //Toast.makeText(getApplicationContext(), "WiFi connected = "+session.sWiFi(), Toast.LENGTH_SHORT).show();
            }
        } else {
            session.setWiFi("WiFi disabled");
            //Toast.makeText(getApplicationContext(), "WiFi = "+session.sWiFi(), Toast.LENGTH_LONG).show(); /*==by joko feb, 6==*/
        }
    }

    private void showLoading(){
        pDialog2 = new ProgressDialog(MainActivity.this);
        pDialog2.setMessage("Sedang mencetak...");
        pDialog2.setIndeterminate(false);
        pDialog2.setCancelable(false);
        pDialog2.show();
    }

    private void hideLoading(){
        pDialog2.dismiss();
    }

    private class getLatLng extends AsyncTask<String, Integer, JSONObject> {
        private NetworkLocator netLoc1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            netLoc1 = new NetworkLocator(getApplicationContext());
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                netLoc1.getLocation();
                nLong = netLoc1.getLongitude();
                nLat = netLoc1.getLatitude();
                JSONParser jParser = new JSONParser();
                JSONObject json = jParser.getJSONFromUrl(sURL + "?long="+nLong+"&lat="+nLat+"&outlet="+session.nOutlet_ID());
                //JSONObject json = jParser.getJSONFromUrl(sURL + "?outlet_id=" + nOutletId);
                return json;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                // Getting JSON Array from URL
                //String token = json.getString("token");
                //showMessage("loc",String.valueOf(session.latitude())+","+String.valueOf(session.longitude()));
                session.setLatLng(nLat, nLong);
            } catch (Exception e) {

            }
        }
    }
}