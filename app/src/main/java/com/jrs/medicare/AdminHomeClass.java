package com.jrs.medicare;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
 * Created by SUHAIL on 11/20/2017.
 */

public class AdminHomeClass extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference dataRef,ref;
    private String uid;
    private Query mQuery;
    private RecyclerView recycle;
    private Calendar c;
    private long time;
    private MenuItem searchItem,notItem;
    private TextView count,noDr;
    private SearchView search;
    private FirebaseRecyclerAdapter<AdminVerifyRecycler,VerifyRecylerHolder> adapter;
    private  FirebaseRecyclerAdapter<Doc,DoctokAdminHolder> a;

    @Override
    public void onStart() {
        super.onStart();
        getView().setBackgroundColor(Color.WHITE);
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
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft=getFragmentManager().beginTransaction();
                AdminComplaintRecycle fragment=new AdminComplaintRecycle();
                ft.replace(R.id.admin_home,fragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });
        View v=menu.findItem(R.id.action_admin_signout).getActionView();
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(),SignInActivity.class));
                getActivity().finish();
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
                                                b.putString("fr","home");
                                                fr.setArguments(b);
                                                ft.replace(R.id.admin_home, fr);
                                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                                ft.addToBackStack(null);
                                                ft.commit();
                                            }
                                        });

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.activity_admin_home,container,false);
        if (container!=null){
            container.removeAllViews();
        }
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.home);
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(false);
        mAuth=FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        dataRef= FirebaseDatabase.getInstance().getReference().child("Temporary Doctors");
        recycle= (RecyclerView) v.findViewById(R.id.admin_dr_vrfy_recycler);
        recycle.setHasFixedSize(true);
        ref= FirebaseDatabase.getInstance().getReference();
        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(llm);
        recycle.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        noDr= (TextView) v.findViewById(R.id.admin_home_noDr);
        noDr.setVisibility(View.GONE);
        c=Calendar.getInstance();
        v.requestFocus();
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                       getActivity().finish();
                    }

                }
                return false;
            }
        });
        recycler();
        return v;
    }
    public void recycler(){
        mQuery=dataRef.orderByChild("timeStamp");
        adapter=new FirebaseRecyclerAdapter<AdminVerifyRecycler, VerifyRecylerHolder>(
                AdminVerifyRecycler.class,
                R.layout.admin_dr_vrfy_recycle,
                VerifyRecylerHolder.class,
                mQuery
        )
        {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount()==0){
                    noDr.setVisibility(View.VISIBLE);
                }else {
                    noDr.setVisibility(View.GONE);
                }
            }

            @Override
            protected void populateViewHolder(VerifyRecylerHolder viewHolder, final AdminVerifyRecycler model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setCity(model.getCity());
                time=model.getTimeStamp();
                viewHolder.setTimeStamp(timeChange(time)+" ago");
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentTransaction ft=getFragmentManager().beginTransaction();
                        Fragment ad=new AdminVerifyReqstView();
                        Bundle b=new Bundle();
                        b.putString("id",model.getUid());
                        ad.setArguments(b);
                        ft.replace(R.id.admin_home,ad);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });
            }

        };
        recycle.setAdapter(adapter);
    }
    public static class VerifyRecylerHolder extends RecyclerView.ViewHolder{
        public TextView name,city,ago;
        public VerifyRecylerHolder(View itemView) {
            super(itemView);
            name= (TextView) itemView.findViewById(R.id.admin_vrfy_recycle_drName);
            city= (TextView) itemView.findViewById(R.id.admin_vrfy_recycle_city);
            ago= (TextView) itemView.findViewById(R.id.admin_vrfy_recycle_ago);
        }
        public void setName(String s){
            name.setText(s);
        }
        public void setCity(String s){
            city.setText(s);
        }
        public void setTimeStamp(String s){
            ago.setText(s);
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
    public static class DoctokAdminHolder extends RecyclerView.ViewHolder{
        TextView textName,textDpt,textCity,textCnD,d1,d2,d3,d4,d5,d6,d7;
        private String id;

        public DoctokAdminHolder(final View itemView) {
            super(itemView);
            textName= (TextView) itemView.findViewById(R.id.t1);
            textCnD= (TextView) itemView.findViewById(R.id.t2);
            d1= (TextView) itemView.findViewById(R.id.d1);
            d2= (TextView) itemView.findViewById(R.id.d2);
            d3= (TextView) itemView.findViewById(R.id.d3);
            d4= (TextView) itemView.findViewById(R.id.d4);
            d5= (TextView) itemView.findViewById(R.id.d5);
            d6= (TextView) itemView.findViewById(R.id.d6);
            d7= (TextView) itemView.findViewById(R.id.d7);
            textCity= (TextView) itemView.findViewById(R.id.nearBy);

        }

        public void setName(String name) {
            textName.setText(name);
        }

        public void setDepartment(String department) {
            textCnD.setText(department);
        }
        public void setCity(String city){
            textCity.setText(city);
        }
        public void setDay1(String day1)
        {
            d1.setText(day1);
            d1.setBackgroundResource(R.drawable.text_view_border);
        }
        public void setDay2(String day2)
        {
            d2.setText(day2);
            d2.setBackgroundResource(R.drawable.text_view_border);
        }
        public void setDay3(String day3)
        {
            d3.setText(day3);
            d3.setBackgroundResource(R.drawable.text_view_border);
        }
        public void setDay4(String day4)
        {
            d4.setText(day4);
            d4.setBackgroundResource(R.drawable.text_view_border);
        }
        public void setDay5(String day5)
        {
            d5.setText(day5);
            d5.setBackgroundResource(R.drawable.text_view_border);
        }
        public void setDay6(String day6)
        {
            d6.setText(day6);
            d6.setBackgroundResource(R.drawable.text_view_border);
        }
        public void setDay7(String day7)
        {
            d7.setText(day7);
            d7.setBackgroundResource(R.drawable.text_view_border);
        }

    }
    public void notification(){
        ref.child("Admin").child("Doctor Issues").addValueEventListener(new ValueEventListener() {
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
