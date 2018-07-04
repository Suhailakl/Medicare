package com.jrs.medicare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

/**
 * Created by SUHAIL on 12/28/2017.
 */

public class DoctorNotificationFragment extends Fragment {
    private String uid;
    private DatabaseReference dataRef;
    private FirebaseAuth mAuth;
    private Query mQuery;
    private String remA,refA,docId,from,status;
    private long t;
    private RecyclerView recyclerView;
    private Calendar c;
    private String uId,dId;
    private TextView not;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.doctor_notification_fragment,container,false);
        if (container!=null){
            container.removeAllViews();
        }
        getActivity().setTitle(R.string.ntfction);
        mAuth= FirebaseAuth.getInstance();
        dataRef= FirebaseDatabase.getInstance().getReference();
        uid=mAuth.getCurrentUser().getUid();
        not= (TextView) v.findViewById(R.id.doc_no_not);
        not.setVisibility(View.GONE);
        recyclerView= (RecyclerView) v.findViewById(R.id.doctor_notification_recycle);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        c= Calendar.getInstance();
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
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataRef.child("Doctor Notification").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    if (data.hasChild("referredActive")){
                        data.child("referredActive").getRef().removeValue();
                    }
                    else if (data.hasChild("tokFullActive")){
                        data.child("tokFullActive").getRef().removeValue();
                    }
                    else if (data.hasChild("tActive")){
                        data.child("tActive").getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mQuery=dataRef.child("Doctor Notification").child(uid).orderByChild("time");
        FirebaseRecyclerAdapter<UserNotModel,DoctorNotModelHolder> adapter=new FirebaseRecyclerAdapter<UserNotModel, DoctorNotModelHolder>(
                UserNotModel.class,
                R.layout.user_notification,
                DoctorNotModelHolder.class,
                mQuery
        ) {
            @Override
            public DoctorNotModelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return super.onCreateViewHolder(parent, viewType);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount()==0){
                    not.setVisibility(View.VISIBLE);
                }else {
                    not.setVisibility(View.GONE);
                }
            }

            @Override
            protected void populateViewHolder(final DoctorNotModelHolder viewHolder, final UserNotModel model, int position) {
                viewHolder.itemView.setVisibility(View.GONE);
                refA=model.getReferredActive();
                t=model.getTime();
                from=model.getFrom();
                uId=model.getUid();
                dId=model.getdId();
                final String time=timeChange(t);
                if (from!=null){
                    dataRef.child("Users").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String userName=dataSnapshot.child("name").getValue().toString();
                                dataRef.child("Docters").child(model.getFrom()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String dName=dataSnapshot.getValue().toString();
                                    viewHolder.setNot(userName+" have been referred from Dr."+dName);
                                    viewHolder.setTime(time+" ago");
                                    viewHolder.itemView.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                                    MedicalRecord mR=new MedicalRecord();
                                    Bundle b=new Bundle();
                                    b.putString("refPat","yes");
                                    mR.setArguments(b);
                                    ft.replace(R.id.content_doc_home,mR);
                                    ft.commit();
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else if (dId!=null){
                    dataRef.child("Users").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String dName=dataSnapshot.child("name").getValue().toString();
                            viewHolder.setNot(dName+" Sent You An Image");
                            viewHolder.setTime(time+" ago");
                            viewHolder.itemView.setVisibility(View.VISIBLE);
                            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                                    TestResults t=new TestResults();
                                    ft.replace(R.id.content_doc_home,t);
                                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                    ft.commit();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    viewHolder.setNot("Token Completed on "+model.getDate());
                    viewHolder.setTime(time+" ago");
                    viewHolder.itemView.setVisibility(View.VISIBLE);

                }
                }

        };
        recyclerView.setAdapter(adapter);
    }
    public static class DoctorNotModelHolder extends RecyclerView.ViewHolder{
        public TextView not,time;
        public DoctorNotModelHolder(View itemView) {
            super(itemView);
            not= (TextView) itemView.findViewById(R.id.usr_noT);
            time= (TextView) itemView.findViewById(R.id.usr_noT_time);
        }
        public void setNot(String s){
            not.setText(s);
        }
        public void setTime(String t){
            time.setText(t);
        }
    }
    public String timeChange(long ts){
        String time="";
        long curTime=c.getTimeInMillis();
        long tImi=curTime-ts;
        long tIs=tImi/1000;

        if (tIs<60){
            time="moments";
        }
        else
        {
            long tIm=tIs/60;
            if (tIm<60){
                time=String.valueOf(tIm)+"m";
            }
            else{
                long tIh=tIm/60;
                if (tIh<24){
                    time=String.valueOf(tIh)+"h";
                }
                else {
                    long tId=tIh/24;
                    if (tId<365) {
                        time = String.valueOf(tId) + "day";
                    }
                    else {
                        long tIy=tId/365;
                        time=String.valueOf(tIy) + "year";
                    }
                }
            }
        }
        return time;
     }
    }
