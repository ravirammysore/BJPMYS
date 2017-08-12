package in.appfocus.messageit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import in.appfocus.messageit.helpers.Utilities;
import in.appfocus.messageit.models.Customer;
import io.realm.Realm;


public class DeviceInfoActivity extends AppCompatActivity {

    EditText etDeviceId, etDeviceGmailAccount;
    Realm realm;
    Customer customer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etDeviceId = (EditText) findViewById(R.id.etDeviceId);

        realm = Realm.getDefaultInstance();

        customer = realm.where(Customer.class).findFirst();

        loadDeviceInfo();
    }

    private void loadDeviceInfo(){

        //in case the app failed to fetch id on initial load, we do it here
        if(!Utilities.isStringANumber(customer.getDeviceId())){
            fetchAndSaveDeviceID();
        }

        etDeviceId.setText(customer.getDeviceId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.device_info,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.action_share_device_info){
            shareDeviceInfo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareDeviceInfo(){
        if(!Utilities.isInputGiven(etDeviceId)) return;

        String shareBody = etDeviceId.getText().toString();

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "My Device ID");

        startActivity(Intent.createChooser(sharingIntent, "Share Device ID Via"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void fetchAndSaveDeviceID(){
        String deviceID = Utilities.findDeviceID(DeviceInfoActivity.this);
        try{
            realm.beginTransaction();
            customer.setDeviceId(deviceID);
            realm.commitTransaction();

        }catch (Exception ex){
            Crashlytics.log("fetchAndSaveDeviceID-" + ex.getMessage());
        }
    }
}
