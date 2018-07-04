package com.jrs.medicare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignInWithNumber extends AppCompatActivity {
    public EditText editText;
    public Button btn;
    public String num;
    public ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_with_number);
        getSupportActionBar().setTitle("Verify OTP");
        editText= (EditText) findViewById(R.id.editText);
        btn= (Button) findViewById(R.id.signin_with_no_bt);
        progress=new ProgressDialog(this);
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage("Signing In...");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                progress.show();
                reqstcode();
            }
        });
    }
    public void reqstcode(){
        num=editText.getText().toString().trim();
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+num ,60, TimeUnit.SECONDS, SignInWithNumber.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(SignInWithNumber.this, "Verification Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String otpId) {
                super.onCodeAutoRetrievalTimeOut(otpId);
                Toast.makeText(SignInWithNumber.this,"Timeout", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String otpId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(otpId, forceResendingToken);
                progress.dismiss();
                String otpcode=otpId;
                Intent i=new Intent(SignInWithNumber.this,otpVerify.class);
                Bundle b=new Bundle();
                b.putString("numCode",otpcode);
                i.putExtras(b);
                startActivity(i);
            }
            });
    }
}
