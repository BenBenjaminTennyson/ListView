package com.example.listview;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;

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
import com.google.firebase.storage.UploadTask;

public class DetailRecordActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference record;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imageRef;

    private DataObjectRecord data;

    private int REQUEST_MAP = 1, PICK_IMAGE = 100;;

    private String state, autoNumber, ID;;

    private Uri imageUri;

    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private int year, month, day;

    private ImageView signPicture;
    private Spinner spinner_sign;
    private Spinner spinner_worker_Photo;
    private Button btn_date_Photo;
    private Spinner spinner_worker_Build;
    private Button btn_date_Builder;
    private Button btn_address;
    private EditText detail;
    private ImageButton btn_pictureLocation;

    private Button btn_OK, btn_Cancel;

    private ArrayAdapter<String> adapter_sign;
    private ArrayAdapter<String> adapter_worker_Photo;
    private ArrayAdapter<String> adapter_worker_Build;

    private ArrayList<Integer> signPictures = new ArrayList<Integer>(Arrays.asList   (R.mipmap.ic_launcher, R.drawable.sign1_stop, R.drawable.sign2_traffic, R.drawable.sign3_nopark));
    private ArrayList<String> workers; //= new String[] {"Choose Employee","Film" , "Ja" , "Kong" , "Arm"};
    private ArrayList<String> signs; //= new String[] {"Choose Sign", "Stop", "Traffic", "No Parking"};

    private long count = -1;
    private boolean isAddPicture = false;
    private String location;
    private Date photoDate;
    private Date buildDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_record);
        Intent i = getIntent();

        state = i.getStringExtra("state");
        ID = i.getStringExtra("id");

        Log.d("state",state);

        database = FirebaseDatabase.getInstance();
        record = database.getReference();
        storage = FirebaseStorage.getInstance();    //prepare storage
        storageRef = storage.getReferenceFromUrl("gs://listview-6ed38.appspot.com/images/"); //prepare storage

        setUp();

