package com.mkyong.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.SignalStrength;
import com.mkyong.HttpRequest;
import android.os.StrictMode;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.widget.Toast;  
import android.os.Handler;
import java.util.Arrays;

public class HelloWorldActivity extends Activity {
    /** Called when the activity is first created. */
    public MyLocationListener locationListener;
    public myPhoneStateListener pslistener;
    public Handler handler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);

		try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            
			TelephonyManager TelephonManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        	pslistener = new myPhoneStateListener();
			TelephonManager.listen(pslistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		// Create the Handler object (on the main thread by default)
		handler = new Handler();
		// Start the initial runnable task by posting through the handler
		handler.post(runnableCode);		
    }
    private Runnable runnableCode = new Runnable() {
		@Override
		public void run() {
		  // Do something here on the main thread
		  String lat = locationListener.getLat();
		  String lon = locationListener.getLon();
		  String ss = pslistener.ss();
		  if(lon != "-1" && lat != "-1" && ss != "-1"){
			HttpRequest.post("http://mapmytrip.mybluemix.net/datapoint").contentType("application/json").send("{\"signalstrength\":" + ss + ", \"signaltype\": \"4G\", \"lat\": " + lat + ", \"lon\": " + lon + "}").code();
			Log.d("wtf", "Values posted");
		  }
  		  Log.d("wtf", "SIGNAL: " + ss + " LAT:" + lat + " LON:" + lon);
		  handler.postDelayed(runnableCode, 10000);
		}
	};
}
		
class MyLocationListener implements LocationListener {
	String lat = "-1";
	String lon = "-1";

	public String getLat(){
		return lat;
	}
	
	public String getLon(){
		return lon;
	}

	@Override
    public void onLocationChanged(Location loc) {
        lon = String.valueOf(loc.getLongitude());
        lat = String.valueOf(loc.getLatitude());
    }
    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}

class myPhoneStateListener extends PhoneStateListener {

    int ss = -1;
    
    public String ss(){
    	return String.valueOf(ss);
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        ss = signalStrength.getGsmSignalStrength();
        ss = (2 * ss) - 113; // -> dBm
    }
}
