package id.kasandra.retail;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CatListAdapter extends BaseAdapter {
    
	private Activity activity;
	//private SharedFunctions func;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    //public ImageLoader_ imageLoader;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private SessionManager session;
    private Context mContext;
    Typeface font;

    public CatListAdapter(Activity a, ArrayList<HashMap<String, String>> d, Context context) {
        activity = a;
        data=d;
       // func = new SharedFunctions();
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //imageLoader=new ImageLoader_(activity.getApplicationContext());
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
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
                vi = inflater.inflate(R.layout.list_category_main, null);


            session = new SessionManager(vi.getContext());
            font = Typeface.createFromAsset(mContext.getAssets(), "quattrocentobold.ttf");

            TextView cat_name = (TextView) vi.findViewById(R.id.title);
            ImageView imgDisc = (ImageView) vi.findViewById(R.id.imgDisc);
            imgDisc.setVisibility(View.GONE);

            cat_name.setTypeface(font);
            //TextView address = (TextView) vi.findViewById(R.id.address);
            //TextView status = (TextView) vi.findViewById(R.id.status);
            //TextView expdate = (TextView) vi.findViewById(R.id.expdate);
            cat_name.setTextColor(Color.BLACK);

            //TextView role = (TextView)vi.findViewById(R.id.role);
            ImageView pImageView = (ImageView) vi.findViewById(R.id.feedImage1);
            if(session.screenSize() < 7.9 && session.screenSize() > 6.5) {
                cat_name.setTextSize(15);
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(80,80);
                layoutParams.gravity= Gravity.CENTER;
                pImageView.setLayoutParams(layoutParams);
                //pImageView.setLayoutParams(new TableRow.LayoutParams(80, 80));
            } else if(session.screenSize() < 6.5) {
                cat_name.setTextSize(14);
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(80,80);
                layoutParams.gravity= Gravity.CENTER;
                pImageView.setLayoutParams(layoutParams);
                //pImageView.setLayoutParams(new TableRow.LayoutParams(80, 80));
            } else {
                cat_name.setTextSize(17);
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(150,150);
                layoutParams.gravity= Gravity.CENTER;
                pImageView.setLayoutParams(layoutParams);
                //pImageView.setLayoutParams(new TableRow.LayoutParams(150, 150));
            }
            pImageView.setPadding(5,5,5,5);
            pImageView.setImageDrawable(vi.getResources().getDrawable(R.drawable.imgview_bg));

            HashMap<String, String> cat = new HashMap<String, String>();
            cat = data.get(position);

            cat_name.setText(cat.get(MainActivity.TAG_NAME));
            final int idcat = Integer.parseInt(cat.get(MainActivity.TAG_ID));
            String discount = cat.get(MainActivity.TAG_DISCOUNT);
            if(discount.equals("") || discount.equals("null")){
                discount = "0";
            }
            if(Integer.parseInt(discount) > 0){
                imgDisc.setVisibility(View.VISIBLE);
            }
            //session.setCatID(idcat);
            //name.setText(cat.get(MainActivity.TAG_ADDR));
	        //status.setText(cat.get(MainActivity.TAG_STATUS));
	        //expdate.setText("Expiry Date: " + pelaut.get(Fragment2.TAG_EXPDATE));
	        //role.setText("Position: " + pelaut.get(Fragment2.TAG_ROLE));
	        //imageLoader.DisplayImage(cat.get(MainActivity.TAG_ICON), aliasPicture);
            if (cat.get(MainActivity.TAG_ICON) != null) {
            //pImageView.setImageUrl(cat.get(MainActivity.TAG_ICON), imageLoader);
            pImageView.setVisibility(View.VISIBLE);

                try {
                    File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            File.separator + "kasandra" + File.separator + "category" + File.separator + cat.get(MainActivity.TAG_ICON)); //+ "." + mFormat.name().toLowerCase());
                    Bitmap image = BitmapFactory.decodeFile(myImageFile.getAbsolutePath());
                    pImageView.setImageBitmap(image);
                    pImageView.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
                } catch (Exception e) {
                    Glide.with(vi.getContext()).load("https://kasandra.biz/images/" + cat.get(MainActivity.TAG_ICON)).into(pImageView);
                    downloadImagetoDevice(cat.get(MainActivity.TAG_ICON));
                }
                /*try {
                    if (isConnected()) {
                        Glide.with(vi.getContext()).load("https://kasandra.biz/images/" + cat.get(MainActivity.TAG_ICON)).into(pImageView);
                        downloadImagetoDevice(cat.get(MainActivity.TAG_ICON));
                    } else {
                        File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                File.separator + "kasandra" + File.separator + "category" + File.separator + cat.get(MainActivity.TAG_ICON)); //+ "." + mFormat.name().toLowerCase());
                        Bitmap image = BitmapFactory.decodeFile(myImageFile.getAbsolutePath());
                        pImageView.setImageBitmap(image);
                        pImageView.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("kasandra", "kasandra debug problem getting data:" + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("kasandra", "kasandra debug problem getting data:" + e.getMessage());
                }*/
            /*if(isInternetOn()) {
                Glide.with(vi.getContext()).load("https://kasandra.biz/images/" + cat.get(MainActivity.TAG_ICON)).into(pImageView);
                downloadImagetoDevice(cat.get(MainActivity.TAG_ICON));
            } else {
                File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "kasandra" + File.separator + "category" + File.separator + cat.get(MainActivity.TAG_ICON)); //+ "." + mFormat.name().toLowerCase());
                Bitmap image = BitmapFactory.decodeFile(myImageFile.getAbsolutePath());
                pImageView.setImageBitmap(image);
                pImageView.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
                //Glide.with(this).load(getImage(my_drawable_image_name)).into(myImageView);
            }*/
            /*pImageView
                    .setResponseObserver(new PImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });*/
        } else {
            pImageView.setVisibility(View.GONE);
        }

            vi.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    //arg0.setSelected(true);
                    //Toast.makeText(arg0.getContext(), "Category ID"+String.valueOf(idcat), Toast.LENGTH_SHORT).show();
                    session.setCatID(idcat);
                    //MainActivity.findp();
                    //new MainActivity.getProducts.execute(idcat);

                    //if(mContext instanceof MainActivity){
                    ((MainActivity)mContext).findp("");
                    //}
                }
            });

	        return vi;
    	} catch (Exception e) {
    		return null;
    	}
    }

    /*public int getImage(String imageName) {

        int drawableResourceId = this.getResources().getIdentifier(imageName, "drawable", this.getPackageName());

        return drawableResourceId;
    }*/
    private void downloadImagetoDevice(final String filename) {
        final BasicImageDownloader downloader = new BasicImageDownloader(new BasicImageDownloader.OnImageLoaderListener() {
            @Override
            public void onError(BasicImageDownloader.ImageError error) {
                /*Toast.makeText(ImageActivity.this, "Error code " + error.getErrorCode() + ": " +
                        error.getMessage(), Toast.LENGTH_LONG).show();
                imgDisplay.setImageResource(RES_ERROR);
                tvPercent.setVisibility(View.GONE);
                pbLoading.setVisibility(View.GONE);*/
                error.printStackTrace();
            }

            @Override
            public void onProgressChange(int percent) {
                //pbLoading.setProgress(percent);
                //tvPercent.setText(percent + "%");
            }

            @Override
            public void onComplete(Bitmap result) {
                        /* save the image - I'm gonna use JPEG */
                final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
                        /* don't forget to include the extension into the file name */
                final File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "kasandra" + File.separator + "category" + File.separator + filename); //+ "." + mFormat.name().toLowerCase());
                BasicImageDownloader.writeToDisk(myImageFile, result, new BasicImageDownloader.OnBitmapSaveListener() {
                    @Override
                    public void onBitmapSaved() {
                        Toast.makeText(mContext, "Image saved as: " + myImageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onBitmapSaveError(BasicImageDownloader.ImageError error) {
                        /*Toast.makeText(mContext, "Error code " + error.getErrorCode() + ": " +
                                error.getMessage(), Toast.LENGTH_LONG).show();*/
                        error.printStackTrace();
                    }
                }, mFormat, false);
                //imgView.setImageBitmap(result);
                //imgView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
            }
        });
        downloader.download("https://kasandra.biz/images/" + filename, true);
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