//        setSpinner();

        spinner_sign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                signPicture.setImageResource(signPictures.get(spinner_sign.getSelectedItemPosition()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d("OK","click OK");
                    Log.d("state",state);
                    OKActivity();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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
                try {
                    setFinishDate(btn_date_Photo,"photo");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_date_Builder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    setFinishDate(btn_date_Builder,"build");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailRecordActivity.this, MapsActivity.class);
                startActivityForResult(i, REQUEST_MAP);
            }
        });

        btn_pictureLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGellery();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                imageUri = data.getData();
                btn_pictureLocation.setImageURI(imageUri);
                Bitmap bitmap = ((BitmapDrawable) btn_pictureLocation.getDrawable()).getBitmap();
                int sizeBitmap = sizeOf(bitmap);
                int percentSize = ((int) (1000000000/sizeBitmap));
                Log.d("size of pic",sizeOf(bitmap)+"");
                Log.d("percent",percentSize+"");
                isAddPicture = true;
            } else if (requestCode == REQUEST_MAP) {
                btn_address.setText(data.getStringExtra("address"));
                location = data.getStringExtra("location");

            }
        }
    }

    private void openGellery() {
        Intent gellery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gellery, PICK_IMAGE);
    }

    private void OKActivity() throws ParseException {
        verifyRecord();
    }

    private void closeActivityMain(){
        finish();
    }

    private void createRecord() throws ParseException {
        count = count + 1;
        autoNumber = count+"";
        record.child("AutoNumber").setValue(count);

        Date photoDate = new SimpleDateFormat("MM/dd/yyyy").parse(btn_date_Photo.getText().toString());
        Date buildDate = new SimpleDateFormat("MM/dd/yyyy").parse(btn_date_Builder.getText().toString());

        DataObjectRecord dataObjectRecord = new DataObjectRecord(autoNumber,spinner_worker_Photo.getSelectedItem().toString(),spinner_worker_Build.getSelectedItem().toString(), btn_address.getText().toString(), detail.getText().toString(),spinner_sign.getSelectedItemPosition(),1, calendar.getTime(), photoDate, buildDate, location);
        dataObjectRecord.addAmountImage(1);
        Bitmap bitmap = ((BitmapDrawable) btn_pictureLocation.getDrawable()).getBitmap();
        uploadImage(bitmap,autoNumber, "1");
        record.child("Record").child(autoNumber).setValue(dataObjectRecord);

//        UploadImage uploadImage = new UploadImage(autoNumber,"1",autoNumber+"1_1.jpg", btn_pictureLocation.getDrawable());
//        record.child("Images").setValue(uploadImage);

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void editRecord() throws ParseException {
        Date photoDate = new SimpleDateFormat("MM/dd/yyyy").parse(btn_date_Photo.getText().toString());
        Date buildDate = new SimpleDateFormat("MM/dd/yyyy").parse(btn_date_Builder.getText().toString());

        DataObjectRecord dataObjectRecord = new DataObjectRecord(data.getID(),spinner_worker_Photo.getSelectedItem().toString(),spinner_worker_Build.getSelectedItem().toString(), btn_address.getText().toString(), detail.getText().toString(),spinner_sign.getSelectedItemPosition(),1, data.getStartDate(), photoDate, buildDate, location);
        dataObjectRecord.addAmountImage(1);
        record.child("Record").child(data.getID()).setValue(dataObjectRecord);

        Bitmap bitmap = ((BitmapDrawable) btn_pictureLocation.getDrawable()).getBitmap();
        uploadImage(bitmap,data.getID(), "1");

        Toast.makeText(getApplicationContext(),"Edit Success", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void verifyRecord() throws ParseException {

        if( spinner_sign.getSelectedItemPosition() != 0
                && spinner_worker_Photo.getSelectedItemPosition() != 0
                && btn_date_Photo.getText().toString() != "-"
                && spinner_worker_Build.getSelectedItemPosition() != 0
                && btn_date_Builder.getText().toString() != "-"
                && isAddPicture
        ){
            if("create".equals(state)) createRecord();
            else if ("edit".equals(state)) editRecord();
        }else{
            Toast.makeText(getApplicationContext(),"Please complete the information", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadImage(Bitmap bitmap, String primaryKey, String status) {
        int maxSize = 10000000;
        int sizeBitmap = sizeOf(bitmap);
        int percentSize = (int) (maxSize*100/sizeBitmap);
        Log.d("percent",percentSize+"");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, (sizeBitmap>=maxSize) ? 20 : 100, baos);
        byte[] data = baos.toByteArray();

        imageRef = storageRef.child(primaryKey+"_"+status+"_0.jpg"); //prepare storage

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

    private void setUp(){
        setAutoNumber();

        spinner_sign    = (Spinner) findViewById(R.id.spinner_sign);
        spinner_worker_Photo  = (Spinner) findViewById(R.id.spinner_worker_Photo);
        spinner_worker_Build  = (Spinner) findViewById(R.id.spinner_worker_Build);

        signPicture = (ImageView) findViewById(R.id.image_sign);
        btn_OK = (Button) findViewById(R.id.btn_OK);
        btn_Cancel = (Button) findViewById(R.id.btn_No);
        btn_date_Photo = (Button) findViewById(R.id.btn_date_Photo);
        btn_date_Builder = (Button) findViewById(R.id.btn_date_Build);
        btn_address = (Button) findViewById(R.id.button_location);
        detail = (EditText) findViewById(R.id.detail_editText);
        btn_pictureLocation = (ImageButton) findViewById(R.id.Location_imageButton);

        Log.d("image alopha", btn_pictureLocation.getImageAlpha()+"");
        Log.d("image drawable", btn_pictureLocation.getDrawable()+"");
//        Log.d("image", btn_pictureLocation.get()+"");


        record.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set users = ((Map) dataSnapshot.getValue()).keySet();

//                Log.d("user Role", user.get("Role").toString());

//                workers = new String[users.size()];
                workers = new ArrayList<>();
                int i = 1;
                workers.add(0, "Choose Employee");
                for(Object s : users){
                    if (!"Manager".equals(((Map) ((Map) dataSnapshot.getValue()).get(s+"")).get("Role").toString())) {
                        workers.add(i++, (s + ""));
                    }
                }

                adapter_worker_Photo = new ArrayAdapter<String>(DetailRecordActivity.this,
                        android.R.layout.simple_list_item_1, workers);
                adapter_worker_Build = new ArrayAdapter<String>(DetailRecordActivity.this,
                        android.R.layout.simple_list_item_1, workers);

                spinner_worker_Photo.setAdapter(adapter_worker_Photo);
                spinner_worker_Build.setAdapter(adapter_worker_Build);

                adapter_worker_Photo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                adapter_worker_Build.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                record.child("Signs").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Set signsSet = ((Map) dataSnapshot.getValue()).keySet();
//                        signs = new String[signsSet.size()+1];
                        signs = new ArrayList<>();
                        int i = 1;
                        signs.add(0, "Choose Sign");
                        for(Object s : signsSet){
                            signs.add(i++, s+"");
                        }

                        adapter_sign   = new ArrayAdapter<String>(DetailRecordActivity.this,
                                android.R.layout.simple_list_item_1, signs);

                        adapter_sign.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        spinner_sign.setAdapter(adapter_sign);


                        try {
                            setUpToEdit();
                            setToDetail();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAutoNumber(){
        record.child("AutoNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                count = dataSnapshot.getValue(Integer.class);
//                Log.d("count", count+"");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setUpToEdit() throws IOException {
        if(ID != null) {
            Log.d("id edit",ID);
            record.child("Record").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        data = snapshot.getValue(DataObjectRecord.class);

                        if(ID.equals(data.getID())) {

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReferenceFromUrl("gs://listview-6ed38.appspot.com/images/").child(data.getID()+"_"+"1_0.jpg");

                            String photoDate = (new SimpleDateFormat("MM/dd/yyyy")).format(data.getFinishCameraDate());
                            String buildDate = (new SimpleDateFormat("MM/dd/yyyy")).format(data.getFinishBuildDate());

//                            Log.d("photo",(adapter_sign.getPosition(data.getEmployeeCameraID()))+"");
                            signPicture.setImageResource(signPictures.get(data.getSignID()));
                            spinner_sign.setSelection(data.getSignID());
                            spinner_worker_Photo.setSelection( adapter_worker_Photo.getPosition(data.getEmployeeCameraID()));
                            btn_date_Photo.setText(photoDate);
                            spinner_worker_Build.setSelection( adapter_worker_Build.getPosition(data.getEmployeeBuildID()));
                            btn_date_Builder.setText(buildDate);
                            btn_address.setText(data.getAddress());
                            detail.setText(data.getDetailObject());
//                            btn_pictureLocation.getDrawable().get

                            try {
                                final File localFile = File.createTempFile("images", ".jpg");
                                Log.d("file name",data.getID()+"_"+data.getStatus()+"_0");
                                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                        btn_pictureLocation.setImageBitmap(bitmap);

                                        if(bitmap != null){
                                            isAddPicture = true;
                                        }

                                        Log.d("pictureLocation", "picture location is found "+ bitmap.toString());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                    }
                                });
                            } catch (IOException e ) {}

                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void setToDetail(){
        if("detail".equals(state)){
            signPicture.setEnabled(false);
            spinner_sign.setEnabled(false);
            spinner_worker_Photo.setEnabled(false);
            btn_date_Photo.setEnabled(false);
            spinner_worker_Build.setEnabled(false);
            btn_date_Builder.setEnabled(false);
            btn_address.setEnabled(false);
            detail.setEnabled(false);
            btn_pictureLocation.setEnabled(false);
        }
    }

    private void setFinishDate(final Button btn_date,String type) throws ParseException {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(DetailRecordActivity.this,
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                btn_date.setText(day + "/" + (month + 1) + "/" + year);

                if(btn_date_Photo != null){
                    try {
                        photoDate = new SimpleDateFormat("MM/dd/yyyy").parse(btn_date_Photo.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(btn_date_Builder != null){
                    try {
                        buildDate = new SimpleDateFormat("MM/dd/yyyy").parse(btn_date_Builder.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                Date selectDate = null;
                try {
                    selectDate = new SimpleDateFormat("MM/dd/yyyy").parse(btn_date.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if("photo".equals(type) && btn_date_Builder.getText() != ""  && (selectDate.after(buildDate) || selectDate.equals(buildDate))){
                    Toast.makeText(getApplicationContext(),"camera date is must before build date", Toast.LENGTH_LONG).show();
                    btn_date_Photo.setText("");
                }else if("build".equals(type) && btn_date_Photo.getText() != "" && (selectDate.before(photoDate) || selectDate.equals(photoDate))) {
                    Toast.makeText(getApplicationContext(), "build date is must after camera date", Toast.LENGTH_LONG).show();
                    btn_date_Builder.setText("");
                }
            }}, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setSpinner() {

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    protected int sizeOf(Bitmap data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return data.getRowBytes() * data.getHeight();
        } else {
            return data.getByteCount();
        }
    }
}
