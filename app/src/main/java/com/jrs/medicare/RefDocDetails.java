package com.jrs.medicare;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

/**
 * Created by SUHAIL on 11/5/2017.
 */

public class RefDocDetails extends Fragment {
    ProgressBar refPro;
    private DatabaseReference dataRef, rootRef, docRef;
    private FirebaseAuth mAuth;
    private String usrname, id,mRkey,dName,csView;
    private TextView drName, adrs, hspName, dpmt, numbr, days, time;
    private LinearLayout layout;
    private Button ref;
    private  PopupWindow popupWindow;
    String user;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.refer_doctor_details, container, false);
        setHasOptionsMenu(true);
        layout = (LinearLayout) v.findViewById(R.id.ref_doc_layout);
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popup = inflater.inflate(R.layout.popup_ref_doc_details, null);
        boolean focusable = true;
        popupWindow = new PopupWindow(popup, 650, 150, focusable);
        refPro = (ProgressBar) v.findViewById(R.id.ref_doc_progress);
        Bundle b = this.getArguments();
        if (getArguments() != null) {
            id = b.getString("uid");
            mRkey=b.getString("mRkey");
            csView=b.getString("csView");
        }
        popup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                MedicalRecord mR=new MedicalRecord();
                ft.replace(R.id.ref_doc_layout, mR);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                return true;
            }
        });
        if (container != null) {
            container.removeAllViews();
        }
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        MedicalRecordView mR = new MedicalRecordView();
                        Bundle b=new Bundle();
                        b.putString("key",mRkey);
                        b.putString("csView",csView);
                        mR.setArguments(b);
                        ft.replace(R.id.ref_doc_layout, mR);
                        ft.addToBackStack(null);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                        return true;
                    }
                }
                return false;
            }
        });
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();
        drName = (TextView) v.findViewById(R.id.ref_doc_name);
        adrs = (TextView) v.findViewById(R.id.ref_doc_ads);
        hspName = (TextView) v.findViewById(R.id.ref_doc_hspname);
        dpmt = (TextView) v.findViewById(R.id.ref_doc_dpt);
        numbr = (TextView) v.findViewById(R.id.ref_doc_num);
        days = (TextView) v.findViewById(R.id.ref_doc_days);
        time = (TextView) v.findViewById(R.id.ref_doc_time);
        ref = (Button) v.findViewById(R.id.ref_doc_ref);
        dataRef = FirebaseDatabase.getInstance().getReference();
        docRef = FirebaseDatabase.getInstance().getReference().child("Docters");
        refPro.setVisibility(View.INVISIBLE);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataRef.child("Docters").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String dpt = dataSnapshot.child("department").getValue().toString();
                String hsp = dataSnapshot.child("working hospital").getValue().toString();
                String ads = dataSnapshot.child("address").getValue().toString();
                String num = dataSnapshot.child("mobile number").getValue().toString();
                final String tokTot = (String) dataSnapshot.child("total tokens").getValue();
                final String timeStart = (String) dataSnapshot.child("consultation starts at").getValue();
                final String timeEnd = (String) dataSnapshot.child("consultation ends at").getValue();
                time.setText(timeStart + " To " + timeEnd);
                drName.setText("Dr." + name);
                dpmt.setText(dpt);
                hspName.setText("Works At " + hsp);
                adrs.setText(ads);
                numbr.setText("+91 " + num);
                String day1 = (String) dataSnapshot.child("day1").getValue();
                String day2 = (String) dataSnapshot.child("day2").getValue();
                String day3 = (String) dataSnapshot.child("day3").getValue();
                String day4 = (String) dataSnapshot.child("day4").getValue();
                String day5 = (String) dataSnapshot.child("day5").getValue();
                String day6 = (String) dataSnapshot.child("day6").getValue();
                String day7 = (String) dataSnapshot.child("day7").getValue();
                days.setText(null);
                if (day1 != null) {
                    days.setText(day1);
                }
                if (day2 != null) {
                    if (day1 != null) {
                        days.append("," + day2);
                    } else {
                        days.append(day2);
                    }
                }
                if (day3 != null) {
                    if (day1 != null || day2 != null) {
                        days.append("," + day3);
                    } else {
                        days.append(day3);
                    }
                }
                if (day4 != null) {
                    if (day1 != null || day2 != null || day3 != null) {
                        days.append("," + day4);
                    } else {
                        days.append(day4);
                    }
                }
                if (day5 != null) {
                    if (day1 != null || day2 != null || day3 != null || day4 != null) {
                        days.append("," + day5);
                    } else {
                        days.append(day5);
                    }
                }
                if (day6 != null) {
                    if (day1 != null || day2 != null || day3 != null || day4 != null || day5 != null) {
                        days.append("," + day6);
                    } else {
                        days.append(day6);
                    }
                }
                if (day7 != null) {
                    if (day1 != null || day2 != null || day3 != null || day4 != null || day5 != null || day6 != null) {
                        days.append("," + day7);
                    } else {
                        days.append(day7);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c=Calendar.getInstance();
                refPro.setVisibility(View.VISIBLE);
                dataRef.child("Medical Record").child(mRkey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.child("dId").getRef().setValue(dataSnapshot.child("doctorId").getValue().toString());
                        DatabaseReference mRef=dataRef.child("Medical Record").push().getRef();
                        mRef.child("doctor").setValue(drName.getText().toString().trim());
                        mRef.child("patient").setValue(dataSnapshot.child("patient").getValue().toString());
                        final String userId=dataSnapshot.child("userId").getValue().toString();
                        mRef.child("docKey").setValue(id+userId);
                        mRef.child("doctorId").setValue(id);
                        mRef.child("date").setValue(dataSnapshot.child("date").getValue().toString());
                        mRef.child("referred from").setValue(mRkey);
                        mRef.child("spinnerKey").setValue(drName.getText().toString().trim()+dataSnapshot.child("uid").getValue().toString());
                        mRef.child("userId").setValue(userId);
                        mRef.child("uid").setValue(dataSnapshot.child("uid").getValue().toString());
                        mRef.child("refKey").setValue(id+"yes");
                        DatabaseReference docN=dataRef.child("Doctor Notification").child(id).push().getRef();
                        docN.child("referredActive").setValue("yes");
                        docN.child("uid").setValue(dataSnapshot.child("uid").getValue().toString());
                        docN.child("from").setValue(user);
                        docN.child("time").setValue(c.getTimeInMillis());
                        DatabaseReference u=dataRef.child("User Notification").child(dataSnapshot.child("uid").getValue().toString()).push().getRef();
                        u.child("referredActive").setValue("yes");
                        u.child("time").setValue(c.getTimeInMillis());
                        u.child("doctorId").setValue(id);
                        u.child("from").setValue(user);
                        dataRef.child("Test Results").orderByChild("mRkey").equalTo(mRkey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot data:dataSnapshot.getChildren()) {
                                    Calendar c=Calendar.getInstance();
                                    long timeinMi=c.getTimeInMillis();
                                    String time=String.valueOf(timeinMi);
                                    dataRef.child("Test Results").child(data.getKey()).child("key").setValue(id+time);
                                    dataRef.child("Test Results").child(data.getKey()).child("time").setValue(timeinMi);
                                    dataRef.child("Test Results").child(data.getKey()).child("dateKey").setValue(id+data.child("date").getValue().toString());
                                    dataRef.child("Test Results").child(data.getKey()).child("docKey").setValue(id+userId);
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
                        dataSnapshot.child("doctorId").getRef().removeValue();
                        dataSnapshot.child("docKey").getRef().removeValue();
                        dataSnapshot.child("spinnerKey").getRef().removeValue();
                        Toast.makeText(getActivity(), "Patient Referred Successfully", Toast.LENGTH_SHORT).show();
                        refPro.setVisibility(View.GONE);
                        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
