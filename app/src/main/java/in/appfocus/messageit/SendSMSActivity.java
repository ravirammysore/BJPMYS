package in.appfocus.messageit;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.appfocus.messageit.helpers.Utilities;
import in.appfocus.messageit.models.Contact;
import in.appfocus.messageit.models.Group;
import in.appfocus.messageit.models.History;
import in.appfocus.messageit.models.Settings;
import in.appfocus.messageit.models.Customer;
import io.realm.Realm;
import io.realm.RealmResults;

public class SendSMSActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Realm realm;
    RealmResults<Group> groupsAll;
    Group groupSelected;
    List<String> selectedPhoneNumbers;
    EditText etSMSContent;
    String strUrlSendSMS;
    String strUrlCustomerCheck;
    String strUrlBalanceCheck;

    CoordinatorLayout thisLayout;

    ProgressDialog progressDialog;
    Settings settings;
    Customer customer;

    TextView tvCharactersCount;

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prepareTextWidgets();

        selectedPhoneNumbers = new ArrayList<>();

        realm = Realm.getDefaultInstance();

        settings = realm.where(Settings.class).findFirst();
        customer = realm.where(Customer.class).findFirst();

        loadAllGroupsToSpinner();

    }

    private void prepareTextWidgets() {
        etSMSContent = (EditText)findViewById(R.id.etSMSContent);
        thisLayout = (CoordinatorLayout) findViewById(R.id.layoutSendSMS);
        progressDialog = new ProgressDialog(SendSMSActivity.this);
        tvCharactersCount = (TextView)findViewById(R.id.tvCharactersCount);

        //for displaying count on initial load only
        //tvCharactersCount.setText("No of characters: "+String.valueOf(etSMSContent.getText().length()));
        tvCharactersCount.setText("No of characters: "+ etSMSContent.getText().length());

        etSMSContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //This sets a textview to the current length
                //tvCharactersCount.setText("No of characters: "+String.valueOf(charSequence.length()));
                tvCharactersCount.setText("No of characters: "+ charSequence.length());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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

        if (id == R.id.actionCheckBalance) {
            checkBalance();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendSMS() {

        if(selectedPhoneNumbers.size()==0){
            Snackbar.make(thisLayout, "No contacts selected!",
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        if(Utilities.isInputGiven(etSMSContent)){
            beginSMSSendingProcess();
        }
    }

    //There were a couple of other approaches of posting to volley, they are not working!
    //the request body was not going across!! Those methods are still hanging there, unused, after this methods
    //RKR 12/07/2015
    private void beginSMSSendingProcess(){
        Boolean result = true;
        strUrlCustomerCheck = "http://webapplication120170706091816.azurewebsites.net/api/values";

        String senderId, deviceId, noOfContacts;
        Customer cust = realm.where(Customer.class).findFirst();

        if(cust.getSenderId()!=null && !cust.getSenderId().isEmpty())
            senderId = cust.getSenderId();
        else
            senderId = "invalid";

        if(cust.getDeviceId()!=null && !cust.getDeviceId().isEmpty())
            deviceId = cust.getDeviceId();
        else
            deviceId = "invalid";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("SenderId", senderId);
        params.put("DeviceId", deviceId);
        //below value will be cast into integer by ASP.NET framework automatically, since the server side dto wants to receive it as int
        params.put("NoOfContactsSel", String.valueOf(selectedPhoneNumbers.size()));

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Checking Customer Info...");
        pDialog.show();

        JsonObjectRequest req = new JsonObjectRequest(strUrlCustomerCheck, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //do something
                        try {
                            pDialog.hide();
                            String result = response.getString("IsRequestValid");
                            if(result.equalsIgnoreCase("true"))
                                sendSMSRequestToAcharya();
                                //Toast.makeText(SendSMSActivity.this, "Valid Customer", Toast.LENGTH_SHORT).show();
                            else{
                                Toast.makeText(SendSMSActivity.this, "Invalid Customer", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(SendSMSActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //some issue with backend, so we still let the user send SMS!!
                pDialog.hide();
                sendSMSRequestToAcharya();
                //Toast.makeText(SendSMSActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);
    }

    private void beginSMSSendingProcessNeitherWorking(){
        strUrlCustomerCheck = "http://webapplication120170706091816.azurewebsites.net/api/values";

        final String emailId, deviceId, noOfContacts;
        Customer cust = realm.where(Customer.class).findFirst();

        if(cust.getDeviceGmailAccount()!=null && !cust.getDeviceGmailAccount().isEmpty())
            emailId = cust.getDeviceGmailAccount();
        else
            emailId = "invalid";

        if(cust.getDeviceId()!=null && !cust.getDeviceId().isEmpty())
            deviceId = cust.getDeviceId();
        else
            deviceId = "invalid";

        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";
        final String TAG = "mytag";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                strUrlCustomerCheck, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Log.d("myTag","inside getParams");
                Map<String, String> params = new HashMap<String, String>();
                params.put("EmaildID", "ravi.raver@gmail.com");
                params.put("DeviceId", "1234");
                //params.put("NoOfContactsSel", "25");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.d("myTag","inside getHeaders");
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", "Fiddler");
                //Content-Length: 51
                //headers.put("Content-Length","51");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjReq);
    }

    private void beginSMSSendingProcessNotWorking(){

        String myUrl = "http://webapplication120170706091816.azurewebsites.net/api/values";

        String emailId, deviceId, noOfContacts;
        Customer cust = realm.where(Customer.class).findFirst();

        if(cust.getDeviceGmailAccount()!=null && !cust.getDeviceGmailAccount().isEmpty())
            emailId = cust.getDeviceGmailAccount();
        else
            emailId = "invalid";

        if(cust.getDeviceId()!=null && !cust.getDeviceId().isEmpty())
            deviceId = cust.getDeviceId();
        else
            deviceId = "invalid";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                myUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("mytag", response.toString());
                        //pDialog.hide();
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    //Log.d("mytag", "Error: " + error.getMessage());
                    }

                })
                {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("EmaildID", "ravi.raver@gmail.com");
                params.put("DeviceId", "something");
                params.put("NoOfContactsSel", "50");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjReq);
    }

    private String prepareMobileNosList(){
        StringBuilder sb = new StringBuilder();
        for(String s:selectedPhoneNumbers) {
            sb.append(s);
            sb.append(",");
        }
        return sb.toString();
    }

    private void prepareUrlForSendSMS(){
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

        strUrlSendSMS = MessageFormat.format("{0}?uid={1}&pin={2}&sender={3}" +
                "&route={4}&mobile={5}&message={6}&pushid=1&unicode=1",
                webAddr,uid,apiPin,sid,route,mobileNos,messageEncoded);

        Log.d("mytag", strUrlSendSMS);
    }

    private void sendSMSRequestToAcharya(){

        progressDialog.setMessage("Contacting SMS Server!");
        //show progressDialog
        progressDialog.show();

        prepareUrlForSendSMS();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, strUrlSendSMS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        // Result handling
                        progressDialog.dismiss();

                        Calendar calander = Calendar.getInstance();
                        SimpleDateFormat simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String date = simpledateformat.format(calander.getTime());

                        final History history = new History();
                        history.setPushid(response);
                        history.setMessage(etSMSContent.getText().toString());
                        history.setGroup(groupSelected.getName());
                        history.setDate(date);

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                settings.incrementNoOfSends();
                                realm.insertOrUpdate(history);
                            }
                        });
                        Snackbar.make(thisLayout, "Message sent!", Snackbar.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                progressDialog.dismiss();
                Toast.makeText(SendSMSActivity.this, "Unable to send, Try again!", Toast.LENGTH_SHORT).show();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void checkBalance(){
        progressDialog.setMessage("Please wait...");
        //show progressDialog
        progressDialog.show();

        prepareBalanceCheckUrl();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, strUrlBalanceCheck,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        // Result handling
                        progressDialog.dismiss();
                        Snackbar.make(thisLayout, "Balance is "+response, Snackbar.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                progressDialog.dismiss();
                Toast.makeText(SendSMSActivity.this, "Error,Try again!", Toast.LENGTH_SHORT).show();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the queue
        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void prepareBalanceCheckUrl(){
        strUrlBalanceCheck = null;
        //http://yourdomain.com/api/balance.php?uid=your_uid&pin=your_pin&route=0
        String webAddr, uid,sid,apiPin,route,mobileNos,messageEncoded=null;

        webAddr = settings.getSmsBalanceUrl();
        apiPin = customer.getApiPin();
        uid =customer.getUid();
        route = customer.getRoute();

        strUrlBalanceCheck = MessageFormat.format("{0}?uid={1}&pin={2}&route={3}",
                webAddr,uid,apiPin,route);

        Log.d("mytag", strUrlBalanceCheck);


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


