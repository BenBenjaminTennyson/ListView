package com.example.listview;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import android.content.ClipData;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SubmitActivity extends AppCompatActivity {

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imageRef;

    private Button btn, btn_submit, btn_cancle;
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;
    private GridView gvGallery;
    private GalleryAdapter galleryAdapter;
    private ArrayList<Uri> mArrayUri;

    private String id;
    private int status;
    private int REQUEST_SUBMIT = 2;
    private FirebaseDatabase database;
    private DatabaseReference record;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_picture);

        Intent i = getIntent();

        id = i.getStringExtra("id");
        status = Integer.parseInt(i.getStringExtra("status"));

        storage = FirebaseStorage.getInstance();    //prepare storage
        storageRef = storage.getReferenceFromUrl("gs://listview-6ed38.appspot.com/images/"); //prepare storage

        mArrayUri = new ArrayList<Uri>();

        btn =  (Button) findViewById(R.id.btn_select_photo);
        btn_submit =  (Button) findViewById(R.id.btn_submit);
        btn_cancle =  (Button) findViewById(R.id.btn_cancel);
        gvGallery = (GridView)findViewById(R.id.gv);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });

        gvGallery.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                    gvGallery.removeView(v);
                gvGallery.setAdapter(galleryAdapter);
                gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                        .getLayoutParams();
                mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);
                    Log.d("remove","yes");
                return false;
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitConfirm();
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
        try {
             if(requestCode == REQUEST_SUBMIT && resultCode == RESULT_OK) {
                 Log.d("submit OK", "OKKKKKKKKKKKKKKKKKKKK");
                 submitActivity();
                 // When an Image is picked
             }else if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
                selectImage(data);
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void selectImage(Intent data){
        // Get the Image from data

        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        imagesEncodedList = new ArrayList<String>();
        if(data.getData()!=null){

            Uri mImageUri=data.getData();

            // Get the cursor
            Cursor cursor = getContentResolver().query(mImageUri,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imageEncoded  = cursor.getString(columnIndex);
            cursor.close();

            mArrayUri = new ArrayList<Uri>();
            mArrayUri.add(mImageUri);
            galleryAdapter = new GalleryAdapter(getApplicationContext(),mArrayUri);
            gvGallery.setAdapter(galleryAdapter);
            gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                    .getLayoutParams();
            mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

        } else {
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                mArrayUri = new ArrayList<Uri>();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mArrayUri.add(uri);
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    imagesEncodedList.add(imageEncoded);
                    cursor.close();

                    galleryAdapter = new GalleryAdapter(getApplicationContext(),mArrayUri);
                    gvGallery.setAdapter(galleryAdapter);
                    gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                            .getLayoutParams();
                    mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

                }
                Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
            }
        }
    }

    private void submitConfirm(){
        if (mArrayUri.size() != 0) {
            Intent i = new Intent(getApplicationContext(), PopUpConfirmActivity.class);
            i.putExtra("msg", "Are you sure to Submit?");
            startActivityForResult(i, REQUEST_SUBMIT);
        }else{
            Toast.makeText(getApplicationContext(),"Please select image in your device", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitActivity(){
        Bitmap bitmap = null;
            for (int i = 0; i < mArrayUri.size(); i++) {

                try {
                    if (Uri.parse(mArrayUri.get(i).toString()) != null) {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mArrayUri.get(i).toString()));
                    }
                } catch (Exception e) {
                    //handle exception
                }
                uploadImage(bitmap, id, status + 1, i);
            }

        database = FirebaseDatabase.getInstance();
        record = database.getReference();
        record.child("Record").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataObjectRecord data = snapshot.getValue(DataObjectRecord.class);
                    if(id.equals(data.getID())) {
                        DataObjectRecord dataObjectRecord = new DataObjectRecord(data.getID(),data.getEmployeeCameraID(),data.getEmployeeBuildID(), data.getAddress(),data.getDetailObject(),data.getSignID(),status + 1, data.getStartDate(), data.getFinishCameraDate(), data.getFinishBuildDate(),data.getLocation());
                        dataObjectRecord.addAmountImage(data.getAmountImage()+mArrayUri.size());
                        record.child("Record").child(id).setValue(dataObjectRecord);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Toast.makeText(this, "Submit success", Toast.LENGTH_LONG).show();

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void closeActivity(){
        finish();
    }

    public void uploadImage(Bitmap bitmap, String primaryKey, int status, int count) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        imageRef = storageRef.child(primaryKey+"_"+status+"_"+count+".jpg"); //prepare storage

        UploadTask uploadTask = imageRef.putBytes(data);            //upload image to storage --> images/(primarykey data).jpg
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // unsuccess upload
                Toast.makeText(getApplicationContext(),"Unsuccess Upload", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // success upload
            }
        });
    }
}
