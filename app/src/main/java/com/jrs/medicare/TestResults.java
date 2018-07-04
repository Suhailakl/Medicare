package com.jrs.medicare;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
 * Created by SUHAIL on 11/10/2017.
 */

public class TestResults extends Fragment {
    private DatabaseReference dataRef;
    private FirebaseAuth mAuth;
    Query mQuery;
    private RecyclerView recycle;
    private String uid;
    private long time;
    private Calendar c;
    private ProgressBar pro;
    private TextView no;
    private MenuItem item;
    private TextView count;
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
    }

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQuery=dataRef.child("Test Results").orderByChild("key").startAt(uid).endAt(uid+"\uf8ff");
        FirebaseRecyclerAdapter<TestResultsModel,TestResultViewHolder> adapter=new FirebaseRecyclerAdapter<TestResultsModel, TestResultViewHolder>(
                TestResultsModel.class,
                R.layout.test_results_recycle,
                TestResultViewHolder.class,
                mQuery

        ) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount()==0){
                    no.setVisibility(View.VISIBLE);
                    pro.setVisibility(View.GONE);
                }
            }

            @Override
            protected void populateViewHolder(TestResultViewHolder viewHolder, final TestResultsModel model, int position) {
                viewHolder.setImage(getContext(),model.getImage());
                viewHolder.setDate("Consulted On "+model.getDate());
                viewHolder.setObj("Subject : "+model.getSubject());
                viewHolder.setName("Send By "+model.getName());
                time=model.getTime();
                viewHolder.setTime(timeChange(time)+" ago");
                if (model.getReferred()!=null){
                    viewHolder.setRfr();
                }
                pro.setVisibility(View.GONE);
                final String k=model.getmRkey();
                viewHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentTransaction ft=getFragmentManager().beginTransaction();
                        ImageViewer iv=new ImageViewer();
                        Bundle b=new Bundle();
                        b.putString("url",model.getImage());
                        b.putString("tstRslt","yes");
                        iv.setArguments(b);
                        ft.replace(R.id.test_results,iv);
                        ft.addToBackStack("value");
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.commit();
                    }
                });

                viewHolder.name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        MedicalRecordView Mr=new MedicalRecordView();
                        Bundle b=new Bundle();
                        b.putString("key",k);
                        b.putString("backTest","yes");
                        Mr.setArguments(b);
                        ft.replace(R.id.test_results,Mr);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });
            }
        };
        recycle.setAdapter(adapter);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container!=null){
            container.removeAllViews();
        }
        getActivity().setTitle(R.string.tstReslt);
        setHasOptionsMenu(true);
        View v=inflater.inflate(R.layout.activity_test_results,container,false);
        dataRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        recycle= (RecyclerView) v.findViewById(R.id.test_result_recycler_view);
        recycle.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(llm);
        c=Calendar.getInstance();
        pro= (ProgressBar) v.findViewById(R.id.test_result_pro);
        no= (TextView) v.findViewById(R.id.test_result_noTest);
        no.setVisibility(View.GONE);
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
    public  static class TestResultViewHolder extends RecyclerView.ViewHolder{
        public ImageView image,rfrIcon;
        View mView;
        public TextView name,time,date,sbj,ago,rfr;
        public TestResultViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

            name= (TextView) itemView.findViewById(R.id.test_rslt_recycle_sndBy);
            time= (TextView) itemView.findViewById(R.id.test_rslt_recycle_ago);
            date= (TextView) itemView.findViewById(R.id.test_rslt_recycle__cnsltOn);
            sbj= (TextView) itemView.findViewById(R.id.test_rslt_recycle_sbjct);
            ago= (TextView) itemView.findViewById(R.id.test_rslt_recycle_ago);
            rfr= (TextView) itemView.findViewById(R.id.test_rslt_is_rfrd);
            rfrIcon= (ImageView) itemView.findViewById(R.id.test_rslt_is_rfrd_icon);

        }
        public void setRfr(){
            rfr.setVisibility(View.VISIBLE);
            rfrIcon.setVisibility(View.VISIBLE);
        }
        public void setImage(Context c,String url){
            image= (ImageView) mView.findViewById(R.id.test_result_image);
            Glide.with(c).load(url).into(image);
        }
        public void setName(String s){
            name.setText(s);
        }
        public void  setDate(String d){
            date.setText(d);
        }
        public void setObj(String sb){
            sbj.setText(sb);
        }
        public void setTime(String t){
            ago.setText(t);
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
    public void notification(){
        dataRef.child("Doctor Notification").child(uid).addValueEventListener(new ValueEventListener() {
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
