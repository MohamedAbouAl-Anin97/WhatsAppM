package com.example.whatsappm.view.activities.display;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.whatsappm.R;
import com.example.whatsappm.common.Common;
import com.jsibbold.zoomage.ZoomageView;

public class ViewImageActivity extends AppCompatActivity {

    private ZoomageView viewPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        initView();


        viewPic.setImageBitmap(Common.IMAGE_BITMAP);



    }
    private void initView()
    {
        viewPic = findViewById(R.id.image_view);
    }
}