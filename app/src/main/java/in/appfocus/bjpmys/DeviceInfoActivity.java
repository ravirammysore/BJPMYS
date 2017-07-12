package in.appfocus.bjpmys;

import android.accounts.Account;
import android.accounts.AccountManager;
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

import in.appfocus.bjpmys.helpers.Utilities;
import in.appfocus.bjpmys.models.Customer;
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

        Utilities.checkPhoneStatePermissions(this);
        Utilities.checkAccountsPermission(this);

        etDeviceId = (EditText) findViewById(R.id.etDeviceId);
        etDeviceGmailAccount = (EditText) findViewById(R.id.etDeviceGmailAccount);

        realm = Realm.getDefaultInstance();

        loadDeviceInfo();
    }

    private void loadDeviceInfo(){
        Customer customer = realm.where(Customer.class).findFirst();
        etDeviceGmailAccount.setText(customer.getDeviceGmailAccount());
        etDeviceId.setText(customer.getDeviceId());
    }

    public void FindDeviceInfo(View v){
        String deviceID=null;
        String deviceGmailAccount = null;
        try{
            TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
            deviceID = tm.getDeviceId();

            Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
            deviceGmailAccount = accounts[0].name;
        }catch (SecurityException ex){
            Toast.makeText(this, "No permission to fetch Device Info!", Toast.LENGTH_LONG).show();
        }
        catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        etDeviceGmailAccount.setText(deviceGmailAccount);
        etDeviceId.setText(deviceID);

        try{
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Customer customer = realm.where(Customer.class).findFirst();
                    customer.setDeviceGmailAccount(etDeviceGmailAccount.getText().toString());
                    customer.setDeviceId(etDeviceId.getText().toString());
                    Toast.makeText(DeviceInfoActivity.this, "Device Info Saved!", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception ex){
            Toast.makeText(this, "Device info not saved!", Toast.LENGTH_SHORT).show();
        }

        //realm.where(Customer.class).findFirst()
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
        if(!Utilities.isInputGiven(etDeviceGmailAccount,etDeviceId)) return;

        String shareBody = etDeviceGmailAccount.getText().toString() +"#"+ etDeviceId.getText().toString();

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "My Device Info");

        startActivity(Intent.createChooser(sharingIntent, "Share Device Info Via"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
