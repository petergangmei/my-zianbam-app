package com.zianbam.yourcommunity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class FeaturePictureActivity extends AppCompatActivity {
    Uri imageUri;
    String myUrl ="";
    StorageTask uploadTask;
    StorageReference storageReference;
    Button choosefile, uploadfile;
    ImageView image_profile;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    EditText caption;
    Bitmap thumb_bitmap = null;
    private  StorageReference thumbImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_picture);

        mAuth = FirebaseAuth.getInstance();

        image_profile = findViewById(R.id.image_profile);
        choosefile = findViewById(R.id.choosefile);
        uploadfile = findViewById(R.id.uploadfile);
        caption = findViewById(R.id.caption);


        android.support.v7.widget.Toolbar toolbar =  findViewById(R.id.tool_bar);
        toolbar.setTitle("Informations");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


//        storageReference = FirebaseStorage.getInstance().getReference("Profiles");
        thumbImageRef = FirebaseStorage.getInstance().getReference("Feature_photos");

        choosefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(FeaturePictureActivity.this);
            }
        });



    }



    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        imageUri = result.getUri();
        image_profile.setImageURI(imageUri);
        choosefile.setVisibility(View.GONE);
        uploadfile.setVisibility(View.VISIBLE);
        uploadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
//                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                    Toast.makeText(FeaturePictureActivity.this, "uploading..", Toast.LENGTH_LONG).show();
                    finish();



                    File thumb_filePath = new File(imageUri.getPath());
                    String userid = mAuth.getUid();

                    try {
                        thumb_bitmap = new Compressor(getApplicationContext())
                                .setMaxHeight(100)
                                .setMaxHeight(100)
                                .setQuality(20)
                                .compressToBitmap(thumb_filePath);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,50, byteArrayOutputStream);

                    final byte[] final_image = byteArrayOutputStream.toByteArray();

                    final StorageReference thumb_file = thumbImageRef.child(System.currentTimeMillis()+".jpg");
                    UploadTask uploadTask = thumb_file.putBytes(final_image);


                    uploadTask.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {
                            if (!task.isSuccessful()){
                                throw task.getException();
                            }
                            return thumb_file.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                Uri downloadUri = task.getResult();
                                myUrl = downloadUri.toString();

                                Calendar calForTime = Calendar.getInstance();
                                SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                                final String currentTime =currentTimeFormat.format(calForTime.getTime());

                                Calendar calForDate = Calendar.getInstance();
                                SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
                                String currentDate = currentDateFormat.format(calForDate.getTime());


                                reference = FirebaseDatabase.getInstance().getReference("Feature_photos").child(mAuth.getUid());
                                 String id = reference.push().getKey();
//                                HashMap<String, Object> hashMap = new HashMap<>();
//                                hashMap.put("imageURL", myUrl);
//                                hashMap.put("post_text", caption.getText().toString());
//                                hashMap.put("id", id);
//                                hashMap.put("time", currentTime);
//                                hashMap.put("date", currentDate);
//                                hashMap.put("timestamp", System.currentTimeMillis());
//                                hashMap.put("publisher", mAuth.getUid());
//                                reference.child(id).setValue(hashMap);


                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                                HashMap<String, Object> hashMap2 = new HashMap<>();
                                hashMap2.put("post_text", caption.getText().toString());
                                hashMap2.put("publisher", mAuth.getUid());
                                hashMap2.put("postid", id);
                                hashMap2.put("type", "feature_photo");
                                hashMap2.put("imageUrl", myUrl);
                                hashMap2.put("time", currentTime);
                                hashMap2.put("date", currentDate);
                                hashMap2.put("timestamp", System.currentTimeMillis());
                                reference.child(id).setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(FeaturePictureActivity.this, "Feature photo uploaded!", Toast.LENGTH_SHORT).show();
                                        finish();
                                        DatabaseReference rf = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
                                        HashMap<String, Object> hashM = new HashMap<>();
                                        hashM.put("featurephoto", System.currentTimeMillis()+86400000 );
                                        rf.updateChildren(hashM);

                                    }
                                });

//                            Toast.makeText(UpdateProfilePictureActivity.this, "Profile picture Uploaded!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    choosefile.setVisibility(View.GONE);
                    uploadfile.setVisibility(View.VISIBLE);
                }else {
                    Toast.makeText(getApplicationContext(), "something went wrong!!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), UpdateProfilePictureActivity.class));
                    finish();
                }

            }
        });


    }
}
