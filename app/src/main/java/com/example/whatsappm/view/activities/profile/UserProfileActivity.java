package com.example.whatsappm.view.activities.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.whatsappm.R;
import com.example.whatsappm.databinding.ActivityUserProfileBinding;

public class UserProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_user_profile);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String receiverID = intent.getStringExtra("userID");
        String userProfile = intent.getStringExtra("userProfile");


        if (receiverID!=null){

            binding.toolbar.setTitle(userName);
            if (userProfile != null) {
                if (userProfile.equals("")){
                    binding.imageProfile.setImageResource(R.drawable.icon_male_photo);  // set  default image when profile user is null
                } else {
                    Glide.with(this).load(userProfile).into( binding.imageProfile);
                }
            }
        }


    }
}