package id.kasandra.retail;

/**
 * Created by Dios on 4/14/2016.
 */


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class EmptyFragment extends Activity {

    private Context mContext;
    private SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new SessionManager(getApplicationContext());
    }

}
