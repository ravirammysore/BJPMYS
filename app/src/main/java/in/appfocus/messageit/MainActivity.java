package in.appfocus.messageit;

import android.Manifest;
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
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import in.appfocus.messageit.helpers.Utilities;
import in.appfocus.messageit.models.Customer;
import in.appfocus.messageit.models.Group;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Realm realm;
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

        realm = Realm.getDefaultInstance();

        if(!hasPermissions())
            requestAllPermissions();
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
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    public boolean hasPermissions() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : Utilities.PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    private void requestAllPermissions(){
        ActivityCompat.requestPermissions(this, Utilities.PERMISSIONS, 1000);
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
            FindDeviceInfoIfNotDone();

        }
        else{
            Toast.makeText(this, "Error - App needs all permissions!", Toast.LENGTH_LONG).show();
        }
    }

    public void FindDeviceInfoIfNotDone(){
        Customer customer = realm.where(Customer.class).findFirst();
        String deviceID=customer.getDeviceId();

        //if device ID not already fetched
        if(!Utilities.isStringANumber(deviceID)){
            try{
                TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
                deviceID = tm.getDeviceId();

            //this will not happen as we are checking before itself, but this is more for compliance
            }catch (SecurityException ex){
                deviceID = "Permission denied for device id";
            }
            catch (Exception ex){
                deviceID = "Problem finding device id";
                Crashlytics.log(ex.getMessage());
            }
        }

        try{
            realm.beginTransaction();
            customer.setDeviceId(deviceID);
            realm.commitTransaction();
        }catch (Exception ex){
            Crashlytics.log(ex.getMessage());
        }
    }
}