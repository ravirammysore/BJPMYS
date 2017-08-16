package in.appfocus.messageit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import in.appfocus.messageit.helpers.Utilities;

public class HelpActivity extends AppCompatActivity {
    EditText etHelpContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etHelpContent = (EditText)findViewById(R.id.etHelpContent);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.actionHelpSend){
            if(Utilities.isInputGiven(etHelpContent))
                sendHelpEmail();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnack(String msg) {
        CoordinatorLayout view = (CoordinatorLayout) findViewById(R.id.layoutHelp);
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void sendHelpEmail(){
        String[] addresses ={"ravikr@appfocus.in","pramodh@digitali.in"};
        try{
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

            emailIntent.setType("text/plain");
            emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this

            emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MSGit Help Required");
            emailIntent.putExtra(Intent.EXTRA_TEXT, etHelpContent.getText().toString());
            startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
        }
        catch (Exception ex){
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            Crashlytics.log("sendHelpEmail-" + ex.getMessage());
        }
    }

}
