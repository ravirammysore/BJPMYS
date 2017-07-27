package in.appfocus.messageit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import in.appfocus.messageit.helpers.Utilities;
import in.appfocus.messageit.models.Contact;
import io.realm.Realm;

public class EditContactActivity extends AppCompatActivity {
    String contactId;
    Realm realm;
    EditText etName,etPhone,etContactNotes, etDOB, etDOA;
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
        etDOB = (EditText)findViewById(R.id.etDOB);
        etDOA = (EditText)findViewById(R.id.etDOA);
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

        if(id==R.id.actionSaveContact) {
            if (Utilities.isInputGiven(etName, etPhone)) {
                int lenth = etPhone.getText().length();
                //9901242044 09901242044 919901242044 +919901242044
                if (lenth >= 10 && lenth <= 13)
                    saveContact();
                else
                    etPhone.setError("check Phone no");
                //true since we handled it
                return true;
            }
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

    public void btnSelectDOBClicked(View v){
        startActivityForResult(new Intent(this,DateSelectActivity.class),100);
    }

    public void btnSelectAnniversaryClicked(View v){
        startActivityForResult(new Intent(this,DateSelectActivity.class),200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            if(resultCode==RESULT_OK){
                etDOB.setText("Selected");
            }
            return;
        }

        if(requestCode==200){
            if(resultCode==RESULT_OK){
                etDOA.setText("Selected");
            }
            return;
        }
    }
}
