package com.example.listview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class PopUpMenuActivity extends Activity {

    private Button btn_edit, btn_map, btn_camera, btn_photo;

    private Intent getIntent;

    private final static int REQUEST_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_menu);

        getIntent = getIntent();

        btn_edit = (Button) findViewById(R.id.btn_edit);
        btn_map = (Button) findViewById(R.id.btn_map);
        btn_camera = (Button) findViewById(R.id.btn_camera);
        btn_photo = (Button) findViewById(R.id.btn_photo);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int weigth = dm.widthPixels;
        int heigth = dm.heightPixels;

        //getWindow().setLayout(weigth, heigth);
        getWindow().setLayout((int) (weigth*.8), (int) (heigth*.4));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        btn_edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DetailRecordActivity.class);

                i.putExtra("address", getIntent.getStringExtra("address"));
                i.putExtra("image", getIntent.getStringExtra("image"));
                i.putExtra("creator", getIntent.getStringExtra("creator"));
                i.putExtra("builder", getIntent.getStringExtra("builder"));
                i.putExtra("state", getIntent.getStringExtra("edit"));

                startActivityForResult(i, REQUEST_EDIT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);

        switch (requestCode)
        {
            case REQUEST_EDIT:
                if(resultCode == RESULT_OK) editRecord(dataIntent);
                break;
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
}
