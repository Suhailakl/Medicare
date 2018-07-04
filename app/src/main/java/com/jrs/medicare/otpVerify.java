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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class otpVerify extends AppCompatActivity {
    private FirebaseAuth mauth;
    private String otpNum;
    private EditText otpType;
    private DatabaseReference dataRef,docDataRef,usrRef,ref;
    private String nmbr;
    private SmsVerifyCatcher smsVerifyCatcher;
    private ProgressDialog progress;
    private Calendar c;
    private Button otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);
        dataRef = FirebaseDatabase.getInstance().getReference().child("Users");
        docDataRef=FirebaseDatabase.getInstance().getReference().child("Temporary Doctors");
        usrRef=FirebaseDatabase.getInstance().getReference().child("Usernames");
        ref=FirebaseDatabase.getInstance().getReference();
        getSupportActionBar().setTitle("Verify Account");
        otp = (Button) findViewById(R.id.verifyOtp);
        otpType = (EditText) findViewById(R.id.otpType);
        mauth = FirebaseAuth.getInstance();
        progress=new ProgressDialog(this);
        progress.setMessage("Verifying...");
        progress.setCanceledOnTouchOutside(false);
        c=Calendar.getInstance();
        SmsVerifyCatcher();
        otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                listnerOtp();
            }
        });

    }
    public void SmsVerifyCatcher(){
      smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                otpType.setText(code);
                //set code in edit text
                //then you can send verification code to server
                smsVerifyCatcher.setPhoneNumberFilter("56161174");
                otp.performClick();
            }
        });
    }
    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\d{6}");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }
    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void listnerOtp(){
                progress.show();
                otpNum= otpType.getText().toString().trim();
                Bundle bundle = getIntent().getExtras();
                String s = bundle.getString("code");
                String n=bundle.getString("numCode");
                if (s==null){
                    signInWithCredential(PhoneAuthProvider.getCredential(n,otpNum));
                }
                else {
                    signInWithCredential(PhoneAuthProvider.getCredential(s, otpNum));
                }
    }
    public void signInWithCredential(PhoneAuthCredential phoneAuthCredential){
        mauth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progress.dismiss();
                if (task.isSuccessful()) {
                    final Bundle uBundle=getIntent().getExtras();
                    final Bundle dBundle=getIntent().getExtras();
                    final String user_id=mauth.getCurrentUser().getUid();
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("Users").hasChild(user_id)||dataSnapshot.child("Docters").hasChild(user_id)){
                                Toast.makeText(otpVerify.this, "Sign In Successfully", Toast.LENGTH_SHORT).show();
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child("Docters").hasChild(user_id)){
                                            Intent i=new Intent(otpVerify.this,DoctorHome.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            finish();
                                        }
                                        else  if (dataSnapshot.child("Users").hasChild(user_id)){
                                            Intent i=new Intent(otpVerify.this,userHome.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                //startAlarmService();

                            }
                            else if (uBundle.getString("mail")==null&&dBundle.getString("docmail")==null) {
                                ref.child("Temporary Doctors").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(user_id)){
                                            Intent i=new Intent(otpVerify.this,UnVerifiedDoctor.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(otpVerify.this, "User did n't exists", Toast.LENGTH_SHORT).show();
                                            Intent i=new Intent(otpVerify.this,SignInWithNumber.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                }
                                else{
                                    readData();
                                    Toast.makeText(otpVerify.this, "Sign In Successfully", Toast.LENGTH_SHORT).show();
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child("Docters").hasChild(user_id)){
                                            Intent i=new Intent(otpVerify.this,DoctorHome.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            finish();
                                        }
                                        else  if (dataSnapshot.child("Users").hasChild(user_id)){
                                            Intent i=new Intent(otpVerify.this,userHome.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            finish();
                                        }
                                        else {
                                            Intent i=new Intent(otpVerify.this,UnVerifiedDoctor.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                            finish();
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
                } else {
                    Toast.makeText(otpVerify.this, "Failed To SignIn With Credential"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void readData(){
        final String user_id=mauth.getCurrentUser().getUid();
        Bundle bundle=getIntent().getExtras();
        final DatabaseReference usr=usrRef.child(user_id);
        final DatabaseReference cur_user=dataRef.child(user_id);
        final DatabaseReference doc_cur_user=docDataRef.child(user_id);
        usr.setValue(bundle.getString("usrname"));
        cur_user.child("username").setValue(bundle.getString("usrname"));
        cur_user.child("name").setValue(bundle.getString("name"));
        cur_user.child("gender").setValue(bundle.getString("gen"));
        cur_user.child("age").setValue(bundle.getString("age"));
        cur_user.child("address").setValue(bundle.getString("adrs"));
        cur_user.child("email").setValue(bundle.getString("mail"));
        cur_user.child("mobile number").setValue(bundle.getString("mobno"));
        String no=bundle.getString("mobno");
        if (no!=null){
            ref.child("Id").child(no).setValue(user_id);
        }
        Bundle docBundle=getIntent().getExtras();
        doc_cur_user.child("name").setValue(docBundle.getString("docname"));
        if (docBundle.getString("docname")!=null){
            doc_cur_user.child("uid").setValue(user_id);
            doc_cur_user.child("timeStamp").setValue(c.getTimeInMillis());
        }
        doc_cur_user.child("gender").setValue(docBundle.getString("docgen"));
        doc_cur_user.child("address").setValue(docBundle.getString("docadrs"));
        doc_cur_user.child("email").setValue(docBundle.getString("docmail"));
        doc_cur_user.child("education qualification").setValue(docBundle.getString("docqua"));
        doc_cur_user.child("registration number").setValue(docBundle.getString("docReg"));
        doc_cur_user.child("department").setValue(docBundle.getString("docDpmt"));
        doc_cur_user.child("working hospital").setValue(docBundle.getString("docHsptl"));
        doc_cur_user.child("city").setValue(docBundle.getString("docCity"));
        doc_cur_user.child("total tokens").setValue(docBundle.getString("docTok"));
        doc_cur_user.child("consultation starts at").setValue(docBundle.getString("docCnsltTimeStart"));
        doc_cur_user.child("consultation ends at").setValue(docBundle.getString("docCnsltTimeEnd"));
        doc_cur_user.child("mobile number").setValue(docBundle.getString("docmobno"));
        String nu=bundle.getString("docmobno");
        if (nu!=null){
            ref.child("Id").child(nu).setValue(user_id);
        }
        doc_cur_user.child("day1").setValue(docBundle.getString("docSun"));
        doc_cur_user.child("day2").setValue(docBundle.getString("docMon"));
        doc_cur_user.child("day3").setValue(docBundle.getString("docTue"));
        doc_cur_user.child("day4").setValue(docBundle.getString("docWed"));
        doc_cur_user.child("day5").setValue(docBundle.getString("docThi"));
        doc_cur_user.child("day6").setValue(docBundle.getString("docFri"));
        doc_cur_user.child("day7").setValue(docBundle.getString("docSat"));

    }
    public void startAlarmService(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 10);

        Intent intent = new Intent(this, TestService.class);

        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),24*60*60*1000, pintent);
    }
}
