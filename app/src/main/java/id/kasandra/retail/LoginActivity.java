package id.kasandra.retail;

/**
 * Created by Joko on 21/04/2016.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import id.kasandra.retail.welcome.WelcomeScreenHelper;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private ProgressDialog mProgressDialog;

    private GoogleApiClient mGoogleApiClient;

    private Toolbar toolbar;
    private EditText inputName, inputEmail, inputPassword;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword;
    private Button btnSignIn;
    private Button btnLinkToRegister;
    private SessionManager session;
    private String sProvider, sMCC, sMNC, sAddress, sIMEI;
    private double nLat, nLong;
    private int nLAC;
    private int nCID;
    private static final int MY_PERMISSIONS_REQUEST = 0;
    final Handler handler7 = new Handler();
    final Handler handler4 = new Handler();
    int n4 = 0;
    boolean isLoading4 = false;
    //private NetworkLocator netLoc1;
    private String personName, personEmail, personId;
    String email;
    String password;
    private WelcomeScreenHelper sampleWelcomeScreen;
    boolean internetAvailable = false;
    double screenInches;

    public static String URL_LOGIN = "https://kasandra.biz/appdata/loginapp_dev.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session manager
        session = new SessionManager(getApplicationContext());

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        int dens=dm.densityDpi;
        double wi=(double)width/(double)dens;
        double hi=(double)height/(double)dens;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi,2);
        screenInches = Math.sqrt(x+y);
        //if(session.isLoggedIn()) {
            // The welcome screen for this app (only one that automatically shows)
            sampleWelcomeScreen = new WelcomeScreenHelper(this, SampleWelcomeActivity.class);
            sampleWelcomeScreen.show(savedInstanceState);
        //}

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        //findViewById(R.id.sign_out_button).setOnClickListener(this);
        //findViewById(R.id.disconnect_button).setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        // [END customize_button]
        setGoogleButtonText(signInButton, getResources().getString(R.string.signin_text));

        new isOnline().execute("");
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, LoginClientActivity.class);
            startActivity(intent);
            finish();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_sign);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(getResources().getString(R.string.labelloginuser));

        //inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        //inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email1);
        inputPassword = (EditText) findViewById(R.id.input_password1);
        btnSignIn = (Button) findViewById(R.id.btn_signin);

        //inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(screenInches < 6) {
                    showMessage("Informasi", getResources().getString(R.string.screensize));
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            new isOnline().execute("");

                            handler7.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (internetAvailable) {
                                        submitForm();
                                    } else {
                                        Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                                    }
                                }
                            }, 3000);
                        }
                    });
                }
            }
        });

        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegister);

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(screenInches < 6) {
                    showMessage("Informasi", getResources().getString(R.string.screensize));
                } else {
                    Intent i = new Intent(getApplicationContext(),
                            DisclaimerActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);

        MenuItem client = menu.findItem(R.id.action_client);
        client.setVisible(false);
        MenuItem outlet = menu.findItem(R.id.action_outlet);
        outlet.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    protected void setGoogleButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            /*if(!isInternetOn()) {
                Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
            } else {*/
            new isOnline().execute("");
            if(internetAvailable){
                handleSignInResult(result);
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
            }
                /*try {
                    if (!isConnected()) {
                        Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                    } else {
                        handleSignInResult(result);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("fao", "fao debug problem getting data:" + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("fao", "fao debug problem getting data:" + e.getMessage());
                }*/
            //}
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
            personId = acct.getId();
            //Toast.makeText(getApplicationContext(), personName + "!" + personEmail, Toast.LENGTH_SHORT).show();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)) {

                    // Should we show an explanation?
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) && (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION))){

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(LoginActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    showProgressDialog();
                    new checkGoogleAcc().execute(personName + "!" + personEmail);
                }
            } else {
                showProgressDialog();
                new checkGoogleAcc().execute(personName + "!" + personEmail);
            }
            //checkGoogleAcc(personName, personEmail);
            //session.setLogin(true);
            //session.setUserEmail(personEmail);

            //updateUI(true);
            /*Intent intent = new Intent(SignInActivity.this, PeternakRegActivity.class);
            startActivity(intent);
            finish();*/
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(new ContextThemeWrapper(LoginActivity.this, android.R.style.Theme_Holo_Light_Dialog));
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(LoginActivity.this, android.R.style.Theme_Holo_Light_Dialog));
                builder.setTitle("Syarat & Ketentuan");
                //builder.setMessage("Dengan mempergunakan layanan KASANDRA, Anda dianggap telah memahami dan menyetujui seluruh syarat & ketentuan yang tertera di bawah ini:\n\n1. Layanan KASANDRA adalah layanan yang disediakan oleh PT Intelligence Dynamics.\n\n2. Layanan KASANDRA disediakan kepada Anda secara bebas biaya dalam kondisi “apa adanya”. Segala akibat yang disebabkan dari penggunaan layanan ini bukan tanggung jawab PT Intelligence Dynamics.\n\n3. Seluruh hak cipta yang terkait dengan layanan KASANDRA adalah milik PT Intelligence Dynamics. Anda tidak diperkenankan menyalin, mengubah, menghilangkan isi, tampilan, logo dan gambar untuk keperluan komersial maupun non-komersial tanpa ijin tertulis dari PT Intelligence Dynamics\n\n4. Anda mempercayakan data yang Anda masukkan dalam layanan ini kepada PT Intelligence Dynamics. Keamanan data pribadi Anda dijamin oleh PT Intelligence Dynamics dan tidak akan diserahkan kepada pihak lain manapun.\n\n5. Beberapa konten yang disajikan dalam layanan Petelur.ID bisa berasal dari pihak lain, termasuk iklan dan konten komersial lainnya. Hak cipta dari konten yang berasal dari luar tetap menjadi milik pemegang hak cipta masing-masing.");
                builder.setMessage(getResources().getString(R.string.labeldisclaimer));
                builder.setPositiveButton("Setuju", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(screenInches < 6) {
                                    showMessage("Informasi", getResources().getString(R.string.screensize));
                                } else {
                                    signIn();
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

                AlertDialog alertdialog = builder.create();
                alertdialog.setCanceledOnTouchOutside(false);
                alertdialog.show();

                Button buttonbackground = alertdialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                //buttonbackground.setBackgroundColor(Color.rgb(68,137,254));
                buttonbackground.setTextColor(Color.RED);

                Button buttonbackground1 = alertdialog.getButton(DialogInterface.BUTTON_POSITIVE);
                buttonbackground1.setBackgroundColor(Color.rgb(230,33,41));
                buttonbackground1.setTextColor(Color.WHITE);
                //showMessage("Syarat & Ketentuan", "Dengan mempergunakan layanan Petelur.ID, Anda dianggap telah memahami dan menyetujui seluruh syarat &amp; ketentuan yang tertera di bawah ini:\\n\\n1. Layanan Petelur.ID adalah layanan yang disediakan oleh PT Intelligence Dynamics.\\n\\n2. Layanan Petelur.ID disediakan kepada Anda secara bebas biaya dalam kondisi “apa adanya”. Segala akibat yang disebabkan dari penggunaan layanan ini bukan tanggung jawab PT Intelligence Dynamics.\\n\\n3. Seluruh hak cipta yang terkait dengan layanan Petelur.ID adalah milik PT Intelligence Dynamics. Anda tidak diperkenankan menyalin, mengubah, menghilangkan isi, tampilan, logo dan gambar untuk keperluan komersial maupun non-komersial tanpa ijin tertulis dari PT Intelligence Dynamics\\n\\n4. Anda mempercayakan data yang Anda masukkan dalam layanan ini kepada PT Intelligence Dynamics. Keamanan data pribadi Anda dijamin oleh PT Intelligence Dynamics dan tidak akan diserahkan kepada pihak lain manapun.\\n\\n5. Beberapa konten yang disajikan dalam layanan Petelur.ID bisa berasal dari pihak lain, termasuk iklan dan konten komersial lainnya. Hak cipta dari konten yang berasal dari luar tetap menjadi milik pemegang hak cipta masing-masing.");
                break;
            /*case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;*/
        }
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        email = inputEmail.getText().toString().trim();
        password = inputPassword.getText().toString().trim();

        // Check for empty data in the form
        if (!email.isEmpty() && !password.isEmpty()) {

            handler4.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkLogin(email, password);
                }
            }, 1000);
            // login user
            /*if(!isInternetOn()) {
                Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
            } else {*/

            //}
        } else {
            // Prompt user to enter credentials
            Toast.makeText(getApplicationContext(),
                    "Please enter the username and password!", Toast.LENGTH_LONG)
                    .show();
        }
        //Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
    }

    private boolean validateEmail() {
        email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            //try {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            /*} catch (Exception e) {
                // do nothing?
            }*/
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            //try {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            /*} catch (Exception e) {
                // do nothing?
            }*/
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String sEmail) {
        return !TextUtils.isEmpty(sEmail) && android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
                /*case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;*/
            }
        }
    }

    private void checkLogin(final String sEmail2, final String sPassword2) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        //mProgressDialog.setMessage("Logging in ...");
        showProgressDialog();
        //Toast.makeText(getApplicationContext(), email + "!" + password, Toast.LENGTH_LONG).show();
        new sendData().execute(sEmail2 + "!" + sPassword2);
    }

    final Runnable handlerTask4 = new Runnable() {

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            n4++;
            //Log.d("debug","n4="+n4);
            if (n4 >= 5) {
                if (isLoading4) {
                    checkLogin(email, password);
                    isLoading4 = false;
                    n4 = 0;
                    Thread.currentThread().interrupt();
                }
            }
            handler4.postDelayed(handlerTask4, 1000);
        }
    };

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
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", result[0]);
                jsonObject.put("password", result[1]);

                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl(URL_LOGIN, params);
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
                //JSONObject jObj = new JSONObject(json);
                boolean error = json.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // user successfully logged in
                    // Create login session
                    hideProgressDialog();

                    int uid = json.getInt("uid");
                    String returnmessage = json.getString("msg");
                    JSONObject grp = json.getJSONObject("user");
                    String user_name = grp.getString("name");
                    String user_email = grp.getString("email");
                    String user_fullname = grp.getString("fullname");
                    String user_passwd = grp.getString("password");
                    String user_client = grp.getString("client");

                    //String grp_name = grp.getString("group");

                    //Toast.makeText(getApplicationContext(),returnmessage, Toast.LENGTH_LONG).show();

                    session.setLogin(true);
                    session.setUserEmail(user_email);
                    session.setUserID(uid);
                    session.setUserName(user_name);
                    session.setFullName(user_fullname);
                    session.setPasswd(user_passwd);
                    session.setClientName(user_client);

                    // Launch main activity
                    Intent intent = new Intent(LoginActivity.this, LoginClientActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Error in login. Get the error message
                    hideProgressDialog();
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
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private class checkGoogleAcc extends AsyncTask<String, Integer, JSONObject> {
        private boolean bAddressFound = false;
        //NetworkLocator netLoc1;

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

            int nRetry = 0;
            try {
                /*netLoc1.getLocation();
                sMCC = String.valueOf(netLoc1.getMCC());
                sMNC = String.valueOf(netLoc1.getMNC());
                nLAC = netLoc1.getLAC();
                nCID = netLoc1.getCELLID();
                //sIMEI = netLoc1.findDeviceID();
                nLong = netLoc1.getLongitude();
                nLat = netLoc1.getLatitude();

                while (!bAddressFound && ++nRetry <= 3) {   //retry 3 times if address not found
                    sAddress = netLoc1.getAddress(nLong, nLat);
                    if (!sAddress.equals("Address not found")) {
                        bAddressFound = true;
                    } else {
                        Thread.sleep(3000);
                    }
                }*/
                //send to server
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("personname", result[0]);
                jsonObject.put("personemail", result[1]);
                //jsonObject.put("password", result[2]);
                /*jsonObject.put("lat", nLat);
                jsonObject.put("long", nLong);
                jsonObject.put("mcc", sMCC);
                jsonObject.put("mnc", sMNC);
                jsonObject.put("lac", nLAC);
                jsonObject.put("cellid", nCID);
                jsonObject.put("address", sAddress);
                jsonObject.put("imei", sIMEI);*/

                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl(URL_LOGIN, params);
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
                //JSONObject jObj = new JSONObject(json);
                boolean error = json.getBoolean("error");

                // Check for error node in json
                if (!error) {
                    // user successfully logged in
                    // Create login session
                    //hideProgressDialog();

                    int uid = json.getInt("uid");
                    String returnmessage = json.getString("msg");
                    JSONObject grp = json.getJSONObject("user");
                    String user_name = grp.getString("name");
                    String user_email = grp.getString("email");
                    String user_fullname = grp.getString("fullname");
                    String user_client = grp.getString("client");

                    //String grp_name = grp.getString("group");

                    //Toast.makeText(getApplicationContext(),returnmessage, Toast.LENGTH_LONG).show();

                    session.setLogin(true);
                    session.setUserEmail(user_email);
                    session.setUserID(uid);
                    session.setUserName(user_name);
                    session.setFullName(user_fullname);
                    session.setClientName(user_client);

                    //hideProgressDialog();
                    // Launch main activity
                    Intent intent = new Intent(LoginActivity.this, LoginClientActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Error in login. Get the error message
                    //hideProgressDialog();
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
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    //new sendData().execute(fullname + "!" + email + "!" + password);

                    showProgressDialog();
                    new checkGoogleAcc().execute(personName + "!" + personEmail);
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
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}

