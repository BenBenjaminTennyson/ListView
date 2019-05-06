package com.example.listview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CREATE = 1, REQUEST_DELETE = 2, REQUEST_EDIT = 3;
    public static boolean signIn = false;

    public ListView listView;
    public CustomAdapter customAdapter;

    private FirebaseDatabase db;
    private DatabaseReference mDBRecord;

    private Intent intent;

    private ArrayList<DataObjectRecord> records , recordsShowing, recordsComplete;
    private ArrayList<String> keyRecords, keyRecordsShowing;

    private ArrayList<Integer> signs = new ArrayList<Integer>();

    private String[] filters =      {   "Working", "Completed"   };

    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;

    private ImageButton btn_newRecord;
    private Button btn_filter;
    private FloatingActionButton btn_create;

    private int indexSeclectRecord = -1, count;
    private String selectRecordID, role, username;
    private boolean filterWorking = true, longClick = false;

    public MainActivity() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menubar_main, menu);
        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!signIn){
            startActivity(new Intent(this,LoginActivity.class));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayList = new ArrayList<>();
        customAdapter = new CustomAdapter();
        records = new ArrayList<DataObjectRecord>();
        recordsComplete = new ArrayList<DataObjectRecord>();
        recordsShowing = new ArrayList<DataObjectRecord>();
        signs = new ArrayList<Integer>(Arrays.asList   (R.mipmap.ic_launcher, R.drawable.sign1_stop, R.drawable.sign2_traffic, R.drawable.sign3_nopark));

        db = FirebaseDatabase.getInstance();
        mDBRecord = db.getReference().child("Record");

        intent = getIntent();
        role = intent.getStringExtra("role");
        username = intent.getStringExtra("username");

        listView = (ListView) findViewById(R.id.listView);
        btn_newRecord = (ImageButton) findViewById(R.id.btn_newRecord);
        btn_filter = (Button) findViewById(R.id.btn_filter);
        btn_create = (FloatingActionButton) findViewById(R.id.btn_newRecord);

        btn_filter.setText(filters[0]);

        setRecord();
        if("Employee".equals(role+"")) {
//            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) btn_create.getLayoutParams();
//            p.setAnchorId(View.NO_ID);
//            btn_create.setLayoutParams(p);
            btn_create.setVisibility(View.INVISIBLE);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!longClick) openMenuRecord(view, position);
//                else if (!filterWorking ) {
//                    Intent i = new Intent(getApplicationContext(), DetailRecordActivity.class);
//                    i.putExtra("state", "detail");
//                    i.putExtra("id",records.get(records.indexOf(recordsShowing.get(position))).getID());
//                    i.putExtra("status",recordsShowing.get(position).getStatus()+"");
//                    startActivity(i);
//                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(filterWorking || records.get(records.indexOf(recordsShowing.get(position))).getStatus() != 1) {
                    indexSeclectRecord = records.indexOf(recordsShowing.get(position));
                    longClick = true;
                    openDeleteRecord(position);
                }
                return false;
            }
        });

        btn_newRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityNewRecord();
            }
        });
        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFilter(customAdapter, listView);
            }
        }) ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                signIn = false;
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);

        switch (requestCode)
        {
            case REQUEST_DELETE:
                if(resultCode == RESULT_OK) deleteRecord();
                break;
            case REQUEST_EDIT:
                if(resultCode == RESULT_OK) editRecord(dataIntent);
        }

        longClick = false;
    }

    private void openMenuRecord(View view, Integer position){
        Intent i = new Intent(MainActivity.this, PopUpMenuActivity.class);

        DataObjectRecord data = customAdapter.getRecord(position);
        String id = data.getID();
        String location = data.getLocation();
//        Log.d("id to submit",id);
//        Log.d("status to submit",recordsShowing.get(position).getStatus()+"");
        if(data.getStatus() == 5) i.putExtra("state", "detail");
        i.putExtra("id",id);
        i.putExtra("status",data.getStatus()+"");
        i.putExtra("role",role);
        i.putExtra("location",location);
        i.putExtra("sign",data.getSignID()+"");

        Log.d("sign Home" , data.getSignID()+"");

        indexSeclectRecord = position;

        startActivityForResult(i, REQUEST_EDIT);
    }

    private void openDeleteRecord(Integer position){
        Intent i = new Intent(getApplicationContext(), PopUpConfirmActivity.class);

        selectRecordID = records.get(records.indexOf(recordsShowing.get(position))).getID();
        i.putExtra("id",selectRecordID);
        i.putExtra("state", "delete");

        indexSeclectRecord = position;
        startActivityForResult(i, REQUEST_DELETE);
    }

    private void openActivityNewRecord() {
        Intent intent = new Intent(MainActivity.this, DetailRecordActivity.class);
//        startActivity(intent);
        intent.putExtra("state","create");
        startActivityForResult(intent, REQUEST_CREATE);
    }

    private void changeFilter(CustomAdapter customAdapter, ListView listView){
        btn_filter.setText(filters[(Arrays.asList(filters).indexOf(btn_filter.getText())+1)%2]);
        filterWorking = !filterWorking;
        setRecord();
        customAdapter.setUp(recordsShowing,signs);
        listView.setAdapter(customAdapter);
    }

    private void deleteRecord(){

        removeImage();
        mDBRecord.orderByChild("id").equalTo(selectRecordID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()) {
                    dataSnapshot1.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void editRecord(Intent dataIntent) {
        listView.setAdapter(customAdapter);
        indexSeclectRecord = -1;
    }

    private void removeImage(){
        DataObjectRecord data = records.get(indexSeclectRecord);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        for(int i = 1 ; i < 6 ; i++) {
            for (int j = 0; j < data.getAmountImage(); j++) {
                StorageReference storageRef = storage.getReferenceFromUrl("gs://listview-6ed38.appspot.com/images/").child(data.getID() + "_" + i + "_" + j + ".jpg");
                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
            }
        }
        indexSeclectRecord = 1;
    }

    class CustomAdapter extends BaseAdapter{
        private ArrayList<String> addresses;
        private ArrayList<String> cameraMans;
        private ArrayList<Integer> images;
        private ArrayList<String> builders;

        private ArrayList<DataObjectRecord> records;
        private ArrayList<Integer> signs;

        public void setUp (ArrayList<String> addresses, ArrayList<String> cameraMans , ArrayList<Integer> images, ArrayList<String> builders){
            this.addresses = addresses;
            this.images = images;
            this.cameraMans = cameraMans;
            this.builders = builders;
        }

        public void setUp (ArrayList<DataObjectRecord> records, ArrayList<Integer> signs){
            this.records = records;
            this.signs = signs;
        }

        @Override
        public int getCount() {
            return records.size();
        }

        @Override
        public DataObjectRecord getItem(int position) {
            return records.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.customlayout,null);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            TextView textView_address = (TextView) convertView.findViewById(R.id.textView_address);
            TextView textView_cameraMan = (TextView) convertView.findViewById(R.id.textView_cameraMan);
            TextView textView_builder = (TextView) convertView.findViewById(R.id.textView_builder);
            ImageView color_record = (ImageView) convertView.findViewById(R.id.border_record);

            imageView.setImageResource(this.signs.get(this.records.get(position).getSignID()));
            textView_address.setText(this.records.get(position).getAddress());
            textView_cameraMan.setText(this.records.get(position).getEmployeeCameraID());
            textView_builder.setText(this.records.get(position).getEmployeeBuildID());
            color_record.setImageResource(setColorRecord(this.records.get(position)));

            return convertView;
        }

        private Integer setColorRecord(DataObjectRecord record){
            if(record.getStatus() == 2) return (R.drawable.bg_record_photograph);
            else if(record.getStatus() == 4) return (R.drawable.bg_record_build);
            else if(record.getStatus() == 5) return (R.drawable.bg_record_complete);
            return (R.drawable.bg_record_working);
        }

        public DataObjectRecord getRecord(int position){
            return this.records.get(position);
        }
    }

    private void setRecord(){
        mDBRecord.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recordsShowing = new ArrayList<DataObjectRecord>();
                keyRecords = new ArrayList<String>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    DataObjectRecord record = snapshot.getValue(DataObjectRecord.class);
                    int status = record.getStatus();
                    if ((isManager()
                            && ((filterWorking && (status != 3 && status != 5))
                            || (!filterWorking && status == 5))
                        )
                        || (!isManager()
                            && ((filterWorking
                                && ((status != 5 && status != 2 && status != 4) && ((status == 1 && record.getEmployeeCameraID().equals(username)) || (status == 3 && record.getEmployeeBuildID().equals(username)))))
                            || (!filterWorking
                                && (status == 5 && (record.getEmployeeCameraID().equals(username) || record.getEmployeeBuildID().equals(username)))))
                    )) {
                        recordsShowing.add(record);
                        keyRecords.add(snapshot.getKey());
                    }
                    records.add(record);
                    keyRecords.add(snapshot.getKey());
                }

                Collections.sort(recordsShowing, new Comparator<DataObjectRecord>() {
                    @Override
                    public int compare(DataObjectRecord o1, DataObjectRecord o2) {
                        return o1.compareTo(o2);
                    }
                });

                customAdapter.setUp(recordsShowing, signs);
                listView.setAdapter(customAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean isManager(){
        return "Manager".equals(role+"");
    }
}
