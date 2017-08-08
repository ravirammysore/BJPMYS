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

import in.appfocus.messageit.helpers.Utilities;
import in.appfocus.messageit.models.Customer;
import io.realm.Realm;


public class DeviceInfoActivity extends AppCompatActivity {

    EditText etDeviceId, etDeviceGmailAccount;
    Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Utilities.checkPhoneStatePermissions(this);

        etDeviceId = (EditText) findViewById(R.id.etDeviceId);

        realm = Realm.getDefaultInstance();

        loadDeviceInfo();
    }

    private void loadDeviceInfo(){

        Customer customer = realm.where(Customer.class).findFirst();
        String deviceId = customer.getDeviceId();

        if(deviceId==null || deviceId=="")
            etDeviceId.setText("Device ID is empty");
        else
            etDeviceId.setText(deviceId);
    }

    public void FindDeviceInfo(View v){
        String deviceID=null;
        try{
            TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
            deviceID = tm.getDeviceId();

        }catch (SecurityException ex){
            Toast.makeText(this, "No permission to fetch Device ID!", Toast.LENGTH_LONG).show();
        }
        catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        etDeviceId.setText(deviceID);

        try{
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Customer customer = realm.where(Customer.class).findFirst();
                    customer.setDeviceId(etDeviceId.getText().toString());
                    Toast.makeText(DeviceInfoActivity.this, "Device ID Saved!", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception ex){
            Toast.makeText(this, "Device info not saved!", Toast.LENGTH_SHORT).show();
        }

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
}
