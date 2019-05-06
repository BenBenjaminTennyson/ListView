/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.listview;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
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
import java.util.Map;
import java.util.Set;

public class PaiAR extends AppCompatActivity implements View.OnClickListener {
  private static final String TAG = PaiAR.class.getSimpleName();
  private static final double MIN_OPENGL_VERSION = 3.0;

  private ArFragment arFragment;
  private ModelRenderable andyRenderable;

  private Button mButton;
  private Button selectButton;
  private Button changeModelButton;
  private ImageView imageView;
  private View main;
  int para = 0;
  int modeling = 0;

  private int REQUEST_CODE = 1;
    private ArrayList<Bitmap> bitmaps;
    private String id, status;
    private ArrayList<Object> signs;
    private int indexImage = 0;


    @Override
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  // CompletableFuture requires api level 24
  // FutureReturnValueIgnored is not valid
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle("PaiAR");


    if (!checkIsSupportedDeviceOrFinish(this)) {
      return;
    }
    setContentView(R.layout.activity_ux);
    arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
    main = findViewById(R.id.main);
    imageView = (ImageView) findViewById(R.id.imageView);
    mButton = findViewById(R.id.arbutton1);
    mButton.setOnClickListener(this);
    selectButton = findViewById(R.id.select_btn);
    selectButton.setOnClickListener(this);
    changeModelButton = findViewById(R.id.changeSign_btn);
    changeModelButton.setOnClickListener(this);

    setUp();

        // When you build a Renderable, Sceneform loads its resources in the background while returning
    // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
//    ModelRenderable.builder()
//        .setSource(this, R.raw.st3)
//        .build()
//        .thenAccept(renderable -> andyRenderable = renderable)
//        .exceptionally(
//            throwable -> {
//              Toast toast =
//                  Toast.makeText(this, "Unable to load stopsign renderable", Toast.LENGTH_LONG);
//              toast.setGravity(Gravity.CENTER, 0, 0);
//              toast.show();
//              return null;
//            });

    arFragment.setOnTapArPlaneListener(
        (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
          if (andyRenderable == null) {
            return;
          }

          // Create the Anchor.
          Anchor anchor = hitResult.createAnchor();
          AnchorNode anchorNode = new AnchorNode(anchor);
          anchorNode.setParent(arFragment.getArSceneView().getScene());

          // Create the transformable andy and add it to the anchor.
          TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
          andy.setParent(anchorNode);
          andy.setRenderable(andyRenderable);
          andy.select();
        });
  }

    //                Bitmap b = Screenshot.takescreenshotOfRootView(imageView);
//                imageView.setImageBitmap(b);
//                main.setBackgroundColor(Color.parseColor("#999999"));
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.arbutton1:
                if (para==1){
                    Toast.makeText(this, "ON", Toast.LENGTH_SHORT).show();
                    imageView.setVisibility(View.VISIBLE);
                    para = 0;
                    mButton.setText("ON");

                }else{
                    Toast.makeText(this, "OFF", Toast.LENGTH_SHORT).show();
                    imageView.setVisibility(View.INVISIBLE);
                    para = 1;
                    mButton.setText("OFF");
                }
                return ;
            case R.id.select_btn:
//                Toast.makeText(this, "Get Image", Toast.LENGTH_SHORT).show();
//                Intent intent =new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
                imageView.setImageBitmap(bitmaps.get(indexImage%bitmaps.size()));
                Log.d("change Image",(indexImage%bitmaps.size())+"");
                Log.d("number of bitmap", bitmaps.size()+"");
                indexImage++;
                return ;
