package com.jrs.medicare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by SUHAIL on 12/31/2017.
 */

public class DoctorSettings extends Fragment {
    private TextView name,edQua,regNo,dpt,city,hsp,hspAdrs,tkNo,mobNo,mail,changePwd,deactAcnt,tk,deactNum,deactOk;
    private Button update;
    private Spinner strt_hr,strt_mt,strt_format,end_hr,end_mt,end_format;
    private CheckBox sun,mon,tue,wed,thi,fri,sat;
    private FirebaseAuth mAuth;
    private DatabaseReference dataRef;
    private String uid,strtAt,endAt;
    private RadioGroup rdo;
    private PopupWindow popup;
    private FrameLayout frame;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.doctor_settings,container,false);
        if (container!=null){
            container.removeAllViews();
        }
        getActivity().setTitle(R.string.navAcnt);
        dataRef= FirebaseDatabase.getInstance().getReference();
        mAuth= FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        name= (TextView) v.findViewById(R.id.doc_stngs_name);
        edQua= (TextView) v.findViewById(R.id.doc_stngs_ed_qua);
        dpt= (TextView) v.findViewById(R.id.doc_stngs_dpmt);
        regNo= (TextView) v.findViewById(R.id.doc_stngs_reg_no);
        city= (TextView) v.findViewById(R.id.doc_stngs_city);
        hsp= (TextView) v.findViewById(R.id.doc_stngs_hsp_name);
        hspAdrs= (TextView) v.findViewById(R.id.doc_stngs_adrs);
        tkNo= (TextView) v.findViewById(R.id.doc_stngs_tk);
        mobNo= (TextView) v.findViewById(R.id.doc_stngs_mob_no);
        mail= (TextView) v.findViewById(R.id.doc_stngs_mail);
        changePwd= (TextView) v.findViewById(R.id.doc_stngs_change_pwd);
        deactAcnt= (TextView) v.findViewById(R.id.doc_stngs_cancel_act);
        update= (Button) v.findViewById(R.id.doc_stngs_updt_btn);
        strt_hr= (Spinner) v.findViewById(R.id.doc_stngs_strt_time_hr);
        strt_mt= (Spinner) v.findViewById(R.id.doc_stngs_strt_time_mt);
        strt_format= (Spinner) v.findViewById(R.id.doc_stngs_strt_time_format);
        end_hr= (Spinner) v.findViewById(R.id.doc_stngs_end_time_hr);
        end_mt= (Spinner) v.findViewById(R.id.doc_stngs_end_time_mt);
        end_format= (Spinner) v.findViewById(R.id.doc_stngs_end_time_format);
        tk= (TextView) v.findViewById(R.id.doc_stngs_tk);
        List<String> sHr=new ArrayList<>();
        for (int i=1;i<=12;i++){
            sHr.add(String.valueOf(i));
        }
        ArrayAdapter<String> sHrAdapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,sHr);
        sHrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        strt_hr.setAdapter(sHrAdapter);
        end_hr.setAdapter(sHrAdapter);
         List<String> sMt=new ArrayList<>();
        for (int i=0;i<60;i++){
            if (String.valueOf(i).length()==1){
                sMt.add("0"+String.valueOf(i));
            }
            else {
                sMt.add(String.valueOf(i));
            }
        }
        ArrayAdapter<String> sMtAdapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,sMt);
        sMtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        strt_mt.setAdapter(sMtAdapter);
        end_mt.setAdapter(sMtAdapter);
        List<String> sFormat=new ArrayList<>();
        sFormat.add("AM");
        sFormat.add("PM");
        ArrayAdapter<String> sFormatAdapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,sFormat);
        sFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        strt_format.setAdapter(sFormatAdapter);
        end_format.setAdapter(sFormatAdapter);
        sun= (CheckBox) v.findViewById(R.id.doc_sunday);
        mon= (CheckBox) v.findViewById(R.id.doc_monday);
        tue= (CheckBox) v.findViewById(R.id.doc_tuesday);
        thi= (CheckBox) v.findViewById(R.id.doc_thirsday);
        fri= (CheckBox) v.findViewById(R.id.doc_friday);
        sat= (CheckBox) v.findViewById(R.id.doc_saturday);
        wed= (CheckBox) v.findViewById(R.id.doc_wednsaday);
        rdo= (RadioGroup) v.findViewById(R.id.doc_stngs_rdo_grp);
        frame= (FrameLayout) v.findViewById(R.id.doc_settings_frame_layout);
        frame.getForeground().setAlpha(0);
        deactAcnt= (TextView) v.findViewById(R.id.doc_stngs_cancel_act);
        inflater= (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View pop=inflater.inflate(R.layout.cancel_account,null);
        deactNum= (EditText) pop.findViewById(R.id.usr_stngs_deact_num);
        deactOk= (Button) pop.findViewById(R.id.usr_stngs_deact_num_ok);
        popup = new PopupWindow(pop, 650, 750, true);
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        pop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popup.dismiss();
                frame.getForeground().setAlpha(0);
                return false;
            }
        });
        popup.setOutsideTouchable(true);
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                frame.getForeground().setAlpha(0);
            }
        });
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                        StartConsult fh=new StartConsult();
                        ft.replace(R.id.content_doc_home,fh);
                        ft.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                        return true;
                    }
                }
                return false;
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        dataRef.child("Docters").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("name").getValue().toString());
                edQua.setText(dataSnapshot.child("education qualification").getValue().toString());
                dpt.setText(dataSnapshot.child("department").getValue().toString());
                regNo.setText(dataSnapshot.child("registration number").getValue().toString());
                city.setText(dataSnapshot.child("city").getValue().toString());
                hsp.setText(dataSnapshot.child("working hospital").getValue().toString());
                hspAdrs.setText(dataSnapshot.child("address").getValue().toString());
                tkNo.setText(dataSnapshot.child("total tokens").getValue().toString());
                mobNo.setText(dataSnapshot.child("mobile number").getValue().toString());
                if (dataSnapshot.child("email").exists()){
                   mail.setText(dataSnapshot.child("email").getValue().toString());
                }
                strtAt=dataSnapshot.child("consultation starts at").getValue().toString();
                endAt=dataSnapshot.child("consultation ends at").getValue().toString();
                StartTimeSelector(strtAt);
                EndTimeSelector(endAt);
                if (dataSnapshot.child("day1").exists()){
                    days("Sunday");
                }
                if (dataSnapshot.child("day2").exists()){
                    days("Monday");
                }
                if (dataSnapshot.child("day3").exists()){
                    days("Tuesday");
                }
                if (dataSnapshot.child("day4").exists()){
                    days("Wednesday");
                }
                if (dataSnapshot.child("day5").exists()){
                    days("Thirsday");
                }
                if (dataSnapshot.child("day6").exists()){
                    days("Friday");
                }
                if (dataSnapshot.child("day7").exists()){
                    days("Saturday");
                }
                if (dataSnapshot.child("gender").getValue().toString().equals("Male")){
                    rdo.check(R.id.doc_stngs_rdo_male);
                }
                else {
                    rdo.check(R.id.doc_stngs_rdo_female);
                }
               mobNo.setText(dataSnapshot.child("mobile number").getValue().toString());
                mail.setText(dataSnapshot.child("email").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference mRef=dataRef.child("Docters").child(uid).getRef();
                mRef.child("name").setValue(name.getText().toString().trim());
                int id=rdo.getCheckedRadioButtonId();
                if (id==R.id.doc_stngs_rdo_male){
                    mRef.child("gender").setValue("Male");
                }else {
                    mRef.child("gender").setValue("Female");
                }
                mRef.child("education qualification").setValue(edQua.getText().toString().trim());
                mRef.child("registration number").setValue(regNo.getText().toString().trim());
                mRef.child("department").setValue(dpt.getText().toString().trim());
                mRef.child("city").setValue(city.getText().toString().trim());
                mRef.child("working hospital").setValue(hsp.getText().toString().trim());
                mRef.child("address").setValue(hspAdrs.getText().toString().trim());
                mRef.child("total tokens").setValue(tkNo.getText().toString().trim());
                String mnt=strt_mt.getSelectedItem().toString().trim();
                if (mnt.equals("00")){
                    mRef.child("consultation starts at").setValue(strt_hr.getSelectedItem().toString().trim()+strt_format.getSelectedItem().toString().trim());
                }
                else {
                    mRef.child("consultation starts at").setValue(strt_hr.getSelectedItem().toString().trim()+":"+strt_mt.getSelectedItem().toString().trim()+strt_format.getSelectedItem().toString().trim());

                }
                String Emnt=end_mt.getSelectedItem().toString().trim();
                if (Emnt.equals("00")){
                    mRef.child("consultation ends at").setValue(end_hr.getSelectedItem().toString().trim()+end_format.getSelectedItem().toString().trim());
                }
                else {
                    mRef.child("consultation ends at").setValue(end_hr.getSelectedItem().toString().trim()+":"+end_mt.getSelectedItem().toString().trim()+end_format.getSelectedItem().toString().trim());

                }
                mRef.child("total token").setValue(tk.getText().toString().trim());
                if (sun.isChecked()){
                    mRef.child("day1").setValue("Sunday");
                }
                else {
                    mRef.child("day1").removeValue();
                }
                if (mon.isChecked()){
                    mRef.child("day2").setValue("Monday");
                }
                else {
                    mRef.child("day2").removeValue();
                }
                if (tue.isChecked()){
                    mRef.child("day3").setValue("Tuesday");
                }
                else {
                    mRef.child("day3").removeValue();
                }
                if (wed.isChecked()){
                    mRef.child("day4").setValue("Wednesday");
                }
                else {
                    mRef.child("day4").removeValue();
                }
                if (thi.isChecked()){
                    mRef.child("day5").setValue("Thursday");
                }
                else {
                    mRef.child("day5").removeValue();
                }
                if (fri.isChecked()){
                    mRef.child("day6").setValue("Friday");
                }
                else {
                    mRef.child("day6").removeValue();
                }
                if (sat.isChecked()){
                    mRef.child("day7").setValue("Saturday");
                }
                else {
                    mRef.child("day7").removeValue();
                }
                Toast.makeText(getActivity(), "Successfully Updated", Toast.LENGTH_SHORT).show();
            }

        });
        changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser().isEmailVerified()){
                    mAuth.sendPasswordResetEmail(mail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getActivity(), "You are signed in using Phone", Toast.LENGTH_SHORT).show();
                }
            }
        });
        deactAcnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frame.getForeground().setAlpha(200);
                popup.showAtLocation(frame, Gravity.CENTER, 0, 0);
                dataRef.child("User Notification").orderByChild("doctorId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                deactOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c=Calendar.getInstance();
                        if (deactNum.getText().toString().equals(mobNo.getText().toString())){
                            dataRef.child("Admin").child("Doctor Issues").orderByChild("docKey").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            dataRef.child("Token Id").orderByChild("doctorId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener()  {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String ud = "";
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                            ud= data.child("uid").getValue().toString();
                                            data.getRef().removeValue();
                                        }
                                    final DatabaseReference u = dataRef.child("User Notification").child(ud).push().getRef();
                                    u.child("removedActive").setValue("yes");
                                    u.child("time").setValue(c.getTimeInMillis());
                                    u.child("doctorId").setValue(uid);
                                    u.child("status").setValue("token");
                                    dataRef.child("Docters").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            u.child("docName").setValue(dataSnapshot.child("name").getValue().toString().trim());
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            dataRef.child("Medical Record").orderByChild("doctorId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        DatabaseReference u=dataRef.child("User Notification").child(data.child("uid").getValue().toString()).push().getRef();
                                        u.child("removedActive").setValue("yes");
                                        u.child("time").setValue(c.getTimeInMillis());
                                        u.child("doctorId").setValue(uid);
                                        u.child("status").setValue("record");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                           dataRef.child("Id").child(mobNo.getText().toString()).removeValue();
                            Handler h=new Handler();
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dataRef.child("Docters").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Toast.makeText(getActivity(), "Successfully Cancelled Your Account", Toast.LENGTH_SHORT).show();
                                            dataSnapshot.getRef().removeValue();
                                            if ( mAuth.getCurrentUser()!=null) {
                                                mAuth.getCurrentUser().delete();
                                                startActivity(new Intent(getActivity(),SignInActivity.class));
                                                getActivity().finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            },2000);

                        }
                        else{
                            Toast.makeText(getActivity(), "Incorrect Number", Toast.LENGTH_SHORT).show();
                            deactNum.setText(null);
                        }

                    }
                });
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
    private void StartTimeSelector(String s){
        if (s.contains("AM")){
            strt_format.setSelection(0);

        }
        else {
            strt_format.setSelection(1);
        }
    if (s.length()==3) {
        String strt_h = s.substring(0, 1);
        for (int i = 0; i < strt_hr.getAdapter().getCount(); i++) {
            if (String.valueOf(strt_hr.getAdapter().getItem(i)).equals(strt_h)) {
                strt_hr.setSelection(i);

            }
        }
            strt_mt.setSelection(0);
    }
    else if (s.length()==6){
        String strt_h=s.substring(0,1);
        for (int i=0;i<strt_hr.getAdapter().getCount();i++) {
            if (String.valueOf(strt_hr.getAdapter().getItem(i)).equals(strt_h)){
                strt_hr.setSelection(i);

            }
        }
        String strt_m=s.substring(2,4);
        for (int i=0;i<strt_mt.getAdapter().getCount();i++) {
            if (String.valueOf(strt_mt.getAdapter().getItem(i)).equals(strt_m)){
                strt_mt.setSelection(i);
            }
        }
    }
    else if (s.length()==7){
        String strt_h=s.substring(0,2);
        for (int i=0;i<strt_hr.getAdapter().getCount();i++) {
            if (String.valueOf(strt_hr.getAdapter().getItem(i)).equals(strt_h)){
                strt_hr.setSelection(i);

            }
        }
        String strt_m=s.substring(3,5);
        for (int i=0;i<strt_mt.getAdapter().getCount();i++) {
            if (String.valueOf(strt_mt.getAdapter().getItem(i)).equals(strt_m)){
                strt_mt.setSelection(i);
            }
        }
    }
    else {
        String strt_h=s.substring(0,2);
        for (int i=0;i<strt_hr.getAdapter().getCount();i++) {
            if (String.valueOf(strt_hr.getAdapter().getItem(i)).equals(strt_h)){
                strt_hr.setSelection(i);

            }
        }
        strt_mt.setSelection(0);
    }
    }
    private void EndTimeSelector(String s){
        if (s.contains("AM")){
            end_format.setSelection(0);

        }
        else {
            end_format.setSelection(1);
        }
        if (s.length()==3) {
            String strt_h = s.substring(0, 1);
            for (int i = 0; i < end_hr.getAdapter().getCount(); i++) {
                if (String.valueOf(end_hr.getAdapter().getItem(i)).equals(strt_h)) {
                    end_hr.setSelection(i);

                }
            }
                end_mt.setSelection(0);
        }
    else if (s.length()==6){
                String strt_h=s.substring(0,1);
                for (int i=0;i<end_hr.getAdapter().getCount();i++) {
                    if (String.valueOf(end_hr.getAdapter().getItem(i)).equals(strt_h)){
                        end_hr.setSelection(i);

                    }
                }
                String strt_m=s.substring(2,4);
                for (int i=0;i<end_mt.getAdapter().getCount();i++) {
                    if (String.valueOf(end_mt.getAdapter().getItem(i)).equals(strt_m)){
                        end_mt.setSelection(i);
                    }
                }
            }
            else if (s.length()==7){
                String strt_h=s.substring(0,2);
                for (int i=0;i<end_hr.getAdapter().getCount();i++) {
                    if (String.valueOf(end_hr.getAdapter().getItem(i)).equals(strt_h)){
                        end_hr.setSelection(i);

                    }
                }
                String strt_m=s.substring(3,5);
                for (int i=0;i<end_mt.getAdapter().getCount();i++) {
                    if (String.valueOf(end_mt.getAdapter().getItem(i)).equals(strt_m)){
                        end_mt.setSelection(i);
                    }
                }
            }
            else {
            String strt_h=s.substring(0,2);
            for (int i=0;i<end_hr.getAdapter().getCount();i++) {
                if (String.valueOf(end_hr.getAdapter().getItem(i)).equals(strt_h)){
                    end_hr.setSelection(i);

                }
            }
            end_mt.setSelection(0);
        }
        }
        private void days(String d){
            switch(d){
                case "Sunday":sun.setChecked(true);
                    break;
                case  "Monday":mon.setChecked(true);
                    break;
                case "Tuesday":tue.setChecked(true);
                    break;
                case "Wednesday":wed.setChecked(true);
                    break;
                case "Thirsday":thi.setChecked(true);
                    break;
                case "Friday":fri.setChecked(true);
                    break;
                case "Saturday":sat.setChecked(true);
                    break;
            }
        }
    }
