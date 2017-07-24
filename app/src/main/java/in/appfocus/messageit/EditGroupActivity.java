package in.appfocus.messageit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import in.appfocus.messageit.helpers.Utilities;
import in.appfocus.messageit.models.Group;
import io.realm.Realm;

public class EditGroupActivity extends AppCompatActivity {
    String strGroupId = null;
    Realm realm;
    Group group;
    EditText etGrpName,etGrpNotes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm = Realm.getDefaultInstance();

        setTitle("Edit Group");
        strGroupId = getIntent().getStringExtra("groupId");

        etGrpName = (EditText)findViewById(R.id.etGroupName);
        etGrpNotes = (EditText)findViewById(R.id.etGroupNote);
        
        displayGroupDetails();
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

    private void displayGroupDetails(){
        group = realm.where(Group.class).equalTo("id",strGroupId).findFirst();

        etGrpName.setText(group.getName());
        etGrpNotes.setText(group.getNote());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
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
            deleteGroup();
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
                    group.setName(etGrpName.getText().toString());
                    group.setNote(etGrpNotes.getText().toString());
                }
            });
            Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();

        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        finish();
    }

    private void deleteGroup(){
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Do you really want to delete the group?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    group.deleteFromRealm();
                                    Toast.makeText(EditGroupActivity.this, "Group Deleted!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }catch (Exception ex){
                            Toast.makeText(EditGroupActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        //after deleting the group, don't return to contact list!!
                        Intent intent = new Intent(EditGroupActivity.this,GroupsListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}
