package com.jrs.medicare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by SUHAIL on 11/22/2017.
 */

public class UserNotificationFragment extends Fragment {
    private String uid;
    private DatabaseReference dataRef;
    private FirebaseAuth mAuth;
    private Query mQuery;
    private String remA,refA,docId,from,status;
    private long t;
    private RecyclerView recyclerView;
    private Calendar c;
    private TextView not;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.user_notification_fragment,container,false);
        if (container!=null){
            container.removeAllViews();
        }
        getActivity().setTitle(R.string.ntfction);
        mAuth=FirebaseAuth.getInstance();
        dataRef= FirebaseDatabase.getInstance().getReference();
        uid=mAuth.getCurrentUser().getUid();
        recyclerView= (RecyclerView) v.findViewById(R.id.user_notification_recycle);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        c=Calendar.getInstance();
        not= (TextView) v.findViewById(R.id.user_no_not);
       return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataRef.child("User Notification").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    if (data.hasChild("referredActive")){
                        data.child("referredActive").getRef().removeValue();
                    }
                    else if (data.hasChild("removedActive")){
                        data.child("removedActive").getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mQuery=dataRef.child("User Notification").child(uid).orderByChild("time");
        FirebaseRecyclerAdapter<UserNotModel,UserNotModelHolder> adapter=new FirebaseRecyclerAdapter<UserNotModel, UserNotModelHolder>(
                UserNotModel.class,
                R.layout.user_notification,
                UserNotModelHolder.class,
                mQuery
        ) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount()==0){
                    not.setVisibility(View.VISIBLE);
                }
                else {
                    not.setVisibility(View.GONE);
                }
            }

            @Override
            protected void populateViewHolder(final UserNotModelHolder viewHolder, UserNotModel model, int position) {
                viewHolder.itemView.setVisibility(View.GONE);
                remA=model.getRemovedActive();
                refA=model.getReferredActive();
                t=model.getTime();
                from=model.getFrom();
                status=model.getStatus();
                docId=model.getDoctorId();
                final String time=timeChange(t);
                if (from!=null){
                    dataRef.child("Docters").child(docId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                              viewHolder.setNot("You have been referred to Dr."+dataSnapshot.child("name").getValue().toString());
                              viewHolder.setTime(time+" ago");
                            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getActivity(), "Please book the doctor", Toast.LENGTH_SHORT).show();
                                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                                    BookDocFragment f=new BookDocFragment();
                                    Bundle b=new Bundle();
                                    b.putString("uid",docId);
                                    b.putString("notBack","notBack");
                                    f.setArguments(b);
                                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                    ft.replace(R.id.content_user_home,f);
                                    ft.addToBackStack("null");
                                    ft.commit();
                                }
                            });
                            viewHolder.itemView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else {
                    if (status.equals("record")){
                        viewHolder.setNot("Your Consulted Dr."+model.getDocName()+" has been removed");
                        viewHolder.setTime(time+" ago");
                        viewHolder.itemView.setVisibility(View.VISIBLE);
                    }
                    else {
                        viewHolder.setNot("Your Booked Dr."+model.getDocName()+" has been removed");
                        viewHolder.setTime(time+" ago");
                        viewHolder.itemView.setVisibility(View.VISIBLE);

                    }
                }

            }
        };
        recyclerView.setAdapter(adapter);
    }
    public static class UserNotModelHolder extends RecyclerView.ViewHolder{
        public TextView not,time;
        public UserNotModelHolder(View itemView) {
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
