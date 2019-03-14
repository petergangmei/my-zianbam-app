package com.zianbam.yourcommunity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.fenchtose.nocropper.CropperImageView;
import com.fenchtose.nocropper.CropperView;

import java.io.File;

public class CropSelectedimgActivity extends AppCompatActivity {
    TextView next, textView;
    ImageView imageView1,close,toggle,rotate;
    CropperImageView cropperImageView;
    String selectedimageurl;

    private static final int REQUEST_CODE_READ_PERMISSION = 22;
    private static final int REQUEST_GALLERY = 21;
    private static final String TAG = "MainActivity";

    private Bitmap originalBitmap;
    private Bitmap mBitmap;
    private boolean isSnappedToCenter = false;
    private int rotationCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_selectedimg);
        Intent intent = getIntent();

        close = findViewById(R.id.close);
        next = findViewById(R.id.next);
        toggle = findViewById(R.id.toggle_button);
        rotate = findViewById(R.id.rotate_button);
         textView = findViewById(R.id.textview);
        imageView1 = findViewById(R.id.imageview1);

//        cropperImageView = (CropperView)findViewById(R.id.cropImageView);
        CropperView cropperView = (CropperView)findViewById(R.id.cropImageView);
        initView();
//        cropperView.setVisibility(View.GONE);


        selectedimageurl = intent.getStringExtra("selectedImageUrl");
        Uri uri = Uri.parse(selectedimageurl);
        String  path  = uri.getPath();
        File imgFile = new  File(path);

        Bitmap originalBitmap = BitmapFactory.decodeFile("/storage/emulated/0/DCIM/Camera/IMG_20190310_211125.jpg");
        imageView1.setImageBitmap(originalBitmap);

        cropperView.setImageBitmap(originalBitmap);
        File sd = Environment.getExternalStorageDirectory();
//        File image = new File(sd+filePath, imageName);
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
//        bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
//        cropperImageView.setImageBitmap(originalBitmap);
//        Toast.makeText(this, "toast "+originalBitmap, Toast.LENGTH_LONG).show();
        textView.setText(sd+"...."+imgFile.getAbsolutePath()+ " bitimage:- "+originalBitmap);


//        Toast.makeText(this, "path: "+selectedimageurl, Toast.LENGTH_LONG).show();

        //        Uri selectedimageurl = intent.getStringExtra("selectedImageUrl");
//        Uri myUri = Uri.parse(intent.getStringExtra("selectedImageUrl"));
//        Toast.makeText(this, "image "+selectedimageurl, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "useri"+myUri, Toast.LENGTH_SHORT).show();
//        imageView1.setImageURI(uri);



//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cropandcontinue();
//            }
//        });
//
//        toggle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                togglecrop();
//            }
//        });
//
//        rotate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }



//    private void togglecrop() {
//        boolean enable = cropperImageView.isGestureEnabled();
//        cropperImageView.setGestureEnabled(!enable);
//        Toast.makeText(this, "Gesture: "+(enable?"Enabled":"Disabled"), Toast.LENGTH_SHORT).show();
//    }
//
//    public void cropandcontinue() {
//        mBitmap = cropperImageView.cropBitmap();
//        if (mBitmap != null){
//            cropperImageView.setImageBitmap(mBitmap);
//        }
//    }
//
    public void initView() {

    }
}
