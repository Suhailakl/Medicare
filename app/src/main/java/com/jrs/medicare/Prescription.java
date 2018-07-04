package com.jrs.medicare;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Prescription extends Fragment{
    private String key;
    private TextView name,regno,date,dpt,prspn;
    private DatabaseReference dataRef;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container!=null){
            container.removeAllViews();
        }
        getActivity().setTitle(R.string.prscptn);
        InputMethodManager in=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        View v= inflater.inflate(R.layout.activity_prescription,container,false);
        Bundle b=this.getArguments();
        if (getArguments()!=null){
            key=b.getString("key");
        }
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        UserPrescriptionView up=new UserPrescriptionView();
                        ft.replace(R.id.content_user_home,up);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                        return true;
                    }
                }
                return false;
            }
        });
        name= (TextView) v.findViewById(R.id.user_prscptn_name);
        regno= (TextView) v.findViewById(R.id.user_prscptn_regNo);
        date= (TextView) v.findViewById(R.id.user_prscptn_date);
        dpt= (TextView) v.findViewById(R.id.user_prscptn_dptmt);
        prspn= (TextView) v.findViewById(R.id.user_prscptn);
        dataRef= FirebaseDatabase.getInstance().getReference();
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataRef.child("Medical Record").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("prescription").getValue().toString()!=null) {
                    name.setText(dataSnapshot.child("doctor").getValue().toString());
                    date.setText(dataSnapshot.child("date").getValue().toString());
                    prspn.setText(dataSnapshot.child("prescription").getValue().toString());
                            regno.setText(dataSnapshot.child("registration number").getValue().toString());
                            dpt.setText(dataSnapshot.child("department").getValue().toString());
                }else {
                    prspn.setText(null);
                    name.setText(dataSnapshot.child("doctor").getValue().toString());
                    date.setText(dataSnapshot.child("date").getValue().toString());
                    dataRef.child("Docters").child(dataSnapshot.child("dId").getValue().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            regno.setText(dataSnapshot.child("registration number").getValue().toString());
                            dpt.setText(dataSnapshot.child("department").getValue().toString());
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
