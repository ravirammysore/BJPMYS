package in.appfocus.bjpmys;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.appfocus.bjpmys.models.ReportItem;

public class DeliveryReport extends AppCompatActivity {

    String pushid=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pushid = getIntent().getStringExtra("pushid");
        fetchDeliveryReport();
    }

    private void fetchDeliveryReport(){

        String url=null;
        url = "http://smsalertbox.com/api/dlr.php?uid=626a706d7973&pin=Fa3a6e79a945dfbeb0369647865e13a1&pushid="+pushid+"&rtype=json";
        final ArrayList<ReportItem> reportItems = new ArrayList<>();

        /*
        The JsonArrayRequest object is created via a constructor with 3 args:
        1)url
        2)ResponseListener<JSONArray>
        3)ErrorListener
        */
        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        TextView tvDeliveryReport = (TextView)findViewById(R.id.tvDelipveryReport);
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

                            }
                            catch(JSONException e) {
                                //TODO: something?
                                strReport = "Could not download!";
                            }
                            catch (Exception e){
                                //TODO: something?
                            }
                        }
                        tvDeliveryReport.setText(strReport);
                        Log.d("mytag",reportItems.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //TODO: something?
                    }
                }); //end of constructor!

        // Add the request to the queue
        Volley.newRequestQueue(this).add(request);
    }
}
