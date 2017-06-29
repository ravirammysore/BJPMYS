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
                History history = (History)adapterView.getItemAtPosition(i);
                startActivity(new Intent(HistoryActivity.this,DeliveryReport.class)
                        .putExtra("pushid",history.getPushid()));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("mytag","onDestroy-CLActivity");
        realm.close();
    }
}
