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

public class PopUpMenuActivity extends Activity {

    private Button btn_detail, btn_map, btn_camera, btn_photo;
    private Intent getIntent;
    private String role, status;

    private final static int REQUEST_EDIT = 1, REQUEST_SUBMIT = 2, REQUEST_APPROVE = 3;
    private double[][] resolutions = new double[][] {new double[] {.625,.38}, new double[] {.625, .205}};
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_menu);

        getIntent = getIntent();
        role = getIntent.getStringExtra("role");
        status = getIntent.getStringExtra("status");
        int indexRole = ((role.equals("Manager")) ? 1 : 0);

        double sWeigth = resolutions[indexRole][0];
        double sHeigth = resolutions[indexRole][1];

        btn_detail = (Button) findViewById(R.id.btn_detail);
        btn_map = (Button) findViewById(R.id.btn_map);
        btn_camera = (Button) findViewById(R.id.btn_camera);
        btn_photo = (Button) findViewById(R.id.btn_submit);

        setButton();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        dialog = new Dialog(this);

        int weigth = dm.widthPixels;
        int heigth = dm.heightPixels;

        getWindow().setLayout((int) (weigth*sWeigth), (int) (heigth*sHeigth));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(params);


        btn_detail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DetailRecordActivity.class);
                i.putExtra("state", (isManager()) ? "edit" : "detail");
                i.putExtra("id",getIntent.getStringExtra("id"));
                startActivityForResult(i, REQUEST_EDIT);
            }
        });

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), (isManager()) ? ApproveActivity.class : MapsActivity.class);
//                Intent i = new Intent(getApplicationContext(), MapsActivity.class);

//                i.putExtra("state", "photo");
                i.putExtra("state", (isManager()) ? "approve" : "direction");
                i.putExtra("id",getIntent.getStringExtra("id"));
                i.putExtra("status",getIntent.getStringExtra("status"));
                i.putExtra("location", getIntent.getStringExtra("location"));

//                Log.d("ID Approve:",getIntent.getStringExtra("id"));
//                Log.d("Status Approve:",getIntent.getStringExtra("status"));

                startActivityForResult(i, (isManager()) ? REQUEST_APPROVE : -1);
//                startActivity(i);
            }
        });

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), ApproveActivity.class);
//                startActivityForResult(i, REQUEST_APPROVE);
            }
        });

        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SubmitActivity.class);

                i.putExtra("state", "photo");
                i.putExtra("id",getIntent.getStringExtra("id"));
                i.putExtra("status",getIntent.getStringExtra("status"));

                startActivityForResult(i, REQUEST_SUBMIT);
            }
        });
    }

    private void setButton() {
        switch (role+""){
            case "null":
                break;
            case "Manager":
                btn_map.setText("Approve");
                switch (status+""){
                    case "null":
                        break;
                    case "1" :
                        btn_detail.setText("Edit");
                        break;
                }
                break;
            case "Employee":
                break;
        }

        if("5".equals(status+"")) btn_map.setText("Photo");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);

        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_EDIT:
                    editRecord(dataIntent);
                    break;
                case REQUEST_SUBMIT:
                    finish();
                    break;
                case REQUEST_APPROVE:
                    finish();
                    break;
            }
        }
    }

    private void editRecord(Intent dataIntent){
        Intent intent = new Intent();
        intent.putExtra("msg_address", dataIntent.getStringExtra("msg_address"));
        intent.putExtra("msg_creator", dataIntent.getStringExtra("msg_creator"));
        intent.putExtra("msg_builder", dataIntent.getStringExtra("msg_builder"));
        intent.putExtra("msg_datePhoto", dataIntent.getStringExtra("msg_datePhoto"));
        intent.putExtra("msg_dateBuild", dataIntent.getStringExtra("msg_dateBuild"));
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean isManager(){
        return "Manager".equals(role+"");
    }
}
