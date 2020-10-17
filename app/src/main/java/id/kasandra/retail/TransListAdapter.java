package id.kasandra.retail;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class TransListAdapter extends BaseAdapter {

    private Activity activity;
    //private SharedFunctions func;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    //public ImageLoader_ imageLoader;
    //ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private SessionManager session;
    private Context mContext;
    Typeface font;

    public TransListAdapter(Activity a, ArrayList<HashMap<String, String>> d, Context context) {
        activity = a;
        data=d;
        // func = new SharedFunctions();
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //imageLoader=new ImageLoader_(activity.getApplicationContext());
        //if (imageLoader == null)
        //    imageLoader = AppController.getInstance().getImageLoader();
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
                vi = inflater.inflate(R.layout.list_transaction, null);

            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
            otherSymbols.setDecimalSeparator(',');
            otherSymbols.setGroupingSeparator('.');
            DecimalFormat df = new DecimalFormat("#,###", otherSymbols);

            session = new SessionManager(vi.getContext());
            font = Typeface.createFromAsset(mContext.getAssets(), "quattrocentobold.ttf");

            TextView trans_no = (TextView) vi.findViewById(R.id.transno);
            TextView price = (TextView) vi.findViewById(R.id.price);
            TextView transdate = (TextView) vi.findViewById(R.id.transdate);

            price.setTypeface(font);
            price.setTextColor(Color.BLACK);
            transdate.setTypeface(font);
            trans_no.setTypeface(font);
            trans_no.setTextColor(Color.BLACK);

            if(session.screenSize() < 7.9) {
                trans_no.setTextSize(19);
                price.setTextSize(17);
                transdate.setTextSize(18);
                trans_no.setLayoutParams(new TableRow.LayoutParams(70, TableRow.LayoutParams.WRAP_CONTENT));
                price.setLayoutParams(new TableRow.LayoutParams(120, TableRow.LayoutParams.WRAP_CONTENT));
            } else {
                /*trans_no.setTextSize(22);
                price.setTextSize(20);
                transdate.setTextSize(21);
                trans_no.setLayoutParams(new TableRow.LayoutParams(90, TableRow.LayoutParams.WRAP_CONTENT));
                price.setLayoutParams(new TableRow.LayoutParams(150, TableRow.LayoutParams.WRAP_CONTENT));*/
            }

            //TextView status = (TextView) vi.findViewById(R.id.status);
            //TextView expdate = (TextView) vi.findViewById(R.id.expdate);

            //TextView role = (TextView)vi.findViewById(R.id.role);
            //ImageView aliasPicture = (ImageView) vi.findViewById(R.id.list_image);

            HashMap<String, String> trans = new HashMap<String, String>();
            trans = data.get(position);

            trans_no.setText("#"+ String.valueOf(df.format(Double.parseDouble(trans.get(TransactionActivity.TAG_REALNO)))));
            price.setText("Rp "+ String.valueOf(df.format(Double.parseDouble(trans.get(TransactionActivity.TAG_PRICE)))));
            //transdate.setText("#"+trans.get(TransactionActivity.TAG_ID));

            String updated_date = trans.get(TransactionActivity.TAG_DATE);
            SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date testDate_1 = null;
            try {
                testDate_1 = sdf_1.parse(updated_date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            SimpleDateFormat formatter_1 = new SimpleDateFormat("dd MMM yy");
            transdate.setText(formatter_1.format(testDate_1));
            final int idtrans = Integer.parseInt(trans.get(TransactionActivity.TAG_ID));
            final int notrans = Integer.parseInt(trans.get(TransactionActivity.TAG_NO));
            final int transrealno = Integer.parseInt(trans.get(TransactionActivity.TAG_REALNO));
            //session.setTransID(idtrans);
            //session.setTransNO(notrans);
            //name.setText(cat.get(MainActivity.TAG_ADDR));
            //status.setText(cat.get(MainActivity.TAG_STATUS));
            //expdate.setText("Expiry Date: " + pelaut.get(Fragment2.TAG_EXPDATE));
            //role.setText("Position: " + pelaut.get(Fragment2.TAG_ROLE));
            //imageLoader.DisplayImage(cat.get(MainActivity.TAG_ICON), aliasPicture);

            vi.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    //arg0.setSelected(true);
                    session.setTransNO(notrans);
                    session.setTransID(idtrans);
                    session.setTransRealNo(transrealno);
                    //MainActivity.findp();
                    //new MainActivity.getProducts.execute(idcat);

                    //if(mContext instanceof MainActivity){
                    ((TransactionActivity)mContext).showDetail();
                    //Toast.makeText(arg0.getContext(), "Call func "+session.nTransID(), Toast.LENGTH_SHORT).show();
                    //}
                }
            });

            return vi;
        } catch (Exception e) {
            return null;
        }
    }

}