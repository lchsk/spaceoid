package com.pigletlogic.spaceoid.android;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.pigletlogic.spaceoid.IActivityRequestHandler;
import com.pigletlogic.spaceoid.Spaceoid;
import com.pigletlogic.util.Constants;

public class AndroidLauncher extends AndroidApplication implements IActivityRequestHandler {
	
	//private PowerManager.WakeLock wl;
	protected AdView adView;
	private final int SHOW_ADS = 1;
    private final int HIDE_ADS = 0;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // works
		
		// WakeLock
		//PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		//wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Spaceoid");
		
		
		// AD
		// Create the layout
        RelativeLayout layout = new RelativeLayout(this);
        
        // Do the stuff that initialize() would do for you
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        
        View gameView = initializeForView(new Spaceoid(this));
        
       // gameView.
        
        //ca-app-pub-1191354220196405/7358500573
        adView = new AdView(this); // Put in your secret key here
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(Constants.AD_UNIT_ID);
        adView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice("5D33F510C4283C229DD075B87AEAFADA") // xperia
        .addTestDevice("0075A20DC32761F75337C70FC4352466") // nexus


        
        .build();
        
        adView.loadAd(adRequest);
        
        layout.addView(gameView);
        RelativeLayout.LayoutParams adParams = 
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
            adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

            layout.addView(adView, adParams);
            
            setContentView(layout);

		
		// ---
		
	//AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		//config.useWakelock = true;
		//initialize(new Spaceoid(), config);
	}
	
	protected Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
        	
        	Gdx.app.debug("HandleMessage", "msg: " + msg);
        	
            switch(msg.what) {
                case SHOW_ADS:
                {
                	Gdx.app.debug("Handler. SHOW_ADS: ", adView.toString());
                    adView.setVisibility(View.VISIBLE);
                    break;
                }
                case HIDE_ADS:
                {
                    adView.setVisibility(View.GONE);
                    break;
                }
            }
        }
    };
	
	@Override protected void onPause() {

		//wl.release();
		super.onPause();
		}

		@Override protected void onResume() {

		//wl.acquire();
		super.onResume();
		}

		@Override
	    public void showAds(boolean show) {
			Gdx.app.debug("showAds", "show: " + show);
	       handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
	       Gdx.app.debug("showAds", "after sending message " + show);
	    }
}
