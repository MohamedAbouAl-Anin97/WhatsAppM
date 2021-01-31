package com.example.whatsappm.view.activities.auth;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.whatsappm.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
public class SendigOTP extends AppCompatActivity {

    //widgets
    private EditText phoneNumberEditText;
    private Button nextButton;
    private ProgressBar pro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendig_o_t_p);

        initView();
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendingOTP();
            }
        });
    }

    //to initialize view
    private void initView()
    {
        phoneNumberEditText = findViewById(R.id.editTxt_phone_number);
        nextButton = findViewById(R.id.btn_next);
        pro = findViewById(R.id.progressBar);
    }
    //sending OTP function
    private void sendingOTP()
    {
        if(phoneNumberEditText.getText().toString().isEmpty())
        {
            Toast.makeText(this, "please enter your phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        pro.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.GONE);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+20" + phoneNumberEditText.getText().toString(),
                60,
                TimeUnit.SECONDS,
                SendigOTP.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
                    {
                        pro.setVisibility(View.GONE);
                        nextButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e)
                    {
                        pro.setVisibility(View.GONE);
                        nextButton.setVisibility(View.VISIBLE);
                        Toast.makeText(SendigOTP.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token)
                    {
                        pro.setVisibility(View.GONE);
                        nextButton.setVisibility(View.VISIBLE);

                        Intent intent =new Intent(getApplicationContext(),VerifyOTPActivity.class);
                        intent.putExtra("mobile",phoneNumberEditText.getText().toString().trim());
                        intent.putExtra("verificationId",verificationId);
                        startActivity(intent);

                    }
                }
        );

    }
}