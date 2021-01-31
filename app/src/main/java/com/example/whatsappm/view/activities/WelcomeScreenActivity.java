package com.example.whatsappm.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.whatsappm.R;
import com.example.whatsappm.view.activities.auth.SendigOTP;

public class WelcomeScreenActivity extends AppCompatActivity {

    //widgets
    Button agree_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        initView();
        agree_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getApplicationContext(),SendigOTP.class);
                startActivity(intent);
            }
        });

    }

    //initView
    protected void initView()
    {
        agree_btn = findViewById(R.id.btn_agree);
    }

}