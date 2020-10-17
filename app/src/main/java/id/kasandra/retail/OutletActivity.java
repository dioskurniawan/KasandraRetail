package id.kasandra.retail;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

//import com.getbase.floatingactionbutton.FloatingActionButton;

/**
 * Created by Joko on 3/21/2017.
 */

public class OutletActivity extends AppCompatActivity {

    private ListView listView;
    private static final int MY_PERMISSIONS_1 = 0;

    private int nClientId = 0;
    private SessionManager session;
    SQLiteHandler db;
    SQLiteDatabase write;
    SQLiteDatabase read;
    private ProgressDialog pDialog;
    final Handler handler = new Handler();
    int n = 0;
    public static String URL_INPUT = "https://kasandra.biz/appdata/getdata_dev2.php";
    private OutletListAdapter listAdapter;
    private List<GetItem> getItems;
    TextView toolbar_title;
    TextView txtEmpty;
    static boolean internetAvailable = false;
    private int trans_detail_server, trans_detail_local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText("Langkah 3/3 : Pilih Outlet");

        // Session manager
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());
        write = db.getWritableDatabase();
        read = db.getReadableDatabase();

        if(session.screenSize() < 7.9) {
            toolbar_title.setTextSize(20);
        } else {
            toolbar_title.setTextSize(25);
        }
        internetAvailable = session.isInternetAvailable();
        txtEmpty = (TextView) findViewById(R.id.empty);
        pDialog = new ProgressDialog(new ContextThemeWrapper(OutletActivity.this, android.R.style.Theme_Holo_Light_Dialog));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if(session.nOutlet_ID() > 0) {
            toolbar.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            txtEmpty.setText("");
            session.setWiFi("WiFi disconnected");
            //gettrans();
            Intent intent = new Intent(OutletActivity.this, MainActivity.class);
            intent.putExtra("id_main", 0);
            startActivity(intent);
            finish();
        }

        if (nClientId == 0) {
            nClientId = session.nClient_ID();
        }
        // check nClientId from persistence store
        if (session.nClient_ID() != -999) {
            nClientId = session.nClient_ID();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadOutlet();
                showMessage("Informasi","Untuk menambahkan lokasi Outlet baru silakan melalui website kasandra.biz");
            }
        });

        listView = (ListView) findViewById(R.id.listCategory);

        getItems = new ArrayList<GetItem>();

        listAdapter = new OutletListAdapter(OutletActivity.this, getItems);

        if(listView.getFooterViewsCount() > 0){
            listView.removeAllViews();
        }

        if(listView.getFooterViewsCount() > 0){
            listView.removeAllViews();
        }
        listView.setAdapter(listAdapter);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(OutletActivity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                } else {
                    ActivityCompat.requestPermissions(OutletActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_1);
                }
            } else {
                new isOnline().execute("");
                if(internetAvailable){
                    if(session.nOutlet_ID() > 0){
                    } else {
                        loadOutlet();
                    }
                } else {
                }
            }
        } else {
            new isOnline().execute("");
            if(internetAvailable){
                if(session.nOutlet_ID() > 0){
                } else {
                    loadOutlet();
                }
            } else {
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);

        MenuItem user = menu.findItem(R.id.action_user);
        user.setVisible(false);
        MenuItem client = menu.findItem(R.id.action_client);
        client.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadOutlet() {
        getItems.clear();
        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
        // We first check for cached request
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        //Cache cache = AppController.getInstance().getRequestQueue().getCache().get(URL_NEWS).serverDate;
        //AppController.getInstance().getRequestQueue().getCache().invalidate(URL_NEWS+String.valueOf(page), true);
        cache2.clear();
        Cache.Entry entry2 = cache2.get(URL_INPUT+"?view_outlet=" + nClientId);

        if (entry2 != null) {
            // fetch the data from cache
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
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq2 = new JsonObjectRequest(Request.Method.GET,
                    URL_INPUT+"?view_outlet=" + nClientId, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d("kasandra debug", "Response: " + response.toString());
                    parseJsonFeed(response);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("kasandra debug", "Error: " + error.getMessage());
                }
            });
			/*jsonReq.setRetryPolicy(new DefaultRetryPolicy(5000,
					DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
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
            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq2);
        }
    }

    private void parseJsonFeed(JSONObject response) {
        try {

            JSONArray feedArray = response.getJSONArray("details");
            int rows = feedArray.length();
            if(rows > 0){
                txtEmpty.setVisibility(View.GONE);
            } else {
                txtEmpty.setVisibility(View.VISIBLE);
            }

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                GetItem item = new GetItem();
                item.setId(feedObj.getInt("out_id"));
                item.setName(feedObj.getString("name"));
                item.setCode(feedObj.getString("addr"));

                getItems.add(item);
            }

            /*if(feedArray.length() < 20) {
                if(list.getFooterViewsCount() > 0){
                    btnLoadMore.setVisibility(View.GONE);
                    list.removeFooterView(btnLoadMore);
                    //Log.d("footer remove1", String.valueOf(listView.getFooterViewsCount()));
                }
            }*/
            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void gettrans() {
        try {
                pDialog.setMessage("Loading...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
                /*isLoading = true;
                n=0;
                new Thread(handlerTask).start();*/
            //handlerTask.run();
        } catch (Exception e) {

        }
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(URL_INPUT+"?transdetail=" + session.nOutlet_ID());

        if (entry2 != null) {
            try {
                String data = new String(entry2.data, "UTF-8");
                try {
                    parseJsonTrans(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            JsonObjectRequest jsonReq2 = new JsonObjectRequest(Request.Method.GET,
                    URL_INPUT+"?transdetail=" + session.nOutlet_ID(), null, new Response.Listener<JSONObject>() {
                //"https://kasandra.biz/wbagenpass.json", null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    parseJsonTrans(response);
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

    private void parseJsonTrans(JSONObject response) {
        try {

            String returnmessage = response.getString("msg");
            JSONArray params = response.getJSONArray("details");
            for (int i = 0; i < params.length(); i++) {
                JSONObject obj = params.getJSONObject(i);
                trans_detail_server = obj.getInt("count");
            }

            Cursor cursorDet = read.rawQuery("select count(*) from m_transactions_detail;", null);
            int rows = cursorDet.getCount();
            if(rows > 0){
                cursorDet.moveToFirst();
                trans_detail_local = cursorDet.getInt(0);
            }
            cursorDet.close();

            if(trans_detail_local == trans_detail_server) {
                Intent intent = new Intent(OutletActivity.this, MainActivity.class);
                intent.putExtra("id_main", 1);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(OutletActivity.this, MainActivity.class);
                intent.putExtra("id_main", 0);
                startActivity(intent);
                finish();
            }

            Log.d("transcount",trans_detail_local+" - "+trans_detail_server);
            pDialog.dismiss();

        } catch (JSONException e) {
            pDialog.dismiss();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //new populateProductList().execute();
                    loadOutlet();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
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

    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(new ContextThemeWrapper(OutletActivity.this, android.R.style.Theme_Holo_Light_Dialog));
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
