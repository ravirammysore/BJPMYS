package in.appfocus.bjpmys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.appfocus.bjpmys.models.Contact;
import in.appfocus.bjpmys.models.ContactAdapter;
import in.appfocus.bjpmys.models.Group;
import in.appfocus.bjpmys.models.History;
import in.appfocus.bjpmys.models.HistoryAdapter;
import in.appfocus.bjpmys.models.ReportItem;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class HistoryActivity extends AppCompatActivity {
    Realm realm;
    String groupId =null;
    HistoryAdapter historyAdapter;
    RealmResults lstHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm = Realm.getDefaultInstance();

        lstHistory = realm.where(History.class).findAll();

        ListView lvHistory = (ListView) findViewById(R.id.lvHistory);
        historyAdapter = new HistoryAdapter(this, lstHistory);
        lvHistory.setAdapter(historyAdapter);

        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Contact contact = (Contact)adapterView.getItemAtPosition(i);
                //startActivity(new Intent(ContactListActivity.this,EditContactActivity.class)
                  //      .putExtra("contactId",contact.getId()));
                //Toast.makeText(ContactListActivity.this, contact.getNote(), Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("mytag","onDestroy-CLActivity");
        realm.close();
    }
}
