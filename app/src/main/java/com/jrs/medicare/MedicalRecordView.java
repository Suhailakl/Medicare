package com.jrs.medicare;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.Calendar;

/**
 * Created by SUHAIL on 10/29/2017.
 */

public class MedicalRecordView extends Fragment {
    private TextView date,ptName,drName,drCity;
    private TextView doc2,date2,ptName2;
    private TextView doc3,date3,ptName3,rfrdHistory;
    private TextView rfrdHstry,count;
    private DatabaseReference dataRef;
    private FirebaseAuth mAuth;
    private String ui,key,back,backRef,tk,backTest,docView,csView,sid;
    private Button updt,refOk,refView;
    private RelativeLayout l1,l2,l3;
    private ImageView l1_i1,l1_i2,l1_i3,l1_i4,l1_i5,l1_i6,l1_i7,l1_i8,l1_i9,l1_i10;
    private TextView l1_s1,l1_s2,l1_s3,l1_s4,l1_s5,l1_s6,l1_s7,l1_s8,l1_s9,l1_s10;
    private RelativeLayout layout;
    private EditText prscrptn,abt,prscrptn2,abt2,prscrptn3,abt3;
    private Query mQuery;
    private ListView list;
    private PopupWindow popupWindow,popUpSuccessWindow;
    private ProgressBar refPro;
    private RelativeLayout scrollView,btnLayout;
    private Toast msg;
    private KeyboardVisibilityEvent kEvent;
    private ScrollView scroll;
    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.getSupportActionBar().show();
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.doctor_home, menu);
        View v=menu.findItem(R.id.icon_doc_notification).getActionView();
        count= (TextView) v.findViewById(R.id.admin_notification_count);
        count.setText("0");
        notification();
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft=getFragmentManager().beginTransaction();
                DoctorNotificationFragment f=new DoctorNotificationFragment();
                ft.replace(R.id.content_doc_home,f);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });
        inflater.inflate(R.menu.search,menu);
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final SearchView search= (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        search.setQueryHint("Search for Docter");
        search.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                DoctorSearchFragment fr=new DoctorSearchFragment();
                Bundle b=new Bundle();
                b.putString("mr",key);
                fr.setArguments(b);
                ft.replace(R.id.content_doc_home, fr);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container!=null){
            container.removeAllViews();
        }
         final View v=inflater.inflate(R.layout.activity_medical_record_view,container,false);
        getActivity().setTitle(R.string.record);
        layout = (RelativeLayout) v.findViewById(R.id.mR_scroll);
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popup = inflater.inflate(R.layout.popup_ref, null);
        View popupSuccess=inflater.inflate(R.layout.popup_ref_doc_details,null);
        drName= (TextView) popup.findViewById(R.id.ref_doc_h_name);
        drCity= (TextView) popup.findViewById(R.id.ref_doc_h_cityo);
        refOk= (Button) popup.findViewById(R.id.ref_doc_ok);
        refView= (Button) popup.findViewById(R.id.ref_doc_view);
        refPro= (ProgressBar) popup.findViewById(R.id.ref_doc_h_progress);
        refPro.setVisibility(View.GONE);
        boolean focusable = true;
        popupWindow = new PopupWindow(popup, 650, 450, focusable);
        popUpSuccessWindow=new PopupWindow(popupSuccess,650,150,focusable);
        btnLayout= (RelativeLayout) v.findViewById(R.id.mR_btn_layout);
        popupSuccess.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popUpSuccessWindow.dismiss();
                if (back==null){
                    MedicalRecord mr=new MedicalRecord();
                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_doc_home,mr);
                    ft.commit();
                    return true;
                }
                else {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    StartConsult sC=new StartConsult();
                    Bundle b=new Bundle();
                    b.putString("active","no");
                    b.putString("tk",tk);
                    sC.setArguments(b);
                    ft.replace(R.id.content_doc_home,sC);
                    ft.addToBackStack(null);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }

                return false;
            }
        });
        popup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
        setHasOptionsMenu(true);
        Bundle b=this.getArguments();
        if (getArguments()!=null){
            key=b.getString("key");
            back=b.getString("back");
            backRef=b.getString("backRef");
            tk=b.getString("tk");
            backTest=b.getString("backTest");
            csView=b.getString("csView");
            sid=b.getString("uid");
        }
        setHasOptionsMenu(true);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (back=="yes") {
                            StartConsult sC=new StartConsult();
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.admin_complaint_view,sC);
                            ft.commit();
                            return true;

                        }
                        else if (backTest=="yes"){
                            TestResults tr=new TestResults();
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.test_results,tr);
                            ft.commit();
                            return true;
                        }
                        else if (csView=="yes") {
                            DocConsultationStartedView ds = new DocConsultationStartedView();
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.mR_scroll, ds);
                            ft.commit();
                            return true;


                        }else {
                            MedicalRecord mr = new MedicalRecord();
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.activity_medical_record, mr);
                            ft.commit();
                            return true;
                        }

                    }
                }
                return false;
            }
        });
        date= (TextView) v.findViewById(R.id.mR_date);
        ptName= (TextView) v.findViewById(R.id.mR_patient);
        prscrptn= (EditText) v.findViewById(R.id.mR_prscptn);
        abt= (EditText) v.findViewById(R.id.mR_about);
        updt= (Button) v.findViewById(R.id.mR_btn_updt);
        rfrdHstry= (TextView) v.findViewById(R.id.textView42);
        doc2= (TextView) v.findViewById(R.id.mR_doc1);
        date2= (TextView) v.findViewById(R.id.mR_ref_date);
        ptName2= (TextView) v.findViewById(R.id.mR_ref_patient);
        prscrptn2= (EditText) v.findViewById(R.id.mR_ref_prscptn);
        abt2= (EditText) v.findViewById(R.id.mR_ref_about);
        l1=(RelativeLayout) v.findViewById(R.id.mR_layout1);
        l2=(RelativeLayout) v.findViewById(R.id.mR_layout2);
        l3=(RelativeLayout) v.findViewById(R.id.mR_layout3);
        doc3= (TextView) v.findViewById(R.id.mR_doc2);
        date3= (TextView) v.findViewById(R.id.mR_ref_date2);
        ptName3= (TextView) v.findViewById(R.id.mR_ref_patient2);
        prscrptn3= (EditText) v.findViewById(R.id.mR_ref_prscptn2);
        abt3= (EditText) v.findViewById(R.id.mR_ref_about2);
        mAuth=FirebaseAuth.getInstance();
        ui=mAuth.getCurrentUser().getUid();
        list= (ListView) v.findViewById(R.id.mR_listView);
        scrollView= (RelativeLayout) v.findViewById(R.id.mR_scroll);
        dataRef= FirebaseDatabase.getInstance().getReference();
        l1_i1= (ImageView) v.findViewById(R.id.mR_image1);
        l1_i2= (ImageView) v.findViewById(R.id.mR_image2);
        l1_i3= (ImageView) v.findViewById(R.id.mR_image3);
        l1_i4= (ImageView) v.findViewById(R.id.mR_image4);
        l1_i5= (ImageView) v.findViewById(R.id.mR_image5);
        l1_i6= (ImageView) v.findViewById(R.id.mR_image6);
        l1_i7= (ImageView) v.findViewById(R.id.mR_image7);
        l1_i8= (ImageView) v.findViewById(R.id.mR_image8);
        l1_i9= (ImageView) v.findViewById(R.id.mR_image9);
        l1_i10= (ImageView) v.findViewById(R.id.mR_image10);
        l1_s1= (TextView) v.findViewById(R.id.mR_l1_i1_s1);
        l1_s2= (TextView) v.findViewById(R.id.mR_l1_i1_s2);
        l1_s3= (TextView) v.findViewById(R.id.mR_l1_i1_s3);
        l1_s4= (TextView) v.findViewById(R.id.mR_l1_i1_s4);
        l1_s5= (TextView) v.findViewById(R.id.mR_l1_i1_s5);
        l1_s6= (TextView) v.findViewById(R.id.mR_l1_i1_s6);
        l1_s7= (TextView) v.findViewById(R.id.mR_l1_i1_s7);
        l1_s8= (TextView) v.findViewById(R.id.mR_l1_i1_s8);
        l1_s9= (TextView) v.findViewById(R.id.mR_l1_i1_s9);
        l1_s10= (TextView) v.findViewById(R.id.mR_l1_i1_s10);
        l2.setVisibility(View.GONE);
        l3.setVisibility(View.GONE);
        rfrdHstry.setVisibility(View.GONE);
        scroll= (ScrollView) v.findViewById(R.id.mR_scroll_view);
        kEvent.setEventListener(getActivity(), new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen){
                    btnLayout.setVisibility(View.GONE);
                    scroll.setPadding(0,0,0,0);
                }
                else {
                    btnLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        if (sid!=null) {
            popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
            dataRef.child("Docters").child(sid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    final String name=dataSnapshot.child("name").getValue().toString();
                    String city=dataSnapshot.child("city").getValue().toString();
                    drName.setText(name);
                    drCity.setText(city);
                    refView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            RefDocDetails rf = new RefDocDetails();
                            Bundle b = new Bundle();
                            b.putString("uid", sid);
                            b.putString("mRkey", key);
                            b.putString("csView", csView);
                            b.putString("dName", name);
                            rf.setArguments(b);
                            ft.replace(R.id.mR_scroll, rf);
                            ft.addToBackStack(null);
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                            ft.commit();
                        }
                    });
                    refOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refPro.setVisibility(View.VISIBLE);
                            final Calendar c = Calendar.getInstance();
                            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            in.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            dataRef.child("Medical Record").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final String uid = sid;
                                    DatabaseReference mRef = dataRef.child("Medical Record").push().getRef();
                                    mRef.child("doctor").setValue(name);
                                    dataSnapshot.child("dId").getRef().setValue(dataSnapshot.child("doctorId").getValue().toString());
                                    mRef.child("patient").setValue(dataSnapshot.child("patient").getValue().toString());
                                    final String userId = dataSnapshot.child("userId").getValue().toString();
                                    mRef.child("docKey").setValue(uid + userId);
                                    mRef.child("doctorId").setValue(uid);
                                    mRef.child("date").setValue(dataSnapshot.child("date").getValue().toString());
                                    mRef.child("referred from").setValue(key);
                                    mRef.child("spinnerKey").setValue(name + dataSnapshot.child("uid").getValue().toString());
                                    mRef.child("userId").setValue(userId);
                                    mRef.child("uid").setValue(dataSnapshot.child("uid").getValue().toString());
                                    mRef.child("refKey").setValue(uid + "yes");
                                    DatabaseReference docN = dataRef.child("Doctor Notification").child(uid).push().getRef();
                                    docN.child("referredActive").setValue("yes");
                                    docN.child("uid").setValue(dataSnapshot.child("uid").getValue().toString());
                                    docN.child("from").setValue(ui);
                                    docN.child("time").setValue(c.getTimeInMillis());
                                    DatabaseReference u = dataRef.child("User Notification").child(dataSnapshot.child("uid").getValue().toString()).push().getRef();
                                    u.child("referredActive").setValue("yes");
                                    u.child("time").setValue(c.getTimeInMillis());
                                    u.child("doctorId").setValue(uid);
                                    u.child("from").setValue(ui);
                                    dataRef.child("Test Results").orderByChild("mRkey").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                Calendar c = Calendar.getInstance();
                                                long timeinMi = c.getTimeInMillis();
                                                String time = String.valueOf(timeinMi);
                                                dataRef.child("Test Results").child(data.getKey()).child("key").setValue(uid + time);
                                                dataRef.child("Test Results").child(data.getKey()).child("time").setValue(timeinMi);
                                                dataRef.child("Test Results").child(data.getKey()).child("dateKey").setValue(uid + data.child("date").getValue().toString());
                                                dataRef.child("Test Results").child(data.getKey()).child("docKey").setValue(uid + userId);
                                                dataRef.child("Test Results").child(data.getKey()).child("referred").setValue("yes");
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    if (dataSnapshot.hasChild("refKey")) {
                                        dataSnapshot.child("refKey").getRef().removeValue();
                                    }
                                    dataSnapshot.child("spinnerKey").getRef().removeValue();
                                    dataSnapshot.child("doctorId").getRef().removeValue();
                                    dataSnapshot.child("docKey").getRef().removeValue();
                                    Toast.makeText(getActivity(), "Patient Referred Successfully", Toast.LENGTH_SHORT).show();
                                    refPro.setVisibility(View.GONE);
                                    popupWindow.dismiss();
                                    popUpSuccessWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataRef.child("Medical Record").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("prescription")) {
                    prscrptn.setText(dataSnapshot.child("prescription").getValue().toString());
                }
                if (dataSnapshot.hasChild("about")){
                    abt.setText(dataSnapshot.child("about").getValue().toString());
                }
                        ptName.setText(dataSnapshot.child("patient").getValue().toString());
                        date.setText(dataSnapshot.child("date").getValue().toString());
                if (dataSnapshot.hasChild("referred from")) {
                    rfrdHstry.setVisibility(View.VISIBLE);
                    l2.setVisibility(View.VISIBLE);
                    String k = dataSnapshot.child("referred from").getValue().toString();
                    image1(k);
                    dataRef.child("Medical Record").child(k).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("prescription")) {
                                if (dataSnapshot.hasChild("prescription")) {
                                    prscrptn2.setText(dataSnapshot.child("prescription").getValue().toString());
                                    prscrptn2.setEnabled(false);
                                }
                                if (dataSnapshot.hasChild("about")){
                                    abt2.setText(dataSnapshot.child("about").getValue().toString());
                                    abt2.setEnabled(false);
                                }
                                ptName2.setText(dataSnapshot.child("patient").getValue().toString());
                                date2.setText(dataSnapshot.child("date").getValue().toString());
                                doc2.setText(dataSnapshot.child("doctor").getValue().toString());
                            if (dataSnapshot.hasChild("referred from")) {
                                l3.setVisibility(View.VISIBLE);
                                String k = dataSnapshot.child("referred from").getValue().toString();
                                dataRef.child("Medical Record").child(k).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("prescription")) {
                                            if (dataSnapshot.hasChild("prescription")) {
                                                prscrptn3.setText(dataSnapshot.child("prescription").getValue().toString());
                                                prscrptn3.setEnabled(false);
                                            }
                                            if (dataSnapshot.hasChild("about")){
                                                abt3.setText(dataSnapshot.child("about").getValue().toString());
                                                abt3.setEnabled(false);
                                            }

                                            ptName3.setText(dataSnapshot.child("patient").getValue().toString());
                                            date3.setText(dataSnapshot.child("date").getValue().toString());

                                            doc3.setText(dataSnapshot.child("doctor").getValue().toString());
                                        }
                                        else {
                                            rfrdHstry.setVisibility(View.GONE);
                                            l2.setVisibility(View.GONE);
                                            scrollView.setOnTouchListener(new View.OnTouchListener() {
                                                @Override
                                                public boolean onTouch(View v, MotionEvent event) {
                                                    return true;
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
                                rfrdHstry.setVisibility(View.GONE);
                                l2.setVisibility(View.GONE);
                                l3.setVisibility(View.GONE);
                                scrollView.setOnTouchListener( new View.OnTouchListener(){
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        return true;
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
        image();
        updt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataRef.child("Medical Record").child(key).child("about").setValue(abt.getText().toString().trim());
                dataRef.child("Medical Record").child(key).child("prescription").setValue(prscrptn.getText().toString().trim());
                dataRef.child("Medical Record").child(key).child("updateActive").setValue("active");
                Toast.makeText(getActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        });


    }
    public  void image(){
        dataRef.child("Medical Record").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("Test Results").hasChild("image1")){
                        l1_i1.setVisibility(View.VISIBLE);
                        l1_s1.setVisibility(View.VISIBLE);
                        final String u=dataSnapshot.child("Test Results").child("image1").child("img1").getValue().toString();
                        final String s=dataSnapshot.child("Test Results").child("image1").child("sbj1").getValue().toString();
                        Glide.with(getActivity()).load(u).into(l1_i1);
                        l1_s1.setText(s);
                        l1_i1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (backTest.equals("yes")) {
                                    imageSelector2(u, s);
                                }else if (back.equals("yes")){
                                    imageSelector3(u, s);
                                }
                                else {
                                    imageSelector(u,s);
                                }
                            }
                        });
                    }
                    if (dataSnapshot.child("Test Results").hasChild("image2")){
                        l1_i2.setVisibility(View.VISIBLE);
                        l1_s2.setVisibility(View.VISIBLE);
                        final String u=dataSnapshot.child("Test Results").child("image2").child("img2").getValue().toString();
                        final String s=dataSnapshot.child("Test Results").child("image2").child("sbj2").getValue().toString();
                        Glide.with(getActivity()).load(u).into(l1_i2);
                        l1_s2.setText(s);
                        l1_i2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (backTest.equals("yes")) {
                                    imageSelector2(u, s);
                                }else if (back.equals("yes")){
                                    imageSelector3(u, s);
                                }
                                else {
                                    imageSelector(u,s);
                                }
                            }
                        });
                    }
                    if (dataSnapshot.child("Test Results").hasChild("image3")){
                        l1_i3.setVisibility(View.VISIBLE);
                        l1_s3.setVisibility(View.VISIBLE);
                        final String u=dataSnapshot.child("Test Results").child("image3").child("img3").getValue().toString();
                        final String s=dataSnapshot.child("Test Results").child("image3").child("sbj3").getValue().toString();
                        Glide.with(getActivity()).load(u).into(l1_i3);
                        l1_s3.setText(s);
                        l1_i3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (backTest.equals("yes")) {
                                    imageSelector2(u, s);
                                }else if (back.equals("yes")){
                                    imageSelector3(u, s);
                                }
                                else {
                                    imageSelector(u,s);
                                }
                            }
                        });
                    }
                if (dataSnapshot.child("Test Results").hasChild("image4")){
                    l1_i4.setVisibility(View.VISIBLE);
                    l1_s4.setVisibility(View.VISIBLE);
                    final String u=dataSnapshot.child("Test Results").child("image4").child("img4").getValue().toString();
                    final String s=dataSnapshot.child("Test Results").child("image4").child("sbj4").getValue().toString();
                    Glide.with(getActivity()).load(u).into(l1_i4);
                    l1_s4.setText(s);
                    l1_i4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (backTest.equals("yes")) {
                                imageSelector2(u, s);
                            }else if (back.equals("yes")){
                                imageSelector3(u, s);
                            }
                            else {
                                imageSelector(u,s);
                            }
                        }
                    });
                }
                if (dataSnapshot.child("Test Results").hasChild("image5")){
                    l1_i5.setVisibility(View.VISIBLE);
                    l1_s5.setVisibility(View.VISIBLE);
                    final String u=dataSnapshot.child("Test Results").child("image5").child("img5").getValue().toString();
                    final String s=dataSnapshot.child("Test Results").child("image5").child("sbj5").getValue().toString();
                    Glide.with(getActivity()).load(u).into(l1_i5);
                    l1_s5.setText(s);
                    l1_i5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (backTest.equals("yes")) {
                                imageSelector2(u, s);
                            }else if (back.equals("yes")){
                                imageSelector3(u, s);
                            }
                            else {
                                imageSelector(u,s);
                            }
                        }
                    });
                }
                if (dataSnapshot.child("Test Results").hasChild("image6")){
                    l1_i6.setVisibility(View.VISIBLE);
                    l1_s6.setVisibility(View.VISIBLE);
                    final String u=dataSnapshot.child("Test Results").child("image6").child("img6").getValue().toString();
                    final String s=dataSnapshot.child("Test Results").child("image6").child("sbj6").getValue().toString();
                    Glide.with(getActivity()).load(u).into(l1_i6);
                    l1_s6.setText(s);
                    l1_i6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (backTest.equals("yes")) {
                                imageSelector2(u, s);
                            }else if (back.equals("yes")){
                                imageSelector3(u, s);
                            }
                            else {
                                imageSelector(u,s);
                            }
                        }
                    });
                }
                if (dataSnapshot.child("Test Results").hasChild("image7")){
                    l1_i7.setVisibility(View.VISIBLE);
                    l1_s7.setVisibility(View.VISIBLE);
                    final String u=dataSnapshot.child("Test Results").child("image7").child("img7").getValue().toString();
                    final String s=dataSnapshot.child("Test Results").child("image7").child("sbj7").getValue().toString();
                    Glide.with(getActivity()).load(u).into(l1_i7);
                    l1_s7.setText(s);
                    l1_i7.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (backTest.equals("yes")) {
                                imageSelector2(u, s);
                            }else if (back.equals("yes")){
                                imageSelector3(u, s);
                            }
                            else {
                                imageSelector(u,s);
                            }
                        }
                    });
                }
                if (dataSnapshot.child("Test Results").hasChild("image8")){
                    l1_i8.setVisibility(View.VISIBLE);
                    l1_s8.setVisibility(View.VISIBLE);
                    final String u=dataSnapshot.child("Test Results").child("image8").child("img8").getValue().toString();
                    final String s=dataSnapshot.child("Test Results").child("image8").child("sbj8").getValue().toString();
                    Glide.with(getActivity()).load(u).into(l1_i8);
                    l1_s8.setText(s);
                    l1_i8.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (backTest.equals("yes")) {
                                imageSelector2(u, s);
                            }else if (back.equals("yes")){
                                imageSelector3(u, s);
                            }
                            else {
                                imageSelector(u,s);
                            }
                        }
                    });
                }
                if (dataSnapshot.child("Test Results").hasChild("image9")){
                    l1_i9.setVisibility(View.VISIBLE);
                    l1_s9.setVisibility(View.VISIBLE);
                    final String u=dataSnapshot.child("Test Results").child("image9").child("img9").getValue().toString();
                    final String s=dataSnapshot.child("Test Results").child("image9").child("sbj9").getValue().toString();
                    Glide.with(getActivity()).load(u).into(l1_i9);
                    l1_s9.setText(s);
                    l1_i9.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (backTest.equals("yes")) {
                                imageSelector2(u, s);
                            }else if (back.equals("yes")){
                                imageSelector3(u, s);
                            }
                            else {
                                imageSelector(u,s);
                            }
                        }
                    });
                }
                if (dataSnapshot.child("Test Results").hasChild("image10")){
                    l1_i10.setVisibility(View.VISIBLE);
                    l1_s10.setVisibility(View.VISIBLE);
                    final String u=dataSnapshot.child("Test Results").child("image10").child("img10").getValue().toString();
                    final String s=dataSnapshot.child("Test Results").child("image10").child("sbj10").getValue().toString();
                    Glide.with(getActivity()).load(u).into(l1_i10);
                    l1_s10.setText(s);
                    l1_i10.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (backTest.equals("yes")) {
                                imageSelector2(u, s);
                            }else if (back.equals("yes")){
                                imageSelector3(u, s);
                            }
                            else {
                                imageSelector(u,s);
                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void image1(String key){
        dataRef.child("Medical Record").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Test Results").hasChild("image1")){
                    l1_i1.setVisibility(View.VISIBLE);
                    l1_s1.setVisibility(View.VISIBLE);
                    final String u=dataSnapshot.child("Test Results").child("image1").child("img1").getValue().toString();
                    final String s=dataSnapshot.child("Test Results").child("image1").child("sbj1").getValue().toString();
                    Glide.with(getActivity()).load(u).into(l1_i1);
                    l1_s1.setText(s);
                    l1_i1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (backTest=="yes") {
                                imageSelector2(u, s);
                            }else if (back=="yes"){
                                imageSelector3(u, s);
                            }
                            else {
                                imageSelector(u,s);
                            }
                        }
                    });
                }
                if (dataSnapshot.child("Test Results").hasChild("image2")){
                    l1_i2.setVisibility(View.VISIBLE);
                    l1_s2.setVisibility(View.VISIBLE);
                    final String u=dataSnapshot.child("Test Results").child("image2").child("img2").getValue().toString();
                    final String s=dataSnapshot.child("Test Results").child("image2").child("sbj2").getValue().toString();
                    Glide.with(getActivity()).load(u).into(l1_i2);
                    l1_s2.setText(s);
                    l1_i2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (backTest=="yes") {
                                imageSelector2(u, s);
                            }else if (back=="yes"){
                                imageSelector3(u, s);
                            }
                            else {
                                imageSelector(u,s);
                            }
                        }
                    });
                }
                if (dataSnapshot.child("Test Results").hasChild("image3")){
                    l1_i3.setVisibility(View.VISIBLE);
                    l1_s3.setVisibility(View.VISIBLE);
                    final String u=dataSnapshot.child("Test Results").child("image3").child("img3").getValue().toString();
                    final String s=dataSnapshot.child("Test Results").child("image3").child("sbj3").getValue().toString();
                    Glide.with(getActivity()).load(u).into(l1_i3);
                    l1_s3.setText(s);
                    l1_i3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (backTest=="yes") {
                                imageSelector2(u, s);
                            }else if (back=="yes"){
                                imageSelector3(u, s);
                            }
                            else {
                                imageSelector(u,s);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public  void imageSelector(String u,String s){
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ImageViewer iv=new ImageViewer();
        Bundle b=new Bundle();
        b.putString("url",u);
        b.putString("sbjct",s);
        iv.setArguments(b);
        ft.replace(R.id.activity_medical_record,iv);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }
    public  void imageSelector2(String u,String s){
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ImageViewer iv=new ImageViewer();
        Bundle b=new Bundle();
        b.putString("url",u);
        b.putString("sbjct",s);
        iv.setArguments(b);
        ft.replace(R.id.test_results,iv);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }
    public  void imageSelector3(String u,String s){
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        ImageViewer iv=new ImageViewer();
        Bundle b=new Bundle();
        b.putString("url",u);
        b.putString("sbjct",s);
        iv.setArguments(b);
        ft.replace(R.id.activity_start_consult,iv);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }
    public void notification(){
        dataRef.child("Doctor Notification").child(ui).addValueEventListener(new ValueEventListener() {
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
}
