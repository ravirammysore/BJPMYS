package in.appfocus.messageit.database;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import in.appfocus.messageit.models.Contact;
import in.appfocus.messageit.models.Group;
import io.realm.Realm;
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

    File exportRealmFile;

    public RealmBackupRestore(Activity activity, Realm realm) {
        this.realm = realm;
        this.activity = activity;
    }

    public String backupToDevice() {

        String msg;
        try {
            EXPORT_REALM_PATH.mkdirs();

            // create a backupToDevice file
            exportRealmFile = new File(EXPORT_REALM_PATH, EXPORT_REALM_FILE_NAME);

            // if backupToDevice file already exists, delete it
            exportRealmFile.delete();

            //copy current realm to backupToDevice file
            realm.writeCopyTo(exportRealmFile);
        }

        catch (SecurityException ex) {
            Toast.makeText(activity, "No permission", Toast.LENGTH_SHORT).show();
        }

        catch (Exception e) {
            Toast.makeText(activity, "Failed!", Toast.LENGTH_SHORT).show();
            Crashlytics.log("backupToDevice-" + e.getMessage());
        }

        msg = "File exported to Path: " + EXPORT_REALM_PATH + "/" + EXPORT_REALM_FILE_NAME;
        //causing issues
        //realm.close();
        return msg;
    }

    public void backupToEmail(){
        backupToDevice();
        sendFileAsEmail(exportRealmFile);
    }

    public void restore() {
        //checkStoragePermissions(activity);
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
            //was this the line causing exception? - Ravi
            //return file.getAbsolutePath();
        }
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Failed!", Toast.LENGTH_SHORT).show();
        }
        catch (SecurityException ex) {
            Toast.makeText(activity, "No permission", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(activity, "Failed!", Toast.LENGTH_SHORT).show();
            Crashlytics.log("restore-copyBundledRealmFile-" + e.getMessage());
        }
        return null;
    }

    private String dbPath(){
        return realm.getPath();
    }

    private void sendFileAsEmail(File file){
        try{
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MSGit Contacts Backup");
            emailIntent.putExtra(Intent.EXTRA_TEXT, prepareEmailBody());
            Uri uri = Uri.fromFile(file);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            activity.startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
        }
        catch (Exception ex){
            Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
            Crashlytics.log("sendFileAsEmail-" + ex.getMessage());
        }
    }

    private String prepareEmailBody(){
        StringBuilder sb = new StringBuilder();
        sb.append("Contacts in your app\n");
        RealmResults<Group> groupsAll = realm.where(Group.class).findAll();
        for (Group g : groupsAll){
            sb.append("\n"+g.getName() + "\n\n");
            for(Contact c:g.getContacts()){
                sb.append(c.getName() + " : " + c.getMobileNo() + "\n");
                if(c.getDob()!=null){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                    try {
                        String dateString = dateFormat.format(c.getDob());
                        sb.append("DOB:"+ dateString + "\n");
                    }
                    catch (Exception e) {

                    }
                }
                if(c.getDoa()!=null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                    try {
                        String dateString = dateFormat.format(c.getDoa());
                        sb.append("DOA:" + dateString + "\n");
                    } catch (Exception e) {

                    }
                }
                sb.append("\n");
            }
            sb.append("-----------\n");
        }
        return sb.toString();
    }
}