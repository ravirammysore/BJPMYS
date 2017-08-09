package in.appfocus.messageit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;

import in.appfocus.messageit.models.Customer;
import in.appfocus.messageit.models.ReportItem;
import in.appfocus.messageit.models.Settings;
import io.realm.Realm;

public class DeliveryReport extends AppCompatActivity {

    String pushid=null;
    Realm realm;
    Settings settings;
    Customer customer;
    TextView tvDeliveryReport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvDeliveryReport = (TextView)findViewById(R.id.tvDelipveryReport);

        realm = Realm.getDefaultInstance();
        settings = realm.where(Settings.class).findFirst();
        customer = realm.where(Customer.class).findFirst();

        pushid = getIntent().getStringExtra("pushid");
        fetchDeliveryReport();
    }

    private void fetchDeliveryReport(){

        String strDeliveryReportUrl = MessageFormat.format("{0}?uid={1}&pin={2}&pushid={3}&rtype=json",
                settings.getSmsDeliveryReportUrl(),customer.getUid(),customer.getApiPin(),pushid);

        //currently not using a list view to show report, may be later
        final ArrayList<ReportItem> reportItems = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, strDeliveryReportUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        try {
                            //if the response is a plain string message (which cannot be converted to array)
                            //then the below line will throw JSONException
                            JSONArray jsonArray = new JSONArray(response);
                            //if we successfully got a json array, parse it and convert to a java bean
                            //currently i am not really using the java bean, but in future might store it to db
                            tvDeliveryReport.setMovementMethod(new ScrollingMovementMethod());
                            String strReport = "";
                            for(int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ReportItem reportItem = new ReportItem();

                                reportItem.setSmsid(jsonObject.getString("smsid"));
                                reportItem.setMobile(jsonObject.getString("mobile"));
                                reportItem.setStatus(jsonObject.getString("status"));

                                reportItems.add(reportItem);

                                strReport += "\n"+reportItem.getMobile()+ ":" + reportItem.getStatus() + "\n";
                                tvDeliveryReport.setText(strReport);

                            }

                        } catch (JSONException e) {
                            //error in converting to json array means we got some error message from a server
                            //so just show it to user as it is!
                            tvDeliveryReport.setText(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvDeliveryReport.setText(error.getMessage());
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringRequest.setShouldCache(false);
        // Add the request to the queue
        Volley.newRequestQueue(this).add(stringRequest);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    //not using this, since it could not display any string response got from server
    private void fetchDeliveryReportOldVersion(){

        String strDeliveryReportUrl = MessageFormat.format("{0}?uid={1}&pin={2}&pushid={3}&rtype=json",
                settings.getSmsDeliveryReportUrl(),customer.getUid(),customer.getApiPin(),pushid);

        final ArrayList<ReportItem> reportItems = new ArrayList<>();

        /*
        The JsonArrayRequest object is created via a constructor with 3 args:
        1)strUrlSendSMS
        2)ResponseListener<JSONArray>
        3)ErrorListener
        */
        JsonArrayRequest request = new JsonArrayRequest(strDeliveryReportUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        tvDeliveryReport.setMovementMethod(new ScrollingMovementMethod());
                        String strReport = "";
                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ReportItem reportItem = new ReportItem();

                                reportItem.setSmsid(jsonObject.getString("smsid"));
                                reportItem.setMobile(jsonObject.getString("mobile"));
                                reportItem.setStatus(jsonObject.getString("status"));

                                reportItems.add(reportItem);

                                strReport += "\n"+reportItem.getMobile()+ ":" + reportItem.getStatus() + "\n";
                                tvDeliveryReport.setText(strReport);

                            }
                            catch(JSONException e) {
                                //TODO: something?
                                tvDeliveryReport.setText("Could not load, Try again.");
                            }
                            catch (Exception e){
                                //TODO: something?
                                tvDeliveryReport.setText("Could not load, Try again.");
                            }
                        }
                        Log.d("mytag",reportItems.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //TODO: something?
                        tvDeliveryReport.setText("Could not load, Try again.");
                    }
                }); //end of constructor!

        // Add the request to the queue
        Volley.newRequestQueue(this).add(request);
    }
}
