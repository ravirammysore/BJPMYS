package in.appfocus.messageit.helpers;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * Created by Ravi on 15/05/2017.
 */

public class Utilities {

    //Add all permissions needed by app here
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
        Boolean result;

        try{
            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            result = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        }
        catch (SecurityException ex){
            //benefit of doubt!
            result = true;
        }

        return result;
    }

    /*public static void checkStoragePermissions(Activity activity) {

        // Storage Permissions
        int REQUEST_PERMISSION = 1;

        String[] PERMISSIONS_ALL = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_ALL,
                    REQUEST_PERMISSION
            );
            Log.d(TAG,"requested permission");
        }
    }

    public static void checkContactsPermissions(Activity activity) {

        // some integer
        int REQUEST_PERMISSION = 1;

        String[] PERMISSIONS_ALL = {
                Manifest.permission.READ_CONTACTS
        };
        // Check if we have permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_ALL,
                    REQUEST_PERMISSION
            );
            Log.d(TAG,"requested permission");
        }
    }

    public static void checkPhoneStatePermissions(Activity activity) {

        // some integer
        int REQUEST_PERMISSION = 1;

        String[] PERMISSIONS_ALL = {
                Manifest.permission.READ_PHONE_STATE
        };
        // Check if we have permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_ALL,
                    REQUEST_PERMISSION
            );
            Log.d(TAG,"requested permission");
        }
    }
    */
}
