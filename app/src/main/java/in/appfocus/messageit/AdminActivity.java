package in.appfocus.messageit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import in.appfocus.messageit.database.RealmBackupRestore;
import in.appfocus.messageit.helpers.Utilities;
import in.appfocus.messageit.models.Customer;
import io.realm.Realm;

public class AdminActivity extends AppCompatActivity {

    Realm realm;
    EditText etUid,etSId, etApiPin,etRoute, etAppTitle, etAppSubTitle;
    Customer customer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etUid = (EditText)findViewById(R.id.etUID);
        etSId= (EditText)findViewById(R.id.etSenderId);
        etApiPin = (EditText)findViewById(R.id.etApiPin);
        etRoute = (EditText)findViewById(R.id.etRoute);
        etAppTitle = (EditText)findViewById(R.id.etAppTitle);
        etAppSubTitle = (EditText)findViewById(R.id.etAppSubTitle);

        realm = Realm.getDefaultInstance();
        loadAdminDetails();

        //Utilities.checkStoragePermissions(AdminActivity.this);

    }

    private void loadAdminDetails(){
        customer = realm.where(Customer.class).findFirst();

        etSId.setText(customer.getSenderId());
        etUid.setText(customer.getUid());
        etApiPin.setText(customer.getApiPin());
        etRoute.setText(customer.getRoute());
        etAppTitle.setText(customer.getAppTitle());
        etAppSubTitle.setText(customer.getAppSubTitle());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Deriving classes should always call through to the base implementation.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.actionBackupToDevice) {
            startBackupToDevice();
            //true since we handled it
            return true;
        }

        if (id == R.id.actionBackupToEmail) {
            startBackupToEmail();
            //true since we handled it
            return true;
        }

        if (id == R.id.actionRestore) {
            startRestore();
            //true since we handled it
            return true;
        }

        if(id == R.id.action_save_admin){
            //if(Utilities.isInputGiven(etAppTitle,etAppSubTitle,etUid,etSId, etApiPin,etRoute))
            if(Utilities.isInputGiven(etAppTitle))
                saveAdminDetails();
            return  true;
        }

        /*
        * Official doc:
        * When you successfully handle a menu item, return true.
        * If you don't handle the menu item, you should call the superclass implementation
        * of onOptionsItemSelected() (b.t.w - the default implementation returns false).*/
        return super.onOptionsItemSelected(item);
    }

    private void saveAdminDetails(){
        try{
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    customer.setAppTitle(etAppTitle.getText().toString());
                    customer.setAppSubTitle(etAppSubTitle.getText().toString());
                    customer.setSenderId(etSId.getText().toString());
                    customer.setUid(etUid.getText().toString());
                    customer.setApiPin(etApiPin.getText().toString());
                    customer.setRoute(etRoute.getText().toString());
                    Toast.makeText(AdminActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            Crashlytics.log("saveAdminDetails-" + ex.getMessage());
        }
    }
    private void showSnack(String msg) {
        CoordinatorLayout view = (CoordinatorLayout) findViewById(R.id.layoutAdmin);
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");

        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("mytag", "Uri: " + uri.toString());
                //showImage(uri);
            }
        }
    }

    private void startBackupToDevice(){
        RealmBackupRestore realmBackupRestore= new RealmBackupRestore(AdminActivity.this,realm);
        String msg = realmBackupRestore.backupToDevice();
        if(msg!=null) showSnack("Backup successful "+msg);
    }

    private void startBackupToEmail(){
        RealmBackupRestore realmBackupRestore= new RealmBackupRestore(AdminActivity.this,realm);
        realmBackupRestore.backupToEmail();
    }

    private void startRestore(){
        new AlertDialog.Builder(this)
                .setTitle("Confirm Restore")
                .setMessage("App will exit after restore. You will lose any data which was created after the backupToDevice file was created. Do you want to restore now?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            new RealmBackupRestore(AdminActivity.this,realm).restore();
                            //lets exit the app,
                            //else main activity will still be pointing at old realm file (or its id) and freak out-Ravi
                            Runtime.getRuntime().exit(0);
                            //not sure what the bil function did - ravi
                            //chooseExcelFileToImport();
                        }
                        catch (Exception ex){
                            Toast.makeText(AdminActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}