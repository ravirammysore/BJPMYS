package in.appfocus.messageit;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d("mytag","onCreate-Help");//from ubuntu system
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.help,menu);
        //as per google doc
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("mytag","onResume-Help");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("mytag","onDestroy-Help");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.actionHelpSend){
            showSnack("Your question was sent!");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnack(String msg) {
        CoordinatorLayout view = (CoordinatorLayout) findViewById(R.id.layoutHelp);
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
