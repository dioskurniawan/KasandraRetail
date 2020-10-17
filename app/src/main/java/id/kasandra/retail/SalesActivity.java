package id.kasandra.retail;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SalesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String sURL = "https://kasandra.biz/appdata/getdata_dev1.php";
    private ArrayList<HashMap<String, String>> categorylist = new ArrayList<HashMap<String, String>>();
    private ListView list;
    public static final String TAG_CATEGORY = "category";
    public static final String TAG_LASTUPDATE = "lastupdate";
    public static final String TAG_ID = "cat_id";
    public static final String TAG_NAME = "name";
    public static final String TAG_DATE = "updated_date";
    public static final String TAG_ICON = "picture_path";

    public String sLastUpdate;
    public JSONArray category = null;
    public String sQuery = "";
    private static final int MY_PERMISSIONS_1 = 0;

    private int nClientId = 0;
    private SessionManager session;
    private ProgressDialog pDialog;
    final Handler handler = new Handler();
    int n = 0;
    int count;
    private FragmentTabHost mTabHost;
    GridLayout layout;
    TableLayout table_layout, table_layout1;
    ScrollView scroll;
    TextView subtotal, total;
    SQLiteHandler db;
    SQLiteDatabase write;
    SQLiteDatabase read;
    private String[] sProduct, nPrice, nQty, nID;
    double tp, prices;
    Typeface font1, font2;
    DecimalFormat df;

    String column1 = "Nama Item", column2 = "Harga", column3 = "Jumlah", column4 = "Hapus";
    Cursor cursor;
    /*String column_value1 = null, column_value2 = null, column_value3 = null;
    column1 = "Nama Item";
    column2 = "Harga Satuan";
    column3 = "Jumlah";
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.action_a);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        db = new SQLiteHandler(getApplicationContext());
        write = db.getWritableDatabase();
        read = db.getReadableDatabase();

        LinearLayout layoutRight = (LinearLayout) findViewById(R.id.layout_total);
        layoutRight.setBackgroundColor(getResources().getColor(R.color.bg_login));

        font1 = Typeface.createFromAsset(getAssets(), "quattrocentobold.ttf");
        font2 = Typeface.createFromAsset(getAssets(), "GenBasR.ttf");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');
        df = new DecimalFormat("#,###", otherSymbols);

        layout = (GridLayout) findViewById(R.id.grid_layout);
        scroll = (ScrollView) findViewById(R.id.scroll);
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
        TextView tax2 = (TextView) findViewById(R.id.tax2);
        tax2.setTypeface(font1);

        pDialog = new ProgressDialog(new ContextThemeWrapper(SalesActivity.this, android.R.style.Theme_Holo_Light_Dialog));
        session = new SessionManager(this.getApplicationContext());

        session.setSubTotal(0);

        if (nClientId == 0) {
            nClientId = session.nClient_ID();
        }
        // check nClientId from persistence store
        if (session.nClient_ID() != -999) {
            nClientId = session.nClient_ID();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                findp();
            }
        } else {
            new populateCatList().execute();
            findp();
        }

        showItem();

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

        /*mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager());
        mTabHost.getTabWidget().setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));

        mTabHost.addTab(mTabHost.newTabSpec("fragmenta").setIndicator("Kg/1000"), EmptyFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("fragmentb").setIndicator("Hen Day"), EmptyFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("fragmentc").setIndicator("FCR"), EmptyFragment.class, null);

        for (int h = 0; h < mTabHost.getTabWidget().getChildCount(); h++) {
            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(h).findViewById(android.R.id.title);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
            tv.setTextColor(Color.BLACK);
        }*/
    }

    private void showItem() {
        //write.execSQL("DELETE FROM m_transactions;");
        table_layout1.removeAllViews();

        TableRow row1 = new TableRow(getApplicationContext());
        row1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        //row1.setBackgroundColor(getResources().getColor(R.color.bg_login));

        TextView tv_ = new TextView(getApplicationContext());
        tv_.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv_.setGravity(Gravity.CENTER);
        tv_.setTypeface(font1, Typeface.BOLD);
        tv_.setTextColor(Color.WHITE);
        tv_.setTextSize(18);
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
        tv2_.setTextSize(18);
        tv2_.setText(column1);
        row1.addView(tv2_);

        TextView tv3_ = new TextView(getApplicationContext());
        tv3_.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv3_.setGravity(Gravity.RIGHT);
        tv3_.setTypeface(font1, Typeface.BOLD);
        tv3_.setTextColor(Color.WHITE);
        tv3_.setTextSize(18);
        tv3_.setText(column2);
        row1.addView(tv3_);

        TextView tv4_ = new TextView(getApplicationContext());
        tv4_.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv4_.setGravity(Gravity.RIGHT);
        tv4_.setTypeface(font1, Typeface.BOLD);
        tv4_.setTextColor(Color.WHITE);
        tv4_.setTextSize(18);
        tv4_.setText(column3);
        row1.addView(tv4_);

        TextView tv5_ = new TextView(getApplicationContext());
        tv5_.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv5_.setGravity(Gravity.CENTER);
        tv5_.setTypeface(font1, Typeface.BOLD);
        tv5_.setTextColor(Color.WHITE);
        tv5_.setTextSize(18);
        tv5_.setText(column4);
        row1.addView(tv5_);

        table_layout1.addView(row1);

        table_layout.removeAllViews();
        //if(rows == 0) {
        //} else {
        //row.removeView(row);
        // inner for loop
        cursor = read.rawQuery("select a.trans_id, count(a.qty) as qty_, count(a.qty) * b.prod_price_sell as totalprice, b.* from m_transactions a, m_products b where a.prod_id=b.prod_id GROUP BY a.prod_id ORDER BY a.trans_id ASC;", null);
        //select a.* from fSnapshots a JOIN (SELECT MAX(b.snap_ts) 'max_snap_ts' FROM fSnapshots b where b.house_id = '$house_id' GROUP BY date(b.snap_ts)) c ON c.max_snap_ts = a.snap_ts order by a.snap_ts asc;
        int rows = cursor.getCount();
        int cols = cursor.getColumnCount();

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
            cursor.moveToFirst();

            // outer for loop
            count = 0;
            sProduct = new String[cursor.getCount()];
            nQty = new String[cursor.getCount()];
            nPrice = new String[cursor.getCount()];
            nID = new String[cursor.getCount()];
            // outer for loop
            for (int i = 0; i < rows; i++) {

                sProduct[i] = cursor.getString(4);
                nQty[i] = cursor.getString(1);
                nPrice[i] = cursor.getString(7);
                nID[i] = cursor.getString(3);

                TableRow row = new TableRow(getApplicationContext());
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT));
                if(i %2 == 0) {
                    row.setBackgroundColor(getResources().getColor(R.color.lightblue));
                } else {
                    row.setBackgroundColor(getResources().getColor(R.color.white));
                }

                count++;
                TextView tv = new TextView(getApplicationContext());
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                tv.setGravity(Gravity.CENTER);
                tv.setTypeface(font1);
                tv.setTextSize(16);
                tv.setText(String.valueOf(count));
                row.addView(tv);

                TextView tv2 = new TextView(getApplicationContext());
                tv2.setLayoutParams(new TableRow.LayoutParams(200, TableRow.LayoutParams.WRAP_CONTENT));
                tv2.setGravity(Gravity.LEFT);
                tv2.setTypeface(font2, Typeface.BOLD);
                tv2.setTextSize(15);
                tv2.setText(cursor.getString(4));
                row.addView(tv2);

                TextView tv3 = new TextView(getApplicationContext());
                tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                tv3.setGravity(Gravity.RIGHT);
                tv3.setTypeface(font2, Typeface.BOLD);
                tv3.setTextSize(15);
                tv3.setText(df.format(cursor.getDouble(7)));
                row.addView(tv3);

                TextView tv4 = new TextView(getApplicationContext());
                tv4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                tv4.setGravity(Gravity.RIGHT);
                tv4.setTypeface(font2, Typeface.BOLD);
                tv4.setTextSize(15);
                tv4.setText(cursor.getString(1));
                row.addView(tv4);

                Button btn1 = new Button(getApplicationContext());
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
                        write.execSQL("delete from m_transactions where trans_id in (select trans_id from m_transactions where prod_id="+nID[finalI1]+" LIMIT 1)");
                        //Log.d("kasandra debug","DELETE FROM m_transactions where prod_id='"+nID[finalI1]+"' LIMIT 1");
                        Snackbar.make(view, sProduct[finalI]+" Dihapus", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        /*double totalp = session.nSubTotal()-Double.parseDouble(nPrice[finalI2]);
                        //prices = Double.parseDouble(cursor.getString(2));
                        subtotal.setText(": Rp. "+String.valueOf(totalp));
                        total.setText(": Rp. "+String.valueOf(totalp + (totalp * 10/100)));*/
                        showItem();
                        //table_layout.removeViewAt(finalI);
                    }
                });
                cursor.moveToNext();
                table_layout.removeView(row);
                table_layout.addView(row);
            }

            Cursor cursor1 = read.rawQuery("select sum(b.prod_price_sell) as total_price from m_transactions a, m_products b where a.prod_id=b.prod_id;", null);
            cursor1.moveToFirst();
            session.setSubTotal(cursor1.getDouble(0));
            subtotal.setText(": Rp. "+ String.valueOf(df.format(session.nSubTotal())));
            total.setText(": Rp. "+ String.valueOf(df.format(session.nSubTotal() + (session.nSubTotal() * 10/100))));
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
                InputStream in = new URL(urldisplay).openStream();
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

    final Runnable handlerTask = new Runnable() {

        @Override
        public void run() {

            n++;
            if (n >= 15) { // upload data every 30 seconds
                Toast.makeText(SalesActivity.this, "Failed to display data, please check your internet connection", Toast.LENGTH_SHORT).show();
                if(pDialog != null || pDialog.isShowing()) {
                    pDialog.dismiss();
                    n = 0;
                    handler.removeCallbacks(handlerTask);
                }
            }
            handler.postDelayed(handlerTask, 1000);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        //new populateList().execute();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
            }
        } else {
            new populateCatList().execute();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class populateCatList extends AsyncTask<String, Integer, JSONObject> {
        private CatListAdapter adapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                pDialog.setMessage("Fetching data...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
                handlerTask.run();
            } catch (Exception e) {

            }
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                JSONParser jParser = new JSONParser();
                JSONObject json = jParser.getJSONFromUrl(sURL + "?user_id="+nClientId + "&jwt=" + session.sJWT());
                Log.d("kasandra", "kasandra debug : json=" + json.toString() + "Log :" + sURL + "?user_id=" + nClientId);
                return json;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                pDialog.dismiss();
                handler.removeCallbacks(handlerTask);
                categorylist.clear();
                // Getting JSON Array from URL
                category = json.getJSONArray(TAG_CATEGORY);
                sLastUpdate = json.getString(TAG_LASTUPDATE);
                for(int i = 0; i < category.length(); i++) {
                    JSONObject c = category.getJSONObject(i);
                    // Storing  JSON item in a Variable
                    String nID = String.valueOf(c.getInt(TAG_ID));
                    String sName = c.getString(TAG_NAME);
                    String sTS = c.getString(TAG_DATE);
                    String sPicture = c.getString(TAG_ICON);
                    String token = c.getString("token");
                    session.setJWT(token);
                    // Adding value HashMap key => value for displaying in a ListView
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_LASTUPDATE, sLastUpdate);
                    map.put(TAG_ID, nID);
                    map.put(TAG_NAME, sName);
                    map.put(TAG_DATE, sTS);
                    map.put(TAG_ICON, sPicture);
                    categorylist.add(map);

                    //session.setCatID(Integer.parseInt(nID));
                }
                list=(ListView) findViewById(R.id.listCategory);
                adapter = new CatListAdapter(SalesActivity.this, categorylist, SalesActivity.this);
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
                if(pDialog != null || pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                //Toast.makeText(mContext, "Problem getting data:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void findp(){
        layout.removeAllViews();
        new getProducts().execute();
    }

    private class getProducts extends AsyncTask<String, Integer, JSONObject> {
        //private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*try {
                pDialog = new ProgressDialog(mContext);
                pDialog.setMessage("Sedang memproses...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            } catch (Exception e) {

            }*/
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

            //pDialog.dismiss();
            try {
                boolean error = json.getBoolean("error");

                if (!error) {

                    //String id = json.getString("id");
                    String returnmessage = json.getString("msg");
                    JSONArray params = json.getJSONArray("details");
                    if(params.length() > 0){
                        //write.execSQL("DELETE FROM fHouses");
                    }
                    //Toast.makeText(getActivity().getApplicationContext(),"jumlah json: " + params.length(), Toast.LENGTH_LONG).show();

                    for(int i=0; i<params.length(); i++) {
                        JSONObject obj = params.getJSONObject(i);
                        /*int id_house = Integer.parseInt(obj.getString("house_id"));
                        String name = obj.getString("name");
                        String status = obj.getString("status");
                        double longitude = Double.parseDouble(obj.getString("long"));
                        double latitude = Double.parseDouble(obj.getString("lat"));
                        String datetime = obj.getString("datetime");
                        int chick_num = Integer.parseInt(obj.getString("chick_num"));
                        String lastupdate = obj.getString("lastupdate");
                        int group_id = Integer.parseInt(obj.getString("grp_id"));*/
                        final int id = Integer.parseInt(obj.getString("prod_id"));
                        final String name = obj.getString("prod_name");
                        String code = obj.getString("prod_code");
                        String price_buy = obj.getString("prod_price_buy");
                        final String price_sell = obj.getString("prod_price_sell");
                        final String updated_date = obj.getString("updated_date");
                        final int category_id = Integer.parseInt(obj.getString("category_id"));
                        final String photo = obj.getString("prod_photo");

                        if(code.equals("") || code.equals("null")){
                            code = "";
                        }
                        if(price_buy.equals("") || price_buy.equals("null")){
                            price_buy = "0";
                        }

                        //Log.d("kasandra debug", "INSERT INTO m_products VALUES(" + id + ", '" + name + "', '" + code + "', " + price_buy + ", " + price_sell + ", " + category_id + ", '" + updated_date + "', '" + photo + "');");
                        Cursor c = read.rawQuery("SELECT prod_id FROM m_products WHERE prod_id="+id, null);
                        int rows = c.getCount();
                        c.moveToFirst();
                        if(rows == 0) {
                            write.execSQL("INSERT INTO m_products VALUES(" + id + ", '" + name + "', '" + code + "', " + price_buy + ", " + price_sell + ", " + category_id + ", '" + updated_date + "', '" + photo + "');");
                        }
                        /*for (int z = 0; z < rows; z++) {
                            Toast.makeText(getBaseContext(), "ID = "+c.getString(0)+" - "+id, Toast.LENGTH_SHORT).show();
                            if(!(c.getString(0).equals(String.valueOf(id)))) {
                                write.execSQL("INSERT INTO m_products VALUES(" + id + ", '" + name + "', '" + code + "', " + price_buy + ", " + price_sell + ", " + category_id + ", '" + updated_date + "', '" + photo + "');");
                            }
                            c.moveToNext();
                        }*/

                        LinearLayout LL = new LinearLayout(getApplicationContext());
                        LL.setOrientation(LinearLayout.VERTICAL);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            LL.setBackground(getResources().getDrawable(R.drawable.img_border));
                        }
                        //LL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        //LL.setWeightSum(6f);
                        LL.setLayoutParams(LLParams);

                        ImageView imgView = new ImageView(getApplicationContext());
                        imgView.setId(i+1);
                        imgView.setLayoutParams(new TableRow.LayoutParams(250, 250));
                        imgView.setPadding(5,5,5,5);
                        imgView.setImageDrawable(getResources().getDrawable(R.drawable.imgview_bg));
                        //new DownloadImageTask(imgView).execute("https://kasandra.biz/images/"+obj.getString("prod_photo"));
						Glide.with(getApplicationContext()).load("https://kasandra.biz/images/"+obj.getString("prod_photo")).into(imgView);
                        
                        /*View v = new View(getApplicationContext());
                        v.setLayoutParams(new LinearLayout.LayoutParams(200,3));
                        v.setBackgroundColor(getResources().getColor(R.color.lightblue));*/

                        TextView txtName = new TextView(getApplicationContext());
                        txtName.setId(i+1);
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
                                Snackbar.make(view, name+" - Rp."+df.format(Double.parseDouble(price_sell)), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                                /*double tp = session.nSubTotal();
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
                                */
                                //table_layout.removeAllViews();
                                //table_layout1.removeAllViews();
                                write.execSQL("INSERT INTO m_transactions (prod_id, qty, datetime) VALUES(" + id + ", 1, datetime('now', 'localtime'));");
                                showItem();
                            }
                        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new populateCatList().execute();
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

    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(getApplicationContext());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
