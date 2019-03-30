package com.example.listview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class NewRecordActivity extends AppCompatActivity {

    private Button btn_OK, btn_Cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        setSpinner();

        btn_OK = (Button) findViewById(R.id.btn_OK);
        btn_Cancel = (Button) findViewById(R.id.btn_Cancel);

        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityMain();
            }
        });

        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityMain();
            }
        });

    }

    private void openActivityMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void setSpinner() {
        Spinner spinner_sign    = (Spinner) findViewById(R.id.spinner_sign);
        Spinner spinner_worker  = (Spinner) findViewById(R.id.spinner_worker);

        ArrayAdapter<String> adapter_sign   = new ArrayAdapter<String>(NewRecordActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sign));
        ArrayAdapter<String> adapter_worker = new ArrayAdapter<String>(NewRecordActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.worker));

        adapter_sign.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_worker.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_sign.setAdapter(adapter_sign);
        spinner_worker.setAdapter(adapter_worker);
    }
}
