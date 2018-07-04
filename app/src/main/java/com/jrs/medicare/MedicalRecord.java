package com.jrs.medicare;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MedicalRecord extends Fragment {
    private FirebaseAuth mAuth;
    private String user,refPat;
    private DatabaseReference dataRef;
    private Query mQery;
    private RecyclerView recyclerView;
    private TextView noMr,noUser,noRef,count;
    private ProgressBar progress;
    private MenuItem item;
    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
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
        inflater.inflate(R.menu.search,menu);
        item=menu.findItem(R.id.action_search);
        try {
            // Associate searchable configuration with the SearchView
            SearchManager searchManager =
                    (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            final SearchView searchView =
                    (SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setQueryHint("Search By Username");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    progress.setVisibility(View.VISIBLE);
                    mQery = dataRef.child("Medical Record").orderByChild("docKey").equalTo(user+s);
                    FirebaseRecyclerAdapter<MediRec, MediRecViewHolder> adapter = new FirebaseRecyclerAdapter<MediRec, MediRecViewHolder>(
                            MediRec.class,
                            R.layout.activity_medical_record_recycle,
                            MediRecViewHolder.class,
                            mQery
                    ) {
                        @Override
                        protected void populateViewHolder(MediRecViewHolder viewHolder, final MediRec model, final int position) {
                            progress.setVisibility(View.GONE);
                            viewHolder.setName(model.getPatient());
                            viewHolder.setDate(model.getDate());
                            viewHolder.setUserId(model.getUserId());
                            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                                    MedicalRecordView mR=new MedicalRecordView();
                                    Bundle b=new Bundle();
                                    String id=getRef(position).getKey();
                                    b.putString("key",id);
                                    mR.setArguments(b);
                                    ft.replace(R.id.activity_medical_record,mR);
                                    ft.addToBackStack(null);
                                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                    ft.commit();

                                }
                            });
                        }

                        @Override
                        public void onDataChanged() {
                            super.onDataChanged();
                        }
                    };

                    recyclerView.setAdapter(adapter);
                    return false;
                }

                @Override
            public boolean onQueryTextChange(String s) {
                    if (s.isEmpty()){
                        recycler();
                    }
                    return false;
                }
            });

        }catch(Exception e){e.printStackTrace();}
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_medical_record, container, false);
        if (container!=null){
            container.removeAllViews();
        }
        setHasOptionsMenu(true);
        progress= (ProgressBar) v.findViewById(R.id.mR_progress);
        noMr= (TextView) v.findViewById(R.id.no_mR);
        noUser= (TextView) v.findViewById(R.id.mR_noUser);
        noRef= (TextView) v.findViewById(R.id.mR_noRef);
        noRef.setVisibility(View.GONE);
        noUser.setVisibility(View.GONE);
        noMr.setVisibility(View.GONE);
        recyclerView = (RecyclerView) v.findViewById(R.id.mR_recycle);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();
        dataRef = FirebaseDatabase.getInstance().getReference();
        Bundle b=this.getArguments();
        if (getArguments()!=null){
            refPat=b.getString("refPat");
            getActivity().setTitle(R.string.rfrl);
        }
        else {
            getActivity().setTitle(R.string.record);
        }
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
        super.onViewCreated(view, savedInstanceState);
        recycler();

    }
    public void recycler(){
  if (refPat==null) {
      mQery = dataRef.child("Medical Record").orderByChild("doctorId").equalTo(user);
  }else {
      mQery=dataRef.child("Medical Record").orderByChild("refKey").equalTo(user+"yes");

  }
        FirebaseRecyclerAdapter<MediRec, MediRecViewHolder> adapter = new FirebaseRecyclerAdapter<MediRec, MediRecViewHolder>(
                MediRec.class,
                R.layout.activity_medical_record_recycle,
                MediRecViewHolder.class,
                mQery
        ) {
            @Override
            protected void populateViewHolder(final MediRecViewHolder viewHolder, MediRec model, final int position) {
                progress.setVisibility(View.GONE);
                viewHolder.setName(model.getPatient());
                viewHolder.setDate(model.getDate());
                String u=model.getUid();
                dataRef.child("Usernames").child(u).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.setUserId(dataSnapshot.getValue().toString().trim());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentTransaction ft=getFragmentManager().beginTransaction();
                        MedicalRecordView mR=new MedicalRecordView();
                        Bundle b=new Bundle();
                        String id=getRef(position).getKey();
                        b.putString("key",id);
                        mR.setArguments(b);
                        ft.replace(R.id.activity_medical_record,mR);
                        ft.addToBackStack(null);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();

                    }
                });
            }

            @Override
            public void onDataChanged() {
                if (getItemCount()==0){
                    progress.setVisibility(View.GONE);
                    if (refPat!=null){
                        noRef.setVisibility(View.VISIBLE);
                    }else {
                        noMr.setVisibility(View.VISIBLE);
                    }
                    item.setVisible(false);
                }
                else {
                    noMr.setVisibility(View.GONE);
                    noRef.setVisibility(View.GONE);
                    item.setVisible(true);
                }
                super.onDataChanged();
            }
        };
        recyclerView.setAdapter(adapter);
    }
    public  static class MediRecViewHolder extends RecyclerView.ViewHolder{
        TextView name,dateText,userIdText;
        public MediRecViewHolder(View itemView) {
            super(itemView);
            name= (TextView) itemView.findViewById(R.id.mRR_name);
            dateText= (TextView) itemView.findViewById(R.id.mRR_date);
            userIdText= (TextView) itemView.findViewById(R.id.mRR_uid);
        }
        public void setName(String patient){
            name.setText(patient);
        }
        public void setDate(String date){
            dateText.setText(date);
        }
        public void setUserId(String userId){
            userIdText.setText(userId);
        }
    }
    public void notification(){
        dataRef.child("Doctor Notification").child(user).addValueEventListener(new ValueEventListener() {
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

