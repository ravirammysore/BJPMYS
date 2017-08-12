package in.appfocus.messageit;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import in.appfocus.messageit.helpers.Utilities;
import in.appfocus.messageit.models.Customer;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Realm realm;
    ArrayList<String> lstPermissionsMissing = new ArrayList<>();
    TextView tvAppTitle,tvAppSubTitle;
    Customer customer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,QuickContact.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        setTitle("Home");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tvAppTitle = (TextView)findViewById(R.id.tvAppTitle);
        tvAppSubTitle = (TextView)findViewById(R.id.tvAppSubTitle);

        realm = Realm.getDefaultInstance();
        customer = realm.where(Customer.class).findFirst();

        if(!hasAllPermissions())
            requestMissingPermissions();

        //IMPORTANT
        //we cannot query for device ID here as it would execute before permission asking is complete!
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //just to safeguard against IllegalStateException
        if(realm.isClosed()) realm = Realm.getDefaultInstance();

        loadAppTitle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //Deriving classes should always call through to the base implementation.

        //for now i don't want menu on main activity
        //getMenuInflater().inflate(R.menu.main, menu);
        return  super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        //
        // The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //not required for now
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation thisLayout item clicks here.
        int id = item.getItemId();
        Intent intent=null;

        switch (id){
            case R.id.nav_Add_Phone:
                intent=new Intent(this,QuickContact.class);
                break;
            case R.id.nav_send_sms:
                intent=new Intent(this,SendSMSActivity.class);
                break;
            case R.id.nav_admin:
                intent=new Intent(this,AdminActivity.class);
                break;
            case R.id.nav_help:
                intent=new Intent(this,HelpActivity.class);
                break;
            case R.id.nav_groups:
                intent=new Intent(this,GroupsListActivity.class);
                break;
            case R.id.nav_history:
                intent=new Intent(this,HistoryActivity.class);
                break;
            case R.id.nav_device_info:
                intent=new Intent(this,DeviceInfoActivity.class);
                break;
        }

        if(intent!=null)
            startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        //return true was the default implementation by Android Studio once i chose the navigation drawer template
        return true;
    }

    public boolean hasAllPermissions() {

        //in case of older android versions, this function will return true
        Boolean result = true;
        try{
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (String permission : Utilities.PERMISSIONS_ALL) {
                    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                        lstPermissionsMissing.add(permission);
                        result = false;
                    }
                }
            }
        }
        catch (Exception ex){
            Toast.makeText(this, "Error while checking permissions", Toast.LENGTH_SHORT).show();
            Crashlytics.log("hasAllPermissions-" + ex.getMessage());
        }

        return result;
    }

    private void requestMissingPermissions(){
        try{
            //we will request only the missing permissions
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(lstPermissionsMissing.size()>0){
                    String[] PERMISSIONS_MISSING = lstPermissionsMissing.toArray(new String[lstPermissionsMissing.size()]);
                    ActivityCompat.requestPermissions(this,PERMISSIONS_MISSING, 1000);
                }
            }
        }
        catch (Exception ex){
            Toast.makeText(this, "Error while requesting permissions", Toast.LENGTH_SHORT).show();
            Crashlytics.log("requestMissingPermissions-" + ex.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Boolean areAllPermissionGranted =true;

        for(int grantResult : grantResults){
            if(grantResult==PackageManager.PERMISSION_DENIED){
                areAllPermissionGranted =false;
                break;
            }
        }

        if(areAllPermissionGranted){
            //if device id is not already fetched, fetch and store it
            if(!Utilities.isStringANumber(customer.getDeviceId())){
                fetchAndSaveDeviceID();
            }

        }
        else{
            Toast.makeText(this, "Error - App needs all permissions!", Toast.LENGTH_LONG).show();
        }
    }

    private void loadAppTitle(){
        tvAppTitle.setText(customer.getAppTitle());
        tvAppSubTitle.setText(customer.getAppSubTitle());
    }

    private void fetchAndSaveDeviceID(){
        String deviceID = Utilities.findDeviceID(MainActivity.this);
        try{
            realm.beginTransaction();
            customer.setDeviceId(deviceID);
            realm.commitTransaction();

        }catch (Exception ex){
            Crashlytics.log("fetchAndSaveDeviceID-" + ex.getMessage());
        }
    }
}