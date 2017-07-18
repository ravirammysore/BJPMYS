package in.appfocus.bjpmys;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import in.appfocus.bjpmys.helpers.Utilities;
import in.appfocus.bjpmys.models.Contact;
import in.appfocus.bjpmys.models.Group;
import io.realm.Realm;

public class CreateContactActivity extends AppCompatActivity {
    Realm realm;
    String groupId;
    EditText etName,etPhone,etContactNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Create Contact");
        groupId=getIntent().getStringExtra("groupId");

        realm=Realm.getDefaultInstance();

        etName = (EditText)findViewById(R.id.etContactName);
        etPhone = (EditText)findViewById(R.id.etPhoneNo);
        etContactNotes = (EditText)findViewById(R.id.etContactNotes);

        Utilities.checkContactsPermissions(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Deriving classes should always call through to the base implementation.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.edit_contact,menu);
        //as per google's documentation
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.actionSaveContact){
            if(Utilities.isInputGiven(etName,etPhone))
                saveContact();
            //true since we handled it
            return true;
        }

        if(id==R.id.actionDeleteContact){
            //true since we handled it
            //to do:just clear text
            return true;
        }

        if(id==R.id.actionImportContact){
            importContact();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveContact(){

        final Contact contact = new Contact(etName.getText().toString(),
                etPhone.getText().toString(),
                etContactNotes.getText().toString());


        final Group group = realm.where(Group.class)
                .equalTo("id",groupId)
                .findFirst();

        try{
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    group.getContacts().add(contact);
                }
            });
            Toast.makeText(this, "Contact Added", Toast.LENGTH_SHORT).show();

        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void importContact(){
        //Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        
        /* I am using the below config as this will present a UI which will 
        let me pick specific number out of many, for a given person. This a perfect match for the
         given situation!
         
         I have not done much R&D on contacts querying for now*/
        Intent intent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, 200);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if(reqCode!=200) return;

        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            // getData() method will have the Content Uri (like a path) of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the (selected) phone number (possibly out of many)
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            etName.setText(name);
            etPhone.setText(phoneNo);
            
        } catch (Exception e) {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }
}
