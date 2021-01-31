package com.example.whatsappm.view.activities.status;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappm.R;
import com.example.whatsappm.databinding.ActivityAddStatusBinding;
import com.example.whatsappm.managers.ChatService;
import com.example.whatsappm.models.StatusModel;
import com.example.whatsappm.service.FirebaseService;
import com.example.whatsappm.view.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.UUID;

public class AddStatusActivity extends AppCompatActivity {

    private Uri image_uri;

    private ActivityAddStatusBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding= DataBindingUtil.setContentView(this,R.layout.activity_add_status);

        image_uri = MainActivity.image_uri;
        setInfo();
        initClick();



    }

    private void setInfo(){

        Glide.with(this).load(image_uri).into(binding.imageView);

    }

    private void initClick(){

        //back button
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //send image button
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new FirebaseService(AddStatusActivity.this).uploadImageToFireBaseStorage(image_uri, new FirebaseService.OnCallBack() {
                    @Override
                    public void onUploadSuccess(String imageUrl) {
                        StatusModel status = new StatusModel();
                        status.setId(UUID.randomUUID().toString());
                        status.setCreatedDate(new ChatService(AddStatusActivity.this).getCurrentDate());
                        status.setImageStatus(imageUrl);
                        status.setUserID(FirebaseAuth.getInstance().getUid());
                        status.setViewCount("0");
                        status.setTextStatus(binding.edDescription.getText().toString().trim());


                        new FirebaseService(AddStatusActivity.this).addNewStatus(status, new FirebaseService.OnAddNewStatusCallBack() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(AddStatusActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onFailed() {

                                Toast.makeText(AddStatusActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        });

                    }

                    @Override
                    public void onUploadFailed(Exception e) {

                        Log.e("TAG", "onUploadFailed: ",e );

                    }
                });

            }
        });

    }

}