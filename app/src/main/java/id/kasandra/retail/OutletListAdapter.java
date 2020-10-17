package id.kasandra.retail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class OutletListAdapter extends BaseAdapter {
	private Activity activity;
	private Context mContext;
	private LayoutInflater inflater;
	private List<GetItem> getItems;
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();
	private SessionManager session;
	Typeface font1;
	FancyButton link;
	private String sURL = "https://kasandra.biz/appdata/getdata_dev1.php";
	boolean internetAvailable = false;

	public OutletListAdapter(Activity activity, List<GetItem> getItems) {
		this.activity = activity;
		this.getItems = getItems;

		font1 = Typeface.createFromAsset(activity.getAssets(), "quattrocentobold.ttf");
		mContext=activity;
	}

	@Override
	public int getCount() {
		return getItems.size();
	}

	@Override
	public Object getItem(int location) {
		return getItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {

		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.list_outlet, null);

		session = new SessionManager(activity.getApplicationContext());

		/*if(mContext instanceof NewsFragment){
			((YourActivityName)mContext).yourDesiredMethod();
		}*/

		internetAvailable = session.isInternetAvailable();
		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		final View finalConvertView = convertView;

		link = (FancyButton) convertView.findViewById(R.id.link);
		TextView name = (TextView) convertView.findViewById(R.id.title);
		TextView addr = (TextView) convertView.findViewById(R.id.address);
		name.setTypeface(font1);
		addr.setTypeface(font1);
		/*TextView timestamp = (TextView) convertView
				.findViewById(R.id.upddate);
		TextView code = (TextView) convertView
				.findViewById(R.id.code);
		TextView price_sell = (TextView) convertView
				.findViewById(R.id.price_sell);*/
		ImageView pImageView = (ImageView) convertView
				.findViewById(R.id.feedImage1);
		//pImageView.setLayoutParams(new TableRow.LayoutParams(250, 250));
		pImageView.setPadding(5,5,5,5);
		//pImageView.setImageDrawable(finalConvertView.getResources().getDrawable(R.drawable.imgview_bg));

		if(session.screenSize() < 7.9) {
			name.setTextSize(20);
			pImageView.setLayoutParams(new TableRow.LayoutParams(100, 100));
		} else {
			name.setTextSize(25);
			pImageView.setLayoutParams(new TableRow.LayoutParams(150, 150));
		}

		final GetItem item = getItems.get(position);

		pImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new isOnline().execute("");
				if(internetAvailable){
					new saveOutlet().execute(session.sIMEI() + "!" + item.getId());
					session.setOutlet(item.getId(), item.getName());
					Intent intent = new Intent(mContext, MainActivity.class);
					intent.putExtra("id_main", 0);
					activity.startActivity(intent);
					activity.finish();
				} else {
					Toast.makeText(mContext, R.string.no_internet2, Toast.LENGTH_LONG).show();
				}
			}
		});

		name.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new isOnline().execute("");
				if(internetAvailable){
					new saveOutlet().execute(session.sIMEI() + "!" + item.getId());
					session.setOutlet(item.getId(), item.getName());
					Intent intent = new Intent(mContext, MainActivity.class);
					intent.putExtra("id_main", 0);
					activity.startActivity(intent);
					activity.finish();
				} else {
					Toast.makeText(mContext, R.string.no_internet2, Toast.LENGTH_LONG).show();
				}
			}
		});

		link.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new isOnline().execute("");
				if(internetAvailable){
					new saveOutlet().execute(session.sIMEI() + "!" + item.getId());
					session.setOutlet(item.getId(), item.getName());
					Intent intent = new Intent(mContext, MainActivity.class);
					intent.putExtra("id_main", 0);
					activity.startActivity(intent);
					activity.finish();
				} else {
					Toast.makeText(mContext, R.string.no_internet2, Toast.LENGTH_LONG).show();
				}
			}
		});

		name.setText(item.getName());
		if (item.getCode().equals("null")) {
			addr.setVisibility(View.GONE);
		} else {
			addr.setText(item.getCode());
		}
		/*Display display = ((WindowManager) mContext.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		int orientation = display.getOrientation();
		switch(orientation) {
			case Configuration.ORIENTATION_PORTRAIT:
				if (item.getName().length() > 25) {
					String lastWord = item.getName().substring(item.getName().lastIndexOf(" ") + 1);
					name.setText(item.getName().replace(" " + lastWord, "")+"\n"+lastWord);
				}
				break;
			case Configuration.ORIENTATION_LANDSCAPE:
				//
				break;
		}*/
		/*String price_ = df.format(Double.parseDouble(item.getPrice_sell()));
		price_sell.setText("Rp"+price_);

		String date = item.getTimeStamp();
		Date testDate = null;
		try {
			testDate = sdf.parse(date);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		timestamp.setText(formatter.format(testDate));

		// Chcek for empty status message
		if (!TextUtils.isEmpty(item.getCode())) {
			code.setText("("+item.getCode()+")");
			code.setVisibility(View.VISIBLE);
		} else {
			// status is empty, remove from view
			code.setVisibility(View.GONE);
		}*/

		// user profile pic
		//profilePic.setImageUrl(item.getProfilePic(), imageLoader);

		// Feed image
		/*if (item.getImage() != null) {
			//pImageView.setImageUrl(item.getImage(), imageLoader);
			Glide.with(finalConvertView.getContext()).load("https://kasandra.biz/images/" + item.getImage()).into(pImageView);
			pImageView.setVisibility(View.VISIBLE);
			pImageView
					.setResponseObserver(new PImageView.ResponseObserver() {
						@Override
						public void onError() {
						}

						@Override
						public void onSuccess() {
						}
					});
		} else {
			pImageView.setVisibility(View.GONE);
		}*/

		return convertView;
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

	private class saveOutlet extends AsyncTask<String, Integer, JSONObject> {

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
				jsonObject.put("imei", result[0]);
				jsonObject.put("outlet_id", result[1]);

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

				// Check for error node in json
				if (!error) {

					String id = json.getString("id");
					String returnmessage = json.getString("msg");
					JSONObject params = json.getJSONObject("param");

				} else {
					// Error in login. Get the error message
					String errorMsg = json.getString("error_msg");
					Toast.makeText(mContext,
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
}
