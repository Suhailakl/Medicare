package com.jrs.medicare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MailVerify extends AppCompatActivity {
    private ImageButton imgbtn;
    private Toast to;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference dataRef;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(MailVerify.this,MainActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_verify);
        getSupportActionBar().setTitle("Check Email");
        imgbtn= (ImageButton) findViewById(R.id.img_btn_verfy);
        to=Toast.makeText(MailVerify.this,"Minimize app and go to your Email",Toast.LENGTH_LONG);
        final Intent imgBtnIntentnext = getPackageManager().getLaunchIntentForPackage("com.android.email");
        firebaseAuth= FirebaseAuth.getInstance();
        dataRef= FirebaseDatabase.getInstance().getReference();
        final FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user.isEmailVerified()){
            finish();
            Toast.makeText(this, "Verified", Toast.LENGTH_SHORT).show();
            dataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("Docters").hasChild(user.getUid())){
                        startActivity(new Intent(MailVerify.this,DoctorHome.class));
                    }
                    else if (dataSnapshot.child("Users").hasChild(user.getUid())){
                        startActivity(new Intent(MailVerify.this,userHome.class));
                    }
                    else {
                        startActivity(new Intent(MailVerify.this,UnVerifiedDoctor.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(this, "Mail Id Not Verified", Toast.LENGTH_SHORT).show();
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(MailVerify.this,SignInActivity.class));
                    Toast.makeText(MailVerify.this, "Please Signin after mail verification", Toast.LENGTH_SHORT).show();
                }

            }, 10000L);
        }
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imgBtnIntent=getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                if(imgBtnIntent!=null) {
                    startActivity(imgBtnIntent);
                }
                    else if (imgBtnIntent==null) {
                    if(imgBtnIntentnext!=null) {
                        startActivity(imgBtnIntentnext);
                    }else{
                        to.show();
                    }
                    }
                }


                }
        );
    }
}
