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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.appfocus.messageit.helpers.Utilities;
import in.appfocus.messageit.models.Contact;
import io.realm.Realm;

public class EditContactActivity extends AppCompatActivity {
    String contactId;
    Realm realm;
    EditText etName,etPhone,etContactNotes, etDOB, etDOA;
    Contact contact;
    Date dateDob = null, dateDoa = null;

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
        if(contact.getDob()!=null) {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String strDob = df.format(contact.getDob());
            etDOB.setText(strDob);
        }
        if(contact.getDoa()!=null) {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String strDoa = df.format(contact.getDoa());
            etDOA.setText(strDoa);
        }
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
           if(isFormValid())
               saveContact();
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
                    contact.setDob(dateDob);
                    contact.setDoa(dateDoa);
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
        startActivityForResult(new Intent(this,DateSelectActivity.class),1000);
    }

    public void btnSelectAnniversaryClicked(View v){
        startActivityForResult(new Intent(this,DateSelectActivity.class),2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode==RESULT_OK){
                String result = data.getStringExtra("result");
                etDOB.setText(result);
            }
            return;
        }

        if(requestCode==2000){
            if(resultCode==RESULT_OK){
                String result = data.getStringExtra("result");
                etDOA.setText(result);
            }
            return;
        }
    }

    private Boolean isFormValid(){
        Boolean isFormValid = false;

        Boolean isNameValid =false, isPhoneValid = false,
                isDOBValid =false, isDOAValid =false;

        //validate contact name
        if(Utilities.isInputGiven(etName)) isNameValid = true;

        //validate contact phone
        if(Utilities.isInputGiven(etPhone)){
            int length = etPhone.getText().length();
            //9901242044 09901242044 919901242044 +919901242044
            if (length >= 10 && length <= 13)
                isPhoneValid = true;
            else
                etPhone.setError("check number");

        }

        //validate contact DOB
        if(etDOB.getText().toString().equalsIgnoreCase(""))
            isDOBValid=true;
        else
        {
            String strDate = etDOB.getText().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
            dateFormat.setLenient(false);
            try {
                dateDob = dateFormat.parse(strDate);
                isDOBValid = true;
            } catch (ParseException e) {
                etDOB.setError("Check date!");
            }
        }

        //validate contact DOA
        if(etDOA.getText().toString().equalsIgnoreCase(""))
            isDOAValid=true;
        else
        {
            String strDate = etDOA.getText().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
            dateFormat.setLenient(false);
            try {
                dateDoa = dateFormat.parse(strDate);
                isDOAValid = true;
            } catch (ParseException e) {
                etDOA.setError("Check date!");
            }
        }

        //contact notes is valid in any form, so not considering it.

        if(isNameValid && isPhoneValid && isDOBValid && isDOAValid)
            isFormValid = true;

        return  isFormValid;
    }
}
