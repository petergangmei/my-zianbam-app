package com.zianbam.yourcommunity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.zianbam.yourcommunity.Helper.GridViewImageHelper;
import com.zianbam.yourcommunity.Model.GridModel;
import com.zianbam.yourcommunity.Model.Post;
import com.zianbam.yourcommunity.Model.User;
import com.zianbam.yourcommunity.Utils.FileSearch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import id.zelory.compressor.Compressor;

public class CreatePostActivity extends AppCompatActivity {

    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 3;
    private static final int IMAGE_PICK_CODE = 1;
    public static final int REQUEST_IMAGE = 100;
    private static final int MyrequestCode = 5;
    private int Num_Grid_Column = 4;

    private  StorageReference thumbImageRef;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private EditText post_text;
    private TextView sharepost_btn;
    private ImageView image_profile, close, opencamera, opengellary,selectedImage;
    LinearLayout loading, uploaded;
    private Spinner spinner1, spinner2;
    GridView gridView;
    String mAppend = "file:/";
    ArrayList<String> directories;
    ScrollView gscroll;
    String selectedImageUrl;
    Bitmap bitmap,finalimage;
    Uri uri;
    String myUrl ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        mAuth = FirebaseAuth.getInstance();
        thumbImageRef = FirebaseStorage.getInstance().getReference("Posts");

        Intent intent = getIntent();
        final String postype = intent.getStringExtra("post_type");
        final String postid = intent.getStringExtra("post_id");


        getProfileImage();
        init(postype, postid);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        opencamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opencameraclass();
            }
        });
        opengellary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opengellaryAct();
            }
        });

