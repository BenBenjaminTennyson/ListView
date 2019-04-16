package com.example.listview;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.Calendar;

public class DetailRecordActivity extends AppCompatActivity {

    private String state;

    private Button btn_OK, btn_Cancel;

    private Spinner spinner_sign;
    private Spinner spinner_worker_Photo;
    private Spinner spinner_worker_Build;

    private ArrayAdapter<String> adapter_sign;
    private ArrayAdapter<String> adapter_worker_Photo;
    private ArrayAdapter<String> adapter_worker_Build;

    private Button btn_date_Photo;
    private Button btn_date_Builder;
    private Button textView_address;

    private DatePickerDialog datePickerDialog;

    private int year, month, day;

    private Calendar calendar;

    private String[] workers = new String[] {"Film" , "Ja" , "Kong" , "Arm"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_record);

        Intent i = getIntent();

        state = i.getStringExtra("state");

        btn_OK = (Button) findViewById(R.id.btn_OK);
        btn_Cancel = (Button) findViewById(R.id.btn_No);
        btn_date_Photo = (Button) findViewById(R.id.btn_date_Photo);
        btn_date_Builder = (Button) findViewById(R.id.btn_date_Build);
        textView_address = (Button) findViewById(R.id.button_location);

        setSpinner();
        setUpToEdit(i);

        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OKActivity();
            }
        });

        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivityMain();
            }
        });

        btn_date_Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDeadLine(btn_date_Photo);
            }
        });

        btn_date_Builder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDeadLine(btn_date_Builder);
            }
        });
    }

    private void OKActivity(){
        Intent intent = new Intent();

        intent.putExtra("msg_address", "1001");
        intent.putExtra("msg_creator", spinner_worker_Photo.getSelectedItem().toString());
        intent.putExtra("msg_builder", spinner_worker_Build.getSelectedItem().toString());
        intent.putExtra("msg_datePhoto", btn_date_Photo.getText());
        intent.putExtra("msg_dateBuild", btn_date_Builder.getText());

        setResult(RESULT_OK, intent);
        finish();
    }

    private void closeActivityMain(){
//        Intent intent = new Intent();
//        setResult(RESULT_OK, intent);
        finish();
    }

    private void setUpToEdit(Intent i){
        if (i.getStringExtra("creator") != null) spinner_worker_Photo.setSelection(Arrays.asList(workers).indexOf(i.getStringExtra("creator")));
        if (i.getStringExtra("builder") != null) spinner_worker_Build.setSelection(Arrays.asList(workers).indexOf(i.getStringExtra("builder")));
        textView_address.setText(i.getStringExtra("address")+"");

    }

    private void setDeadLine(final Button btn_date){
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(DetailRecordActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        btn_date.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setSpinner() {

        spinner_sign    = (Spinner) findViewById(R.id.spinner_sign);
        spinner_worker_Photo  = (Spinner) findViewById(R.id.spinner_worker_Photo);
        spinner_worker_Build  = (Spinner) findViewById(R.id.spinner_worker_Build);

        adapter_sign   = new ArrayAdapter<String>(DetailRecordActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sign));
        adapter_worker_Photo = new ArrayAdapter<String>(DetailRecordActivity.this,
                android.R.layout.simple_list_item_1, workers);
        adapter_worker_Build = new ArrayAdapter<String>(DetailRecordActivity.this,
                android.R.layout.simple_list_item_1, workers);

        adapter_sign.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_worker_Photo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_worker_Build.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_sign.setAdapter(adapter_sign);
        spinner_worker_Photo.setAdapter(adapter_worker_Photo);
        spinner_worker_Build.setAdapter(adapter_worker_Build);
    }
}
