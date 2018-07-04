package com.jrs.medicare;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by SUHAIL on 9/24/2017.
 */

public class TestService extends Service {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Session Expired", Toast.LENGTH_SHORT).show();
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        mAuth.signOut();
        Intent i=new Intent(getApplicationContext(),SignInActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        stopService(intent);
        return super.onStartCommand(intent, flags, startId);
    }


}
