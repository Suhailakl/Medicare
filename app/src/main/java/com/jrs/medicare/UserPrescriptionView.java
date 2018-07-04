package com.jrs.medicare;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

/**
 * Created by SUHAIL on 11/8/2017.
 */

public class UserPrescriptionView extends android.support.v4.app.Fragment{
    private DatabaseReference dataRef;
    private String uid;
    private FirebaseAuth mAuth;
    private Query mQuery;
    private RecyclerView recycle;
    private TextView noCnslt,dctName,cnsltDate;
    private ProgressBar progress;

    @Override
    public void onPause() {
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        InputMethodManager in=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        View v=inflater.inflate(R.layout.activity_user_prescription_view,container,false);
        if (container!=null){
            container.removeAllViews();
        }
        getActivity().setTitle(R.string.prscptn);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                        FragmentHome fh=new FragmentHome();
                        ft.replace(R.id.content_user_home,fh);
                        ft.addToBackStack(null);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                        return true;
                    }
                }
                return false;
            }
        });
        mAuth=FirebaseAuth.getInstance();
        dataRef= FirebaseDatabase.getInstance().getReference();
        uid=mAuth.getCurrentUser().getUid();
        recycle= (RecyclerView) v.findViewById(R.id.user_prscptn_view_recycle);
        recycle.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        recycle.setLayoutManager(llm);
        recycle.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        noCnslt= (TextView) v.findViewById(R.id.activity_user_prescription_noCnslt);
        progress= (ProgressBar) v.findViewById(R.id.user_prscptn_progress);
        noCnslt.setVisibility(View.GONE);
        dctName= (TextView) v.findViewById(R.id.user_prscptn_dctName);
        cnsltDate= (TextView) v.findViewById(R.id.user_prscptn_cnsltOn);
        dctName.setVisibility(View.GONE);
        cnsltDate.setVisibility(View.GONE);
        return v;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQuery=dataRef.child("Medical Record").orderByChild("uidTime").startAt(uid).endAt(uid+"\uf8ff");
        recycle.addItemDecoration(new VerticalItemDecoration(20,false));
        final FirebaseRecyclerAdapter<DocPrcptn,DocPrcptnViewHolder> adapter=new FirebaseRecyclerAdapter<DocPrcptn, DocPrcptnViewHolder>(
                DocPrcptn.class,
                R.layout.user_prscrptn_view,
                DocPrcptnViewHolder.class,
                mQuery
        )

        {
            @Override
            public void onDataChanged() {
                if (getItemCount()==0){
                    progress.setVisibility(View.GONE);
                    noCnslt.setVisibility(View.VISIBLE);

                }
                super.onDataChanged();
            }

            @Override
            protected void populateViewHolder(final DocPrcptnViewHolder viewHolder, final DocPrcptn model, int position) {
                final String key=getRef(position).getKey();
                dataRef.child("Medical Record").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("doctorId")){
                            if (dataSnapshot.hasChild("prescription")) {
                                progress.setVisibility(View.GONE);
                                dctName.setVisibility(View.VISIBLE);
                                cnsltDate.setVisibility(View.VISIBLE);
                                viewHolder.setDate(model.getDate().substring(0, 11));
                                viewHolder.setDoctor("Dr " + model.getDoctor());
                            }
                        }
                        else {
                            progress.setVisibility(View.GONE);
                            dctName.setVisibility(View.VISIBLE);
                            cnsltDate.setVisibility(View.VISIBLE);
                            viewHolder.setDate(model.getDate().substring(0, 11));
                            viewHolder.setDoctor("Dr " + model.getDoctor());

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.support.v4.app.FragmentTransaction ft=getFragmentManager().beginTransaction();
                        Prescription pc=new Prescription();
                        Bundle b=new Bundle();
                        b.putString("key",key);
                        pc.setArguments(b);
                        ft.replace(R.id.activity_user_prescription_recycle,pc);
                        ft.addToBackStack(null);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                    }
                });
            }
        };

        recycle.setAdapter(adapter);
    }
    public static class DocPrcptnViewHolder extends RecyclerView.ViewHolder{
        private TextView docName,docDate;
        public DocPrcptnViewHolder(View itemView) {
            super(itemView);
            docName= (TextView) itemView.findViewById(R.id.user_prscptn_view_name);
            docDate= (TextView) itemView.findViewById(R.id.user_prscptn_view_date);

        }
        public void setDoctor(String doctor){
            docName.setText(doctor);
        }
        public void setDate(String date){
            docDate.setText(date);
        }
    }

}
