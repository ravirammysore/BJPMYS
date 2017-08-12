package in.appfocus.messageit.helpers;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import in.appfocus.messageit.models.Customer;

import static android.content.ContentValues.TAG;

/**
 * Created by Ravi on 15/05/2017.
 */

public class Utilities {

    //Add all permissions needed by the app here
    public static String[] PERMISSIONS_ALL = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    public static Boolean isStringANumber(String input){
        Boolean result = false;

        String regexStr = "^[0-9]*$";

        if(input!=null)
            if(input.matches(regexStr)) result = true;

        return result;
    }

    public static Boolean isInputGiven(EditText...ets){
        Boolean result = true;

        for(EditText et :ets) {
            if (et.getText().toString().trim().equalsIgnoreCase("")) {
                et.setError("Cannot be empty!");
                result = false;
            }
        }
        return result;
    }

    public static Boolean isConnectedToInternet(Context context){
        //benefit of doubt!
        Boolean result = true;

        try{
            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            result = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        }
        catch (SecurityException ex){
            Crashlytics.log("isConnectedToInternet-" + ex.getMessage());
        }
        catch (Exception ex){
            Crashlytics.log("isConnectedToInternet-" + ex.getMessage());
        }

        return result;
    }

    public static String findDeviceID(Context context) {
        String deviceID;

        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceID = tm.getDeviceId();
        }

        catch (SecurityException ex) {
                deviceID = "Permission denied for device id";
        }
        catch (Exception ex) {
                deviceID = "Problem finding device id";
                Crashlytics.log("FindDeviceInfoIfNotDone-" + ex.getMessage());
        }

        return deviceID;
    }

    public static Boolean isStringNullOrEmpty(String string){
        Boolean result=true;

        if(string!=null)
            if(!string.equals(""))
                result = false;

        return result;
    }
}
