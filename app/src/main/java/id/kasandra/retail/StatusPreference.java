package id.kasandra.retail;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.Preference;
import android.support.v7.view.ContextThemeWrapper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StatusPreference extends Preference {

    private ImageView image;
    private TextView title;
    private TextView sub1;
    private TextView sub2;
    private SessionManager session;

    public StatusPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateView(final ViewGroup parent) {
        super.onCreateView(parent);
        LayoutInflater li =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.statepreference, parent, false);
        session = new SessionManager(v.getContext());
        image = (ImageView) v.findViewById(R.id.icon);
        title = (TextView) v.findViewById(R.id.title);
        sub1 = (TextView) v.findViewById(R.id.sub1);
        sub2 = (TextView) v.findViewById(R.id.sub2);
        update();
        return v;
    }

    /**
     * Called to update the status preference
     */
    public void update() {
        //new getWiFiInfo().execute("data");

        WifiManager wm = (WifiManager) getContext().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        boolean connected = ((ConnectivityManager) getContext().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        if (title == null) return;
        if (wm.isWifiEnabled()) {
            if (!connected) {
                title.setText(R.string.no_connected);
                image.setColorFilter(Color.DKGRAY);
                sub1.setText(R.string.click_to_select_network);
                sub2.setVisibility(View.GONE);
                session.setWiFi("WiFi disconnected");
                Toast.makeText(getContext(), "WiFi = "+session.sWiFi(), Toast.LENGTH_LONG).show();
            } else {
                WifiInfo wi = wm.getConnectionInfo();
                title.setText(wi.getSSID());
                image.setColorFilter(getContext().getResources().getColor(R.color.colorAccent));
                updateSignal();
                int ip = wi.getIpAddress();
                sub2.setText(String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff),
                        (ip >> 16 & 0xff), (ip >> 24 & 0xff)));
                sub2.setVisibility(View.VISIBLE);
                session.setWiFi(wi.getSSID());
                Toast.makeText(getContext(), "WiFi connected = "+session.sWiFi(), Toast.LENGTH_LONG).show();
            }
        } else {
            title.setText(R.string.wifi_not_enabled);
            image.setColorFilter(Color.LTGRAY);
            sub1.setText(R.string.click_to_turn_on);
            sub2.setVisibility(View.GONE);
            session.setWiFi("WiFi disabled");
            Toast.makeText(getContext(), "WiFi = "+session.sWiFi(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Called to update the WiFi signal text
     *
     * @return true, if connected to a WiFi network
     */
    boolean updateSignal() {
        WifiManager wm = (WifiManager) getContext().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        boolean connected = ((ConnectivityManager) getContext().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        if (sub1 == null) return wm.isWifiEnabled() && connected;
        if (wm.isWifiEnabled() && connected) {
            WifiInfo wi = wm.getConnectionInfo();
            try {
                sub1.setText(WifiManager.calculateSignalLevel(wi.getRssi(), 100) + "% - " +
                        wi.getLinkSpeed() + " Mbps");
            } catch (ArithmeticException e) {
                // might happen on Android 2.3 devices: https://code.google.com/p/android/issues/detail?id=2555
                sub1.setText(wi.getLinkSpeed() + " Mbps");
            }
            return true;
        } else {
            return false;
        }
    }

    /*public class getWiFiInfo extends AsyncTask<String, Integer, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            String values = arg0[0];

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("get_wifi", session.nUserID());

                // Building Parameters
                final JSONParser jsonParser = new JSONParser();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("tag", "LOC"));
                params.add(new BasicNameValuePair("package", jsonObject.toString()));

                JSONObject json = jsonParser.getJSONFromUrl("http://"+session.sURL()+"/getwifi.php", params);
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

            //Toast.makeText(getContext(),"http://"+session.sURL()+"/getwifi.php", Toast.LENGTH_LONG).show();
            try {
                boolean error = json.getBoolean("error");

                if (!error) {

                    //String id = json.getString("id");
                    String returnmessage = json.getString("msg");
                    JSONArray params = json.getJSONArray("details");
                    if(params.length() > 0){
                    }
                    //Toast.makeText(getActivity().getApplicationContext(),"jumlah json: " + params.length(), Toast.LENGTH_LONG).show();

                    for(int i=0; i<params.length(); i++) {
                        JSONObject obj = params.getJSONObject(i);
                        final int id = Integer.parseInt(obj.getString("id"));
                        final String username = obj.getString("username");
                        final String password = obj.getString("password");

                        session.setWiFiInfo(username, password);
                        Toast.makeText(getContext(),username+" - "+password, Toast.LENGTH_LONG).show();
                    }

                } else {
                    String errorMsg = json.getString("error_msg");
                    Toast.makeText(getContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                //Toast.makeText(getContext(),"Tidak dapat menyambungkan ke server, alamat website atau IP server yang Anda masukkan salah.", Toast.LENGTH_LONG).show();
                //showMessage("Informasi", "Tidak dapat menyambungkan ke server, alamat website atau IP server yang Anda masukkan salah.");
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }*/

    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(new ContextThemeWrapper(getContext(), android.R.style.Theme_Holo_Light_Dialog));
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
