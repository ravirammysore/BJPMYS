package in.appfocus.messageit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import in.appfocus.messageit.helpers.Utilities;
import in.appfocus.messageit.models.Group;
import io.realm.Realm;

public class CreateGroupActivity extends AppCompatActivity  {
    Realm realm;
    EditText etGrpName,etGrpNotes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // I am using same layout file for both edit and create group
        //since they look almost same!
        setContentView(R.layout.activity_edit_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Create Group");
        realm = Realm.getDefaultInstance();

        etGrpName = (EditText)findViewById(R.id.etGroupName);
        etGrpNotes = (EditText)findViewById(R.id.etGroupNote);
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
        super.onCreateOptionsMenu(menu);
        //same menu for both edit and create group
        getMenuInflater().inflate(R.menu.edit_group,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.actionSaveGroupInfo) {
            if(Utilities.isInputGiven(etGrpName))
                saveGroup();
            return true;
        }
        if (id == R.id.actionDeleteGroup) {
            //to do
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveGroup(){
        //realm is crash safe - means data will be safe, but exception will occur!
        //also use realm transaction, it is safer and neat approach
        try{
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Group group = new Group(etGrpName.getText().toString(),
                            etGrpNotes.getText().toString());
                    realm.copyToRealm(group);
                }
            });
            Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();

        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        finish();
    }
}
