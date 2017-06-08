package in.appfocus.bjpmys.database;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import in.appfocus.bjpmys.models.Contact;
import in.appfocus.bjpmys.models.Group;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Ravi on 05/06/2017.
 */
public class RealmBackupRestore {

    private File EXPORT_REALM_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private String EXPORT_REALM_FILE_NAME = "smsapp.realm";
    private String IMPORT_REALM_FILE_NAME = "default.realm"; // Eventually replace this if you're using a custom db name

    private final static String TAG = "mytag";

    private Activity activity;
    private Realm realm;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public RealmBackupRestore(Activity activity, Realm realm) {
        this.realm = realm;
        this.activity = activity;
    }

    public void backup() {
        // First check if we have storage permissions
        checkStoragePermissions(activity);
        File exportRealmFile;

        Log.d(TAG, "Realm DB Path = " + realm.getPath());

        try {
            EXPORT_REALM_PATH.mkdirs();

            // create a backup file
            exportRealmFile = new File(EXPORT_REALM_PATH, EXPORT_REALM_FILE_NAME);

            // if backup file already exists, delete it
            exportRealmFile.delete();

            // copy current realm to backup file
            realm.writeCopyTo(exportRealmFile);
            sendFileAsEmail(exportRealmFile);

        } catch (Exception e) {
            Log.d(TAG,"something went wrong");
            e.printStackTrace();
            Toast.makeText(activity, "Failed!", Toast.LENGTH_SHORT).show();
        }

        String msg = "File exported to Path: " + EXPORT_REALM_PATH + "/" + EXPORT_REALM_FILE_NAME;
        Toast.makeText(activity.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        Log.d(TAG, msg);

        realm.close();
    }

    public void restore() {
        checkStoragePermissions(activity);
        //Restore
        String restoreFilePath = EXPORT_REALM_PATH + "/" + EXPORT_REALM_FILE_NAME;

        Log.d(TAG, "oldFilePath = " + restoreFilePath);

        copyBundledRealmFile(restoreFilePath, IMPORT_REALM_FILE_NAME);
        Log.d(TAG, "Data restore is done");
    }

    private String copyBundledRealmFile(String oldFilePath, String outFileName) {
        try {
            File file = new File(activity.getApplicationContext().getFilesDir(), outFileName);

            FileOutputStream outputStream = new FileOutputStream(file);

            FileInputStream inputStream = new FileInputStream(new File(oldFilePath));

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            Toast.makeText(activity, "Done!", Toast.LENGTH_SHORT).show();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Failed!", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private void checkStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            Log.d(TAG,"requested permission");
        }
    }
    private String dbPath(){
        return realm.getPath();
    }

    private void sendFileAsEmail(File file){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Groups Backup");
        emailIntent.putExtra(Intent.EXTRA_TEXT, prepareEmailBody());
        Uri uri = Uri.fromFile(file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        activity.startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
    }

    private String prepareEmailBody(){
        StringBuilder sb = new StringBuilder();
        sb.append("Contacts in your app\n");
        RealmResults<Group> groupsAll = realm.where(Group.class).findAll();
        for (Group g : groupsAll){
            sb.append("\n"+g.getName() + "\n");
            for(Contact c:g.getContacts()){
                sb.append(c.getName() + " : " + c.getMobileNo() + "\n");
            }
            sb.append("-----------\n");
        }
        return sb.toString();
    }
}