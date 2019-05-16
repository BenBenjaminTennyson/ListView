package com.example.listview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApproveActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference record;
    private DataObjectRecord data;

    private LinearLayout linearLayout;

    private ArrayList<Bitmap> bitmaps;

    private Button btn_approve, btn_reject, btn_cancle;

    private final int REQUEST_APPROVE = 1, REQUEST_REJECT = 2;
    private String id, status;
    private Intent i;
    private int numDelete;
    private View convertView;
    private LayoutInflater inflater;
    private int count = 0;
    private Map<Integer,String> pictures;
    private ArrayList<View> views;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve);

        database = FirebaseDatabase.getInstance();
        record = database.getReference();
//        bitmaps = new ArrayList<Bitmap>();
//        pictures = new HashMap<Integer, String>();
//        views = new ArrayList<>();

        i = getIntent();
        id = i.getStringExtra("id");
        status = i.getStringExtra("status");
        linearLayout = findViewById(R.id.horizontal_gallery);
        inflater = LayoutInflater.from(this);

        record.child("Record").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    data = snapshot.getValue(DataObjectRecord.class);
                    id = i.getStringExtra("id");
//                    Log.d("ID Searching",id);

                    if(id.equals(data.getID())) {
                        Log.d("amount image",data.getAmountImage()+"");
                        for(int i = 1 ; i <= 4 ; i++) {
                            if(i == 3) continue;
                            for (int j = 0; j < data.getAmountImage(); j++) {
                                if(i == 1 && j > 0) continue;
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                String namePic = i + "_" +j;
                                Log.d("item:", i + "_" +j);
                                StorageReference storageRef = storage.getReferenceFromUrl("gs://listview-6ed38.appspot.com/images/").child(data.getID() + "_" + i + "_" + j + ".jpg");
                                try {
                                    final File localFile = File.createTempFile("images", "jpg");
                                    storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());

//                                            Log.d("pictiore firebase", "picture is found :" + bitmap.toString());

//                                            bitmaps.add(bitmap);

                                            if(bitmap != null){
                                                View convertView = inflater.inflate(R.layout.image_approve, linearLayout, false);
                                                ImageView img = convertView.findViewById(R.id.ivGallery);
                                                img.setImageBitmap(bitmap);
//                                                img.setImageResource(R.mipmap.ic_launcher_round);
                                                linearLayout.addView(convertView,count);

//                                                pictures.put(count,linearLayout.getChildAt(count)+"");
//                                                views.add(count,convertView);
//                                                Log.d("Linear View",linearLayout.getChildAt(count)+"");

//                                                Log.d("index",count+"");
//                                                Log.d("count", namePic);
//                                                Log.d("image bitmap", img.getDrawable().toString());
                                                count++;
                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Log.d("failure", "something wrong");
                                        }
                                    });
                                } catch (IOException e) {
                                }
                            }
                        }
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        Log.d("BITMAP LIST last:",bitmaps.size()+"");

        btn_approve =  (Button) findViewById(R.id.btn_approve);
        btn_reject =  (Button) findViewById(R.id.btn_reject);
        btn_cancle =  (Button) findViewById(R.id.btn_cancel);

        setButton();

        btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approveActivity();
            }
        });

        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("5".equals(status+"")) finish();
                else rejectActivity();
            }
        });

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case REQUEST_APPROVE:
                    changeStatus(1, 0);
                    break;
                case REQUEST_REJECT:
                    changeStatus(-1, removeImage());
                    break;
            }
        }
    }

    private int removeImage(){
        numDelete = 0;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        for(int i = 0 ; i < data.getAmountImage() ; i++) {
            StorageReference storageRef = storage.getReferenceFromUrl("gs://listview-6ed38.appspot.com/images/").child(data.getID() + "_" + (data.getStatus()) + "_" + i + ".jpg");
            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    numDelete ++;
                    // File deleted successfully
//                Log.d(TAG, "onSuccess: deleted file");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
//                Log.d(TAG, "onFailure: did not delete file");
                }
            });
        }
        return numDelete;
    }

    private void approveActivity() {
        Intent i = new Intent(getApplicationContext(), PopUpConfirmActivity.class);
        i.putExtra("msg", "Are you sure to Approve?");
        startActivityForResult(i, REQUEST_APPROVE);
    }

    private void rejectActivity() {
        Intent i = new Intent(getApplicationContext(), PopUpConfirmActivity.class);
        i.putExtra("msg", "Are you sure to Reject?");
        startActivityForResult(i, REQUEST_REJECT);
    }

    private void changeStatus(final int result, int numDelete){
        DataObjectRecord dataObjectRecord = new DataObjectRecord(data.getID(),data.getEmployeeCameraID(),data.getEmployeeBuildID(), data.getAddress(),data.getDetailObject(),data.getSignID(),data.getStatus() + result, data.getStartDate(), data.getFinishCameraDate(), data.getFinishBuildDate(),data.getLocation());
        dataObjectRecord.addAmountImage(data.getAmountImage()-numDelete);
        record.child("Record").child(id).setValue(dataObjectRecord);

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void closeActivity() {
        finish();
    }

    private void setButton(){
        switch (status+"") {
            case "5":
                btn_approve.setVisibility(View.INVISIBLE);
                btn_reject.setText("BACK");
                btn_cancle.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
