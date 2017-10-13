package in.appfocus.messageit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import in.appfocus.messageit.helpers.ExcelHelper;
import in.appfocus.messageit.models.Group;
import in.appfocus.messageit.adapters.GroupAdapter;
import io.realm.Realm;
import io.realm.RealmResults;

public class GroupsListActivity extends AppCompatActivity {

    Realm realm;
    RealmResults<Group> lstGroups;
    ListView lvGroups;
    GroupAdapter groupAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),CreateGroupActivity.class));
                //Boolean success = ExcelHelper.exportAllGroupsToExcelFiles(getApplicationContext(),realm);
                //if(success) Toast.makeText(GroupsListActivity.this, "Exported all to folder appfocus", Toast.LENGTH_SHORT).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*VVIP

        I am following a 'sequential-light' navigation model approach in this app
        meaning, all non-leaf activities' launch mode is declared as singleTop in manifest
        so that up navigation button will not recreate the parent activities (which it does normally) and mess up!
        The activities will only be resumed (and inside onResume() if am refreshing data)

        If the user presses hard-back button, the same things happen
        So in either cases (back and up) the user has the same experience!

        I call it sequential as the user needs to navigate activities forward and backward
        in natural order, there is no other order possible

        I also say lite, because no activity is recreated (only refreshed) unless required!

        -Ravi K R
        * */

        //No need to change title once created
        setTitle("Groups");

        /*We HAVE TO query here itself, as the adapter will break if list is empty!
        If the underlying data is changed (via realm transaction), just calling the
        adapter.notifyDataSetChanged() will load fresh data without any additional query!
        I am calling it inside onResume()*/

        realm = Realm.getDefaultInstance();
        lstGroups = realm.where(Group.class).findAll();

        lvGroups = (ListView) findViewById(R.id.lvGroups);
        groupAdapter = new GroupAdapter(this, lstGroups);
        lvGroups.setAdapter(groupAdapter);

        lvGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Group group = (Group) adapterView.getItemAtPosition(i);

                Intent intent = new Intent(GroupsListActivity.this,ContactListActivity.class);
                intent.putExtra("groupId",group.getId());

                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

         /* VVIP
            All realm objects/lists/results are 'live'. Meaning they are auto updated
            upon changes. So don't re-query the source of the listview!
            In the onResume() method, we just need to inform out list adapter that
            the underlying data has changed, so that it would then refresh the list items

            Also, i am not using any data access layer (DAL) for realm since there are some
            pitfalls in that. There are authors who have written workarounds, but
            I decided not to use DAL as I do not want to do R&D on it now.

            Moreover, using realm out of the box is simple enough, so DAL not required.
            * */
        groupAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("mytag","onDestroy-GLActivity");
        realm.close();
    }

}
