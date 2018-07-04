package com.jrs.medicare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

/**
 * Created by SUHAIL on 11/19/2017.
 */

public class AdminComplaint extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference dataRef;
    private String uid,id;
    private RecyclerView recycle;
    private Query mQuery;
    private Calendar c;
    private TextView pat,doc,city,regno,issue,cmplnt;
    private SearchView search;
    private MenuItem searchItem,notItem;
    private TextView count;
    private Button viewBtn;
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

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v=inflater.inflate(R.layout.admin_complaint,container,false);
        if (container!=null){
            container.removeAllViews();
        }
        getActivity().setTitle(R.string.cmplnt);
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
        dataRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        pat= (TextView) v.findViewById(R.id.admin_complaint_patient);
        doc= (TextView) v.findViewById(R.id.admin_complaint_doc);
        city= (TextView) v.findViewById(R.id.admin_complaint_city);
        regno= (TextView) v.findViewById(R.id.admin_complaint_regno);
        issue= (TextView) v.findViewById(R.id.admin_complaint_brief);
        cmplnt= (TextView) v.findViewById(R.id.admin_complaint);
        viewBtn= (Button) v.findViewById(R.id.admin_complaint_goto_btn);
        setHasOptionsMenu(true);
        Bundle b=this.getArguments();
        if (getArguments()!=null){
            id=b.getString("id");
        }
        dataRef.child("Admin").child("Doctor Issues").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String docKey=dataSnapshot.child("docKey").getValue().toString();
                String user=dataSnapshot.child("uid").getValue().toString();
                final String isu=dataSnapshot.child("issue").getValue().toString();
                final String comnplaint=dataSnapshot.child("complaint").getValue().toString();
                if (dataSnapshot.hasChild("active")){
                    dataSnapshot.child("active").getRef().removeValue();
                }
                dataRef.child("Users").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        pat.setText(dataSnapshot.child("name").getValue().toString());
                        dataRef.child("Docters").child(docKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                doc.setText("Dr.");
                                doc.append(dataSnapshot.child("name").getValue().toString());
                                city.setText(dataSnapshot.child("city").getValue().toString());
                                regno.setText(dataSnapshot.child("registration number").getValue().toString());
                                issue.setText(isu);
                                cmplnt.setText(comnplaint);
                                viewBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        FragmentTransaction ft=getFragmentManager().beginTransaction();
                                        AdminVerifyReqstView av=new AdminVerifyReqstView();
                                        Bundle b=new Bundle();
                                        b.putString("ud",docKey);
                                        av.setArguments(b);
                                        ft.replace(R.id.admin_home,av);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }
                                });
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return v;
    }
    public void notification(){
        dataRef.child("Admin").child("Doctor Issues").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    if (data.hasChild("active")){
                        i=i+1;
                    }
                    count.setText(String.valueOf(i));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