//        String ROOT_DIR  = Environment.getExternalStorageDirectory().getPath();
//         String Pictures = ROOT_DIR + "/DCIM/Camera";

    }




    private void populatePosttext(String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                post_text.setText(post.getPost_text());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void opengellaryAct() {
        uploaded.setVisibility(View.GONE);
        sharepost_btn.setEnabled(false);
        Intent intent = new Intent(CreatePostActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);

//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
    private void opencameraclass() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MyrequestCode);
        }else {
            sharepost_btn.setEnabled(false);
            uploaded.setVisibility(View.GONE);

//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intent, MyrequestCode);
            uploaded.setVisibility(View.GONE);
            Intent intent = new Intent(CreatePostActivity.this, ImagePickerActivity.class);
            intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

            // setting aspect ratio
            intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
            intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

            // setting maximum bitmap width and height
            intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
            intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
            intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

            startActivityForResult(intent, REQUEST_IMAGE);
        }
    }


    private void updatePost() {
        sharepost_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(post_text.getText().toString())){
                    Toast.makeText(CreatePostActivity.this, "Post updating, please wait...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    Intent intent = getIntent();
                    final String postid = intent.getStringExtra("post_id");
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("post_text", post_text.getText().toString());
                    reference.child(postid).updateChildren(hashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(CreatePostActivity.this, "Post updated succesfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                }
            }
        });
    }
    private void getProfileImage() {
        reference =  FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           if (dataSnapshot.exists()){
               User user = dataSnapshot.getValue(User.class);
               Picasso.get().load(user.getImageURL()).placeholder(R.drawable.user_avatar)
                       .into(image_profile);
           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createPost() {
        sharepost_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(CreatePostActivity.this, "posting..", Toast.LENGTH_SHORT).show();

                if (uri != null){
                    finish();
                    compressanduploadtask();
                }else {
                    if (!TextUtils.isEmpty(post_text.getText().toString())){
                        finish();
                        Calendar calForTime = Calendar.getInstance();
                        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                        final String currentTime =currentTimeFormat.format(calForTime.getTime());

                        Calendar calForDate = Calendar.getInstance();
                        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
                        String currentDate = currentDateFormat.format(calForDate.getTime());

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                        String postid = reference.push().getKey();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("post_text", post_text.getText().toString());
                        hashMap.put("publisher", mAuth.getUid());
                        hashMap.put("postid", postid);
                        hashMap.put("type", "text_post");
                        hashMap.put("audience", String.valueOf(spinner1.getSelectedItem()));
                        hashMap.put("imageUrl", "null");
                        hashMap.put("time", currentTime);
                        hashMap.put("date", currentDate);
                        hashMap.put("timestamp", System.currentTimeMillis());
                        reference.child(postid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(CreatePostActivity.this, "Post added succesfully!", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        });
                    }else {
                        Toast.makeText(CreatePostActivity.this, "You can not share an empty post.", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
    }

    private void compressanduploadtask() {
        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        final String currentTime =currentTimeFormat.format(calForTime.getTime());

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-mm-yyyy");
        String currentDate = currentDateFormat.format(calForDate.getTime());

        File thumb_filePath = new File(uri.getPath());
        String userid = mAuth.getUid();

        try {
            bitmap = new Compressor(getApplicationContext())
                    .setMaxHeight(100)
                    .setMaxHeight(100)
                    .setQuality(20)
                    .compressToBitmap(thumb_filePath);
        }catch (IOException e){
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50, byteArrayOutputStream);

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

                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                    HashMap<String, Object> hashMap2 = new HashMap<>();

                    hashMap2.put("post_text", post_text.getText().toString());
                    hashMap2.put("publisher", mAuth.getUid());
                    hashMap2.put("postid", id);
                    hashMap2.put("type", "photo_post");
                    hashMap2.put("imageUrl", myUrl);
                    hashMap2.put("time", currentTime);
                    hashMap2.put("date", currentDate);
                    hashMap2.put("audience", String.valueOf(spinner1.getSelectedItem()));
                    hashMap2.put("timestamp", System.currentTimeMillis());
                    reference.child(id).setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();

                        }
                    });

                }
            }
        });

    }



    private void loadSelectedImage(String url) {

        loading.setVisibility(View.VISIBLE);
        selectedImage.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.setVisibility(View.GONE);
                uploaded.setVisibility(View.VISIBLE);
                sharepost_btn.setEnabled(true);
            }
        },2500);
        Picasso.get().load(url).placeholder(R.drawable.image_placeholder).into(selectedImage);
    }


    private void init(String postype, String postid) {

        loading = findViewById(R.id.loading);
        uploaded = findViewById(R.id.uploaded);
        close = findViewById(R.id.close);
        post_text = findViewById(R.id.post_text);
        sharepost_btn = findViewById(R.id.sharepost_btn);
        image_profile = findViewById(R.id.image_profile);
        opencamera = findViewById(R.id.opencamera);
        opengellary = findViewById(R.id.opengellary);
        selectedImage = findViewById(R.id.selecteImage);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
//        spinner2 = (Spinner) findViewById(R.id.spinner2);
//        gscroll = findViewById(R.id.gscroll);
//          gridView = findViewById(R.id.gridView);
//        imageselected = findViewById(R.id.imageselected);
        //        addItemsOnSpinner2();
//        addListenerOnButton();
        addListenerOnSpinnerItemSelection();

        post_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(post_text.getText().toString())) {
                    sharepost_btn.setEnabled(false);
                }else {
                    sharepost_btn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if (postype.equals("new")){
            sharepost_btn.setTag("new");
            sharepost_btn.setText("POST");
            createPost();
        }else {
            sharepost_btn.setTag("update");
            sharepost_btn.setText("POST");
            updatePost();
            populatePosttext(postid);

        }

    }



    private ArrayList<GridModel> getData(){
        ArrayList<GridModel> gridModelst = new ArrayList<>();
        String ROOT_DIR  = Environment.getExternalStorageDirectory().getPath();

        String Pictures = ROOT_DIR + "/DCIM/Camera";

//    File myfiles = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File myfiles = new File(Pictures);
        GridModel g;

        if (myfiles.exists()){
            File[] files = myfiles.listFiles();
            for (int i=0; i<files.length; i++){
                File file = files[i];
                g = new GridModel();
                g.setUri(Uri.fromFile(file));
                g.setName(file.getName());

                gridModelst.add(g);

            }
        }
        return gridModelst;
    }
    private void setupGridViewImages(String selectedFolder){
        final ArrayList<String> imgUrls = FileSearch.getfilePath(selectedFolder);
        ArrayList<String> pathArray = new ArrayList<>();


//        File file = new File(selectedFolder);
//        File[] listfiles = file.listFiles();
//
//        for(int i = 0 ; i < listfiles.length; i++){
//            if (listfiles[i].isFile()){
//                pathArray.add((listfiles[i].getAbsolutePath()));
//            }
//        }

        final GridViewImageHelper gridViewImageHelper = new GridViewImageHelper(getApplicationContext(),
                R.layout.layout_grid_image_view, mAppend, pathArray);

        int gridwidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridwidth/Num_Grid_Column;
        gridView.setColumnWidth(imageWidth);
        Toast.makeText(this, "id"+imgUrls, Toast.LENGTH_LONG).show();
        gridView.setAdapter(gridViewImageHelper);
//        setImageUrl(imgUrls.get(0),imageselected, mAppend);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                setImageUrl(imgUrls.get(position),imageselected,mAppend);
            }
        });



    }
    private void setImageUrl(String imageUrl, ImageView imageView, String mAppend){
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mAppend + imageUrl, imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }
    // add items into spinner dynamically
    public void addItemsOnSpinner2() {

        spinner2 = (Spinner) findViewById(R.id.spinner2);
        List<String> list = new ArrayList<String>();
        list.add("list 1");
        list.add("list 2");
        list.add("list 3");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
    }
    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());

    }
    // get the selected dropdown list value
    public void addListenerOnButton() {


}



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                 uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap btmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                    // loading profile image from local cache
                    loadSelectedImage(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
    }
}
