package com.example.whatsappm.view.activities.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappm.BuildConfig;
import com.example.whatsappm.R;
import com.example.whatsappm.common.Common;
import com.example.whatsappm.view.activities.SplashActivity;
import com.example.whatsappm.view.activities.display.ViewImageActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    private int IMAGE_GALLERY_REQUEST=1;
    private Uri image_uri;

    private TextView tv_username,tv_user_phone;
    private FloatingActionButton cameraButton;
    private LinearLayout lnEditName;
    private EditText newUserName;
    private BottomSheetDialog bottomSheetDialog,bsDialogEditName;
    private CircularImageView imgProfile;
    private ProgressDialog progressDialog;
    private Button signOutBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initView();

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firestore=FirebaseFirestore.getInstance();
        if (firebaseUser != null)
        {
            getInfo();
        }

        clickOnFabButton();

        onClickOnEditNameLn();

        onClickImageToOpen();


        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogSignOut();
            }
        });


    }

    private void getInfo()
    {
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                String userName = documentSnapshot.getString("userName");
                String userPhone = documentSnapshot.getString("userPhone");
                String image_Profile = documentSnapshot.getString("imageProfile");

                tv_username.setText(userName);
                tv_user_phone.setText(userPhone);
                Glide.with(ProfileActivity.this).load(image_Profile).into(imgProfile);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void initView()
    {
        tv_username = findViewById(R.id.tv_user_name);
        tv_user_phone = findViewById(R.id.tv_user_phone);

        cameraButton = findViewById(R.id.fab_camera_button);

        imgProfile = findViewById(R.id.image_profile);

        progressDialog=new ProgressDialog(this);

        lnEditName=findViewById(R.id.ln_edit_name);

        signOutBtn = findViewById(R.id.sign_out_btn);

    }

    private void clickOnFabButton()
    {
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                showBottomSheetPicPhoto();

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

    private void openGallery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select image"),IMAGE_GALLERY_REQUEST);

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
            uploadImageToFirebase();

        }
        if(requestCode==440
                &&resultCode==RESULT_OK
              )
        {
            uploadImageToFirebase();

        }
    }


    private String getFileExtention(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImageToFirebase()
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
                    Toast.makeText(ProfileActivity.this, "uploaded successfully", Toast.LENGTH_SHORT).show();

                    HashMap<String,Object> hashMap =new HashMap<>();
                    hashMap.put("imageProfile",sdownload_url);

                    progressDialog.dismiss();

                    firestore.collection("Users").document(firebaseUser.getUid()).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(ProfileActivity.this, "uploaded successfully", Toast.LENGTH_SHORT).show();
                            getInfo();

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(ProfileActivity.this, "uploaded Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });

        }

    }


    private void showBottomSheetEditName()
    {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_name,null);
        ((View) view.findViewById(R.id.cancelBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsDialogEditName.dismiss();
            }
        });
        newUserName = view.findViewById(R.id.editTextNameEdit);
        ((View) view.findViewById(R.id.saveBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (newUserName.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(ProfileActivity.this, "name can't be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    updateName(newUserName.getText().toString().trim());
                    bsDialogEditName.dismiss();
                }


            }
        });



        bsDialogEditName = new BottomSheetDialog(this);
        bsDialogEditName.setContentView(view);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Objects.requireNonNull(bsDialogEditName.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bsDialogEditName.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                bsDialogEditName=null;
            }
        });

        bsDialogEditName.show();

    }

    private void onClickOnEditNameLn()
    {
        lnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetEditName();
            }
        });
    }

    private void updateName(String newName)
    {
        firestore.collection("Users").document(firebaseUser.getUid()).update("userName",newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                Toast.makeText(ProfileActivity.this, "update is successful", Toast.LENGTH_SHORT).show();
                getInfo();
            }
        });

    }

    private void onClickImageToOpen()
    {
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imgProfile.invalidate();
                Drawable dr =imgProfile.getDrawable();
                Common.IMAGE_BITMAP = ((BitmapDrawable)dr.getCurrent()).getBitmap();
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation
                        (ProfileActivity.this,imgProfile,"image");

                Intent intent = new Intent(ProfileActivity.this, ViewImageActivity.class);
                startActivity(intent,activityOptionsCompat.toBundle());

            }
        });
    }

    private void showDialogSignOut()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setMessage("Do you want to sign out ? ");
        builder.setPositiveButton("sign out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, SplashActivity.class));

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}