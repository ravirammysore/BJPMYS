package in.appfocus.messageit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.appfocus.messageit.adapters.GroupAdapter;
import in.appfocus.messageit.helpers.Utilities;
import in.appfocus.messageit.models.Contact;
import in.appfocus.messageit.models.Group;
import io.realm.Realm;
import io.realm.RealmResults;

public class QuickContact extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Realm realm;
    RealmResults<Group> groupsAll;
    Group groupSelected;

    EditText etName,etPhone,etContactNotes, etDOB, etDOA;
    Date dateDob = null, dateDoa = null;

    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFormValid()){
                    saveContact();
                }
                /*Snackbar.make(view, "Can be saved!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etName = (EditText)findViewById(R.id.etContactName);
        etPhone = (EditText)findViewById(R.id.etPhoneNo);
        etContactNotes = (EditText)findViewById(R.id.etContactNotes);
        etDOB = (EditText)findViewById(R.id.etDOB);
        etDOA = (EditText)findViewById(R.id.etDOA);

        spinner = (Spinner) findViewById(R.id.spinner);

        realm = Realm.getDefaultInstance();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //remember - onResume will be always called :)

        //i din't want to take the approach of refreshing the adapter because
        //i am binding a copy of realm list

        //later we can see some efficient way of doing this!
        loadAllGroupsToSpinner();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void loadAllGroupsToSpinner() {

        groupsAll = realm.where(Group.class).findAll();

        //making a copy of all groups as i don't want to alter originals;
        List<Group> groupListToBind = new ArrayList<>(groupsAll);
        //at start
        groupListToBind.add(0,new Group("SELECT_A_GROUP", "Please select a group", "SomeNote"));

        ArrayAdapter<Group> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupListToBind);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        ((TextView) adapterView.getChildAt(0)).setTextSize(18);

        //get the selected item
        groupSelected = (Group) adapterView.getItemAtPosition(i);

       /* if (groupSelected.getId().equals("SELECT_A_GROUP")) {
            //do nothing
            return;
        }*/

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void btnCreateNewGroupClicked(View v){
        startActivity(new Intent(this,CreateGroupActivity.class));
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
                isDOBValid =false, isDOAValid =false, isGroupValid=false;

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

        //validate group
        if(!groupSelected.getId().equals("SELECT_A_GROUP"))
            isGroupValid=true;
        else
            ((TextView)spinner.getSelectedView()).setError("");

        if(isNameValid && isPhoneValid && isDOBValid
                && isDOAValid && isGroupValid)
            isFormValid = true;

        return isFormValid;
    }

    private void saveContact(){

        final Contact contact = new Contact();
        contact.setName(etName.getText().toString());
        contact.setMobileNo(etPhone.getText().toString());
        contact.setNote(etContactNotes.getText().toString());
        contact.setDob(dateDob);
        contact.setDoa(dateDoa);

      /*  final Group group = realm.where(Group.class)
                .equalTo("id",groupId)
                .findFirst();*/

        try{
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    groupSelected.getContacts().add(contact);
                }
            });
            Toast.makeText(this, "Contact Added", Toast.LENGTH_SHORT).show();

        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
