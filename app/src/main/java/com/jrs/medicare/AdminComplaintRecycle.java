package com.jrs.medicare;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
 * Created by SUHAIL on 11/19/2017.
 */

public class AdminComplaintRecycle extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference dataRef;
    private String uid;
    private RecyclerView recycle;
    private Query mQuery;
    private Calendar c;
    private SearchView search;
    private MenuItem searchItem,notItem;
    private TextView count;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AdminHomeClass ad=new AdminHomeClass();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.admin_complaint_view,ad);
                ft.commit();
                return true;
            default:
                return false;
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.admin_home,menu);
        searchItem=menu.findItem(R.id.action_admin_search);
        notItem=menu.findItem(R.id.action_admin_notification);
        View c=menu.findItem(R.id.action_admin_notification).getActionView();
        count= (TextView) c.findViewById(R.id.admin_notification_count);
        count.setText("0");
        notification();
        ImageView r= (ImageView) c.findViewById(R.id.admin_notification_count_icon);
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft=getFragmentManager().beginTransaction();
                AdminComplaintRecycle fragment=new AdminComplaintRecycle();
                ft.replace(R.id.admin_complaint_view,fragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });
        SearchManager searchManager= (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        search= (SearchView) menu.findItem(R.id.action_admin_search).getActionView();
        search.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        search.setQueryHint("Search Doctors by name");
        search.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                AdminSearchFragment fr = new AdminSearchFragment();
                Bundle b=new Bundle();
                b.putString("fr","cR");
                fr.setArguments(b);
                ft.replace(R.id.admin_home, fr);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        mQuery=dataRef.child("Admin").child("Doctor Issues").orderByChild("timeStamp");
        FirebaseRecyclerAdapter<ComplaintModel,AdminComplaintHolder> adapter=new FirebaseRecyclerAdapter<ComplaintModel, AdminComplaintHolder>(
                ComplaintModel.class,
                R.layout.admin_complaint_view_recylce,
                AdminComplaintHolder.class,
                mQuery
        ) {
            @Override
            protected void populateViewHolder(final AdminComplaintHolder viewHolder, final ComplaintModel model, final int position) {
                String pId=model.getUid();
                final String dId=model.getDocKey();
                dataRef.child("Users").child(pId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String pNme=dataSnapshot.child("name").getValue().toString();
                        dataRef.child("Docters").child(dId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                viewHolder.setPatient(pNme+" Reported against Dr."+dataSnapshot.child("name").getValue().toString());
                                viewHolder.setIssue(model.getIssue());
                                viewHolder.setTime(timeChange(model.getTimeStamp())+" ago");
                                if (model.getActive()!=null){
                                    viewHolder.itemView.setBackgroundColor(Color.YELLOW);
                                }
                                recycle.setVisibility(View.VISIBLE);

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
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id=getRef(position).getKey();
                        FragmentTransaction ft=getFragmentManager().beginTransaction();
                        AdminComplaint ad=new AdminComplaint();
                        Bundle b=new Bundle();
                        b.putString("id",id);
                        ad.setArguments(b);
                        ft.replace(R.id.admin_home,ad);
                        ft.addToBackStack(null);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                    }
                });
            }
        };
        recycle.setAdapter(adapter);

        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.admin_complaint_view,container,false);
        if (container!=null){
            container.removeAllViews();
        }
        getActivity().setTitle(R.string.cmplnts);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        dataRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        recycle= (RecyclerView) v.findViewById(R.id.admin_complaint_recycle);
        recycle.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(llm);
        recycle.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recycle.setVisibility(View.INVISIBLE);
        c=Calendar.getInstance();
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
    public static class AdminComplaintHolder extends RecyclerView.ViewHolder{
public TextView patient,issue,time;
        public AdminComplaintHolder(View itemView) {
            super(itemView);
            patient= (TextView) itemView.findViewById(R.id.admin_complaint_recycle_patient);
            issue= (TextView) itemView.findViewById(R.id.admin_complaint_recycle_complaint);
            time= (TextView) itemView.findViewById(R.id.admin_complaint_recycle_ago);
        }
        public void setPatient(String s){
            patient.setText(s);
        }
        public void setIssue(String s){
            issue.setText(s);
        }
        public void setTime(String s){
            time.setText(s);
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
