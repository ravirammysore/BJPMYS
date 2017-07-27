package in.appfocus.messageit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

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
        int x = datePicker.getDayOfMonth();
        int y = datePicker.getMonth();
        int z = datePicker.getYear();
        setResult(RESULT_OK);
        finish();
    }

    public void btnCancelClicked(View v){
        setResult(RESULT_CANCELED);
        finish();
    }
}
