package com.example.whatsappm.view.activities.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappm.BuildConfig;
import com.example.whatsappm.R;
import com.example.whatsappm.models.user.Users;
import com.example.whatsappm.view.activities.MainActivity;
import com.example.whatsappm.view.activities.profile.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class UserInfoActivity extends AppCompatActivity {

    private CircularImageView imageProfile;
    private EditText userName;
    private Button nextButton;

    private ProgressDialog progressDialog;


    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    private int IMAGE_GALLERY_REQUEST=1;
    private Uri image_uri;
    private BottomSheetDialog bottomSheetDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);


        checkIfUserAlreadyExist();
        initView();
        progressDialog=new ProgressDialog(this);

        nextButton.setOnClickListener(new View.OnClickListener() {@Override
            public void onClick(View view) { if(userName.getText().toString().isEmpty()) {
                    Toast.makeText(UserInfoActivity.this, "please enter your name", Toast.LENGTH_SHORT).show();
                } else {
                      updateData();
                    } }});


        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetPicPhoto();
            }
        });


    }



    //to initialize view
    private void initView()
    {
        imageProfile = findViewById(R.id.image_profile1);
        userName = findViewById(R.id.EditTextName);
        nextButton = findViewById(R.id.nextButton);
    }

    private void updateData()
    {
        progressDialog.setMessage("please wait");
        progressDialog.show();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        String updateUserName = userName.getText().toString().trim();

        if(firebaseUser != null)
        {
            String userId=firebaseUser.getUid();

            Users users = new Users(userId,
                    updateUserName,
                    firebaseUser.getPhoneNumber(),
                    uploadImageToFirebase(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "");

            firebaseFirestore.collection("Users").document(firebaseUser.getUid()).set(users)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(UserInfoActivity.this, "update is successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    progressDialog.dismiss();
                    Toast.makeText(UserInfoActivity.this, "update failed", Toast.LENGTH_SHORT).show();

                }
            });

        }else
            {
                progressDialog.dismiss();
                Toast.makeText(this, "you need to login first", Toast.LENGTH_SHORT).show();
            }

    }

    //is user Already exist ?
    private void checkIfUserAlreadyExist(){

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    if (userName.getText().toString().isEmpty()){
                        Toast.makeText(UserInfoActivity.this, "you should enter your name", Toast.LENGTH_SHORT).show();
                    }else {
                    userName.setText(task.getResult().getString("userName"));
                    Glide.with(UserInfoActivity.this).load(task.getResult().getString("imageProfile")).into(imageProfile);
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));}

                }
                else {Log.d("TAG", "Error getting documents "+task.getException());}
            }
        });

    }

    private void showBottomSheetPicPhoto()
    {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_pick,null);

        ((View) view.findViewById(R.id.ln_gallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
                bottomSheetDialog.dismiss();
            }
        });
        ((View) view.findViewById(R.id.ln_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //to open the camera
                checkCameraPermission();

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Objects.requireNonNull(bottomSheetDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                bottomSheetDialog=null;
            }
        });

        bottomSheetDialog.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},221);
        }
        else {
            openCamera();
        }
    }

    private void openCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_"+ timeStamp +".jpg";

        try {
            File file = File.createTempFile("IMG_"+timeStamp,".jpg",getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            image_uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".provider",file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
            intent.putExtra("listPhotoName",imageFileName);
            startActivityForResult(intent,440);

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_GALLERY_REQUEST
                &&resultCode==RESULT_OK
                &&data !=null
                &&data.getData()!=null)
        {

            image_uri=data.getData();
            Glide.with(UserInfoActivity.this).load(image_uri).into(imageProfile);
            //uploadImageToFirebase();

        }
        if(requestCode==440 && resultCode==RESULT_OK)
        {
           // uploadImageToFirebase();
            Glide.with(UserInfoActivity.this).load(image_uri).into(imageProfile);

        }
    }


    private String getFileExtention(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private String uploadImageToFirebase()
    {

        if (image_uri != null)
        {
            progressDialog.setMessage("uploading...");
            progressDialog.show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ImagesProfile/"+System.currentTimeMillis()+"."+getFileExtention(image_uri));
            storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> urlTask =taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl=urlTask.getResult();

                    final String sdownload_url=String.valueOf(downloadUrl);


                    HashMap<String,Object> hashMap =new HashMap<>();
                    hashMap.put("imageProfile",sdownload_url);
                    hashMap.put("userName", userName.getText().toString());

                    progressDialog.dismiss();

                    db.collection("Users").document(firebaseUser.getUid()).update(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(getApplicationContext(),"upload successfully",Toast.LENGTH_SHORT).show();
                           finish();



                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(), "uploaded Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });

        }


        return image_uri.toString();

    }

    private void openGallery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select image"),IMAGE_GALLERY_REQUEST);

    }


}