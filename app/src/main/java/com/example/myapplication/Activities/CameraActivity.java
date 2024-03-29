package com.example.myapplication.Activities;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;
import com.example.myapplication.Models.Event;
import com.example.myapplication.Models.Picture;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import org.parceler.Parcels;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import static com.example.myapplication.R.drawable.camera;
import static com.example.myapplication.R.drawable.com_facebook_favicon_blue;
import static com.example.myapplication.R.drawable.grapefruit_pink;

public class CameraActivity extends AppCompatActivity {
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private CameraKitView ckv;
    private Button btnCapture;
    private Button btnCancel;
    private Button btnReverse;
    private ImageView ivResult;
    private StorageReference mStorageRef;
    private Button btnUpload;
    public String photoFileName = "photo.jpg";
    private File photoFile;
    private byte[] postImage;
    private TextView tvPicsLeft;
    private Event event;

    private int count;

//    List<Filter> filters = FilterPack.getFilterPack(CameraActivity.this);



    private final String TAG = "CameraActivity";

    //Firebase things
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private DatabaseReference eventRef;
    FirebaseUser user;


    public Uri imguri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mStorageRef = FirebaseStorage.getInstance().getReference("Images");

        ckv = (CameraKitView) findViewById(R.id.camera);
        btnCapture = (Button) findViewById(R.id.btnCapture);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        ivResult = (ImageView) findViewById(R.id.ivResult);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnReverse = (Button) findViewById(R.id.btnReverse);
        tvPicsLeft = (TextView) findViewById(R.id.tvPicsLeft);

        ckv.setFacing(CameraKit.FACING_BACK);

        btnReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ckv.getFacing() == CameraKit.FACING_BACK) {
                    ckv.setFacing(CameraKit.FACING_FRONT);
                    ivResult.setScaleX(-1f);
                }
                else{
                    ckv.setFacing(CameraKit.FACING_BACK);
                    ivResult.setScaleX(1f);
                }
            }
        });


        ckv.setGestureListener(new CameraKitView.GestureListener() {
            @Override
            public void onTap(CameraKitView cameraKitView, float v, float v1) {

            }

            @Override
            public void onLongTap(CameraKitView cameraKitView, float v, float v1) {

            }

            @Override
            public void onDoubleTap(CameraKitView cameraKitView, float v, float v1) {
                if(cameraKitView.getFacing() == CameraKit.FACING_BACK) {
                    cameraKitView.setFacing(CameraKit.FACING_FRONT);
                    ivResult.setScaleX(-1f);
                }
                else{
                    cameraKitView.setFacing(CameraKit.FACING_BACK);
                    ivResult.setScaleX(1f);
                }

            }

            @Override
            public void onPinch(CameraKitView cameraKitView, float v, float v1, float v2) {
                cameraKitView.setZoomFactor(v + v1 + v2);
            }
        });



        ivResult.setImageResource(grapefruit_pink);


        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        event = (Event) Parcels.unwrap(getIntent().getParcelableExtra("event"));
        count = getIntent().getIntExtra("picsLeft", 0);
        tvPicsLeft.setText(Integer.toString(count));


        eventRef = mFirebaseDatabase.getReference("Events/" + event.accessCode);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) { //user is signed in

                } else {

                }
            }
        };


        btnCapture.setOnClickListener(photoOnClickListener);

        btnUpload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                fileUploader();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                count++;
                tvPicsLeft.setText(Integer.toString(count));
                ckv.setVisibility(View.VISIBLE);
                ivResult.setVisibility(View.INVISIBLE);
                btnCapture.setVisibility(View.VISIBLE);
                btnUpload.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);
            }
        });
    }

    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void fileUploader() {

//
       photoFile = getPhotoFileUri(photoFileName);
        //Uri file = FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile);
        Uri file = Uri.fromFile(photoFile);

        String imgRef = System.currentTimeMillis() +".jpg";
        StorageReference ref = mStorageRef.child(imgRef);
        /*
        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        StorageReference riversRef = mStorageRef.child("images/rivers.jpg");
*/
        ref.putBytes(postImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CameraActivity.this, "Image upload success", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CameraActivity.this, "Image upload failure", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });


        // push picture to database
        String mPicId = myRef.push().getKey();

        //TODO -- add createdAt timestamp

        Picture picture = new Picture(imgRef,
                user.getUid(),
                "test");

        myRef.child("Picture").push();
        myRef.child("Picture").child(mPicId).setValue(picture); //pushes the event to firebase

/*
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String accessCode = dataSnapshot.getValue("accessCode");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        */
        eventRef.child("allPictures").child(mPicId).setValue(true);
        myRef.child("UserNodes").child(user.getUid()).child("picturesTaken").child(mPicId).setValue(event.accessCode);
        //TODO: setting the value to picture might error/make things complicated, change later

        Toast.makeText(this, "Picture object uploaded successfully!", Toast.LENGTH_SHORT).show();

        finish();
    }


    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    protected void onStart() {
        super.onStart();
        ckv.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    protected void onResume() {
        super.onResume();
        ckv.onResume();
    }
    @Override
    protected void onPause() {
        ckv.onPause();
        super.onPause();
    }
    @Override
    protected void onStop() {
        ckv.onStop();
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ckv.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /*

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){

            imguri=data.getData();
            ivResult.setImageURI(imguri);
        }
    }
*/
    private View.OnClickListener photoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ckv.captureImage(new CameraKitView.ImageCallback() {
                @Override
                public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {
                    count--;
                    tvPicsLeft.setText(Integer.toString(count));
                    Bitmap bmp = BitmapFactory.decodeByteArray(capturedImage, 0, capturedImage.length);

                    postImage = capturedImage;

                    ivResult.setImageBitmap(Bitmap.createScaledBitmap(bmp, cameraKitView.getWidth(),
                            cameraKitView.getHeight(), false));


                    ivResult.setVisibility(View.VISIBLE);
                    ckv.setVisibility(View.INVISIBLE);
                    // ivResult.setVisibility(View.VISIBLE);
                    btnCapture.setVisibility(View.INVISIBLE);
                    btnUpload.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                }
            });
        }
    };

}
