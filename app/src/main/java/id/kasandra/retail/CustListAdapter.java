package id.kasandra.retail;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import mehdi.sakout.fancybuttons.FancyButton;

public class CustListAdapter extends BaseAdapter {

	private Activity activity;
	//private SharedFunctions func;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    private SessionManager session;
    private Context mContext;
    Typeface font;
    FancyButton btnDelCust;

    public CustListAdapter(Activity a, ArrayList<HashMap<String, String>> d, Context context) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = context;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        //String sID = data.get(position).get(MainActivity.TAG_DID);
        //return Long.parseLong(sID);
    	return 0;
    }

    public void clearAdapter()
    {
        notifyDataSetChanged();
    }   
    
    public View getView(int position, View convertView, ViewGroup parent) {
    	try {
            View vi = convertView;
            if (convertView == null)
                vi = inflater.inflate(R.layout.list_customer_main, null);


            session = new SessionManager(vi.getContext());
            font = Typeface.createFromAsset(mContext.getAssets(), "quattrocentobold.ttf");

            TextView cust_name = (TextView) vi.findViewById(R.id.title);
            cust_name.setTypeface(font);
            cust_name.setTextColor(Color.BLACK);

            if(session.screenSize() < 7.9 && session.screenSize() > 6.5) {
                cust_name.setTextSize(15);
            } else if(session.screenSize() < 6.5) {
                cust_name.setTextSize(14);
            } else {
                cust_name.setTextSize(17);
            }

            HashMap<String, String> cust = new HashMap<String, String>();
            cust = data.get(position);

            cust_name.setText(cust.get("cust_name"));
            final int idcust = Integer.parseInt(cust.get("cust_id"));


            vi.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    session.setCustID(idcust);
                    ((MainActivity)mContext).selectCust("");
                }
            });

            btnDelCust = (FancyButton) vi.findViewById(R.id.delete);
            if(idcust == 1){
                btnDelCust.setVisibility(View.INVISIBLE);
            }
            btnDelCust.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    //Toast.makeText(finalConvertView.getContext(), "id : " + item.getId(), Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Anda yakin ingin menghapus seluruh pesanan di meja nomor "+idcust+" ?");
                    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((MainActivity)mContext).deleteCust(idcust);
                                    //new removeCategory().execute(String.valueOf(item.getId()));
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
            });

	        return vi;
    	} catch (Exception e) {
    		return null;
    	}
    }

    public boolean isConnected() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec(command).waitFor() == 0);
    }

    private final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                //(ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

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
}