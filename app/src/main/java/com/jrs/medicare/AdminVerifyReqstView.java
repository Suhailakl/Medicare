package com.jrs.medicare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
 * Created by SUHAIL on 11/18/2017.
 */

public class AdminVerifyReqstView extends Fragment {
    private DatabaseReference dataRef;
    private FirebaseAuth mAuth;
    private String uid,id,uId,ud;
    private TextView name,city,dpt,regNo,qua,workAt,phone,cnsltDays,time,adrs;
    private Button acpt,rjct;
    private Calendar c;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               getFragmentManager().popBackStackImmediate();
                return true;
            default:
                return false;
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (id!=null) {
            dataRef.child("Temporary Doctors").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String dctName = dataSnapshot.child("name").getValue().toString();
                    String dpmt = dataSnapshot.child("department").getValue().toString();
                    String hsp = dataSnapshot.child("working hospital").getValue().toString();
                    String ads = dataSnapshot.child("address").getValue().toString();
                    String num = dataSnapshot.child("mobile number").getValue().toString();
                    String dCity=dataSnapshot.child("city").getValue().toString();
                    String dRegNo=dataSnapshot.child("registration number").getValue().toString();
                    String dQua=dataSnapshot.child("education qualification").getValue().toString();
                    final String tokTot = (String) dataSnapshot.child("total tokens").getValue();
                    final String timeStart = (String) dataSnapshot.child("consultation starts at").getValue();
                    final String timeEnd = (String) dataSnapshot.child("consultation ends at").getValue();
                    time.setText(timeStart + " To " + timeEnd);
                    name.setText("Dr." + dctName);
                    dpt.setText(dpmt);
                    qua.setText("Qualification : "+dQua);
                    regNo.setText("Registration No : "+dRegNo);
                    city.setText("City : "+dCity);
                    workAt.setText("Works At " + hsp);
                    adrs.setText(ads);
                    phone.setText("+91 " + num);
                    String day1 = (String) dataSnapshot.child("day1").getValue();
                    String day2 = (String) dataSnapshot.child("day2").getValue();
                    String day3 = (String) dataSnapshot.child("day3").getValue();
                    String day4 = (String) dataSnapshot.child("day4").getValue();
                    String day5 = (String) dataSnapshot.child("day5").getValue();
                    String day6 = (String) dataSnapshot.child("day6").getValue();
                    String day7 = (String) dataSnapshot.child("day7").getValue();
                    days(day1,day2,day3,day4,day5,day6,day7);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            acpt.setVisibility(View.VISIBLE);
            rjct.setVisibility(View.VISIBLE);
            acpt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataRef.child("Temporary Doctors").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String user = dataSnapshot.getKey();
                            String name = dataSnapshot.child("name").getValue().toString();
                            String gender = dataSnapshot.child("gender").getValue().toString();
                            String ads = dataSnapshot.child("address").getValue().toString();
                            String num = dataSnapshot.child("mobile number").getValue().toString();
                            String mail = dataSnapshot.child("email").getValue().toString();
                            String day1 = null, day2 = null, day3 = null, day4 = null, day5 = null, day6 = null, day7 = null;
                            if (dataSnapshot.child("day1").exists()) {
                                day1 = dataSnapshot.child("day1").getValue().toString();
                            }
                            if (dataSnapshot.child("day2").exists()) {
                                day2 = dataSnapshot.child("day2").getValue().toString();
                            }
                            if (dataSnapshot.child("day3").exists()) {
                                day3 = dataSnapshot.child("day3").getValue().toString();
                            }
                            if (dataSnapshot.child("day4").exists()) {
                                day4 = dataSnapshot.child("day4").getValue().toString();
                            }
                            if (dataSnapshot.child("day5").exists()) {
                                day5 = dataSnapshot.child("day5").getValue().toString();
                            }
                            if (dataSnapshot.child("day6").exists()) {
                                day6 = dataSnapshot.child("day6").getValue().toString();
                            }
                            if (dataSnapshot.child("day7").exists()) {
                                day7 = dataSnapshot.child("day7").getValue().toString();
                            }
                            String eduQua = dataSnapshot.child("education qualification").getValue().toString();
                            String regNo = dataSnapshot.child("registration number").getValue().toString();
                            String hsptl = dataSnapshot.child("working hospital").getValue().toString();
                            String city = dataSnapshot.child("city").getValue().toString();
                            String strtsAt = dataSnapshot.child("consultation starts at").getValue().toString();
                            String endAt = dataSnapshot.child("consultation ends at").getValue().toString();
                            String tok = dataSnapshot.child("total tokens").getValue().toString();
                            String dptmt = dataSnapshot.child("department").getValue().toString();
                            DatabaseReference ref = dataRef.child("Docters").child(user).getRef();
                            ref.child("uid").setValue(user);
                            ref.child("name").setValue(name);
                            ref.child("gender").setValue(gender);
                            ref.child("address").setValue(ads);
                            if (dataSnapshot.hasChild("mailActive")){
                                ref.child("mailActive").setValue(dataSnapshot.child("mailActive").getValue().toString());
                            }
                            ref.child("mobile number").setValue(num);
                            ref.child("email").setValue(mail);
                            ref.child("day1").setValue(day1);
                            ref.child("day2").setValue(day2);
                            ref.child("day3").setValue(day3);
                            ref.child("day4").setValue(day4);
                            ref.child("day5").setValue(day5);
                            ref.child("day6").setValue(day6);
                            ref.child("day7").setValue(day7);
                            ref.child("education qualification").setValue(eduQua);
                            ref.child("department").setValue(dptmt);
                            ref.child("registration number").setValue(regNo);
                            ref.child("working hospital").setValue(hsptl);
                            ref.child("city").setValue(city);
                            ref.child("consultation starts at").setValue(strtsAt);
                            ref.child("consultation ends at").setValue(endAt);
                            ref.child("total tokens").setValue(tok);
                         /*   dataRef.child("Users").orderByChild("city").equalTo(city).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data:dataSnapshot.getChildren()){

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });*/
                            Toast.makeText(getActivity(), "Successfully Added ", Toast.LENGTH_SHORT).show();
                            dataSnapshot.getRef().removeValue();
                            AdminHomeClass ad=new AdminHomeClass();
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.admin_home,ad);
                            ft.commit();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            });
            rjct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataRef.child("Temporary Doctors").child(id).getRef().removeValue();
                    Toast.makeText(getActivity(), "Successfully Rejected", Toast.LENGTH_SHORT).show();
                    AdminHomeClass ad=new AdminHomeClass();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.admin_home,ad);
                    ft.commit();
                }
            });
        }
        else if (uId!=null){
            dataRef.child("Docters").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String dctName = dataSnapshot.child("name").getValue().toString();
                    String dpmt = dataSnapshot.child("department").getValue().toString();
                    String hsp = dataSnapshot.child("working hospital").getValue().toString();
                    String ads = dataSnapshot.child("address").getValue().toString();
                    String num = dataSnapshot.child("mobile number").getValue().toString();
                    String dCity=dataSnapshot.child("city").getValue().toString();
                    String dRegNo=dataSnapshot.child("registration number").getValue().toString();
                    String dQua=dataSnapshot.child("education qualification").getValue().toString();
                    final String tokTot = (String) dataSnapshot.child("total tokens").getValue();
                    final String timeStart = (String) dataSnapshot.child("consultation starts at").getValue();
                    final String timeEnd = (String) dataSnapshot.child("consultation ends at").getValue();
                    time.setText(timeStart + " To " + timeEnd);
                    name.setText("Dr." + dctName);
                    dpt.setText(dpmt);
                    qua.setText("Qualification : "+dQua);
                    regNo.setText("Registration No : "+dRegNo);
                    city.setText("City : "+dCity);
                    workAt.setText("Works At " + hsp);
                    adrs.setText(ads);
                    phone.setText("+91 " + num);
                    String day1 = (String) dataSnapshot.child("day1").getValue();
                    String day2 = (String) dataSnapshot.child("day2").getValue();
                    String day3 = (String) dataSnapshot.child("day3").getValue();
                    String day4 = (String) dataSnapshot.child("day4").getValue();
                    String day5 = (String) dataSnapshot.child("day5").getValue();
                    String day6 = (String) dataSnapshot.child("day6").getValue();
                    String day7 = (String) dataSnapshot.child("day7").getValue();
                    days(day1,day2,day3,day4,day5,day6,day7);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            rjct.setVisibility(View.VISIBLE);
            rjct.setText("Remove Doctor");
            rjct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dataRef.child("Docters").child(uId).getRef().removeValue();
                    dataRef.child("Id").orderByValue().equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dataRef.child("Id").child(dataSnapshot.getKey()).getRef().removeValue();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    dataRef.child("Token Id").orderByChild("doctorId").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot data:dataSnapshot.getChildren()){
                                DatabaseReference u=dataRef.child("User Notification").child(data.child("uid").getValue().toString()).push().getRef();
                                u.child("removedActive").setValue("yes");
                                u.child("time").setValue(c.getTimeInMillis());
                                u.child("doctorId").setValue(ud);
                                u.child("status").setValue("token");
                                u.child("docName").setValue(dataSnapshot.child("doctor").getValue().toString());
                                dataRef.child("Token Id").child(data.getKey()).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    dataRef.child("Medical Record").orderByChild("doctorId").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot data:dataSnapshot.getChildren()){
                                DatabaseReference u=dataRef.child("User Notification").child(data.child("uid").getValue().toString()).push().getRef();
                                u.child("removedActive").setValue("yes");
                                u.child("time").setValue(c.getTimeInMillis());
                                u.child("doctorId").setValue(ud);
                                u.child("docName").setValue(dataSnapshot.child("doctor").getValue().toString());
                                u.child("status").setValue("record");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    dataRef.child("Admin").child("Doctor Issues").orderByChild("docKey").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot data:dataSnapshot.getChildren()){
                             data.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Toast.makeText(getActivity(), "Successfully Removed", Toast.LENGTH_SHORT).show();
                    AdminHomeClass ad=new AdminHomeClass();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.admin_home,ad);
                    ft.commit();
                }
            });
        }
        else {
            dataRef.child("Docters").child(ud).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String dctName = dataSnapshot.child("name").getValue().toString();
                    String dpmt = dataSnapshot.child("department").getValue().toString();
                    String hsp = dataSnapshot.child("working hospital").getValue().toString();
                    String ads = dataSnapshot.child("address").getValue().toString();
                    String num = dataSnapshot.child("mobile number").getValue().toString();
                    String dCity=dataSnapshot.child("city").getValue().toString();
                    String dRegNo=dataSnapshot.child("registration number").getValue().toString();
                    String dQua=dataSnapshot.child("education qualification").getValue().toString();
                    final String tokTot = (String) dataSnapshot.child("total tokens").getValue();
                    final String timeStart = (String) dataSnapshot.child("consultation starts at").getValue();
                    final String timeEnd = (String) dataSnapshot.child("consultation ends at").getValue();
                    time.setText(timeStart + " To " + timeEnd);
                    name.setText("Dr." + dctName);
                    dpt.setText(dpmt);
                    qua.setText("Qualification : "+dQua);
                    regNo.setText("Registration No : "+dRegNo);
                    city.setText("City : "+dCity);
                    workAt.setText("Works At " + hsp);
                    adrs.setText(ads);
                    phone.setText("+91 " + num);
                    String day1 = (String) dataSnapshot.child("day1").getValue();
                    String day2 = (String) dataSnapshot.child("day2").getValue();
                    String day3 = (String) dataSnapshot.child("day3").getValue();
                    String day4 = (String) dataSnapshot.child("day4").getValue();
                    String day5 = (String) dataSnapshot.child("day5").getValue();
                    String day6 = (String) dataSnapshot.child("day6").getValue();
                    String day7 = (String) dataSnapshot.child("day7").getValue();
                    days(day1,day2,day3,day4,day5,day6,day7);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            rjct.setVisibility(View.VISIBLE);
            rjct.setText("Remove Doctor");
            rjct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataRef.child("Docters").child(ud).getRef().removeValue();

                    dataRef.child("Token Id").orderByChild("doctorId").equalTo(ud).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot data:dataSnapshot.getChildren()){
                                DatabaseReference u=dataRef.child("User Notification").child(data.child("uid").getValue().toString()).push().getRef();
                                u.child("removedActive").setValue("yes");
                                u.child("time").setValue(c.getTimeInMillis());
                                u.child("doctorId").setValue(ud);
                                u.child("status").setValue("token");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    dataRef.child("Medical Record").orderByChild("doctorId").equalTo(ud).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot data:dataSnapshot.getChildren()){
                                DatabaseReference u=dataRef.child("User Notification").child(data.child("uid").getValue().toString()).push().getRef();
                                u.child("removedActive").setValue("yes");
                                u.child("time").setValue(c.getTimeInMillis());
                                u.child("doctorId").setValue(ud);
                                u.child("status").setValue("record");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    dataRef.child("Admin").child("Doctor Issues").orderByChild("docKey").equalTo(ud).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot data:dataSnapshot.getChildren()){
                                data.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Toast.makeText(getActivity(), "Successfully Removed", Toast.LENGTH_SHORT).show();
                    AdminHomeClass ad=new AdminHomeClass();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.admin_home,ad);
                    ft.commit();
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.admin_verify_doc,container,false);
        if (container!=null){
            container.removeAllViews();
        }
        getActivity().setTitle(R.string.vrfyDoc);
        setHasOptionsMenu(true);
        mAuth=FirebaseAuth.getInstance();
        dataRef= FirebaseDatabase.getInstance().getReference();
        name= (TextView) v.findViewById(R.id.ad_vrfy_name);
        city= (TextView) v.findViewById(R.id.ad_vrfy_city);
        dpt= (TextView) v.findViewById(R.id.ad_vrfy_dpt);
        regNo= (TextView) v.findViewById(R.id.ad_vrfy_reg_no);
        qua= (TextView) v.findViewById(R.id.ad_vrfy_edu);
        workAt= (TextView) v.findViewById(R.id.ad_vrfy_hspname);
        phone= (TextView) v.findViewById(R.id.ad_vrfy_num);
        cnsltDays= (TextView) v.findViewById(R.id.ad_vrfy_days);
        time= (TextView) v.findViewById(R.id.tad_vrfy_time);
        adrs= (TextView) v.findViewById(R.id.ad_vrfy_ads);
        acpt= (Button) v.findViewById(R.id.ad_vrfy_accept);
        rjct= (Button) v.findViewById(R.id.ad_vrfy_reject);
        acpt.setVisibility(View.GONE);
        rjct.setVisibility(View.GONE);
        c=Calendar.getInstance();
        Bundle b=this.getArguments();
        if (getArguments()!=null){
            id=b.getString("id");
            uId=b.getString("uid");
            ud=b.getString("ud");
        }
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                            AdminHomeClass ad=new AdminHomeClass();
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.admin_home,ad);
                            ft.commit();

                    }
                }
                return false;
            }
        });
        return v;
    }
    public void days(String day1, String day2, String day3, String day4, String day5, String day6,String day7){
        cnsltDays.setText(null);
        if (day1 != null) {
            cnsltDays.setText(day1);
        }
        if (day2 != null) {
            if (day1 != null) {
                cnsltDays.append("," + day2);
            } else {
                cnsltDays.append(day2);
            }
        }
        if (day3 != null) {
            if (day1 != null || day2 != null) {
                cnsltDays.append("," + day3);
            } else {
                cnsltDays.append(day3);
            }
        }
        if (day4 != null) {
            if (day1 != null || day2 != null || day3 != null) {
                cnsltDays.append("," + day4);
            } else {
                cnsltDays.append(day4);
            }
        }
        if (day5 != null) {
            if (day1 != null || day2 != null || day3 != null || day4 != null) {
                cnsltDays.append("," + day5);
            } else {
                cnsltDays.append(day5);
            }
        }
        if (day6 != null) {
            if (day1 != null || day2 != null || day3 != null || day4 != null || day5 != null) {
                cnsltDays.append("," + day6);
            } else {
                cnsltDays.append(day6);
            }
        }
        if (day7 != null) {
            if (day1 != null || day2 != null || day3 != null || day4 != null || day5 != null || day6 != null) {
                cnsltDays.append("," + day7);
            } else {
                cnsltDays.append(day7);
            }
        }

    }
}
