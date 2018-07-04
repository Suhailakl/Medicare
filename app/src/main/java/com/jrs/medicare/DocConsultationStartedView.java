package com.jrs.medicare;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by SUHAIL on 10/14/2017.
 */

public class DocConsultationStartedView extends Fragment {
    private EditText prscpn,abt;
    private Button sbt,nxt;
    private ImageButton imgBtn;
    private TextView tkno,name,usrId,age,gen,refName,refFrom;
    private DatabaseReference dataRef;
    private FirebaseAuth mAuth;
    private FirebaseUser usr;
    private boolean isRefer;
    private String uid,activePre,tk,MvKey;
    private String formattedDate,date;
    private int curYear,dayOfYr;
    private Calendar c=Calendar.getInstance();
    private MenuItem item,itemRef;
    private LinearLayout layout,layout2;
    private RelativeLayout btnLayout;
    private KeyboardVisibilityEvent kEvent;
    private ProgressBar pro;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_refer){
            //update(tkno.getText().toString().trim());
            isRefer=true;
            sbt.performClick();
        FragmentTransaction ft=getFragmentManager().beginTransaction();
            MedicalRecordView mR=new MedicalRecordView();
            Bundle b=new Bundle();
            b.putString("key",MvKey);
            b.putString("back","yes");
            b.putString("tk",tkno.getText().toString().trim());
            mR.setArguments(b);
            ft.replace(R.id.activity_start_consult,mR);
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refer, menu);
        item=menu.findItem(R.id.action_time);
        itemRef=menu.findItem(R.id.action_refer);
        timer();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        date=formattedDate+getNameOfDay(curYear,dayOfYr);
        dataRef.child("Token Id").orderByChild("doctorKey").equalTo(uid+date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean active=false;
                String t = null;
                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    if (data.hasChild("active")) {
                        active=true;
                        t=data.child("token").getValue().toString();
                    }
                }
                if (active&&t!=null){
                    if (activePre==null){
                         getDetails(t);
                    }
                    else {
                        dataRef.child("Docters").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                    dataRef.child("Token Id").orderByChild("docKey").equalTo(uid+date+(tk)).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot data:dataSnapshot.getChildren()) {
                                                dataRef.child("Token Id").child(data.getKey()).child("active").removeValue();
                                            }
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
                        getDetails(String.valueOf(Integer.parseInt(tk)+1));

                    }

                }
                else { if (activePre==null){
                    getDetails("1");
                }
                else {
                    tkno.setText(tk);
                    getDetails("2");
                }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sbt.performClick();
                dataRef.child("Docters").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int tk=Integer.parseInt(tkno.getText().toString().trim())+1 ;
                        if (tk<=dataSnapshot.child("Bookings").child(date).getChildrenCount()) {
                            getDetails(String.valueOf(tk));
                            dataRef.child("Token Id").orderByChild("docKey").equalTo(uid+date+(tkno.getText().toString().trim())).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data:dataSnapshot.getChildren()) {
                                        dataRef.child("Token Id").child(data.getKey()).child("active").removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            Toast.makeText(getActivity(), "Patients Completed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.activity_start_consult,container,false);
        if (container!=null){
            container.removeAllViews();
        }
        getActivity().setTitle(R.string.cnslt);
        prscpn= (EditText) v.findViewById(R.id.sCBtn_prscptn);
        abt= (EditText) v.findViewById(R.id.sCBtn_about);
        sbt= (Button) v.findViewById(R.id.sCBtn_cnslt_submit);
        nxt= (Button) v.findViewById(R.id.sCBtn_nextToken);
        tkno= (TextView) v.findViewById(R.id.sCtext_tkNo);
        name= (TextView) v.findViewById(R.id.sCtext_name);
        age= (TextView) v.findViewById(R.id.sCtext_age);
        gen= (TextView) v.findViewById(R.id.sCtext_gender);
        usrId= (TextView) v.findViewById(R.id.sCtext_userId);
        dataRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        usr=mAuth.getCurrentUser();
        uid=usr.getUid();
        SimpleDateFormat df=new SimpleDateFormat("dd-MMM-yyyy");
        curYear = c.get(Calendar.YEAR);
        dayOfYr = c.get(Calendar.DAY_OF_YEAR);
        formattedDate=df.format(c.getTime());
        refFrom= (TextView) v.findViewById(R.id.sC_rfFrom);
        refName= (TextView) v.findViewById(R.id.sC_ref_name);
        imgBtn= (ImageButton) v.findViewById(R.id.sC_imageBtn);
        //pro= (ProgressBar) v.findViewById(R.id.sC_progress);
        layout= (LinearLayout) v.findViewById(R.id.sC_layout);
        layout2= (LinearLayout) v.findViewById(R.id.sC_layout2);
        layout2.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        btnLayout= (RelativeLayout) v.findViewById(R.id.sC_Btn_layout);
        btnLayout.setVisibility(View.GONE);
        pro= (ProgressBar) v.findViewById(R.id.sC_pro);
        Bundle b=this.getArguments();
        if (getArguments()!=null){
            activePre=b.getString("active");
            tk=b.getString("tk");
        }
        kEvent.setEventListener(getActivity(), new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen){
                    btnLayout.setVisibility(View.GONE);
                }
                else {
                    btnLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        return v;
    }

    public void timer(){
        dataRef.child("Docters").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String star = dataSnapshot.child("consultation starts at").getValue().toString();
                String en = dataSnapshot.child("consultation ends at").getValue().toString();
                int hr, mt=0;
                long start = 0, end;
                start=(c.get(Calendar.HOUR_OF_DAY)*(1000*60*60))+(c.get(Calendar.MINUTE))*(1000*60);
                int h,m=0;
                    if (en.length() == 3) {
                        if (en.contains("AM")) {
                            h = Integer.valueOf(en.substring(0, 1));
                            m = 0;
                        } else {
                            h = Integer.valueOf(en.substring(0, 1)) + 12;
                            m = 0;
                        }
                    } else if (en.length() == 6) {
                        if (en.contains("AM")) {
                            h = Integer.valueOf(en.substring(0, 1));
                            m = Integer.valueOf(en.substring(2, 4));
                        } else {
                            h = Integer.valueOf(en.substring(0, 1)) + 12;
                            m = Integer.valueOf(en.substring(2, 4));
                        }
                    } else if (en.length()==7){
                        if (en.contains("AM")) {
                            h = Integer.valueOf(en.substring(0, 2));
                            m= Integer.valueOf(en.substring(3, 5));
                        } else {
                            h = Integer.valueOf(en.substring(0, 2)) + 12;
                            m = Integer.valueOf(en.substring(3, 5));
                        }
                    }
                    else {
                        if (en.contains("AM")) {
                            h = Integer.valueOf(en.substring(0, 2));
                            m= 0;
                        } else {
                            h = Integer.valueOf(en.substring(0, 2)) + 12;
                            m = 0;
                        }
                    }
                        end = (h * 1000 * 60 * 60) + (m * 1000 * 60);
                        final long time = (end -start);

                        CountDownTimer timer = new CountDownTimer(time, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                long hr = (millisUntilFinished / 1000) / 3600;
                                long mt = ((millisUntilFinished / 1000) / 60)-(hr*60);
                                long se=(millisUntilFinished / 1000)-(hr*60*60)-(mt*60);
                                item.setTitleCondensed(hr+"h:"+mt+"m" +":"+se+"s");
                            }

                            @Override
                            public void onFinish() {
                                Toast.makeText(getActivity(), "Consultation Completed", Toast.LENGTH_SHORT).show();
                                FragmentTransaction ft=getFragmentManager().beginTransaction();
                                StartConsult fragment=new StartConsult();
                                ft.replace(R.id.activity_consult_starts,fragment);
                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        };
                        timer.start();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public String getNameOfDay(int year, int dayOfYear) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
        String days[] = {"(Sun)", "(Mon)", "(Tue)", "(Wed)", "(Thu)", "(Fri)", "(Sat)"};

        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);

        return days[dayIndex - 1];
    }
    public void getDetails(final String t){
        dataRef.child("Token Id").orderByChild("docKey").equalTo(uid+date+t).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot data:dataSnapshot.getChildren()) {
                    dataRef.child("Token Id").child(data.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            if (!t.equals("1")) {
                                dataRef.child("Token Id").child(dataSnapshot.getKey()).child("active").setValue("yes");
                            }
                            tkno.setText(t);
                            //usrId.setText(dataSnapshot.child("userId").getValue().toString());
                            final String uId=dataSnapshot.child("uid").getValue().toString();
                            dataRef.child("Usernames").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    usrId.setText(dataSnapshot.getValue().toString().trim());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            if (dataSnapshot.hasChild("prescription")){
                                prscpn.setText(dataSnapshot.child("prescription").getValue().toString());
                                abt.setText(dataSnapshot.child("about").getValue().toString());
                            }
                            else {
                                prscpn.setText(null);
                                abt.setText(null);
                            }

                            dataRef.child("Users").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    age.setText(dataSnapshot.child("age").getValue().toString());
                                    gen.setText(dataSnapshot.child("gender").getValue().toString());
                                    name.setText(dataSnapshot.child("name").getValue().toString());
                                    layout.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            dataRef.child("Medical Record").orderByChild("docKey").equalTo(uid+usrId.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data:dataSnapshot.getChildren()){
                                        if (data.hasChild("referred from")){
                                            layout.setVisibility(View.VISIBLE);
                                            final String rfrFrom=data.child("referred from").getValue().toString();
                                            final String key=data.getKey();
                                            dataRef.child("Medical Record").child(rfrFrom).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    refName.setText(dataSnapshot.child("doctor").getValue().toString());
                                                    imgBtn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            FragmentTransaction ft=getFragmentManager().beginTransaction();
                                                            MedicalRecordView mV=new MedicalRecordView();
                                                            Bundle b=new Bundle();
                                                            b.putString("key",key);
                                                            b.putString("back","yes");
                                                            mV.setArguments(b);
                                                            ft.replace(R.id.activity_start_consult,mV);
                                                            ft.addToBackStack(null);
                                                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                                            ft.commit();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                    setHasOptionsMenu(true);
                                    setRetainInstance(true);
                                    pro.setVisibility(View.GONE);
                                    btnLayout.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            sbt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dataRef.child("Token Id").child(dataSnapshot.getKey()).child("prescription").setValue(prscpn.getText().toString().trim());
                                    dataRef.child("Token Id").child(dataSnapshot.getKey()).child("about").setValue(abt.getText().toString().trim());
                                    final DatabaseReference mRef=dataRef.child("Medical Record").child(dataSnapshot.getKey()).getRef();
                                    mRef.child("prescription").setValue(prscpn.getText().toString().trim());
                                    mRef.child("about").setValue(abt.getText().toString().trim());
                                    mRef.child("date").setValue(date);
                                    mRef.child("uid").setValue(uId);
                                    mRef.child("uidTime").setValue(uId+c.getTimeInMillis());
                                    mRef.child("userId").setValue(usrId.getText().toString().trim());
                                    mRef.child("patient").setValue(name.getText().toString().trim());
                                    mRef.child("doctorId").setValue(dataSnapshot.child("doctorId").getValue().toString());
                                    mRef.child("key").setValue(dataSnapshot.child("doctorId").getValue().toString()+usrId.getText().toString().trim()+date);
                                    mRef.child("doctor").setValue(dataSnapshot.child("doctor").getValue().toString());
                                    mRef.child("docKey").setValue(dataSnapshot.child("doctorId").getValue().toString()+usrId.getText().toString().trim());
                                    mRef.child("spinnerKey").setValue(dataSnapshot.child("doctor").getValue().toString()+uId);
                                    mRef.child("docDateKey").setValue(dataSnapshot.child("doctorId").getValue().toString()+date);
                                    mRef.child("dId").setValue(dataSnapshot.child("doctorId").getValue().toString());
                                    MvKey=dataSnapshot.getKey();
                                    dataRef.child("Docters").child(dataSnapshot.child("doctorId").getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            mRef.child("department").setValue(dataSnapshot.child("department").getValue().toString());
                                            mRef.child("registration number").setValue(dataSnapshot.child("registration number").getValue().toString());
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    sbt.setText("Update");
                                    if (!isRefer){
                                        Toast.makeText(getActivity(), "Submitted Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void update(String t){
        dataRef.child("Token Id").orderByChild("docKey").equalTo(uid+date+t).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uId=dataSnapshot.child("uid").getValue().toString();
                dataRef.child("Token Id").child(dataSnapshot.getKey()).child("prescription").setValue(prscpn.getText().toString().trim());
                dataRef.child("Token Id").child(dataSnapshot.getKey()).child("about").setValue(abt.getText().toString().trim());
                dataRef.child("Medical Record").push().getRef().child("prescription").setValue(prscpn.getText().toString().trim());
                dataRef.child("Medical Record").push().getRef().child("about").setValue(abt.getText().toString().trim());
                dataRef.child("Medical Record").push().getRef().child("date").setValue(date);
                dataRef.child("Medical Record").push().getRef().child("uid").setValue(uId);
                dataRef.child("Medical Record").push().getRef().child("userId").setValue(usrId.getText().toString().trim());
                dataRef.child("Medical Record").push().getRef().child("patient").setValue(name.getText().toString().trim());
                dataRef.child("Medical Record").push().getRef().child("doctorId").setValue(dataSnapshot.child("doctorId").getValue().toString());
                dataRef.child("Medical Record").push().getRef().child("doctorId").setValue(dataSnapshot.child("doctor").getValue().toString());
                dataRef.child("Medical Record").push().getRef().child("docKey").setValue(dataSnapshot.child("doctorId").getValue().toString()+usrId.getText().toString().trim());
                dataRef.child("Medical Record").push().getRef().child("spinnerKey").setValue(dataSnapshot.child("doctor").getValue().toString()+uId);
                sbt.setText("Update");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
