package id.kasandra.retail;

/**
 * Created by Joko on 10/05/2017.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class LoginClientActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private ProgressDialog mProgressDialog;

    private Toolbar toolbar;
    private EditText inputName, inputPassword;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword;
    private Button btnConfirm;
    private Button btnLinkCancel;
    private SessionManager session;
    String m2tTimeZoneIs;
    private GoogleApiClient mGoogleApiClient;
    private String name, fullname, email, password;
    SQLiteHandler db;
    SQLiteDatabase write;
    SQLiteDatabase read;
    private static final int MY_PERMISSIONS_1 = 0;
    final Handler handler7 = new Handler();
    boolean internetAvailable = false;
    String clientname, clientpass, clientfullname, clientemail;
    Typeface font1;

    public static String URL_LOGIN = "https://kasandra.biz/appdata/loginapp_dev.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_client);
        // Session manager
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());
        write = db.getWritableDatabase();
        read = db.getReadableDatabase();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        font1 = Typeface.createFromAsset(getAssets(), "quattrocentobold.ttf");
        toolbar = (Toolbar) findViewById(R.id.toolbar_grp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(getResources().getString(R.string.labelregclient));

        TextView txtClient = (TextView) findViewById(R.id.text);
        TextView txtClient2 = (TextView) findViewById(R.id.text2);
        txtClient.setTypeface(font1);
        txtClient2.setTypeface(font1);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputName = (EditText) findViewById(R.id.input_client);
        inputPassword = (EditText) findViewById(R.id.input_password_client);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        btnLinkCancel = (Button) findViewById(R.id.btnLinkCancel);

        /*if(!isInternetAvailable()) {
            Toast.makeText(getApplicationContext(),
                    "Failed to connect", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Connect success", Toast.LENGTH_LONG).show();
        }*/
        // Check if user is already logged in or not
        if (session.isLoggedInClient()) {
            toolbar.setVisibility(View.GONE);
            txtClient.setVisibility(View.GONE);
            txtClient2.setVisibility(View.GONE);
            inputLayoutName.setVisibility(View.GONE);
            inputLayoutPassword.setVisibility(View.GONE);
            inputName.setVisibility(View.GONE);
            inputPassword.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);
            btnLinkCancel.setVisibility(View.GONE);

            showProgressDialog(true);
            /*if(!isInternetOn()) {
                Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginClientActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {*/
            new isOnline().execute("");
            handler7.postDelayed(new Runnable() {
                @Override
                public void run() {
                        if(internetAvailable){
                            new reValidatePassword().execute("pass");
                            hideProgressDialog();
                        } else {
                            hideProgressDialog();
                            Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                            //finish();
                            Intent intent = new Intent(LoginClientActivity.this, OutletActivity.class);
                            //intent.putExtra("id_main", 0);
                            startActivity(intent);
                            finish();
                        }
                }
            }, 2000);
            //}
        }

        inputName.setText(session.sClientName());
        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable(){
                    public void run() {
                        try {
                            submitForm();
                        } catch (final Exception ex) {
                            Log.i("---","Exception in thread");
                        }
                    }
                });
            }
        });

        // Link to Register Screen
        btnLinkCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                session.clearAllData();
                signOut();
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        TimeZone tz = TimeZone.getDefault();
        Date now = new Date();
        int offsetFromUtc = tz.getOffset(now.getTime()) / 3600000;
        m2tTimeZoneIs = Integer.toString(offsetFromUtc);

        //isNetworkAvailable(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);

        MenuItem user = menu.findItem(R.id.action_user);
        user.setVisible(false);
        MenuItem outlet = menu.findItem(R.id.action_outlet);
        outlet.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        name = inputName.getText().toString().trim();
        fullname = inputName.getText().toString();
        name = name.replaceAll(" ","");
        email = session.sUserEmail();
        password = inputPassword.getText().toString().trim();

        // Check for empty data in the form
        if (!name.isEmpty() && !password.isEmpty() && !email.isEmpty()) {
            // login user
            /*if(!isInternetOn()) {
                Toast.makeText(getApplicationContext(), R.string.no_internet1, Toast.LENGTH_LONG).show();
            } else {*/
            new isOnline().execute("");
            handler7.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(getApplicationContext(), String.valueOf(internetAvailable), Toast.LENGTH_LONG).show();
                    if(internetAvailable){
                        if(internetAvailable){
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    checkLogin(name, fullname, password, email);
                                }
                            }, 1000);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                    }
                }
            }, 2000);
            //}
        } else {
            // Prompt user to enter credentials
            Toast.makeText(getApplicationContext(),
                    "Please enter the username and password!", Toast.LENGTH_LONG)
                    .show();
        }
        //Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_client));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_client:
                    validateName();
                    break;
                case R.id.input_password_client:
                    validatePassword();
                    break;
            }
        }
    }

    private void showProgressDialog(boolean cancel) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(cancel);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void checkLogin(final String name, final String fullname_, final String password_, final String email_) {
        // Tag used to cancel the request
        String tag_string_req = "req_client";

        //mProgressDialog.setMessage("Logging in ...");
        showProgressDialog(false);
        //Toast.makeText(getApplicationContext(), "timezone " + timezone, Toast.LENGTH_LONG).show();
        new sendData().execute(name + "!" + password_ + "!" + email_ + "!" + fullname_);

    }

    private class sendData extends AsyncTask<String, Integer, JSONObject> {
        //private NetworkLocator netLoc1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //netLoc1 = new NetworkLocator(getApplicationContext());
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            String userpass = arg0[0];
            String extractString = userpass;
            String[] result = extractString.split("!");

            try {
                //sImei = netLoc1.findDeviceID();
                //send to server
                clientname = result[0];
                clientpass = result[1];
                clientemail = result[2];
                clientfullname = result[3];
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("client", result[0]);
                jsonObject.put("password", result[1]);
                jsonObject.put("email", result[2]);
                jsonObject.put("fullname", result[3]);

                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl(URL_LOGIN, params);
                Log.d("fao", "fao debug : jsonObject=" + jsonObject.toString());

                return json;
            } catch (Exception e) {
                Log.d("fao", "fao debug err3: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            try {
                //JSONObject jObj = new JSONObject(json);
                boolean error = json.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // user successfully logged in
                    // Create login session
                    hideProgressDialog();

                    String uid = json.getString("uid");
                    String returnmessage = json.getString("msg");
                    JSONObject client = json.getJSONObject("user");
                    //String username = client.getString("name");
                    String client_id = client.getString("client_id");
                    String client_name = client.getString("client_name");
                    String client_fullname = client.getString("client_fullname");
                    String client_passwd = client.getString("password");
                    String isadmin = client.getString("isadmin");
                    String client_token = client.getString("token");

                    //String client_name = grp.getString("client");

                    //showMessage("Informasi", returnmessage);
                    Toast.makeText(getApplicationContext(),returnmessage, Toast.LENGTH_LONG).show();

                    session.setLoginClient(true, Integer.parseInt(client_id));
                    //session.setUserName(username);
                    session.setClientName(client_name);
                    session.setClientFullName(client_fullname);
                    session.setClientPasswd(client_passwd);
                    if(isadmin.equals("1")) {
                        session.setIsAdmin(true);
                    } else {
                        session.setIsAdmin(false);
                    }
                    session.setJWT(client_token);
                    //Toast.makeText(getApplicationContext(),String.valueOf(session.isAdmin()), Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(),"client id = " + String.valueOf(session.nGrp_ID()), Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(),"Before permission", Toast.LENGTH_LONG).show();

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                                android.Manifest.permission.READ_PHONE_STATE)
                                != PackageManager.PERMISSION_GRANTED)) {

                            // Should we show an explanation?
                            if ((ActivityCompat.shouldShowRequestPermissionRationale(LoginClientActivity.this,
                                    android.Manifest.permission.READ_PHONE_STATE))){
                            } else {
                                // No explanation needed, we can request the permission.
                                ActivityCompat.requestPermissions(LoginClientActivity.this,
                                        new String[]{android.Manifest.permission.READ_PHONE_STATE},MY_PERMISSIONS_1);
                            }
                        } else {
                            new sendAppInfo().execute(String.valueOf(session.nUserID()));
                        }
                    } else {
                        new sendAppInfo().execute(String.valueOf(session.nUserID()));
                    }
                    //Toast.makeText(getApplicationContext(),"After permission", Toast.LENGTH_LONG).show();
                    // Launch main activity
                    /*if(!session.isAdmin()) {
                        Intent intent = new Intent(LoginClientActivity.this, MainActivity.class);
                        intent.putExtra("num_of_tab", 0);
                        startActivity(intent);
                        finish();
                    } else {*/
                    Intent intent = new Intent(LoginClientActivity.this, OutletActivity.class);
                    //intent.putExtra("id_main", 0);
                    startActivity(intent);
                    finish();
                    write.execSQL("DELETE FROM m_category");
                    write.execSQL("DELETE FROM m_products");
                    write.execSQL("DELETE FROM m_transactions");
                    write.execSQL("DELETE FROM m_transactions_detail");
                    write.execSQL("DELETE FROM m_transactions_temp");
                    write.execSQL("DELETE FROM m_customers_temp");
                    /*AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(LoginClientActivity.this, android.R.style.Theme_Holo_Light_Dialog));
                    builder.setTitle("INFORMASI");
                    builder.setMessage(returnmessage)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(LoginClientActivity.this, OutletActivity.class);
									//intent.putExtra("id_main", 0);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();*/
                    //}
                } else {
                    // Error in login. Get the error message
                    hideProgressDialog();
                    String errorMsg = json.getString("error_msg");
                    String errorCode = json.getString("error_code");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                    if(errorCode.equals("CLIENT_NOT_REGISTERED")){
                        Intent intent = new Intent(LoginClientActivity.this, CreateOutletActivity.class);
                        intent.putExtra("client_name", clientname);
                        intent.putExtra("client_fullname", clientfullname);
                        intent.putExtra("client_pass", clientpass);
                        intent.putExtra("client_email", clientemail);
                        startActivity(intent);
                        finish();
                    }
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

    }

    private class reValidatePassword extends AsyncTask<String, Integer, JSONObject> {
        //private NetworkLocator netLoc1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //netLoc1 = new NetworkLocator(getApplicationContext());
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            String value = arg0[0];
            //String extractString = userpass;
            //String[] result = extractString.split("!");

            try {
                //sImei = netLoc1.findDeviceID();
                //send to server
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("recheck_client", session.nClient_ID());
                jsonObject.put("recheck_pass", session.sClientPasswd());

                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl(URL_LOGIN, params);
                Log.d("fao", "fao debug : jsonObject=" + jsonObject.toString());

                return json;
            } catch (Exception e) {
                Log.d("fao", "fao debug err3: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            try {
                //JSONObject jObj = new JSONObject(json);
                boolean error = json.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // user successfully logged in
                    // Create login session
                    hideProgressDialog();

                    String uid = json.getString("uid");
                    String returnmessage = json.getString("msg");
                    JSONObject grp = json.getJSONObject("client");
                    //String grp_id = grp.getString("grp_id");
                    String client_name = grp.getString("name");
                    String client_passwd = grp.getString("password");

                    //Toast.makeText(getApplicationContext(),returnmessage, Toast.LENGTH_LONG).show();

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                                android.Manifest.permission.READ_PHONE_STATE)
                                != PackageManager.PERMISSION_GRANTED)) {

                            // Should we show an explanation?
                            if ((ActivityCompat.shouldShowRequestPermissionRationale(LoginClientActivity.this,
                                    android.Manifest.permission.READ_PHONE_STATE))){
                            } else {
                                // No explanation needed, we can request the permission.
                                ActivityCompat.requestPermissions(LoginClientActivity.this,
                                        new String[]{android.Manifest.permission.READ_PHONE_STATE},MY_PERMISSIONS_1);
                            }
                        } else {
                            new sendAppInfo().execute(String.valueOf(session.nUserID()));
                        }
                    } else {
                        new sendAppInfo().execute(String.valueOf(session.nUserID()));
                    }
                    /*if(!session.isAdmin()) {
                        Intent intent = new Intent(LoginClientActivity.this, MainActivity.class);
                        intent.putExtra("num_of_tab", 0);
                        startActivity(intent);
                        finish();
                    } else {*/
                        Intent intent = new Intent(LoginClientActivity.this, OutletActivity.class);
						//intent.putExtra("id_main", 0);
                        startActivity(intent);
                        finish();
                    //}

                } else {
                    // Error in login. Get the error message
                    hideProgressDialog();
                    String errorMsg = json.getString("error_msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                    session.clearAllData();
                    signOut();
                    Intent i = new Intent(getApplicationContext(),
                            LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

    }

    private class sendAppInfo extends AsyncTask<String, Integer, JSONObject> {
        NetworkLocator netLoc1;
        String sIMEI;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            netLoc1 = new NetworkLocator(getApplicationContext());
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            String value = arg0[0];

            try {
                netLoc1.getLocation();
                sIMEI = netLoc1.findDeviceID();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("app_version", "1.0");
                jsonObject.put("user_id", value);
                jsonObject.put("client_id", session.nClient_ID());
                jsonObject.put("imei", sIMEI);//"35220107");
                session.setIMEI(sIMEI);

                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl(URL_LOGIN, params);
                Log.d("kasandra", "kasandra debug : jsonObject=" + jsonObject.toString());

                return json;
            } catch (Exception e) {
                Log.d("kasandra", "kasandra debug err4: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            try {
                boolean error = json.getBoolean("error");

                if (!error) {
                    String uid = json.getString("uid");
                    String returnmessage = json.getString("msg");
                    JSONObject user = json.getJSONObject("user");
                } else {
                    String errorMsg = json.getString("error_msg");
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new sendAppInfo().execute(String.valueOf(session.nUserID()));
                } else {

                }
                return;
            }
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

    public boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec(command).waitFor() == 0);
    }

    private class isOnline extends AsyncTask<String, Integer, Boolean> {

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

    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}

