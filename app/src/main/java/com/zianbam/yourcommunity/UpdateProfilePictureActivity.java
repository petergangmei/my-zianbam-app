package com.zianbam.yourcommunity;
//activity_update_profile_picture
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class UpdateProfilePictureActivity extends AppCompatActivity {
    Uri imageUri;
    String myUrl ="";
    StorageTask uploadTask;
    StorageReference storageReference;
    Button choosefile, uploadfile;
    ImageView image_profile;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    Bitmap thumb_bitmap = null;
    private  StorageReference thumbImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_picture);

        mAuth = FirebaseAuth.getInstance();

        image_profile = findViewById(R.id.image_profile);
        choosefile = findViewById(R.id.choosefile);
        uploadfile = findViewById(R.id.uploadfile);

//        storageReference = FirebaseStorage.getInstance().getReference("Profiles");
        thumbImageRef = FirebaseStorage.getInstance().getReference("Profiles");

        choosefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UpdateProfilePictureActivity.this, "Openning...", Toast.LENGTH_SHORT).show();
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(UpdateProfilePictureActivity.this);
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
                    Toast.makeText(UpdateProfilePictureActivity.this, "uploading..", Toast.LENGTH_LONG).show();
                    finish();

                    File thumb_filePath = new File(imageUri.getPath());
                    String userid = mAuth.getUid();
                    try {
                        thumb_bitmap = new Compressor(getApplicationContext())
                                .setMaxHeight(50)
                                .setMaxHeight(50)
                                .setQuality(20)
                                .compressToBitmap(thumb_filePath);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,50, byteArrayOutputStream);

                    final byte[] final_image = byteArrayOutputStream.toByteArray();

                    final StorageReference thumb_file = thumbImageRef.child(mAuth.getUid()+".jpg");
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

                                reference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
//                        String postid = reference.push().getKey();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("imageURL", myUrl);
                                reference.updateChildren(hashMap);
//                        progressDialog.dismiss();

                                Toast.makeText(UpdateProfilePictureActivity.this, "Photo Uploaded!", Toast.LENGTH_SHORT).show();
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
