package com.jrs.medicare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference dataRef;
    private StorageReference sRef;
    private String uid;
    private FirebaseAuth mAuth;
    private TextView count,nav_name,nav_id;
    private RelativeLayout mLayout;
    private ImageButton imgBtn;
    private CircleImageView img;
    private static final int PICK_IMAGE_REQUEST =234 ;
    private Uri filePath,downUri;
    private Context c;
    private ProgressBar pro;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_REQUEST&&resultCode== Activity.RESULT_OK&&data!=null&&data.getData()!=null){
            pro.setVisibility(View.VISIBLE);
            filePath=data.getData();
            StorageReference storageReference = sRef.child("Doctor dp/" + uid + "/" + "dp.jpg");
            storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downUri = taskSnapshot.getDownloadUrl();
                    dataRef.child("Docters").child(uid).child("profile pic").setValue(downUri.toString());
                }
            });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);
        mAuth=FirebaseAuth.getInstance();
        dataRef= FirebaseDatabase.getInstance().getReference();
        sRef= FirebaseStorage.getInstance().getReference();
        uid=mAuth.getCurrentUser().getUid();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setScrimColor(getResources().getColor(android.R.color.transparent));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mLayout= (RelativeLayout) navigationView.getHeaderView(0);
        nav_id= (TextView) mLayout.findViewById(R.id.nav_doc_name);
        nav_name= (TextView) mLayout.findViewById(R.id.nav_doc_id);
        imgBtn= (ImageButton) mLayout.findViewById(R.id.nav_doc_edit_image);
        pro= (ProgressBar) mLayout.findViewById(R.id.nav_doc_pro);
        pro.setVisibility(View.GONE);
        img= (CircleImageView) mLayout.findViewById(R.id.nav_doc_profile_image);
        dataRef.child("Docters").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nav_name.setText("Dr.");
                nav_name.append(dataSnapshot.child("name").getValue().toString());
                if (mAuth.getCurrentUser().isEmailVerified()){
                    nav_id.setText(dataSnapshot.child("email").getValue().toString());
                }
                else {
                    nav_id.setText(dataSnapshot.child("mobile number").getValue().toString());
                }
                if (!dataSnapshot.child("profile pic").exists()){
                    if (dataSnapshot.child("gender").getValue().toString().trim().equals("Male")){
                        img.setImageResource(R.mipmap.ic_male_doc);
                    }
                    else {
                        img.setImageResource(R.mipmap.ic_female_doc);
                    }
                }
                else {
                    String pic=dataSnapshot.child("profile pic").getValue().toString();
                    Glide.with(getBaseContext()).load(pic).into(img);
                    pro.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showFileChooser();
            }
        });
        displaySelectedScreen(R.id.nav_doc_token);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;

                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        getMenuInflater().inflate(R.menu.doctor_home, menu);
        View v=menu.findItem(R.id.icon_doc_notification).getActionView();
        count= (TextView) v.findViewById(R.id.admin_notification_count);
        count.setText("0");
        notification();
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                DoctorNotificationFragment f=new DoctorNotificationFragment();
                ft.replace(R.id.content_doc_home,f);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_myAcount) {
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            DoctorSettings ds=new DoctorSettings();
            ft.replace(R.id.content_doc_home,ds);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

            return true;
        }
        else if (id==R.id.action_doc_logout){
            mAuth.signOut();
            startActivity(new Intent(DoctorHome.this,SignInActivity.class));
            return true;
        }
            displaySelectedScreen(id);
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedScreen(id);
        return true;
    }
    public void displaySelectedScreen(int id){
        Fragment fragment=null;
        switch (id){
            case R.id.nav_doc_token:
                fragment=new StartConsult();
                break;
            case R.id.nav_doc_record:
                fragment=new MedicalRecord();
                break;
            case R.id.nav_doc_rfrl:
                FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                MedicalRecord mR=new MedicalRecord();
                Bundle b=new Bundle();
                b.putString("refPat","yes");
                mR.setArguments(b);
                ft.replace(R.id.content_doc_home,mR);
                ft.commit();
                break;
            case R.id.nav_doc_testReslt:
                fragment=new TestResults();
                break;
            case R.id.doc_bkPt:
                fragment=new DoctorBookedPatients();
                break;
        }
        if (fragment!=null){
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_doc_home,fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
    public void notification(){
        dataRef.child("Doctor Notification").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int c=0;
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    if (data.hasChild("tokFullActive")){
                        c=c+1;
                    }
                }
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    if (data.hasChild("referredActive")){
                        c=c+1;
                    }
                }
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    if (data.hasChild("tActive")){
                        c=c+1;
                    }
                }
                count.setText(String.valueOf(c));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void showFileChooser(){
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select An Image"),PICK_IMAGE_REQUEST);
    }
}
