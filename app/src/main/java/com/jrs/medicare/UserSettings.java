package com.jrs.medicare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
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

/**
 * Created by SUHAIL on 12/29/2017.
 */

public class UserSettings extends Fragment {
    private DatabaseReference dataRef;
    private String uid;
    private FirebaseAuth mAuth;
    private EditText name,age,ads,mob,mail,usrName,deactNum;
    private RadioGroup rdio;
    private Button updt,deactOk;
    private TextView chnge_pwd,cncelAcnt;
    private PopupWindow popup;
    private FrameLayout scroll;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.user_settings,container,false);
        getActivity().setTitle(R.string.navAcnt);
        dataRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        name= (EditText) v.findViewById(R.id.usr_stngs_name);
        age= (EditText) v.findViewById(R.id.usr_stngs_age);
        ads= (EditText) v.findViewById(R.id.usr_stngs_adrs);
        rdio= (RadioGroup) v.findViewById(R.id.usr_stngs_rdo_grp);
        mob= (EditText) v.findViewById(R.id.usr_stngs_mob_no);
        mail= (EditText) v.findViewById(R.id.usr_stngs_mail);
        usrName= (EditText) v.findViewById(R.id.usr_stngs_usr_name);
        chnge_pwd= (TextView) v.findViewById(R.id.usr_stngs_change_pwd);
        cncelAcnt= (TextView) v.findViewById(R.id.usr_stngs_cancel_act);
        updt= (Button) v.findViewById(R.id.usr_stngs_updt_btn);
        inflater= (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View pop=inflater.inflate(R.layout.cancel_account,null);
        deactNum= (EditText) pop.findViewById(R.id.usr_stngs_deact_num);
        deactOk= (Button) pop.findViewById(R.id.usr_stngs_deact_num_ok);
        popup = new PopupWindow(pop, 650, 750, true);
        scroll= (FrameLayout) v.findViewById(R.id.user_settings_sroll_layout);
        scroll.getForeground().setAlpha(0);   
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        pop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    popup.dismiss();
                scroll.getForeground().setAlpha(0);
                return false;
            }
        });
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                scroll.getForeground().setAlpha(0);
            }
        });
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        FragmentHome fh = new FragmentHome();
                        ft.replace(R.id.content_user_home, fh);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
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
        super.onViewCreated(view, savedInstanceState);
        dataRef.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("name").getValue().toString());
                age.setText(dataSnapshot.child("age").getValue().toString());
                ads.setText(dataSnapshot.child("address").getValue().toString());
                mob.setText(dataSnapshot.child("mobile number").getValue().toString());
                mail.setText(dataSnapshot.child("email").getValue().toString());
                usrName.setText(dataSnapshot.child("username").getValue().toString());
                if (dataSnapshot.child("gender").getValue().toString().equals("Male")){
                    rdio.check(R.id.usr_stngs_rdo_male);
                }
                else {
                    rdio.check(R.id.usr_stngs_rdo_female);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        updt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    dataRef.child("Users").child(uid).child("name").setValue(name.getText().toString().trim());
                    dataRef.child("Users").child(uid).child("age").setValue(age.getText().toString().trim());
                    dataRef.child("Users").child(uid).child("address").setValue(ads.getText().toString().trim());
                    if (rdio.getCheckedRadioButtonId()==R.id.usr_stngs_rdo_male){
                        dataRef.child("Users").child(uid).child("gender").setValue("Male");
                    }
                    else {
                        dataRef.child("Users").child(uid).child("gender").setValue("Female");
                    }
                    Toast.makeText(getActivity(), "Profile Has Been Successfully Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
        chnge_pwd.setOnClickListener(new View.OnClickListener() {
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
        cncelAcnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scroll.getForeground().setAlpha(200);
                popup.showAtLocation(scroll, Gravity.CENTER, 0, 0);
                deactOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataRef.child("Admin").child("Doctor Issues").orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        if (deactNum.getText().toString().equals(mob.getText().toString())){
                            dataRef.child("Token Id").orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data:dataSnapshot.getChildren()){
                                        dataRef.child("Token Id").child(data.getKey()).removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            dataRef.child("Id").orderByValue().equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    dataRef.child("Id").child(dataSnapshot.getKey()).getRef().removeValue();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            Toast.makeText(getActivity(), "Successfully Cancelled Your Account", Toast.LENGTH_SHORT).show();
                            dataRef.child("Users").child(uid).removeValue();
                            if ( mAuth.getCurrentUser()!=null) {
                                mAuth.getCurrentUser().delete();
                                startActivity(new Intent(getActivity(),SignInActivity.class));
                                getActivity().finish();
                            }
                        }
                        else{
                            Toast.makeText(getActivity(), "Incorrect Number", Toast.LENGTH_SHORT).show();
                            deactNum.setText(null);
                        }
                    }
                });


            }
        });
    }
    public boolean validate(){
        boolean valid=true;
        if(name.getText().toString().isEmpty()||name.getText().toString().length()>32){
            name.setError("Please Enter a Valid Name");
            valid=false;
        }
        if (age.getText().toString().isEmpty()||age.length()>120){
            age.setError("Please Enter a Valid Age");
            valid=false;
        }
        if(ads.getText().toString().isEmpty()){
            ads.setError("Please Enter a Valid Address");
            valid=false;
        }
        return valid;
    }
}
