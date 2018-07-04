package com.jrs.medicare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class userRegisterActivity extends AppCompatActivity {
    private EditText reg_name,reg_age,reg_adrs,reg_email,reg_pwd,reg_cnfmpwd,reg_mob,reg_usr,usrnm;
    private Button reg_click;
    public String name,age,adrs,email,pwd,confmpwd,nmbr,Gender,Male,Female,gen,otpCode,otpNum,usrname;
    private FirebaseAuth mauth;
    private ProgressDialog sProgress;
    private DatabaseReference dataRef,mRef,ref;
    private Toast to,alt;
    private String TAG;
    private RadioButton radioPhone,radioMail;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private RadioGroup radGrp;
    private RadioGroup radGndr;
    private RadioButton rdoMale,rdoFemale;
    private PhoneAuthProvider otpAuth,mCallbacks;
    private EditText otpType;
    ArrayList<String> user=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        //getSupportActionBar().hide();
        getSupportActionBar().setTitle("User Registration");
        //Firebase.setAndroidContext(this);
        radGrp= (RadioGroup) findViewById(R.id.radioGr);
        radioMail= (RadioButton) findViewById(R.id.radio_mail);
        radioPhone= (RadioButton) findViewById(R.id.radio_phone);
        mauth = FirebaseAuth.getInstance();
        dataRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mRef=FirebaseDatabase.getInstance().getReference().child("Usernames");
        ref=FirebaseDatabase.getInstance().getReference();
        sProgress = new ProgressDialog(this);
        sProgress.setCanceledOnTouchOutside(false);
        reg_name = (EditText) findViewById(R.id.name);
        reg_age = (EditText) findViewById(R.id.age);
        reg_adrs = (EditText) findViewById(R.id.Adrs);
        reg_email = (EditText) findViewById(R.id.email);
        reg_pwd = (EditText) findViewById(R.id.pwd);
        reg_cnfmpwd = (EditText) findViewById(R.id.confm_pwd);
        reg_click = (Button) findViewById(R.id.reg);
        reg_mob = (EditText) findViewById(R.id.mob_no);
        radGndr= (RadioGroup) findViewById(R.id.rdio_gndr);
        rdoMale= (RadioButton) findViewById(R.id.rdo_male);
        rdoFemale= (RadioButton) findViewById(R.id.rdo_female);
        usrnm= (EditText) findViewById(R.id.usrnm);
        to=Toast.makeText(this,"Successfully Registered",Toast.LENGTH_SHORT);
        alt=Toast.makeText(this,"User Already Exists",Toast.LENGTH_SHORT);
        radGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId==R.id.radio_mail){
                    reg_cnfmpwd.setVisibility(View.VISIBLE);
                    reg_pwd.setVisibility(View.VISIBLE);
                }
                else {
                    reg_cnfmpwd.setVisibility(View.GONE);
                    reg_pwd.setVisibility(View.GONE);
                }
            }
        });
        editListener();
    }
    public void editListener(){
       usrnm.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               usrname=usrnm.getText().toString().trim();
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean s=false;
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        String d=data.getValue(String.class);
                        if (usrname.equals(d)){
                           s=true;
                        }
                    }
                    if (s){
                        usrnm.setError("Username Unavailable");
                    }
                    else {
                      addListnerOnRadioBtn();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
           }

           @Override
           public void afterTextChanged(Editable s) {
               if (!usrname.isEmpty()){
                   Drawable myIcon = getResources().getDrawable(R.drawable.ic_done);
                   myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                   usrnm.setError("Available", myIcon);
               }
           }
       });
    }
    public void addListnerOnRadioBtn() {
        reg_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                register();
            }
        });
    }
    public void register() {
        initialize();
        if (!validate()) {
            Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
        } else {
            sProgress.setMessage("Signing Up ...");
            sProgress.show();
            int selectedId = radGrp.getCheckedRadioButtonId();
            switch (selectedId) {
                case R.id.radio_mail:
                    onMailSignUpSuccess();
                    break;
                case R.id.radio_phone:
                    ref.child("Id").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(nmbr)){
                                sProgress.dismiss();
                                Toast.makeText(userRegisterActivity.this, "User Already Exists,Please SignIn", Toast.LENGTH_SHORT).show();
                                reg_mob.setError("Mobile Number Already Exists");
                            }
                            else {
                                reqstCode();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    break;
            }
           // int selectedId = radGrp.getCheckedRadioButtonId();

        }
    }
    public void reqstCode(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+nmbr, 60, TimeUnit.SECONDS, userRegisterActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(userRegisterActivity.this, "Verification Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String otpId) {
                super.onCodeAutoRetrievalTimeOut(otpId);
                Toast.makeText(userRegisterActivity.this,"Timeout", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String otpId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(otpId, forceResendingToken);
                String otpCode=otpId;
                Intent intent=new Intent(userRegisterActivity.this,otpVerify.class);
                Bundle b=new Bundle();
                b.putString("name",name);
                b.putString("age",age);
                b.putString("gen",gen);
                b.putString("adrs",adrs);
                b.putString("mobno",nmbr);
                b.putString("mail",email);
                b.putString("code",otpCode);
                b.putString("usrname",usrname);
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onMailSignUpSuccess(){
    mauth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                to.show();
                sendEmailVerification();
                sProgress.dismiss();
                read();
            }
            else
            {
                alt.show();
                sProgress.dismiss();
            }
        }
    });
    }
    public void sendEmailVerification(){
        final FirebaseUser user=mauth.getInstance().getCurrentUser();
        if(user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        startActivity(new Intent(userRegisterActivity.this,MailVerify.class));
                        finish();
                        Toast.makeText(userRegisterActivity.this,"Check Mail for verification",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }
    public void read(){
        String user_id=mauth.getCurrentUser().getUid();
        final DatabaseReference c=mRef.child(user_id);
        c.setValue(usrname);
        final DatabaseReference current_user=dataRef.child(user_id);
        current_user.child("name").setValue(name);
        current_user.child("gender").setValue(gen);
        current_user.child("age").setValue(age);
        current_user.child("address").setValue(adrs);
        current_user.child("mobile number").setValue(nmbr);
        current_user.child("email").setValue(email);
        current_user.child("username").setValue(usrname);

    }
    public boolean validate(){
        boolean valid=true;
        if(name.isEmpty()||name.length()>32){
            reg_name.setError("Please Enter a Valid Name");
            valid=false;
        }
        if (age.isEmpty()||Integer.parseInt(age)>120){
            reg_age.setError("Please Enter a Valid Age");
            valid=false;
        }
        if(adrs.isEmpty()){
            reg_adrs.setError("Please Enter a Valid Address");
            valid=false;
        }
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if ((email.isEmpty())||(!email.matches(emailPattern))){
            reg_email.setError("Please Enter a Valid Email Address");
            valid=false;
        }
        if (reg_pwd.isShown()){
        if(pwd.isEmpty()||pwd.length()<8){
            reg_pwd.setError("Password Must Be 8 Characters");
            valid=false;
        }
            if (!confmpwd.equals(pwd)) {
                reg_cnfmpwd.setError("Password Must Be Same");
                reg_pwd.setError("Password Must Be Same");
                valid = false;
            }
            if ((pwd.isEmpty()) && (confmpwd.isEmpty())) {
                reg_pwd.setError("Password did n't be empty");
                reg_cnfmpwd.setError("Password did n't be empty");
                valid = false;
            }
        }
        if (nmbr.isEmpty()){
            reg_mob.setError("Invalid Mobile Number");
            valid=false;
        }
        usrname=usrnm.getText().toString().trim();
        if(usrname.isEmpty()||usrnm.getError().equals("Username Unavailable")||usrnm.getError().equals("Invalid Username")) {
            Toast.makeText(userRegisterActivity.this, "Please Type A Valid Username", Toast.LENGTH_SHORT).show();
            usrnm.setError("Invalid Username");
            valid=false;
        }
        return valid;
    }
    public void initialize(){
        name=reg_name.getText().toString().trim();
        email=reg_email.getText().toString().trim();
        age=reg_age.getText().toString().trim();
        adrs=reg_adrs.getText().toString().trim();
        pwd=reg_pwd.getText().toString().trim();
        confmpwd=reg_cnfmpwd.getText().toString().trim();
        nmbr=reg_mob.getText().toString().trim();
        int selectedId=radGndr.getCheckedRadioButtonId();
        switch(selectedId){
            case R.id.rdo_male:gen="Male";
                break;
            case R.id.rdo_female:gen="Female";
                break;
        }
    }
}
