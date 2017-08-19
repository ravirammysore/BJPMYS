package in.appfocus.messageit;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.onegravity.contactpicker.contact.ContactDescription;
import com.onegravity.contactpicker.contact.ContactSortOrder;
import com.onegravity.contactpicker.core.ContactPickerActivity;
import com.onegravity.contactpicker.picture.ContactPictureType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import in.appfocus.messageit.models.Contact;
import in.appfocus.messageit.adapters.ContactAdapter;
import in.appfocus.messageit.models.Group;
import io.realm.Realm;
import io.realm.RealmList;

import static android.provider.ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;

public class ContactListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

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

        // Associate searchable configuration with the SearchView
        /*SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        *//*SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();*//*

        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);*/

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

        //this is working well, but will do it later, no time now!
        /*if(id==R.id.actionImportMultipleContacts){
            importMultipleContacts();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void showSnack(String msg){
        CoordinatorLayout view = (CoordinatorLayout)findViewById(R.id.layoutContactList);
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

    private void importMultipleContacts(){
        //https://github.com/1gravity/Android-ContactPicker

        Intent intent = new Intent(getApplicationContext(), ContactPickerActivity.class)
                //.putExtra(ContactPickerActivity.EXTRA_THEME, mDarkTheme ? R.style.Theme_Dark : R.style.Theme_Light)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_BADGE_TYPE, ContactPictureType.ROUND.name())
                .putExtra(ContactPickerActivity.EXTRA_SHOW_CHECK_ALL, false)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION, ContactDescription.ADDRESS.name())
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .putExtra(ContactPickerActivity.EXTRA_ONLY_CONTACTS_WITH_PHONE,true)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_SORT_ORDER, ContactSortOrder.AUTOMATIC.name());
        startActivityForResult(intent, 5000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 5000 && resultCode == Activity.RESULT_OK &&
                data != null && data.hasExtra(ContactPickerActivity.RESULT_CONTACT_DATA)) {

            // we got a result from the contact picker
            // process contacts
            try{
                List<com.onegravity.contactpicker.contact.Contact> contacts =
                        (List<com.onegravity.contactpicker.contact.Contact>)
                                data.getSerializableExtra(ContactPickerActivity.RESULT_CONTACT_DATA);

                for (com.onegravity.contactpicker.contact.Contact contact : contacts) {
                    // process the contacts...
                    //this is working well, but will do it later, no time now!
                    Log.d("mytag",contact.getPhone(TYPE_MOBILE));
                }
            }
            catch (Exception ex){

            }
        }
    }

    /**
     * Called when the user submits the query. This could be due to a key press on the
     * keyboard or due to pressing a submit button.
     * The listener can override the standard behavior by returning true
     * to indicate that it has handled the submit request. Otherwise return false to
     * let the SearchView handle the submission by launching any associated intent.
     *
     * @param query the query text that is to be submitted
     * @return true if the query has been handled by the listener, false to let the
     * SearchView perform the default action.
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Called when the query text is changed by the user.
     *
     * @param newText the new content of the query text field.
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    @Override
    public boolean onQueryTextChange(String newText) {

        /*try {
            contactAdapter.getFilter().filter(newText);
        }
        catch (Exception ex){
             Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
             return true;
        }*/
       return false;
    }
}


