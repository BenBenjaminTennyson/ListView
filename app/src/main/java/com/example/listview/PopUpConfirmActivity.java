package com.example.listview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class PopUpConfirmActivity extends Activity {

    private Button btn_yes, btn_no, btn_OK;
    private TextView messageTextView;
    private Intent getIntent;
    private String msg, state;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_confirm);

        dialog = new Dialog(this);

        btn_yes = (Button) findViewById(R.id.btn_Yes);
        btn_no = (Button) findViewById(R.id.btn_No);
        btn_OK = (Button) findViewById(R.id.btn_OK);
        messageTextView = (TextView) findViewById(R.id.textView_message);

        getIntent = getIntent();
        msg = getIntent.getStringExtra("msg");
        state = getIntent.getStringExtra("state");

        if(msg != null) setMessage(msg);
        if("alert".equals(state)) setAlertMessage();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int weigth = dm.widthPixels;
        int heigth = dm.heightPixels;

        getWindow().setLayout((int) (weigth*.8), (int) (heigth*.2));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(params);

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("submit","submitttttttttt");
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setAlertMessage() {
        btn_no.setVisibility(View.INVISIBLE);
        btn_yes.setVisibility(View.INVISIBLE);
        btn_OK.setVisibility(View.VISIBLE);
    }

    private void setMessage(String message){
        messageTextView.setText(message);
    }
}
