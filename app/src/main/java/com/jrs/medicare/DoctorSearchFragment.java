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
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Calendar;

/**
 * Created by SUHAIL on 11/20/2017.
 */

public class DoctorSearchFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private String uid,back,mrView,uH;
    private Query mQuery;
    private RecyclerView recycle;
    private Calendar c;
    private long time;
    private MenuItem searchItem,notItem;
    private TextView count,noDr;
    private SearchView search;
    private  FirebaseRecyclerAdapter<Doc,DoctokAdminHolder> a;
    @Override
    public void onStart() {
        super.onStart();
        getView().setBackgroundColor(Color.WHITE);
    }
    @Override
    public void onDestroyView() {
        search.onActionViewCollapsed();
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.admin_search_layout,container,false);
        InputMethodManager in=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null) {
            in.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (container!=null){
            container.removeAllViews();
        }
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        mAuth= FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        ref= FirebaseDatabase.getInstance().getReference();
        recycle= (RecyclerView) v.findViewById(R.id.admin_search_recycle);
        recycle.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycle.setLayoutManager(llm);
        recycle.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        Bundle b=this.getArguments();
        if (getArguments()!=null){
            mrView=b.getString("mr");
            uH=b.getString("uH");
        }
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        back();
                    }

                }
                return false;
            }
        });
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                back();
                return true;
            default:
                return false;
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.search,menu);
        searchItem=menu.findItem(R.id.action_search);
        SearchManager searchManager= (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        search= (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        search.setQueryHint("Search Doctors by name");
        search.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuery=ref.child("Docters").orderByChild("name");
                a=new FirebaseRecyclerAdapter<Doc, DoctokAdminHolder>(
                        Doc.class,
                        R.layout.view_docter,
                        DoctokAdminHolder.class,
                        mQuery
                ) {
                    @Override
                    protected void populateViewHolder(DoctokAdminHolder viewHolder, final Doc model, int position) {
                        viewHolder.setName("Dr." + model.getName());
                        final String st=mQuery.getRef().getParent().toString();
                        viewHolder.setDepartment(model.getDepartment());
                        viewHolder.setCity(model.getCity());
                        final String uid=getRef(position).getKey();
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                    if (uid.equals(mAuth.getCurrentUser().getUid())) {
                                        Toast.makeText(getActivity(), "Refferel Not Allowed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        MedicalRecordView fragment = new MedicalRecordView();
                                        Bundle b = new Bundle();
                                        b.putString("uid", model.getUid());
                                        b.putString("key", mrView);
                                        fragment.setArguments(b);
                                        ft.replace(R.id.content_doc_home, fragment);
                                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }
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
                recycle.setAdapter(a);
            }
        });
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                back();
                return false;
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                mQuery=ref.child("Docters").orderByChild("name").startAt(newText).endAt(newText+"\uf8ff");
                a=new FirebaseRecyclerAdapter<Doc, DoctokAdminHolder>(
                        Doc.class,
                        R.layout.view_docter,
                        DoctokAdminHolder.class,
                        mQuery
                ) {
                    @Override
                    protected void populateViewHolder(DoctokAdminHolder viewHolder, final Doc model, int position) {
                        viewHolder.setName("Dr." + model.getName());
                        final String st=mQuery.getRef().getParent().toString();
                        viewHolder.setDepartment(model.getDepartment());
                        viewHolder.setCity(model.getCity());
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    MedicalRecordView fragment = new MedicalRecordView();
                                    Bundle b = new Bundle();
                                    b.putString("uid", model.getUid());
                                    b.putString("key", mrView);
                                    fragment.setArguments(b);
                                    ft.replace(R.id.content_doc_home, fragment);
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
                recycle.setAdapter(a);
                return false;
            }
        });
        search.setIconifiedByDefault(true);
        search.setFocusable(true);
        search.setIconified(false);
        search.requestFocusFromTouch();

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
    public void back(){
            MedicalRecordView mv=new MedicalRecordView();
            FragmentTransaction ft=getFragmentManager().beginTransaction();
            Bundle b=new Bundle();
            b.putString("key",mrView);
            mv.setArguments(b);
            ft.replace(R.id.content_doc_home,mv);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
    }
}
