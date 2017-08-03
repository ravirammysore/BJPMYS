package in.appfocus.messageit;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.appfocus.messageit.models.Contact;
import in.appfocus.messageit.models.Group;
import io.realm.Realm;
import io.realm.RealmResults;

public class QuickContact extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Realm realm;
    RealmResults<Group> groupsAll;
    Group groupSelected;
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm = Realm.getDefaultInstance();

        loadAllGroupsToSpinner();
    }

    private void loadAllGroupsToSpinner() {

        groupsAll = realm.where(Group.class).findAll();

        //making a copy of all groups as i don't want to alter originals;
        List<Group> groupListToBind = new ArrayList<>(groupsAll);
        //at start
        groupListToBind.add(0,new Group("SELECT_A_GROUP", "Please select a group", "SomeNote"));

        ArrayAdapter<Group> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupListToBind);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
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
}
