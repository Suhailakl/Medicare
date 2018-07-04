package com.jrs.medicare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentHome extends Fragment {
    private SearchView search;
    View view;
    DatabaseReference dataRef;
    private  FirebaseListAdapter<String> firebaseListAdapter;
    ListView mListView;
    private TextView text;
    ArrayList<String> city=new ArrayList<String>();
    private int i=0;
    private String data1,val,var;
    ArrayAdapter<String> adapter;
    ProgressBar progressBar;
    private MenuItem item;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.activity_fragment_home,container,false);
        if (container!=null){
            container.removeAllViews();
        }
        getActivity().setTitle(R.string.home);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar= (ProgressBar) view.findViewById(R.id.search_pro);
        search= (SearchView) view.findViewById(R.id.search);
        search.onActionViewExpanded();
        search.setIconified(false);
        search.setInputType(InputType.TYPE_NULL);
        search.setQueryHint("Search Docter By City Or Department");
        mListView= (ListView) view.findViewById(R.id.mList);
        mListView.setVisibility(View.INVISIBLE);
        dataRef= FirebaseDatabase.getInstance().getReference().child("Docters");
        adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, city);
        mListView.setAdapter(adapter);
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    data1 = data.child("city").getValue(String.class);
                    val = data.child("department").getValue(String.class);
                    if (!city.contains(data1)) {
                        city.add(data1);
                        adapter.notifyDataSetChanged();
                    }
                    if (!city.contains(val)) {
                        city.add(val);
                        adapter.notifyDataSetChanged();
                    }
                    search.setInputType(InputType.TYPE_CLASS_TEXT);
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {

                return false;
            }
            @Override
            public boolean onQueryTextChange(String text) {
                if (!text.isEmpty()){
                    mListView.setVisibility(View.VISIBLE);
                    adapter.getFilter().filter(text);
                }else{
                    /*FragmentHome fh=new FragmentHome();
                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                    ft.replace(R.id.activity_fragment_home,fh);
                    ft.commit();*/
                    adapter.notifyDataSetChanged();
                    mListView.setVisibility(View.INVISIBLE);
                }
                return false;
            }

        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemRef = (String) adapter.getItem(position);
                search.setVisibility(View.INVISIBLE);
                mListView.setVisibility(View.INVISIBLE);
                FragmentTransaction ft=getChildFragmentManager().beginTransaction();
                DocterList fragment=new DocterList();
                Bundle item=new Bundle();
                item.putString("item",itemRef);
                fragment.setArguments(item);
                ft.replace(R.id.activity_fragment_home,fragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });
    }
}