package in.appfocus.messageit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import in.appfocus.messageit.models.History;
import in.appfocus.messageit.adapters.HistoryAdapter;
import io.realm.Realm;
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
