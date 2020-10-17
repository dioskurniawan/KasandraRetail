package id.kasandra.retail;

/**
 * Created by Joko on 21/04/2016.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    private static final int RC_SIGN_IN = 9001;
    private ProgressDialog mProgressDialog;

    private GoogleApiClient mGoogleApiClient;

    private Toolbar toolbar;
    private EditText inputFullName, inputEmail, inputPassword, inputCaptcha;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword, inputLayoutCaptcha;
    private Button btnSignUp;
    private Button btnLinkToSignin;
    private String sProvider, sMCC, sMNC, sAddress, sIMEI;
    private double nLat, nLong;
    private int nLAC;
    private int nCID;
    private static final int MY_PERMISSIONS_1 = 0;
    private static final int MY_PERMISSIONS_2 = 1;
    private String fullname;
    private String email;
    private String password;
    String personName, personEmail, personId;
    final Handler handler4 = new Handler();
    final Handler handler7 = new Handler();
    boolean internetAvailable = false;

    ImageView im;
    Button btn;
    TextView ans;
    Captcha c;
    private SessionManager session;

    public static String URL_LOGIN = "https://kasandra.biz/appdata/loginapp_dev.php";
    TextView tvc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Session manager

        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this, LoginClientActivity.class);
            startActivity(intent);
            finish();
        }

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

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
        setGoogleButtonText(signInButton, getResources().getString(R.string.register_text));

        toolbar = (Toolbar) findViewById(R.id.toolbar_reg);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(getResources().getString(R.string.labelregisteruser));

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutCaptcha = (TextInputLayout) findViewById(R.id.input_layout_captcha);
        inputFullName = (EditText) findViewById(R.id.input_fullname);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputCaptcha = (EditText) findViewById(R.id.input_captcha);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        //tvc = (TextView) findViewById(R.id.textCaptcha);

        inputFullName.addTextChangedListener(new MyTextWatcher(inputFullName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        inputCaptcha.addTextChangedListener(new MyTextWatcher(inputCaptcha));

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable(){
                    public void run() {

                        new isOnline().execute("");

                        handler7.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(internetAvailable){
                                    submitForm();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                                }
                            }
                        }, 2000);
                    }
                });
            }
        });

        btnLinkToSignin = (Button) findViewById(R.id.btnLinkToSignIn);

        // Link to Register Screen
        btnLinkToSignin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        im = (ImageView)findViewById(R.id.imageView1);
        btn = (Button)findViewById(R.id.button1);
        //ans = (TextView)findViewById(R.id.textView1);

        //c = new MathCaptcha(300, 100, MathCaptcha.MathOptions.PLUS_MINUS_MULTIPLY);
        c = new TextCaptcha(300, 100, 5, TextCaptcha.TextOptions.NUMBERS_AND_LETTERS);
        im.setImageBitmap(c.image);
        im.setLayoutParams(new LinearLayout.LayoutParams(c.width, c.height));
        //tvc.setText(c.answer);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //c = new MathCaptcha(300, 100, MathCaptcha.MathOptions.PLUS_MINUS_MULTIPLY);
                c = new TextCaptcha(300, 100, 5, TextCaptcha.TextOptions.NUMBERS_AND_LETTERS);
                im.setImageBitmap(c.image);
                im.setLayoutParams(new LinearLayout.LayoutParams(c.width, c.height));
                //tvc.setText(c.answer);
                //ans.setText(c.answer);
            }
        });
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

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)) {

                    // Should we show an explanation?
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) && (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION))){

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(RegisterActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_2);

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
            //updateUI(true);
            /*Intent intent = new Intent(RegisterActivity.this, PeternakRegActivity.class);
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
            mProgressDialog = new ProgressDialog(this);
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
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        if (!validateCaptcha()) {
            return;
        }

        fullname = inputFullName.getText().toString().trim();
        email = inputEmail.getText().toString().trim();
        password = inputPassword.getText().toString().trim();

        // Check for empty data in the form
        if (!fullname.isEmpty() && !email.isEmpty() && !password.isEmpty()) {

            handler4.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkLogin(fullname, email, password);
                }
            }, 1000);
            // login user
            /*if(!isInternetOn()) {
                Toast.makeText(getApplicationContext(), R.string.no_internet1, Toast.LENGTH_LONG).show();
            } else {*/
                /*try {
                    if (!isConnected()) {
                        Toast.makeText(getApplicationContext(), R.string.no_internet2, Toast.LENGTH_LONG).show();
                    } else {
                        checkLogin(fullname, email, password);
                        //Toast.makeText(getApplicationContext(), "Internet access", Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("fao", "fao debug problem getting data:" + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("fao", "fao debug problem getting data:" + e.getMessage());
                }*/
            //}
        } else {
            // Prompt user to enter credentials
            Toast.makeText(getApplicationContext(),
                    "Mohon isi seluruh kolom, Nama, Email, dan Kata Sandi", Toast.LENGTH_LONG)
                    .show();
        }
        //Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
    }

    private boolean validateName() {
        if (inputFullName.getText().toString().trim().isEmpty()) {
            //try {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            /*} catch (Exception e) {
                // do nothing?
            }*/
            requestFocus(inputFullName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

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

    private boolean validateCaptcha() {
        //Toast.makeText(getApplicationContext(), inputCaptcha.getText().toString().trim() + c.answer, Toast.LENGTH_LONG).show();
        if (inputCaptcha.getText().toString().trim().isEmpty()) {
            //try {
            inputLayoutCaptcha.setError(getString(R.string.err_msg_captcha));
            /*} catch (Exception e) {
                // do nothing?
            }*/
            requestFocus(inputCaptcha);
            return false;
        } else if (!inputCaptcha.getText().toString().trim().equals(c.answer)) {
            //try {
            inputLayoutCaptcha.setError("Jawaban yang Anda masukkan salah, silakan coba kembali");
            /*} catch (Exception e) {
                // do nothing?
            }*/
            requestFocus(inputCaptcha);
            return false;
        } else {
            inputLayoutCaptcha.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
                case R.id.input_fullname:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
                case R.id.input_captcha:
                    validateCaptcha();
                    break;
            }
        }
    }

    private void checkLogin(final String fullname, final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        //mProgressDialog.setMessage("Logging in ...");
        //showProgressDialog();
        //Toast.makeText(getApplicationContext(), email + "!" + password, Toast.LENGTH_LONG).show();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(RegisterActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_1);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                new sendData().execute(fullname + "!" + email + "!" + password);
                showProgressDialog();
            }
        } else {
            new sendData().execute(fullname + "!" + email + "!" + password);
            showProgressDialog();
        }

    }

    private class sendData extends AsyncTask<String, Integer, JSONObject> {
        private boolean bAddressFound = false;
        private NetworkLocator netLoc1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            netLoc1 = new NetworkLocator(getApplicationContext());
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            String userpass = arg0[0];
            String extractString = userpass;
            String[] result = extractString.split("!");

            int nRetry = 0;
            try {
                //int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                //        Manifest.permission.READ_PHONE_STATE);
                // Here, thisActivity is the current activity

                //send to server
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("fullname", result[0]);
                jsonObject.put("emailaddr", result[1]);
                jsonObject.put("password", result[2]);

                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl(URL_LOGIN, params);
                Log.d("fleetm", "fleetm debug : jsonObject=" + jsonObject.toString());

                return json;
            } catch (Exception e) {
                Log.d("fleetm", "fleetm debug err3: " + e.getMessage());
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
                    String user_passwd = grp.getString("password");

                    //String grp_name = grp.getString("group");

                    //Toast.makeText(getApplicationContext(),returnmessage, Toast.LENGTH_LONG).show();

                    session.setLogin(true);
                    session.setUserEmail(user_email);
                    session.setUserID(uid);
                    session.setUserName(user_fullname);
                    session.setPasswd(user_passwd);

                    hideProgressDialog();
                    // Launch main activity
                    Intent intent = new Intent(RegisterActivity.this, LoginClientActivity.class);
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    new sendData().execute(fullname + "!" + email + "!" + password);

                    showProgressDialog();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_2: {
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

    private class checkGoogleAcc extends AsyncTask<String, Integer, JSONObject> {
        //private boolean bAddressFound = false;
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

            int nRetry = 0;
            try {
                /*netLoc1.getLocation();
                sMCC = String.valueOf(netLoc1.getMCC());
                sMNC = String.valueOf(netLoc1.getMNC());
                nLAC = netLoc1.getLAC();
                nCID = netLoc1.getCELLID();
                sIMEI = netLoc1.findDeviceID();
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
                jsonObject.put("regpersonname", result[0]);
                jsonObject.put("regpersonemail", result[1]);
                /*jsonObject.put("password", result[2]);
                jsonObject.put("lat", nLat);
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
                Log.d("fleetm", "fleetm debug : jsonObject=" + jsonObject.toString());

                return json;
            } catch (Exception e) {
                Log.d("fleetm", "fleetm debug err3: " + e.getMessage());
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

                    //String grp_name = grp.getString("group");

                    //Toast.makeText(getApplicationContext(),returnmessage, Toast.LENGTH_LONG).show();

                    session.setLogin(true);
                    session.setUserEmail(user_email);
                    session.setUserID(uid);
                    session.setUserName(user_fullname);

                    hideProgressDialog();
                    // Launch main activity
                    Intent intent = new Intent(RegisterActivity.this, LoginClientActivity.class);
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

    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    Intent i = new Intent(getApplicationContext(),
                            LoginActivity.class);
                    startActivity(i);
                    finish();
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}

