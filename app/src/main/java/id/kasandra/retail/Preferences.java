/*
 * Copyright 2013 Thomas Hoffmann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package id.kasandra.retail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Set;

import id.kasandra.retail.bluetoothprint.DeviceListActivity;

public class Preferences extends PreferenceActivity {

    private final static int[] time_values = {5, 15, 30, 60, 120, 300, 600};

    private StatusPreference status;
    private AppCompatDelegate mDelegate;
    private static String sURL = "https://kasandra.biz/appdata/getdata_dev1.php";
    SessionManager session;
    BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    /**
     * whether the location settings should be enabled by the enable/disable app switch
     */
    private boolean disableLocationSettings = false;

    /*private final Handler handler = new Handler();
    private final Runnable signalUpdater = new Runnable() {
        @Override
        public void run() {
            if (status.updateSignal()) handler.postDelayed(signalUpdater, 1000);
        }
    };
    private final BroadcastReceiver stateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            handler.removeCallbacks(signalUpdater);
            status.update();
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                final NetworkInfo nwi = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (nwi == null) return;
                if (nwi.isConnected()) {
                    // seems to still take some time until NetworkInfo::isConnected returns true
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            status.update();
                        }
                    }, 2000);
                    handler.postDelayed(signalUpdater, 2000);
                }
            }
        }
    };*/

    @SuppressLint("NewApi")
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        /*boolean isEnabled = getPackageManager()
                .getComponentEnabledSetting(new ComponentName(Preferences.this, Receiver.class)) !=
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

        if (Build.VERSION.SDK_INT < 14) {
            menu.findItem(R.id.enable)
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(final MenuItem menuItem) {
                            boolean isEnabled = getPackageManager().getComponentEnabledSetting(
                                    new ComponentName(Preferences.this, Receiver.class)) !=
                                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                            changeEnableState(!isEnabled);
                            return true;
                        }
                    });
        } else {
            CompoundButton enable =
                    (CompoundButton) MenuItemCompat.getActionView(menu.findItem(R.id.enable));
            enable.setChecked(isEnabled);
            enable.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    changeEnableState(isChecked);
                }
            });
        }

        // disable initially if not checked
        if (!isEnabled) {
            enableSettings(false);
        }*/
        return true;
    }

    @SuppressWarnings("deprecation")
    private void enableSettings(final boolean enable) {
        PreferenceScreen ps = getPreferenceScreen();
        // start at 1 to skip "status" preference
        for (int i = 1; i < ps.getPreferenceCount(); i++) {
            ps.getPreference(i).setEnabled(enable);
        }
        if (enable && disableLocationSettings) {
            // disable locations again if disableLocationSettings is set
            findPreference("locations").setEnabled(false);
        }
    }

    /*private void changeEnableState(final boolean enable) {
        enableSettings(enable);
        getPackageManager()
                .setComponentEnabledSetting(new ComponentName(Preferences.this, Receiver.class),
                        enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
        if (!enable) stopService(new Intent(Preferences.this, ScreenChangeDetector.class));
        getPackageManager().setComponentEnabledSetting(
                new ComponentName(Preferences.this, ScreenChangeDetector.class),
                enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }*/

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // action bar overflow menu
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("id_main", 1);
                startActivity(intent);
                finish();
                return true;
            /*case R.id.enable:
                break;
            case R.id.action_wifi_adv:
                try {
                    startActivity(new Intent(Settings.ACTION_WIFI_IP_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } catch (Exception e) {
                    Toast.makeText(this, R.string.settings_not_found_, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_apps:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://search?q=pub:j4velin"))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } catch (ActivityNotFoundException anf) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                                "https://play.google.com/store/apps/developer?id=j4velin"))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } catch (ActivityNotFoundException anf2) {
                        Toast.makeText(this,
                                "No browser found to load https://play.google.com/store/apps/developer?id=j4velin",
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.action_donate:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://j4velin.de/donate.php"))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } catch (ActivityNotFoundException anf) {
                    Toast.makeText(this, "No browser found to load http://j4velin.de/donate.php",
                            Toast.LENGTH_LONG).show();
                }
                break;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*final CheckBoxPreference no_network_off =
                (CheckBoxPreference) findPreference("off_no_network");
        no_network_off.setSummary(getString(R.string.for_at_least,
                PreferenceManager.getDefaultSharedPreferences(this)
                        .getInt("no_network_timeout", Receiver.TIMEOUT_NO_NETWORK)));*/
        /*handler.postDelayed(signalUpdater, 1000);
        IntentFilter ifilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        ifilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(stateChangedReceiver, ifilter);*/
    }

    @Override
    public void onPause() {
        super.onPause();
        //Start.start(this);
        /*handler.removeCallbacks(signalUpdater);
        unregisterReceiver(stateChangedReceiver);*/
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        //ActionBar bar = getActionBar();
        //bar.setDisplayHomeAsUpEnabled(true);

        session = new SessionManager(this.getApplicationContext());

        addPreferencesFromResource(R.xml.preferences);

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= 23 && PermissionChecker
                .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        /*status = (StatusPreference) findPreference("status");
        status.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                WifiManager wm = (WifiManager) getApplicationContext()
                        .getSystemService(Context.WIFI_SERVICE);
                boolean connected = ((ConnectivityManager) getApplicationContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE))
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
                if (wm.isWifiEnabled() && !connected) {
                    try {
                        startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } catch (Exception e) {
                        Toast.makeText(Preferences.this, R.string.settings_not_found_,
                                Toast.LENGTH_SHORT).show();
                    }
                } else if (!wm.isWifiEnabled()) {
                    try {
                        wm.setWifiEnabled(true);
                    } catch (SecurityException ex) {
                        ex.printStackTrace();
                        Toast.makeText(Preferences.this, "No permission to enable WiFi",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } catch (Exception e) {
                        Toast.makeText(Preferences.this, R.string.settings_not_found_,
                                Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });*/

        /*EditTextPreference token = (EditTextPreference) findPreference("token");
        //String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        //token.setSummary("1848BA561E7CAB8F3174749F905363B83058CD2DFED4A5B80224C2A21C5E622A");
        if(session.sToken().equals("Token belum didefinisikan")) {
            Toast.makeText(getApplicationContext(), "Silakan inputkan Token pada menu Outlet melalui website, kemudian Sinkronisasikan aplikasi kembali.", Toast.LENGTH_LONG).show();
        }
        token.setSummary(session.sToken());*/

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        CheckBoxPreference receipt = (CheckBoxPreference) findPreference("receipt");

        receipt.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                //Log.d("MyApp", "Pref " + preference.getKey() + " changed to " + newValue.toString());
                //Toast.makeText(getApplicationContext(), "Pref " + preference.getKey() + " changed to " + newValue.toString(), Toast.LENGTH_LONG).show();
                session.setDoubleReceipt(Boolean.parseBoolean(newValue.toString()));
                return true;
            }
        });
        /*Preference myPref = findPreference( "print" );
        myPref.setOnPreferenceClickListener( new Preference.OnPreferenceClickListener(){
            public boolean onPreferenceClick( Preference pref ){
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(Preferences.this, "Device Not Support", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(Preferences.this,
                                DeviceListActivity.class);
                        startActivityForResult(connectIntent,
                                REQUEST_CONNECT_DEVICE);
                    }
                }
                return true;
            }
        });
        final CheckBoxPreference screen_off = (CheckBoxPreference) findPreference("off_screen_off");
        screen_off.setSummary(getString(R.string.for_at_least,
                prefs.getInt("screen_off_timeout", Receiver.TIMEOUT_SCREEN_OFF)));

        if (!keepWiFiOn(this)) {
            screen_off.setChecked(false);
        }

        screen_off.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                if ((Boolean) newValue) {
                    if (!keepWiFiOn(Preferences.this)) {
                        new AlertDialog.Builder(Preferences.this).setMessage(R.string.sleep_policy)
                                .setPositiveButton(R.string.adv_wifi_settings,
                                        new OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                try {
                                                    startActivity(new Intent(
                                                            Settings.ACTION_WIFI_IP_SETTINGS)
                                                            .addFlags(
                                                                    Intent.FLAG_ACTIVITY_NEW_TASK));
                                                } catch (Exception e) {
                                                    Toast.makeText(Preferences.this,
                                                            R.string.settings_not_found_,
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                .setNegativeButton(android.R.string.cancel, new OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                        return false;
                    }
                    if (android.os.Build.VERSION.SDK_INT >= 11) {
                        APILevel11Wrapper.showNumberPicker(Preferences.this, prefs, screen_off,
                                R.string.for_at_least, 1, 60,
                                getString(R.string.minutes_before_turning_off_wifi_),
                                "screen_off_timeout", Receiver.TIMEOUT_SCREEN_OFF, false);
                    } else {
                        showPre11NumberPicker(Preferences.this, prefs, screen_off,
                                R.string.for_at_least, 1, 60,
                                getString(R.string.minutes_before_turning_off_wifi_),
                                "screen_off_timeout", Receiver.TIMEOUT_SCREEN_OFF, false);
                    }
                }
                return true;
            }
        });

        findPreference("off_no_network")
                .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(final Preference preference,
                                                      final Object newValue) {
                        if ((Boolean) newValue) {
                            if (android.os.Build.VERSION.SDK_INT >= 11) {
                                APILevel11Wrapper
                                        .showNumberPicker(Preferences.this, prefs, preference,
                                                R.string.for_at_least, 1, 60, getString(
                                                        R.string.minutes_before_turning_off_wifi_),
                                                "no_network_timeout", Receiver.TIMEOUT_NO_NETWORK,
                                                false);
                            } else {
                                showPre11NumberPicker(Preferences.this, prefs, preference,
                                        R.string.for_at_least, 1, 60,
                                        getString(R.string.minutes_before_turning_off_wifi_),
                                        "no_network_timeout", Receiver.TIMEOUT_NO_NETWORK, false);
                            }
                        }
                        return true;
                    }
                });

        final CheckBoxPreference on_at = (CheckBoxPreference) findPreference("on_at");
        on_at.setTitle(
                getString(R.string.at_summary, prefs.getString("on_at_time", Receiver.ON_AT_TIME)));
        on_at.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                if ((Boolean) newValue) {
                    String[] time = prefs.getString("on_at_time", Receiver.ON_AT_TIME).split(":");
                    final TimePickerDialog dialog =
                            new TimePickerDialog(Preferences.this, new OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    prefs.edit().putString("on_at_time",
                                            hourOfDay + ":" + (minute < 10 ? "0" + minute : minute))
                                            .commit();
                                    on_at.setTitle(getString(R.string.at_summary, hourOfDay + ":" +
                                            (minute < 10 ? "0" + minute : minute)));
                                }
                            }, Integer.parseInt(time[0]), Integer.parseInt(time[1]), true);
                    dialog.setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            on_at.setChecked(false);
                        }
                    });
                    dialog.setTitle(getString(R.string.turn_wifi_on_at_));
                    dialog.show();
                }
                return true;
            }
        });

        final CheckBoxPreference off_at = (CheckBoxPreference) findPreference("off_at");
        off_at.setTitle(getString(R.string.at_summary,
                prefs.getString("off_at_time", Receiver.OFF_AT_TIME)));
        off_at.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                if ((Boolean) newValue) {
                    String[] time = prefs.getString("off_at_time", Receiver.OFF_AT_TIME).split(":");
                    final TimePickerDialog dialog =
                            new TimePickerDialog(Preferences.this, new OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    prefs.edit().putString("off_at_time",
                                            hourOfDay + ":" + (minute < 10 ? "0" + minute : minute))
                                            .commit();
                                    off_at.setTitle(getString(R.string.at_summary, hourOfDay + ":" +
                                            (minute < 10 ? "0" + minute : minute)));
                                }
                            }, Integer.parseInt(time[0]), Integer.parseInt(time[1]), true);
                    dialog.setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            off_at.setChecked(false);
                        }
                    });
                    dialog.setTitle(getString(R.string.turn_wifi_off_at_));
                    dialog.show();
                }
                return true;
            }
        });

        final Preference on_every = findPreference("on_every");
        final String[] time_names = getResources().getStringArray(R.array.time_names);
        // default 2 hours
        on_every.setTitle(
                getString(R.string.every_summary, prefs.getString("on_every_str", time_names[4])));
        on_every.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                if ((Boolean) newValue) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Preferences.this);
                    builder.setTitle(R.string.turn_wifi_on_every)
                            .setItems(time_names, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    prefs.edit().putInt("on_every_time_min", time_values[which])
                                            .putString("on_every_str", time_names[which]).commit();
                                    on_every.setTitle(
                                            getString(R.string.every_summary, time_names[which]));
                                }
                            });
                    builder.create().show();
                }
                return true;
            }
        });

        Preference locations = findPreference("locations");
        if (BuildConfig.FLAVOR.equals("play")) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK)) {
                locations.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        startActivity(new Intent(Preferences.this, Locations.class));
                        return true;
                    }
                });
            } else {
                locations.setEnabled(false);
                disableLocationSettings = true;
            }
        } else {
            locations.setSummary("Not available in F-Droid version");
            locations.setEnabled(false);
            disableLocationSettings = true;
        }

        final Preference power = findPreference("power_connected");
        power.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                if ((boolean) newValue) {
                    Intent battery =
                            registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                    if (battery != null &&
                            battery.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) > 0) {
                        // already connected to external power
                        prefs.edit().putBoolean("ignore_screen_off", true).commit();
                    }
                } else {
                    prefs.edit().putBoolean("ignore_screen_off", false).commit();
                }
                return true;
            }
        });

        findPreference("log")
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(final Preference preference) {
                        getLogDialog().show();
                        return true;
                    }
                });*/
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

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

}