package in.appfocus.bjpmys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import in.appfocus.bjpmys.helpers.Utilities;
import in.appfocus.bjpmys.models.Contact;
import io.realm.Realm;

public class EditContactActivity extends AppCompatActivity {
    String contactId;
    Realm realm;
    EditText etName,etPhone,etContactNotes;
    Contact contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Edit Contact");
        contactId = getIntent().getStringExtra("contactId");
        realm = Realm.getDefaultInstance();

        etName = (EditText)findViewById(R.id.etContactName);
        etPhone = (EditText)findViewById(R.id.etPhoneNo);
        etContactNotes = (EditText)findViewById(R.id.etContactNotes);

        showContactDetails();
    }

    private void showContactDetails() {

        contact = realm.where(Contact.class).equalTo("id",contactId).findFirst();

        etName.setText(contact.getName());
        etPhone.setText(contact.getMobileNo());
        etContactNotes.setText(contact.getNote());
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
            if(Utilities.isInputGiven(etName,etPhone,etContactNotes))
                saveContact();
            //true since we handled it
            return true;
        }

        if(id==R.id.actionDeleteContact){
            deleteContact();
            //true since we handled it
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveContact(){
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    contact.setName(etName.getText().toString());
                    contact.setMobileNo(etPhone.getText().toString());
                    contact.setNote(etContactNotes.getText().toString());
                }
            });
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void deleteContact(){
        try{
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    contact.deleteFromRealm();
                }
            });
            Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