//            case R.id.changeSign_btn:
//                if (modeling == 0 ){
//                    Toast.makeText(this, "Model Noparking", Toast.LENGTH_SHORT).show();
//                    modeling = 1;
//                    ModelRenderable.builder()
//                            .setSource(this, R.raw.noparking)
//                            .build()
//                            .thenAccept(renderable -> andyRenderable = renderable)
//                            .exceptionally(
//                                    throwable -> {
//                                        Toast toast =
//                                                Toast.makeText(this, "Unable to load noparking renderable", Toast.LENGTH_LONG);
//                                        toast.setGravity(Gravity.CENTER, 0, 0);
//                                        toast.show();
//
//                                        return null;
//                                    });
//                    return;
//                }
//                if (modeling == 1 ){
//                    Toast.makeText(this, "Model Light trafic", Toast.LENGTH_SHORT).show();
//                    modeling = 2;
//                    ModelRenderable.builder()
//                            .setSource(this, R.raw.lighttrafic)
//                            .build()
//                            .thenAccept(renderable -> andyRenderable = renderable)
//                            .exceptionally(
//                                    throwable -> {
//                                        Toast toast =
//                                                Toast.makeText(this, "Unable to load lighttrafic renderable", Toast.LENGTH_LONG);
//                                        toast.setGravity(Gravity.CENTER, 0, 0);
//                                        toast.show();
//
//                                        return null;
//                                    });
//                    return;
//                }
//                if (modeling == 2){
//                    Toast.makeText(this, "Model Stopsign", Toast.LENGTH_SHORT).show();
//                    modeling = 0;
//                    ModelRenderable.builder()
//                            .setSource(this, R.raw.st3)
//                            .build()
//                            .thenAccept(renderable -> andyRenderable = renderable)
//                            .exceptionally(
//                                    throwable -> {
//                                        Toast toast =
//                                                Toast.makeText(this, "Unable to load stopsign renderable", Toast.LENGTH_LONG);
//                                        toast.setGravity(Gravity.CENTER, 0, 0);
//                                        toast.show();
//                                        return null;
//                                    });
//                    return;
//                }
        }
    }

    @Override
    protected void  onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){ // && data != null && data.getData() != null){
//            Uri uri = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);

//                imageView.setImageBitmap(bitmaps.get((bitmaps.indexOf(imageView)+1)%bitmaps.size()));
//            }catch (IOException e){
//                e.printStackTrace();
//            }
        }
    }

  public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
    if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
      Log.e(TAG, "Sceneform requires Android N or later");
      Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
      activity.finish();
      return false;
    }
    String openGlVersionString =
        ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
            .getDeviceConfigurationInfo()
            .getGlEsVersion();
    if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
      Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
      Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
          .show();
      activity.finish();
      return false;
    }
    return true;
  }

  private void setUp() {
      Intent i = getIntent();
      id = i.getStringExtra("id");
      status = i.getStringExtra("status");
      int sign = Integer.parseInt(i.getStringExtra("sign"))-1;

      if ("3".equals(status)) {

//          Toast.makeText(this, "Model Stopsign", Toast.LENGTH_SHORT).show();
          int[] models = {R.raw.st3, R.raw.lighttrafic, R.raw.noparking};
//          modeling = Integer.parseInt(i.getStringExtra("sign"))-1;

          Log.d("sign",i.getStringExtra("sign"));

          ModelRenderable.builder()
                  .setSource(this, models[sign])
                  .build()
                  .thenAccept(renderable -> andyRenderable = renderable)
                  .exceptionally(
                          throwable -> {
                              Toast toast =
                                      Toast.makeText(this, "Unable to load stopsign renderable", Toast.LENGTH_LONG);
                              toast.setGravity(Gravity.CENTER, 0, 0);
                              toast.show();
                              return null;
                          });

          FirebaseDatabase database = FirebaseDatabase.getInstance();
          DatabaseReference record = database.getReference();
          bitmaps = new ArrayList<Bitmap>();

          record.child("Record").addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                  for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                      DataObjectRecord data = snapshot.getValue(DataObjectRecord.class);
                      id = i.getStringExtra("id");
//                    Log.d("ID Searching",id);

                      if (id.equals(data.getID())) {
                          Log.d("amount image", data.getAmountImage() + "");
                          for (int i = 0; i < data.getAmountImage(); i++) {
                              FirebaseStorage storage = FirebaseStorage.getInstance();
                              Log.d("item:", data.getStatus() + "_" +i);
                            Log.d("image",data.getID()+"_"+data.getStatus()+"_"+i+".jpg");
                              StorageReference storageRef = storage.getReferenceFromUrl("gs://listview-6ed38.appspot.com/images/").child(data.getID() + "_2_" + i + ".jpg");
                              try {
                                  final File localFile = File.createTempFile("images", "jpg");
                                  storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                      @Override
                                      public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                          Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());

                                          if (bitmap != null) {
                                              bitmaps.add(bitmap);
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
//                      if(bitmaps.size() != 0) {
                          mButton.setVisibility(View.VISIBLE);
                          selectButton.setVisibility(View.VISIBLE);
//                      }
                      break;
                  }

                  record.child("Signs").addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          Set signsSet = ((Map) dataSnapshot.getValue()).keySet();
                          signs = new ArrayList<>();
                          int i = 0;
                          for(Object s : signsSet){
                              signs.add(i++, s+"");
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

          Log.d("bitmap list", bitmaps.size()+"");
      }
//      else{
//      }
//      changeModelButton.setVisibility(View.GONE);
  }
}
