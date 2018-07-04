package com.jrs.medicare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class UnVerifiedDoctor extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference dataRef;
    private String uid;
    private Calendar c;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_home,menu);
        MenuItem searchItem=menu.findItem(R.id.action_admin_search);
        MenuItem notItem=menu.findItem(R.id.action_admin_notification);
        searchItem.setVisible(false);
        notItem.setVisible(false);
        View v=menu.findItem(R.id.action_admin_signout).getActionView();
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(UnVerifiedDoctor.this,SignInActivity.class));
                finish();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_verified_doctor);
        mAuth=FirebaseAuth.getInstance();
        c=Calendar.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        dataRef= FirebaseDatabase.getInstance().getReference().child("Temporary Doctors");
        //dataRef.child(uid).child("mailActive").getRef().removeValue();
        //dataRef.child(uid).child("timeStamp").setValue(c.getTimeInMillis());
    }
}
