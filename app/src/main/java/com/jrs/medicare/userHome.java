package com.jrs.medicare;
/*   <include layout="@layout/content_user_home"
        android:layout_height="298dp"
        android:id="@+id/SearchListView" />*/

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.jrs.medicare.R.id.nav_home;

public class userHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private DatabaseReference dataRef;
    private ArrayList<String> mDoc = new ArrayList<>();
    private ListView mListView;
    private  FirebaseListAdapter<String> firebaseListAdapter;
    private SearchView search;
    private ArrayAdapter<String> adapter;
    private TextView count;
    private ViewPager viewPager;
    private navFragmentPageAdapter navFragmentPageAdapter;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private String uid;
    private AppBarLayout bar;
    private RelativeLayout mLayout;
    private TextView nav_id,nav_name;
    private static final int PICK_IMAGE_REQUEST =234;
    private StorageReference sRef;
    private Uri filePath,downUri;
    private  ImageButton imgBtn;
    private  ProgressBar pro;
    private  CircleImageView img;
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_REQUEST&&resultCode== Activity.RESULT_OK&&data!=null&&data.getData()!=null){
            pro.setVisibility(View.VISIBLE);
            filePath=data.getData();
            StorageReference storageReference = sRef.child("User dp/" + uid + "/" + "dp.jpg");
            storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downUri = taskSnapshot.getDownloadUrl();
                    dataRef.child("Users").child(uid).child("profile pic").setValue(downUri.toString());
                }
            });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        displaySelectedScreen(nav_home);
        mAuth = FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        dataRef=FirebaseDatabase.getInstance().getReference();
        sRef= FirebaseStorage.getInstance().getReference();
          Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
        dataRef.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nav_name.setText(dataSnapshot.child("name").getValue().toString());
                if (mAuth.getCurrentUser().isEmailVerified()){
                    nav_id.setText(dataSnapshot.child("email").getValue().toString());
                }
                else {
                    nav_id.setText(dataSnapshot.child("mobile number").getValue().toString());
                }
                if (!dataSnapshot.child("profile pic").exists()){
                    if (dataSnapshot.child("gender").getValue().toString().trim().equals("Male")){
                        img.setImageResource(R.mipmap.ic_user_male);
                    }
                    else {
                        img.setImageResource(R.mipmap.ic_user_female);
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
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//Status bar color, set to whatever opacity/color you want

            getWindow().setStatusBarColor(Color.TRANSPARENT); }*/
        dataRef= FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_home, menu);
        View v=menu.findItem(R.id.icon_user_notification).getActionView();
        count= (TextView) v.findViewById(R.id.admin_notification_count);
        count.setText("0");
        notification();
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                UserNotificationFragment fr=new UserNotificationFragment();
                ft.replace(R.id.content_user_home,fr);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final android.support.v7.widget.SearchView searchView =
                (android.support.v7.widget.SearchView) menu.findItem(R.id.action_user_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Doctors");
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment fr=new UserSearchFragment();
                ft.replace(R.id.content_user_home, fr);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_logout:
                mAuth.signOut();
                finish();
                startActivity(new Intent(userHome.this,SignInActivity.class));
                break;
            case R.id.my_acnt:
                FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                UserSettings us=new UserSettings();
                ft.replace(R.id.content_user_home,us);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
        }

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
            case nav_home:
                fragment=new FragmentHome();
                break;
            case R.id.nav_bookDtls:
               fragment=new BookingDetails();
                break;
            case R.id.nav_prscptn:
                fragment=new UserPrescriptionView();
                break;
            case R.id.nav_cmplnt:
                fragment=new Complaint();
                break;
            case R.id.nav_tstRslt:
                fragment=new SendTestResult();
                break;
            case R.id.my_acnt:
                fragment=new MyAccount();
                break;

        }
        if (fragment!=null){
              FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
              ft.replace(R.id.content_user_home,fragment);
              ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }
 public void notification(){
     dataRef.child("User Notification").child(uid).addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
             int c=0;
             for (DataSnapshot data:dataSnapshot.getChildren()){
                 if (data.hasChild("referredActive")){
                     c=c+1;
                 }
             }
             for (DataSnapshot data:dataSnapshot.getChildren()){
                 if (data.hasChild("removedActive")){
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
