package com.example.whatsappm.view.activities.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappm.R;
import com.example.whatsappm.view.activities.profile.ProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    //widgets
    private TextView text_user_name_profile;
    private LinearLayout linearProfile;
    private CircularImageView proImg1;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initView();
        firestore=FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null)
        {
            getInfo();

        }

        viewProfile();


    }

    private void getInfo()
    {
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                String userName = Objects.requireNonNull(documentSnapshot.get("userName")).toString();
                String imageeProfile = documentSnapshot.getString("imageProfile");

                text_user_name_profile.setText(userName);
                Glide.with(SettingsActivity.this).load(imageeProfile).into(proImg1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(SettingsActivity.this, "failed"+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initView()
    {
        text_user_name_profile =findViewById(R.id.txt_user_name_profile);
        linearProfile = findViewById(R.id.linear_info_go_to);
        proImg1 = findViewById(R.id.image_profile1);

    }

    private void viewProfile()
    {
        linearProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
            }
        });

    }

}