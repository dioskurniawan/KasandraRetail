package id.kasandra.retail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by
 */
public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent openMainActivity= new Intent("android.intent.action.LOGINPOS");
                    startActivity(openMainActivity);
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
