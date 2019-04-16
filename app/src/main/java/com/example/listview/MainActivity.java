package com.example.listview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CREATE = 1, REQUEST_DELETE = 2, REQUEST_EDIT = 3;

    public ListView listView;
    public CustomAdapter customAdapter;

    private ArrayList<Integer> images = new ArrayList<>(Arrays.asList   (   R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
                                                                            R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
                                                                            R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
                                                                            R.drawable.ic_launcher_foreground
                                                                        ));

    private ArrayList<String> addresses = new ArrayList<>(Arrays.asList (   "110", "111", "112", "113", "114",
                                                                            "110", "111", "112", "113", "114"
                                                                        ));

    private ArrayList<String> creators = new ArrayList<>(Arrays.asList  (   "Film", "Ja", "Arm", "Kong", "Wan",
                                                                            "Film", "Ja", "Arm", "Kong", "Wan"
                                                                        ));

    private ArrayList<String> builders = new ArrayList<>(Arrays.asList  (   "Film", "Ja", "Arm", "Kong", "Wan",
                                                                            "Film", "Ja", "Arm", "Kong", "Wan"
                                                                        ));

    private String[] filters =      {   "Working", "Completed"   };

    private ArrayList<Integer> imagesCompleted = new ArrayList<>(Arrays.asList      (       R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
                                                                                            R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground
                                                                                    ));

    private ArrayList<String> addressesCompleted = new ArrayList<>(Arrays.asList    (       "110", "111", "112", "113", "114"   ));

    private ArrayList<String> creatorsCompleted = new ArrayList<>(Arrays.asList     (      "Film", "Ja", "Arm", "Kong", "Wan"   ));

    private ArrayList<String> buildersCompleted = new ArrayList<>(Arrays.asList     (      "Film", "Ja", "Arm", "Kong", "Wan"   ));

    private ImageButton btn_newRecord;

    private Button btn_filter;

    private int indexSeclectRecord = -1;
    private boolean workingRecord = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);

        btn_newRecord = (ImageButton) findViewById(R.id.btn_newRecord);
        btn_filter = (Button) findViewById(R.id.btn_filter);
        btn_filter.setText(filters[0]);

        customAdapter = new CustomAdapter();
        customAdapter.setUp(addresses, creators, images);
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openMenuRecord(position);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                openDeleteRecord(position);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);

        switch (requestCode)
        {
            case REQUEST_CREATE:
                if(resultCode == RESULT_OK) createRecord(dataIntent);
                break;
            case REQUEST_DELETE:
                if(resultCode == RESULT_OK) checkRecordToDelete();
                break;
            case REQUEST_EDIT:
                if(resultCode == RESULT_OK) editRecord(dataIntent);
        }
    }

    private void openMenuRecord(Integer position){
        Intent i = new Intent(MainActivity.this, PopUpMenuActivity.class);

        i.putExtra("address", addresses.get(position)+"");
        i.putExtra("image", images.get(position)+"");
        i.putExtra("creator", creators.get(position)+"");
        i.putExtra("builder", builders.get(position)+"");

        indexSeclectRecord = position;

        startActivityForResult(i, REQUEST_EDIT);
//                startActivity(i);
    }

    private void openDeleteRecord(Integer position){
        Intent i = new Intent(getApplicationContext(), PopUpConfirmActivity.class);
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
        if (Arrays.asList(filters).indexOf(btn_filter.getText()) == 0){
            customAdapter.setUp(addresses, creators, images);
            listView.setAdapter(customAdapter);
            workingRecord = true;
        }
        else if (Arrays.asList(filters).indexOf(btn_filter.getText()) == 1){
            customAdapter.setUp(addressesCompleted, creatorsCompleted, imagesCompleted);
            listView.setAdapter(customAdapter);
            workingRecord = false;
        }
    }

    private void createRecord(Intent dataIntent){

        addresses.add(dataIntent.getStringExtra("msg_address"));
        creators.add(dataIntent.getStringExtra("msg_creator"));
        builders.add(dataIntent.getStringExtra("msg_builder"));
        images.add(R.drawable.ic_launcher_foreground);

        listView.setAdapter(customAdapter);
    }

    private void checkRecordToDelete(){
        if(workingRecord){ deleteRecord(images , addresses , creators , builders); }
        else{ deleteRecord(imagesCompleted , addressesCompleted , creatorsCompleted , buildersCompleted); }
    }

    private void deleteRecord(ArrayList<Integer> images , ArrayList<String> addresses , ArrayList<String> creators , ArrayList<String> builders){
        images.remove(indexSeclectRecord);
        addresses.remove(indexSeclectRecord);
        creators.remove(indexSeclectRecord);
        builders.remove(indexSeclectRecord);

        listView.setAdapter(customAdapter);
        indexSeclectRecord = -1;
    }

    private void editRecord(Intent dataIntent) {
        Log.d("edit","yes");
        Log.d("address",addresses.toString());
        images.set(indexSeclectRecord, R.drawable.ic_launcher_foreground);
        addresses.set(indexSeclectRecord, dataIntent.getStringExtra("msg_address"));
        builders.set(indexSeclectRecord, dataIntent.getStringExtra("msg_builder"));
        creators.set(indexSeclectRecord, dataIntent.getStringExtra("msg_creator"));

        listView.setAdapter(customAdapter);
        indexSeclectRecord = -1;
    }

    class CustomAdapter extends BaseAdapter{
        private ArrayList<String> addresses;
        private ArrayList<String> creators;
        private ArrayList<Integer> images;

        public void setUp (ArrayList<String> addresses, ArrayList<String> creators , ArrayList<Integer> images){
            this.addresses = addresses;
            this.images = images;
            this.creators = creators;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
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
            TextView textView_creator = (TextView) convertView.findViewById(R.id.textView_creator);

            imageView.setImageResource(this.images.get(position));
            textView_address.setText(this.addresses.get(position));
            textView_creator.setText(this.creators.get(position));

            return convertView;
        }
    }


}
