package com.example.whatsappm.view.activities.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.example.whatsappm.R;
import com.example.whatsappm.adapters.ChatsAdapter;
import com.example.whatsappm.databinding.ActivityChatsBinding;
import com.example.whatsappm.interfaces.OnReadChatCallBack;
import com.example.whatsappm.managers.ChatService;
import com.example.whatsappm.models.chat.Chats;
import com.example.whatsappm.service.FirebaseService;
import com.example.whatsappm.view.activities.dialog.DialogReviewSendImage;
import com.example.whatsappm.view.activities.profile.UserProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class ChatsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 332 ;
    private ActivityChatsBinding  binding;

    private String receiverID;
    private ChatsAdapter adapter;
    private List<Chats>list = new ArrayList<>();
    private String userProfile,userName;
    private boolean isActionShown = false;
    private ChatService chatService;
    private ImageButton back;

    private int IMAGE_GALLERY_REQUEST=1;
    private Uri image_uri;

    //Audio
    private MediaRecorder mediaRecorder;
    private String audio_path;
    private static final int REQUEST_CORD_PERMISSION = 332;
    private String sTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_chats);




        initialize();
        initBtnClick();

        binding.btnbackc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        readChats();

    }

    private void initialize()
    {
        Intent intent = getIntent();
        receiverID= intent.getStringExtra("userId");
        userName = intent.getStringExtra("userName");
        userProfile = intent.getStringExtra("userProfile");

        chatService = new ChatService(this,receiverID);

        if (receiverID!=null)
        {
            binding.tvUserNameChat.setText(userName);
            if (userProfile != null)
            {
                if (userProfile.equals("")){
                    binding.imgUserProfileChat.setImageResource(R.drawable.icon_male_photo);
                }
                else
                {
                    Glide.with(this).load(userProfile).into(binding.imgUserProfileChat);
                }
            }

        }

        //type some thing on edit text
        binding.edMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (TextUtils.isEmpty(binding.edMessage.getText().toString())){
                    binding.sendMsgBtn.setVisibility(View.INVISIBLE);
                    binding.recordButton.setVisibility(View.VISIBLE);
                }else {
                    binding.sendMsgBtn.setVisibility(View.VISIBLE);
                    binding.recordButton.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        LinearLayoutManager layoutManager =new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        layoutManager.setStackFromEnd(true);
        binding.recyclerViewChat.setLayoutManager(layoutManager);
        adapter = new ChatsAdapter(list,this);
        binding.recyclerViewChat.setAdapter(adapter);




        //initialize record button
        binding.recordButton.setRecordView(binding.recordView);
        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {

                if (!checkPermissionFromDevice()){

                binding.emojiBtn.setVisibility(View.INVISIBLE);
                binding.edMessage.setVisibility(View.INVISIBLE);
                binding.btnFile.setVisibility(View.INVISIBLE);
                binding.cameraBtn.setVisibility(View.INVISIBLE);

                startRecord();
                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

                if(vibrator != null){
                vibrator.vibrate(100);}

                }else {
                    requestPermission();
                }

            }

            @Override
            public void onCancel() {

                try {
                    mediaRecorder.reset();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFinish(long recordTime) {
                binding.emojiBtn.setVisibility(View.VISIBLE);
                binding.edMessage.setVisibility(View.VISIBLE);
                binding.btnFile.setVisibility(View.VISIBLE);
                binding.cameraBtn.setVisibility(View.VISIBLE);


                //stop Recording
                try {
                    sTime = getHumanTimeText(recordTime);
                    stopRecord();

                }catch (Exception e){

                }

            }

            @Override
            public void onLessThanSecond() {
                binding.emojiBtn.setVisibility(View.VISIBLE);
                binding.edMessage.setVisibility(View.VISIBLE);
                binding.btnFile.setVisibility(View.VISIBLE);
                binding.cameraBtn.setVisibility(View.VISIBLE);

            }
        });
        binding.recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                binding.emojiBtn.setVisibility(View.VISIBLE);
                binding.edMessage.setVisibility(View.VISIBLE);
                binding.btnFile.setVisibility(View.VISIBLE);
                binding.cameraBtn.setVisibility(View.VISIBLE);
            }
        });



    }

    @SuppressLint("DefaultLocale")
    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d",
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    private void readChats()
    {
        chatService.readChatData(new OnReadChatCallBack() {
            @Override
            public void onReadSuccess(List<Chats> list) {

                adapter.setList(list);

            }

            @Override
            public void onFailed() {

            }
        });
    }

    private void initBtnClick()
    {

        binding.sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(binding.edMessage.getText().toString()))
                {
                    chatService.sendTextMsg(binding.edMessage.getText().toString());

                    binding.edMessage.setText("");
                }
            }
        });

        binding.imgUserProfileChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ChatsActivity.this, UserProfileActivity.class)
                        .putExtra("userID",receiverID)
                        .putExtra("userProfile",userProfile)
                        .putExtra("userName",userName));
            }
        });


        binding.btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isActionShown)
                {
                    binding.layoutActions.setVisibility(View.GONE);
                    isActionShown=false;
                }else
                    {
                        binding.layoutActions.setVisibility(View.VISIBLE);
                        isActionShown = true;
                    }

            }
        });

        binding.btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openGallery();

            }
        });

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

        if(requestCode==IMAGE_GALLERY_REQUEST&&resultCode==RESULT_OK&&data !=null&&data.getData()!=null)
        {

            image_uri=data.getData();

            //uploadImageToFirebase();
            try {
            Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),image_uri);
            reviewImage(bitmap);

            }catch (Exception e)
             {
               e.printStackTrace();
             }

        }
    }

    private void reviewImage(Bitmap bitmap){

        new DialogReviewSendImage(ChatsActivity.this,bitmap).show(new DialogReviewSendImage.OnCallBack() {
            @Override
            public void onButtonSendClick() {
                //to upload image to firebase storage to get url image
                if (image_uri != null) {
                    final ProgressDialog progressDialog = new ProgressDialog(ChatsActivity.this);
                    progressDialog.setMessage("sending image..");
                    progressDialog.show();


                    //hide action buttons
                    binding.layoutActions.setVisibility(View.GONE);
                    isActionShown = false;

                    new FirebaseService(ChatsActivity.this).uploadImageToFireBaseStorage(image_uri, new FirebaseService.OnCallBack() {
                        @Override
                        public void onUploadSuccess(String imageUrl) {

                            //to send image
                            chatService.sendImage(imageUrl);
                            progressDialog.dismiss();

                        }

                        @Override
                        public void onUploadFailed(Exception e) {

                            e.printStackTrace();

                        }
                    });
                }

            }
        });
    }

    private boolean checkPermissionFromDevice() {
        int write_external_strorage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_strorage_result == PackageManager.PERMISSION_DENIED || record_audio_result == PackageManager.PERMISSION_DENIED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_CORD_PERMISSION);
    }

    private void startRecord(){
        setUpMediaRecorder();

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Recording Error", Toast.LENGTH_SHORT).show();
        }

    }

    private void stopRecord(){
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;

                chatService.sendVoice(audio_path);

            } else {
                Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Stop Recording Error :" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setUpMediaRecorder(){
        String path_save = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ UUID.randomUUID().toString()+"audio_record.m4a";
        audio_path = path_save;

        mediaRecorder = new MediaRecorder();

        try{
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(path_save);
        }catch (Exception e){
            Log.d("TAG", "setUpMediaRecorder: "+e.getMessage());
        }
    }

}