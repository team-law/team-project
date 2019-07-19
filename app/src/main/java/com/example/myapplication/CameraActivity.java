package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.camerakit.CameraKitView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;

import static com.example.myapplication.R.drawable.com_facebook_favicon_blue;

public class CameraActivity extends AppCompatActivity {

    private CameraKitView cameraKitView;
    private Button btnCapture;
    private ImageView ivResult;
    private StorageReference mStorageRef;


    public Uri imguri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        cameraKitView = (CameraKitView) findViewById(R.id.camera);
        btnCapture = (Button) findViewById(R.id.btnCapture);
        ivResult = (ImageView) findViewById(R.id.ivResult);

        ivResult.setImageResource(com_facebook_favicon_blue);


//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.common_google_signin_btn_icon_light_normal);
//        ivResult.setImageBitmap(b);

//        Bitmap bmp = BitmapFactory.decodeByteArray(capturedImage, 0, capturedImage.length);
//
//        ivResult.setImageBitmap(Bitmap.createScaledBitmap(bmp, ivResult.getWidth(),
//                ivResult.getHeight(), false));

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                    @Override
                    public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(capturedImage, 0, capturedImage.length);
                        ivResult.setImageBitmap(Bitmap.createScaledBitmap(bmp, ivResult.getWidth(),
                                ivResult.getHeight(), false));
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }
    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }
    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){

            imguri=data.getData();
            ivResult.setImageURI(imguri);
        }
    }


}
