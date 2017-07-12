package in.appfocus.bjpmys;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import in.appfocus.bjpmys.models.Contact;
import in.appfocus.bjpmys.adapters.ContactAdapter;
import in.appfocus.bjpmys.models.Group;
import io.realm.Realm;
import io.realm.RealmList;

public class ContactListActivity extends AppCompatActivity {

    Realm realm;
    String groupId =null;
    ContactAdapter contactAdapter;
    RealmList lstContacts;
    Group group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getApplicationContext(),CreateContactActivity.class)
                       .putExtra("groupId",groupId));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //i have set android:launchMode="singleTop" for this activity
        //otherwise when up button is used, a new instance of this activity
        // will be created and then this intent extra will be null!
        groupId = getIntent().getStringExtra("groupId");

        realm = Realm.getDefaultInstance();

        group = realm.where(Group.class)
                .equalTo("id",groupId)
                .findFirst();
        lstContacts = group.getContacts();

        ListView lvContacts = (ListView) findViewById(R.id.lvContacts);
        contactAdapter = new ContactAdapter(this, lstContacts);
        lvContacts.setAdapter(contactAdapter);

        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Contact contact = (Contact)adapterView.getItemAtPosition(i);
                startActivity(new Intent(ContactListActivity.this,EditContactActivity.class)
                        .putExtra("contactId",contact.getId()));
                //Toast.makeText(ContactListActivity.this, contact.getNote(), Toast.LENGTH_SHORT).show();
            }
        });

        Log.d("mytag","onCreate-CLActivity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Deriving classes should always call through to the base implementation.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.settings_group,menu);
        //as per google's documentation
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //since this activity will not be recreated (singleTop), refreshing the data will be useful
        //when we are coming back to this activity after any modifications to the underlying data
        setTitle("Contacts in "+group.getName());
        contactAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("mytag","onDestroy-CLActivity");
        realm.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.actionManageGroup){
           //showSnack("To edit group info");
            //true since we handled it
            Intent intent = new Intent(this,EditGroupActivity.class);
            intent.putExtra("groupId",groupId);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSnack(String msg){
        CoordinatorLayout view = (CoordinatorLayout)findViewById(R.id.layoutContactList);
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }
}
