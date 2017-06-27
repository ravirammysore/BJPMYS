package in.appfocus.bjpmys;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import in.appfocus.bjpmys.helpers.Utilities;
import in.appfocus.bjpmys.models.Contact;
import in.appfocus.bjpmys.models.Group;
import in.appfocus.bjpmys.models.Settings;
import in.appfocus.bjpmys.models.Customer;
import io.realm.Realm;
import io.realm.RealmResults;

public class SendSMSActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Realm realm;
    RealmResults<Group> groupsAll;
    Group groupSelected;
    List<String> selectedPhoneNumbers;
    EditText etSMSContent;
    String url;
    CoordinatorLayout thisLayout;

    ProgressDialog progressDialog;
    Settings settings;
    Customer customer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        etSMSContent = (EditText)findViewById(R.id.etSMSContent);
        thisLayout = (CoordinatorLayout) findViewById(R.id.layoutSendSMS);
        progressDialog = new ProgressDialog(SendSMSActivity.this);

        selectedPhoneNumbers = new ArrayList<>();

        realm = Realm.getDefaultInstance();

        settings = realm.where(Settings.class).findFirst();
        customer = realm.where(Customer.class).findFirst();

        loadAllGroupsToSpinner();


    }

    private void loadAllGroupsToSpinner() {

        groupsAll = realm.where(Group.class).findAll();

        //making a copy of all groups as i don't want to alter originals;
        List<Group> groupListToBind = new ArrayList<>(groupsAll);
        //at start
        groupListToBind.add(0,new Group("SELECT_A_GROUP", "Please select a group", "SomeNote"));
        //add at end
        groupListToBind.add(new Group("ALL", "All Groups", "SomeNote"));

        //This din work, will leave it for now.
        /*ArrayAdapter<Group> adapter = new ArrayAdapter<>(this, R.layout.spinner_textview_custom,groupListToBind);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

        ArrayAdapter<Group> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupListToBind);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Deriving classes should always call through to the base implementation.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.send_sms, menu);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        ((TextView) adapterView.getChildAt(0)).setTextSize(18);

        //get the selected item
        groupSelected = (Group) adapterView.getItemAtPosition(i);

        //first reset the list!
        selectedPhoneNumbers.clear();

        if (groupSelected.getId().equals("SELECT_A_GROUP")) {
            //do nothing
            return;
        }
        else if(groupSelected.getId().equals("ALL")){
            //extractAllPhoneNos
            for (Group g : groupsAll) {
                for (Contact c : g.getContacts()) {
                    selectedPhoneNumbers.add(c.getMobileNo());
                }
            }
        }
        else {
            //extractSpecifiedGroupPhoneNos
            for (Contact c : groupSelected.getContacts()) {
                selectedPhoneNumbers.add(c.getMobileNo());
            }
        }

        Snackbar.make(thisLayout, "Total contacts in group:" + selectedPhoneNumbers.size(),
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.actionSend) {
            sendSMS();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendSMS() {

        if(settings.getNoOfSends()>20){
            Toast.makeText(this, "Trial Expired!", Toast.LENGTH_SHORT).show();
            finish();
        }

        if(selectedPhoneNumbers.size()==0){
            Snackbar.make(thisLayout, "No contacts selected!",
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        if(Utilities.isInputGiven(etSMSContent)){
            prepareURL();
            makeVolleyRequest();
        }
    }

    private void prepareURL(){
        String webAddr, uid,sid,apiPin,route,mobileNos,messageEncoded=null;

        apiPin = customer.getApiPin();
        uid =customer.getUid();
        sid= customer.getSenderId();
        route = customer.getRoute();
        webAddr = settings.getSmsGatewayUrl();
        mobileNos = prepareMobileNosList();
        try {
            messageEncoded = URLEncoder.encode(etSMSContent.getText().toString(),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        url = MessageFormat.format("{0}?uid={1}&pin={2}&sender={3}" +
                "&route={4}&mobile={5}&message={6}",
                webAddr,uid,apiPin,sid,route,mobileNos,messageEncoded);

        Log.d("mytag",url);
    }

    private String prepareMobileNosList(){
        StringBuilder sb = new StringBuilder();
        for(String s:selectedPhoneNumbers) {
            sb.append(s);
            sb.append(",");
        }
        return sb.toString();
    }

    private void makeVolleyRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        progressDialog.dismiss();
                        //Toast.makeText(SendSMSActivity.this, "Sent!", Toast.LENGTH_LONG).show();
                        Snackbar.make(thisLayout, "Message sent!",
                                Snackbar.LENGTH_LONG).show();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                settings.incrementNoOfSends();
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                progressDialog.dismiss();
                Toast.makeText(SendSMSActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the queue
        Volley.newRequestQueue(this).add(stringRequest);
        progressDialog.setMessage("Please wait!");
        //show progressDialog
        progressDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }


    //I thought of this approach as the progressDialog.dismiss() will not dismiss when called
    //from the main thread! But i guess volley is handling the request/response in different
    //threads, i will forget the below method. But for any other scenarios, the below
    //approach works well

    /*private class SendSMSAsync extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(SendSMSActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //set message of the progressDialog
            progressDialog.setMessage("Please wait!");
            //show progressDialog
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //simulate some network delay
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(SendSMSActivity.this, "Message Sent!", Toast.LENGTH_LONG).show();
            finish();
        }
    }*/
}



