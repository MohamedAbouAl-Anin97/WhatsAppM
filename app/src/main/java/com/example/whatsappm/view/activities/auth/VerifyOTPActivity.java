package com.example.whatsappm.view.activities.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {

    private EditText inputCode1,inputCode2,inputCode3,inputCode4,inputCode5,inputCode6;
    private Button verifyButton;
    private TextView resendOTPText,mobileNumber;
    private ProgressBar prog;
    private String verificationId;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        initView();
        setupOTPInput();
        //to put the phone number which i received from SendingOTP Activity on the TextView on
        //Verify OTP Activity
        mobileNumber.setText(String.format("+20-%s",getIntent().getStringExtra("mobile")));
        verificationId = getIntent().getStringExtra("verificationId");

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyOTP();
            }
        });

        resendOTPText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendTheCode();
            }
        });
    }

    //to initialize view
    private void initView()
    {
        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);

        resendOTPText = findViewById(R.id.resendOTPText);

        verifyButton = findViewById(R.id.verifyBtn);

        prog = findViewById(R.id.progressBar);

        mobileNumber = findViewById(R.id.mobileNumber);
    }

    //set up input to edit text
    private void setupOTPInput()
    {
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2)
            {
                if(!s.toString().trim().isEmpty())
                {
                    inputCode2.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if(!charSequence.toString().trim().isEmpty())
                {
                    inputCode3.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty())
                {
                    inputCode4.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty())
                {
                    inputCode5.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty())
                {
                    inputCode6.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //verify the otp
    private void verifyOTP()
    {
        if (inputCode1.getText().toString().isEmpty()||

            inputCode2.getText().toString().isEmpty()||
            inputCode3.getText().toString().isEmpty()||
            inputCode4.getText().toString().isEmpty()||
            inputCode5.getText().toString().isEmpty()||
            inputCode6.getText().toString().isEmpty())
        {
            Toast.makeText(this, "please enter valid OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        String code = inputCode1.getText().toString()+
                      inputCode2.getText().toString()+
                      inputCode3.getText().toString()+
                      inputCode4.getText().toString()+
                      inputCode5.getText().toString()+
                      inputCode6.getText().toString();


        if(verificationId !=null)
        {
            prog.setVisibility(View.VISIBLE);
            verifyButton.setVisibility(View.GONE);

            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId,code);
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    prog.setVisibility(View.GONE);
                    verifyButton.setVisibility(View.VISIBLE);

                    if(task.isSuccessful())
                    {
                        FirebaseUser user = task.getResult().getUser();
                        startActivity(new Intent(getApplicationContext(),UserInfoActivity.class));

                      //  if(user != null)
                        //{
                          //  String userId = user.getUid();

                            //Users users = new Users(userId,
                              //      "",
                                //    user.getPhoneNumber(),
                                 //   "",
                                  //  "",
                                   // "",
                                    //"",
                                    //"",
                                    //"",
                                    //"");

                            //firestore.collection("Users").document("UserInfo").collection(userId)
                              //      .add(users).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                               // @Override
                               // public void onSuccess(DocumentReference documentReference)
                                //{
                                  //  Intent intent = new Intent(getApplicationContext(),UserInfoActivity.class);
                                   // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    //startActivity(intent);
                               // }
                            //});

                        //}

                    //}
                    //else
                      //  {
                          //  Toast.makeText(VerifyOTPActivity.this, "the entered verification code was invalid", Toast.LENGTH_SHORT).show();
                        }

                }
            });

        }

    }

    //to resend OTP
    private void resendTheCode()
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+20" + getIntent().getStringExtra("mobile"),
                60,
                TimeUnit.SECONDS,
                VerifyOTPActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(VerifyOTPActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken)
                    {
                        verificationId=newVerificationId;
                        Toast.makeText(VerifyOTPActivity.this, "otp is sent", Toast.LENGTH_SHORT).show();
                    }


                });


    }

}