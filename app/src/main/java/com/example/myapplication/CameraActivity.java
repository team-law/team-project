package com.example.myapplication;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.camerakit.CameraKitView;
import com.example.myapplication.Models.Event;
import com.example.myapplication.Models.Picture;
import com.example.myapplication.Models.UserNode;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.parceler.Parcels;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.R.drawable.com_facebook_favicon_blue;

public class CameraActivity extends AppCompatActivity {

    private CameraKitView cameraKitView;
    private Button btnCapture;
    private ImageView ivResult;
    private StorageReference mStorageRef;
    private Button btnUpload;
    public String photoFileName = "photo.jpg";
    private File photoFile;
    private byte[] postImage;

    private Event event;


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

        cameraKitView = (CameraKitView) findViewById(R.id.camera);
        btnCapture = (Button) findViewById(R.id.btnCapture);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        ivResult = (ImageView) findViewById(R.id.ivResult);

        ivResult.setImageResource(com_facebook_favicon_blue);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        event = (Event) Parcels.unwrap(getIntent().getParcelableExtra("event"));


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

                        FileOutputStream outputStream;


                        try {
                            Bitmap bmp = BitmapFactory.decodeByteArray(capturedImage, 0, capturedImage.length);

                            postImage = capturedImage;

                            ivResult.setImageBitmap(Bitmap.createScaledBitmap(bmp, ivResult.getWidth(),
                                    ivResult.getHeight(), false));

                            outputStream = openFileOutput(photoFileName, Context.MODE_PRIVATE);
                            outputStream.write(capturedImage);
                            outputStream.close();


                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                fileUploader();
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
        eventRef.child("allPictures").child("imgRef").setValue(true);

        Toast.makeText(this, "Picture object uploaded successfully!", Toast.LENGTH_SHORT).show();

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
        cameraKitView.onStart();
        mAuth.addAuthStateListener(mAuthListener);
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
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

}
