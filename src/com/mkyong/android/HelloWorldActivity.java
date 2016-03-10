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
		TextView text = new TextView(this);
		
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

class myPhoneStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int ss = signalStrength.getGsmSignalStrength();
            ss = (2 * ss) - 113; // -> dBm    
            Log.d("wtf", "Signal Strenght is now: " + String.valueOf(ss));
			Log.d("wtf", "Signal Post: " + String.valueOf(HttpRequest.post("http://mapmytrip.mybluemix.net/datapoint").send("{\"signalStrength\": " + String.valueOf(ss) + ", \"signalType\": 4G, \"location\": {\"coordinates\": [10,23]} }").code()));
        }

    }

