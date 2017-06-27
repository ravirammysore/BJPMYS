package in.appfocus.bjpmys;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.appfocus.bjpmys.models.ReportItem;
import io.realm.Realm;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void fetchStats(View v){
        makeVolleyRequest();
    }

    private void makeVolleyRequest(){
        String url=null;
        url = "http://smsalertbox.com/api/dlr.php?uid=626a706d7973&pin=Fa3a6e79a945dfbeb0369647865e13a1&pushid=164086464&rtype=json";
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
                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ReportItem reportItem = new ReportItem();

                                reportItem.setSmsid(jsonObject.getString("smsid"));
                                reportItem.setMobile(jsonObject.getString("mobile"));
                                reportItem.setStatus(jsonObject.getString("status"));

                                reportItems.add(reportItem);
                            }
                            catch(JSONException e) {
                                //TODO: something?
                            }
                            catch (Exception e){
                                //TODO: something?
                            }
                        }
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
