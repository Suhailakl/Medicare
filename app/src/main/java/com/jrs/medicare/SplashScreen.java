package com.jrs.medicare;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by SUHAIL on 11/3/2017.
 */

public class SplashScreen extends AppCompatActivity {
    private ProgressBar splash;
    private DatabaseReference dataref;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);
        splash = (ProgressBar) findViewById(R.id.splash_screen_progressBar);
        dataref= FirebaseDatabase.getInstance().getReference();
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if (isConnected(SplashScreen.this)) {
                    firebaseAuth= FirebaseAuth.getInstance();
                    dataref= FirebaseDatabase.getInstance().getReference();
                    final FirebaseUser user=firebaseAuth.getCurrentUser();
                    if (user!=null&&!user.getUid().equals("WNlVmp9wNQcKgQWoM7NUU7ok6Ar1")) {
                            if (user.getPhoneNumber().isEmpty()) {
                                if (user.isEmailVerified()) {
                                    dataref.child("Docters").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(user.getUid())){
                                                startActivity(new Intent(SplashScreen.this, DoctorHome.class));
                                                finish();
                                            }
                                            else{
                                                dataref.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.hasChild(user.getUid())){
                                                            startActivity(new Intent(SplashScreen.this, userHome.class));
                                                            finish();
                                                        }
                                                        else {
                                                            startActivity(new Intent(SplashScreen.this,UnVerifiedDoctor.class));
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
                                    startActivity(new Intent(SplashScreen.this, MailVerify.class));
                                    finish();
                                }
                            } else {
                                dataref.child("Docters").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(user.getUid())){
                                            startActivity(new Intent(SplashScreen.this, DoctorHome.class));
                                            finish();
                                        }
                                        else{
                                            dataref.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(user.getUid())){
                                                        startActivity(new Intent(SplashScreen.this, userHome.class));
                                                        finish();
                                                    }
                                                    else {
                                                        dataref.child("Temporary Doctors").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.hasChild(user.getUid())){
                                                                    startActivity(new Intent(SplashScreen.this,UnVerifiedDoctor.class));
                                                                }
                                                                else {
                                                                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
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
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                    }
                    else {
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        finish();
                    }
                }
                else {
                    buildDialog(SplashScreen.this).show();
                }

            }
        },3000);

    }
    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && (wifi.isConnectedOrConnecting())))
                return true;
            else return false;
        } else
            return false;
    }
    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this");
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        return builder;

    }
}
