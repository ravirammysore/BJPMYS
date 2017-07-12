package in.appfocus.bjpmys.helpers;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Ravi on 15/05/2017.
 */

public class Utilities {

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

    public static void checkStoragePermissions(Activity activity) {

        // Storage Permissions
        int REQUEST_PERMISSION = 1;

        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS,
                    REQUEST_PERMISSION
            );
            Log.d(TAG,"requested permission");
        }
    }

    public static void checkContactsPermissions(Activity activity) {

        // some integer
        int REQUEST_PERMISSION = 1;

        String[] PERMISSIONS = {
                Manifest.permission.READ_CONTACTS
        };
        // Check if we have permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS,
                    REQUEST_PERMISSION
            );
            Log.d(TAG,"requested permission");
        }
    }

    public static void checkPhoneStatePermissions(Activity activity) {

        // some integer
        int REQUEST_PERMISSION = 1;

        String[] PERMISSIONS = {
                Manifest.permission.READ_PHONE_STATE
        };
        // Check if we have permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS,
                    REQUEST_PERMISSION
            );
            Log.d(TAG,"requested permission");
        }
    }

    public static void checkAccountsPermission(Activity activity) {

        // some integer
        int REQUEST_PERMISSION = 1;

        String[] PERMISSIONS = {
                Manifest.permission.GET_ACCOUNTS
        };
        // Check if we have permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.GET_ACCOUNTS);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS,
                    REQUEST_PERMISSION
            );
            Log.d(TAG,"requested permission");
        }
    }
}
