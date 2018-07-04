package com.jrs.medicare;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class Complaint extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private SpinnerDialog spinnerDialog;
    private ArrayList<String> docList=new ArrayList<>();
    private ArrayList<String> list=new ArrayList<>();
    private Spinner spin;
    private TextInputLayout tiL;
    private String uid;
    private TextView doc,issue,elaborate;
    private Button sbt;
    private Calendar c;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container!=null){
            container.removeAllViews();
        }
        getActivity().setTitle(R.string.cmplnt);
        InputMethodManager in=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        View v= inflater.inflate(R.layout.activity_user_compliant,container,false);
        mAuth=FirebaseAuth.getInstance();
        mRef= FirebaseDatabase.getInstance().getReference();
        uid=mAuth.getCurrentUser().getUid();
        doc= (TextView) v.findViewById(R.id.user_compliant_doc);
        spin= (Spinner) v.findViewById(R.id.user_compliant_spinner);
        tiL= (TextInputLayout) v.findViewById(R.id.textView1234);
        doc.setInputType(0);
        doc.setCursorVisible(false);
        doc.setFocusableInTouchMode(false);
        doc.setFocusable(false);
        issue= (TextView) v.findViewById(R.id.user_compliant_issue);
        issue.setInputType(InputType.TYPE_NULL);
        elaborate= (TextView) v.findViewById(R.id.user_compliant);
        sbt= (Button) v.findViewById(R.id.user_compliant_ok);
        c=Calendar.getInstance();
        addItemToDoc();
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        FragmentHome fh=new FragmentHome();
                        ft.replace(R.id.content_user_home,fh);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
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
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        commonCompliants();
        spin.setAdapter(adapter);
        spinnerDialog=new SpinnerDialog(getActivity(),docList,"Select Doctor",R.style.DialogAnimations_SmileWindow);
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String s, int i) {
                doc.setText(s);
            }
        });
        doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spin.getSelectedItem()=="Select None"){
                    issue.setInputType(InputType.TYPE_CLASS_TEXT);
                    issue.setCursorVisible(true);
                    issue.setFocusableInTouchMode(true);
                    issue.setFocusable(true);
                    tiL.setHint("Or Type Your Issue");
                }
                else {
                        tiL.setHint(null);
                    issue.setInputType(InputType.TYPE_NULL);
                    issue.setCursorVisible(false);
                    issue.setFocusableInTouchMode(false);
                    issue.setFocusable(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference ref=mRef.child("Admin").child("Doctor Issues").push().getRef();
                final String s=doc.getText().toString().trim();
                String result[]=s.split(":");
                String regNo=result[result.length-1];
                if ((spin.getSelectedItem()!="Select None")||(!issue.getText().toString().trim().isEmpty())){
                    if (!s.isEmpty()&&!elaborate.getText().toString().trim().isEmpty()){
                        if (spin.getSelectedItem()=="Select None"){
                            ref.child("issue").setValue(issue.getText().toString().trim());
                        }
                        else {
                            ref.child("issue").setValue(spin.getSelectedItem().toString().trim());
                        }
                        ref.child("complaint").setValue(elaborate.getText().toString().trim());
                        ref.child("active").setValue("yes");
                        ref.child("timeStamp").setValue(c.getTimeInMillis());
                        ref.child("uid").setValue(uid);

                        mRef.child("Docters").orderByChild("registration number").equalTo(regNo).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot data:dataSnapshot.getChildren()) {
                                    String key = data.getKey();
                                    ref.child("docKey").setValue(key);
                                    Toast.makeText(getActivity(), "Successfully Reported", Toast.LENGTH_SHORT).show();
                                    FragmentHome fh=new FragmentHome();
                                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_user_home,fh);
                                    ft.commit();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {


                            }
                        });

                    }
                    else {
                        Toast.makeText(getActivity(), "Fields Should not be Empty", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Fields Should not be Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
    public void addItemToDoc(){
        mRef.child("Docters").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    String name=data.child("name").getValue().toString();
                    String city=data.child("city").getValue().toString();
                    String regNo=data.child("registration number").getValue().toString();
                    docList.add("Dr."+name+" ("+city+")"+"                             RegNo:"+regNo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void commonCompliants(){
        list.add("Select None");
        list.add("Fake Doctor");
        list.add("Consultation Issues");
    }
}
