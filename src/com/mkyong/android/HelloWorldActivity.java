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
        
public class HelloWorldActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        super.onCreate(savedInstanceState);
        TelephonyManager TelephonManager;
		myPhoneStateListener pslistener;    
		int SignalStrength = 0;
		TextView myAwesomeTextView = (TextView)findViewById(R.id.myAwesomeTextView);
		myAwesomeTextView.setText("My Awesome Text");
		
		try {
			pslistener = new myPhoneStateListener();
			TelephonManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			TelephonManager.listen(pslistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
    }
}

class MyLocationListener implements LocationListener {
	String lat = "";
	String lon = "";
	
	public String getLocation(){
		return lat+"/"+lon;
	}
	
	@Override
    public void onLocationChanged(Location loc) {
        editLocation.setText("");
        pb.setVisibility(View.INVISIBLE);
        Toast.makeText(
                getBaseContext(),
                "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                    + loc.getLongitude(), Toast.LENGTH_SHORT).show();
        String longitude = "Longitude: " + loc.getLongitude();
        Log.v(TAG, longitude);
        String latitude = "Latitude: " + loc.getLatitude();
        Log.v(TAG, latitude);
    }
    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

class myPhoneStateListener extends PhoneStateListener {
		LocationManager locationManager = (LocationManager)
		getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new MyLocationListener();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        int ss = -1
        
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            ss = signalStrength.getGsmSignalStrength();
            ss = (2 * ss) - 113; // -> dBm    
            Log.d("wtf", "Signal Strenght is now: " + String.valueOf(ss));
            Log.d("wtf", "Location: " + locationListener.getLocation());
			Log.d("wtf", "Signal Post: " + String.valueOf(HttpRequest.post("http://mapmytrip.mybluemix.net/datapoint").contentType("application/json").send("{\"signalstrength\":" + String.valueOf(ss) +", \"signaltype\": \"4G\", \"location\": {\"coordinates\": [12,18]}}").code()));
        }
	}

