package in.appfocus.messageit;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import in.appfocus.messageit.models.Group;
import io.realm.Realm;

public class CreateContactActivity extends AppCompatActivity {
    Realm realm;
    String groupId;
    EditText etName,etPhone,etContactNotes, etDOB, etDOA;
    Date dateDob = null, dateDoa = null;

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
        etDOB = (EditText)findViewById(R.id.etDOB);
        etDOA = (EditText)findViewById(R.id.etDOA);

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
            if(isFormValid())
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

        final Contact contact = new Contact();
        contact.setName(etName.getText().toString());
        contact.setMobileNo(etPhone.getText().toString());
        contact.setNote(etContactNotes.getText().toString());
        contact.setDob(dateDob);
        contact.setDoa(dateDoa);

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
        startActivityForResult(intent, 100);
    }

    public void btnSelectDOBClicked(View v){
        startActivityForResult(new Intent(this,DateSelectActivity.class),1000);
    }

    public void btnSelectAnniversaryClicked(View v){
        startActivityForResult(new Intent(this,DateSelectActivity.class),2000);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if(reqCode==100){
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

                name = cursor.getString(nameIndex);
                phoneNo = cursor.getString(phoneIndex);

                //remove spaces and hyphens
                phoneNo = phoneNo.replace(" ","");
                phoneNo = phoneNo.replace("-","");

                etName.setText(name);
                etPhone.setText(phoneNo);

            } catch (Exception e) {
                Toast.makeText(this, "Import Failed!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if(reqCode==1000){
            if(resultCode==RESULT_OK){
                String result = data.getStringExtra("result");
                etDOB.setText(result);
            }
            return;
        }

        if(reqCode==2000){
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
