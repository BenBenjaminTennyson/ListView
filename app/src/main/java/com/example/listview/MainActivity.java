package com.example.listview;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int[] images =  {   R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
                        R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground
                    };

    String[] addresses =    {   "110", "111", "112", "113", "114",
                                "110", "111", "112", "113", "114"
                            };

    String[] creators =     {   "Film", "Ja", "Arm", "Kong", "Wan",
                                "Film", "Ja", "Arm", "Kong", "Wan"
                            };

    private ImageButton btn_newRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);

        btn_newRecord = (ImageButton) findViewById(R.id.btn_newRecord);
        btn_newRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityNewRecord();
            }
        });


        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), PopUpActivity.class);
                startActivity(i);
            }
        });
    }

    private void openActivityNewRecord() {
        Intent intent = new Intent(this, NewRecordActivity.class);
        startActivity(intent);

    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return images.length;
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

            imageView.setImageResource(images[position]);
            textView_address.setText(addresses[position]);
            textView_creator.setText(creators[position]);

            return convertView;
        }
    }


}
