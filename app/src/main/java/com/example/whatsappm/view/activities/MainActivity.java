package com.example.whatsappm.view.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.whatsappm.BuildConfig;
import com.example.whatsappm.R;
import com.example.whatsappm.databinding.ActivityMainBinding;
import com.example.whatsappm.menu.CallsFragment;
import com.example.whatsappm.menu.CameraFragment;
import com.example.whatsappm.menu.ChatFragment;
import com.example.whatsappm.menu.StatusFragment;
import com.example.whatsappm.view.activities.contacts.ContactsActivity;
import com.example.whatsappm.view.activities.settings.SettingsActivity;
import com.example.whatsappm.view.activities.status.AddStatusActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_main);

        setUpWithViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        View tab1 = LayoutInflater.from(this).inflate(R.layout.custom_camera_tab,null);
        try {
            binding.tabLayout.getTabAt(0).setCustomView(tab1);
        }catch (Exception e){e.printStackTrace();}


        binding.viewPager.setCurrentItem(1);


        setSupportActionBar(binding.toolbar);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeFabIcon(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setUpWithViewPager(ViewPager viewPager)
    {
        MainActivity.sectionPagerAdapter adapter=new sectionPagerAdapter(getSupportFragmentManager());



        adapter.addFragment(new CameraFragment(),"");
        adapter.addFragment(new ChatFragment(),"Chats");
        adapter.addFragment(new StatusFragment(),"Status");
        adapter.addFragment(new CallsFragment(),"Calls");

        //we need 4 fragments
        viewPager.setAdapter(adapter);


    }
    //3awz arg3 lel hetta deh
    private static class sectionPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mfragmentList = new ArrayList<>();
        private final List<String> mFragmentTiltlList =new ArrayList<>();

        public sectionPagerAdapter (FragmentManager manager){super(manager);}

        @Override
        public Fragment getItem(int position){return mfragmentList.get(position);}

        @Override
        public int getCount (){return mfragmentList.size();}

        public void addFragment(Fragment fragment , String title)
        {
            mfragmentList.add(fragment);
            mFragmentTiltlList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position){return mFragmentTiltlList.get(position);}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id =item.getItemId();

        switch (id)
        {
            case R.id.menu_search: Toast.makeText(MainActivity.this,"Search",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_new_group: Toast.makeText(this, "Action New Group", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_new_broadcast:
                Toast.makeText(this, "Action new Broadcast", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_wa_web:
                Toast.makeText(this, "Action what'sApp Web", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_starred_messages:
                Toast.makeText(this, "Action starred messages", Toast.LENGTH_SHORT).show();
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;

        }


        return super.onOptionsItemSelected(item);
    }


    private void changeFabIcon(final int index)
    {
        binding.fabAction.hide();
        binding.btnAddStatus.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void run() {
                switch (index)
                {
                    case 0:
                        binding.fabAction.hide();
                    break;

                    case 1:
                        binding.fabAction.show();
                        binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_baseline_chat_24));
                        break;
                    case 2:
                        binding.fabAction.show();
                        binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_baseline_camera_alt_24));
                        binding.btnAddStatus.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        binding.fabAction.show();
                        binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_baseline_call_24));
                        break;
                }

            }
        },400);

        performOnClick(index);

    }

    private void performOnClick(final int index){

        binding.fabAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index==1)
                {
                    startActivity(new Intent(MainActivity.this,ContactsActivity.class));
                }else if (index==2)
                {
                    //Toast.makeText(MainActivity.this, "Camera ...", Toast.LENGTH_SHORT).show();
                    //to open camera
                    checkCameraPermission();
                }else {
                    Toast.makeText(MainActivity.this, "Call", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnAddStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Add Status", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    231);

        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    232);
        }
        else {
            openCamera();
        }
    }

    public static Uri image_uri = null;
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

        if (requestCode == 440
                && resultCode == RESULT_OK){
            //uploadToFirebase();
            if (image_uri!=null){
                startActivity(new Intent(MainActivity.this,AddStatusActivity.class)
                        .putExtra("image",image_uri));
            }
        }

    }

    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}