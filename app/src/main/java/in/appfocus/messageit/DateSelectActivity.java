package in.appfocus.messageit;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import java.text.MessageFormat;

public class DateSelectActivity extends AppCompatActivity {

    DatePicker datePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_select);

        datePicker = (DatePicker)findViewById(R.id.datePicker);
        //y,m-1,d
        datePicker.updateDate(1970,0,1);
    }

    public void btnOKClicked(View v){
        int d = datePicker.getDayOfMonth();
        int m = datePicker.getMonth();
        int y = datePicker.getYear();

        //only month is zero based
        String result = MessageFormat.format("{0}/{1}/{2}",d,(m+1),String.valueOf(y));

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",result);
        setResult(Activity.RESULT_OK,returnIntent);

        finish();
    }

    public void btnCancelClicked(View v){
        setResult(RESULT_CANCELED);
        finish();
    }
}
