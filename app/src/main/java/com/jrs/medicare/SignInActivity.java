package com.jrs.medicare;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


public class SignInActivity extends AppCompatActivity  {
    private EditText mail,passwd;
    private Button sign,signByNum,rst;
    private String email,pwd;
    private ProgressDialog progress;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference dataRef;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignInActivity.this,MainActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        dataRef= FirebaseDatabase.getInstance().getReference();
        progress=new ProgressDialog(this);
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage("Signing In...");
        mail= (EditText) findViewById(R.id.sign_email);
        passwd= (EditText) findViewById(R.id.sign_password);
        sign= (Button) findViewById(R.id.email_sign_in_button);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        rst= (Button) findViewById(R.id.rst_btn);
        rst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this,ResetPassword.class));
            }
        });

        signByNum= (Button) findViewById(R.id.sign_num);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
                InputMethodManager inputManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

     signByNum.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             startActivity(new Intent(SignInActivity.this,SignInWithNumber.class));
         }
     });

    }

    public void signin(){
        initialize();
        if(!validate()) {
            Toast.makeText(this, "Signin Failed", Toast.LENGTH_SHORT).show();
        }
        else {
             progress.show();
            mAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(SignInActivity.this, "Signin Problem", Toast.LENGTH_LONG).show();
                        progress.dismiss();
                    }
                    else{
                        progress.dismiss();
                        user=mAuth.getCurrentUser();
                        if (user!=null){
                            if (user.getUid().equals("WNlVmp9wNQcKgQWoM7NUU7ok6Ar1")) {
                                startActivity(new Intent(SignInActivity.this,MainActivity.class));
                                finish();
                            }else {
                                if (user.isEmailVerified()){
                                    dataRef.child("Docters").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(user.getUid())){
                                                    startActivity(new Intent(SignInActivity.this, DoctorHome.class));
                                                    finish();
                                                }
                                                else{
                                                    dataRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.hasChild(user.getUid())){
                                                                startActivity(new Intent(SignInActivity.this, userHome.class));
                                                                finish();
                                                            }
                                                            else {
                                                                startActivity(new Intent(SignInActivity.this,UnVerifiedDoctor.class));
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    //startAlarmService();
                                }else {
                                    finish();
                                    startActivity(new Intent(SignInActivity.this,MailVerify.class));
                                }
                            }
                        }
                    }
                }
            });
        }
    }
    public boolean validate(){
        Boolean valid=true;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if((email.isEmpty())||(!email.matches(emailPattern))){
            mail.setError("invalid Maid Id");
            valid=false;
        }
        if(pwd.isEmpty()||pwd.length()<8){
            passwd.setError("Invalid Password");
            valid=false;
        }
        return valid;
    }
    public void initialize(){
        email=mail.getText().toString().trim();
        pwd=passwd.getText().toString().trim();
    }
    public void startAlarmService(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 10);

        Intent intent = new Intent(this, TestService.class);

        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),60*60*60*1000, pintent);
    }
}

