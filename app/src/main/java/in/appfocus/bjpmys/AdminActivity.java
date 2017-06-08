package in.appfocus.bjpmys;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import in.appfocus.bjpmys.database.RealmBackupRestore;
import in.appfocus.bjpmys.helpers.Utilities;
import in.appfocus.bjpmys.models.Customer;
import io.realm.Realm;

public class AdminActivity extends AppCompatActivity {

    Realm realm;
    EditText etUid,etSId, etApiPin,etRoute;
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

        realm = Realm.getDefaultInstance();
        loadAdminDetails();

        Utilities.checkStoragePermissions(AdminActivity.this);

    }

    private void loadAdminDetails(){
        customer = realm.where(Customer.class).findFirst();

        etSId.setText(customer.getSenderId());
        etUid.setText(customer.getUid());
        etApiPin.setText(customer.getApiPin());
        etRoute.setText(customer.getRoute());
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
        if (id == R.id.actionBackup) {

            try {
                new RealmBackupRestore(AdminActivity.this,realm).backup();
            }
            catch (Exception ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
            //true since we handled it
            return true;
        }

        if (id == R.id.actionRestore) {

            try {
                new RealmBackupRestore(AdminActivity.this,realm).restore();
            }
            catch (Exception ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            //performFileSearch();
            //true since we handled it
            return true;
        }

        if(id == R.id.action_save_admin){
            if(Utilities.isInputGiven(etUid,etSId, etApiPin,etRoute))
                saveAdminDetails();
            return  true;
        }

        /*
        * Official doc:
        * When you successfully handle a menu item, return true. If you don't handle the menu item, you should call the superclass implementation of onOptionsItemSelected() (the default implementation returns false).*/
        return super.onOptionsItemSelected(item);
    }

    private void saveAdminDetails(){
        try{
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
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
}