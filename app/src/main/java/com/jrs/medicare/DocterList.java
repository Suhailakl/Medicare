package com.jrs.medicare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DocterList extends android.support.v4.app.Fragment {
    private DatabaseReference dataref;
    private RecyclerView recyclerView;
    private List<Doc> result;
    private DocViewHolder adapter;
    private String cdt;
    private Query mQuery;
    private int f;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_docter_list, container, false);
        if (container!=null){
            container.removeAllViews();
        }
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentHome fh=new FragmentHome();
                        FragmentTransaction ft=getFragmentManager().beginTransaction();
                        ft.replace(R.id.activity_fragment_home,fh);
                        ft.commit();
                        return true;
                    }
                }
                return false;
            }
        });
        dataref = FirebaseDatabase.getInstance().getReference().child("Docters");
        result = new ArrayList<>();
        recyclerView = (RecyclerView) v.findViewById(R.id.docter_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        Bundle bundle=this.getArguments();
        if(getArguments()!=null) {
            cdt = bundle.getString("item");
        }

        dataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                boolean city=false;
                for (DataSnapshot data:dataSnapshot.getChildren()){
                if (data.hasChild("city")&&cdt.equals(data.child("city").getValue())){
                        city=true;
                    }
                }
                if(city){
                    mQuery=dataref.orderByChild("city").equalTo(cdt);
                    recyclerView.addItemDecoration(new VerticalItemDecoration(20,false));
                    FirebaseRecyclerAdapter<Doc,DocViewHolder> adapter=new FirebaseRecyclerAdapter<Doc, DocViewHolder>(
                            Doc.class,
                            R.layout.view_docter,
                            DocViewHolder.class,
                            mQuery
                    ) {
                        @Override
                        protected void populateViewHolder(final DocViewHolder viewHolder, final Doc model, final int position) {
                            viewHolder.setName("Dr." + model.getName());
                            final String st=mQuery.getRef().getParent().toString();
                            viewHolder.setDepartment(model.getDepartment());
                            viewHolder.setCity(model.getCity());
                            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FragmentTransaction ft=getParentFragment().getFragmentManager().beginTransaction();
                                    BookDocFragment fragment=new BookDocFragment();
                                    Bundle b=new Bundle();
                                    b.putString("uid",model.getUid());
                                    fragment.setArguments(b);
                                    ft.replace(R.id.content_user_home,fragment);
                                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                    ft.addToBackStack(null);
                                    ft.commit();
                                }
                            });
                            String s = model.getDay1();
                            if (s!=null) {
                            if (s.equals("Sunday")) {
                                viewHolder.setDay1("S");
                            }
                            }
                            String m = model.getDay2();
                            if (m!=null) {
                                if (m.equals("Monday")) {
                                    viewHolder.setDay2("M");
                                }
                            }
                            String t=model.getDay3();
                            if (t!=null){
                                if (t.equals("Tuesday")) {
                                    viewHolder.setDay3("T");
                                }
                            }
                            String w=model.getDay4();
                            if (w!=null){
                                if (w.equals("Wednsday")) {
                                    viewHolder.setDay4("W");
                                }
                            }
                            String th=model.getDay5();
                            if (th!=null){
                                if (th.equals("Thirsday")) {
                                    viewHolder.setDay5("T");
                                }
                            }
                            String f=model.getDay6();
                            if (f!=null){
                                if (f.equals("Friday")) {
                                    viewHolder.setDay6("F");
                                }
                            }
                            String sa=model.getDay7();
                            if (sa!=null){
                                if (sa.equals("Saturday")) {
                                    viewHolder.setDay7("S");
                                }
                            }

                        }
                    };
                    recyclerView.setAdapter(adapter);
                }
                else {
                    mQuery=dataref.orderByChild("department").equalTo(cdt);
                    recyclerView.addItemDecoration(new VerticalItemDecoration(20,false));
                    final FirebaseRecyclerAdapter<Doc,DocViewHolder> adapter=new FirebaseRecyclerAdapter<Doc, DocViewHolder>(
                            Doc.class,
                            R.layout.view_docter,
                            DocViewHolder.class,
                            mQuery
                    ) {

                        @Override
                        protected void populateViewHolder(DocViewHolder viewHolder, final Doc model, int position) {
                            viewHolder.setName("Dr."+model.getName());
                            viewHolder.setDepartment(model.getDepartment());
                            viewHolder.setCity(model.getCity());
                            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FragmentTransaction ft=getParentFragment().getFragmentManager().beginTransaction();
                                    BookDocFragment fragment=new BookDocFragment();
                                    Bundle b=new Bundle();
                                    b.putString("uid",model.getUid());
                                    fragment.setArguments(b);
                                    ft.replace(R.id.content_user_home,fragment);
                                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                    ft.addToBackStack(null);
                                    ft.commit();
                                }
                            });
                            String s = model.getDay1();
                            if (s!=null) {
                                if (s.equals("Sunday")) {
                                    viewHolder.setDay1("S");
                                }
                            }
                            String m = model.getDay2();
                            if (m!=null) {
                                if (m.equals("Monday")) {
                                    viewHolder.setDay2("M");
                                }
                            }
                            String t=model.getDay3();
                            if (t!=null){
                                if (t.equals("Tuesday")) {
                                    viewHolder.setDay3("T");
                                }
                            }
                            String w=model.getDay4();
                            if (w!=null){
                                if (w.equals("Wednsday")) {
                                    viewHolder.setDay4("W");
                                }
                            }
                            String th=model.getDay5();
                            if (th!=null){
                                if (th.equals("Thirsday")) {
                                    viewHolder.setDay5("T");
                                }
                            }
                            String f=model.getDay6();
                            if (f!=null){
                                if (f.equals("Friday")) {
                                    viewHolder.setDay6("F");
                                }
                            }
                            String sa=model.getDay7();
                            if (sa!=null){
                                if (sa.equals("Saturday")) {
                                    viewHolder.setDay7("S");
                                }
                            }
                        }
                        @Override
                        public void onDataChanged() {
                            super.onDataChanged();
                        }


                        @Override
                        public Doc getItem(int position) {
                            return super.getItem(position);
                        }
                    };

                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }
    public static class DocViewHolder extends RecyclerView.ViewHolder{
        TextView textName,textDpt,textCity,textCnD,d1,d2,d3,d4,d5,d6,d7;
        private String id;

        public DocViewHolder(final View itemView) {
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


}