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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class StartConsult extends Fragment {
    private CountDownTimer timer;
    private long timeCountInMilliSeconds, dc_millis;
    private long daytime;
    private int hr, mt,hrE,mtE, actHrInMt;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private FirebaseUser user;
    private String uid,active;
    private String cnsltTime,cnsltEnd,t,formattedDate,dat,tk;
    private Integer dc_hr, dc_mt;
    private TextView textDay,textHr,textMt,textSec,strt;
    private ProgressBar pro,cSpro;
    private int cur_year;
    private int dayOfYr,dayOfY,curYear;
    private Calendar c = Calendar.getInstance();
    private RelativeLayout layout;
    private TextView sry,noBk;
    private long nowMillis;
    private String ts;
    private MenuItem item;
    private TextView count;
    private DiscreteSeekBar seek;
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

    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_consults_starts, container, false);
        setHasOptionsMenu(true);
        if (container!=null){
            container.removeAllViews();
        }
        getActivity().setTitle(R.string.home);
        textDay = (TextView) v.findViewById(R.id.consult_day_left);
        textHr= (TextView) v.findViewById(R.id.consult_hour_left);
        textMt= (TextView) v.findViewById(R.id.consult_min_left);
        textSec= (TextView) v.findViewById(R.id.consult_sec_left);
        pro = (ProgressBar) v.findViewById(R.id.consult_doc_pro);
        cSpro = (ProgressBar) v.findViewById(R.id.cS_progress);
        mRef=FirebaseDatabase.getInstance().getReference();
        cur_year = c.get(Calendar.YEAR);
        dayOfYr = c.get(Calendar.DAY_OF_YEAR);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        uid = user.getUid();
        strt= (TextView) v.findViewById(R.id.consult_strtsIn);
        sry= (TextView) v.findViewById(R.id.consult_sec_sry);
        noBk= (TextView) v.findViewById(R.id.consult_sec_noBk);
        seek= (DiscreteSeekBar) v.findViewById(R.id.sC_seek);
        layout= (RelativeLayout) v.findViewById(R.id.cS_layout);
        layout.setVisibility(View.GONE);
        Bundle b=this.getArguments();
        if (getArguments()!=null){
            active=b.getString("active");
            tk=b.getString("tk");

        }
        consultTime();
        return v;
    }
    public void consultTime(){
        mRef.child("Docters").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cnsltTime=dataSnapshot.child("consultation starts at").getValue().toString();
                cnsltEnd=dataSnapshot.child("consultation ends at").getValue().toString();
                ArrayList<String> days=new ArrayList<String>();
                days.add((String) dataSnapshot.child("day1").getValue());
                days.add((String) dataSnapshot.child("day2").getValue());
                days.add((String) dataSnapshot.child("day3").getValue());
                days.add((String) dataSnapshot.child("day4").getValue());
                days.add((String) dataSnapshot.child("day5").getValue());
                days.add((String) dataSnapshot.child("day6").getValue());
                days.add((String) dataSnapshot.child("day7").getValue());
                if (cnsltTime.length()==3){
                    if (cnsltTime.contains("AM")) {
                        hr = Integer.valueOf(cnsltTime.substring(0, 1));
                        mt=0;

                    }
                    else {
                        hr = Integer.valueOf(cnsltTime.substring(0, 1))+12;
                        mt=0;
                    }
                }
                else if (cnsltTime.length()==6){
                    if (cnsltTime.contains("AM")) {
                        hr = Integer.valueOf(cnsltTime.substring(0, 1));
                        mt = Integer.valueOf(cnsltTime.substring(2, 4));
                    }
                    else {
                        hr = Integer.valueOf(cnsltTime.substring(0, 1))+12;
                        mt = Integer.valueOf(cnsltTime.substring(2, 4));
                    }
                }
                else if (cnsltTime.length()==7) {
                    if (cnsltTime.contains("AM")) {
                        hr = Integer.valueOf(cnsltTime.substring(0, 2));
                        mt = Integer.valueOf(cnsltTime.substring(3, 5));
                    } else {
                        hr = Integer.valueOf(cnsltTime.substring(0, 2)) + 12;
                        mt = Integer.valueOf(cnsltTime.substring(3, 5));
                    }
                }
                else {
                    if (cnsltTime.contains("AM")) {
                        hr = Integer.valueOf(cnsltTime.substring(0, 2));
                        mt = 0;
                    } else {
                        hr = Integer.valueOf(cnsltTime.substring(0, 2)) + 12;
                        mt = 0;
                    }
                }
                if (cnsltEnd.length()==3){
                    if (cnsltEnd.contains("AM")) {
                        hrE = Integer.valueOf(cnsltEnd.substring(0, 1));
                        mtE=0;

                    }
                    else {
                        hrE = Integer.valueOf(cnsltEnd.substring(0, 1))+12;
                        mtE=0;
                    }
                }
                else if (cnsltEnd.length()==6){
                    if (cnsltEnd.contains("AM")) {
                        hrE = Integer.valueOf(cnsltEnd.substring(0, 1));
                        mtE = Integer.valueOf(cnsltEnd.substring(2, 4));
                    }
                    else {
                        hrE = Integer.valueOf(cnsltEnd.substring(0, 1))+12;
                        mtE = Integer.valueOf(cnsltEnd.substring(2, 4));
                    }
                }
                else if (cnsltEnd.length()==7) {
                    if (cnsltEnd.contains("AM")) {
                        hrE = Integer.valueOf(cnsltEnd.substring(0, 2));
                        mtE = Integer.valueOf(cnsltEnd.substring(3, 5));
                    } else {
                        hrE = Integer.valueOf(cnsltEnd.substring(0, 2)) + 12;
                        mtE = Integer.valueOf(cnsltEnd.substring(3, 5));
                    }
                }
                else {
                    if (cnsltEnd.contains("AM")) {
                        hrE = Integer.valueOf(cnsltEnd.substring(0, 2));
                        mtE = 0;
                    } else {
                        hrE = Integer.valueOf(cnsltEnd.substring(0, 2)) + 12;
                        mtE = 0;
                    }
                }
                for (int i=dayOfYr;i<dayOfYr+7;i++){
                    String day=getNameOfDay(cur_year,i);
                    if (days.contains(day)){
                        long cur=c.getTimeInMillis();
                       c.set(Calendar.DAY_OF_YEAR,i);
                        c.set(Calendar.HOUR_OF_DAY,hr);
                        c.set(Calendar.MINUTE,mt);
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c.getTime());
                       /* seek.setIndicatorPopupEnabled(true);
                        seek.setMinimumWidth(100);
                        seek.setIndicatorFormatter(formattedDate);*/
                        long cT=c.getTimeInMillis();
                        c.set(Calendar.DAY_OF_YEAR,i);
                        c.set(Calendar.HOUR_OF_DAY,hrE);
                        c.set(Calendar.MINUTE,mtE);
                        long cE=c.getTimeInMillis();
                        if (cE>cur){
                            daytime=cT-cur;
                        }
                        else {
                            continue;
                        }

                        break;
                    }
                }
                timer=new CountDownTimer(daytime,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        int mDisplayHours = (int) ((millisUntilFinished / 1000)/3600);
                        if (mDisplayHours>=24){
                            if (mDisplayHours<48){
                                textDay.setText("1 d");
                                int mDisplayH=mDisplayHours-24;
                                textHr.setText(String.valueOf(mDisplayH+"hr"));
                                int mDisplayM= (int) ((millisUntilFinished / 1000) /60)-(1440+(mDisplayH*60));
                                textMt.setText(String.valueOf(mDisplayM+"m"));
                                int mDisplayS=(int) ((millisUntilFinished / 1000))-(mDisplayHours*60*60)-mDisplayM*60;
                                textSec.setText(String.valueOf(mDisplayS+"s"));
                            }
                            else if (mDisplayHours<72){
                                textDay.setText("2 d");
                                int mDisplayH=mDisplayHours-48;
                                textHr.setText(String.valueOf(mDisplayH+"hr"));
                                int mDisplayM= (int) ((millisUntilFinished / 1000) /60)-(2880+(mDisplayH*60));
                                textMt.setText(String.valueOf(mDisplayM+"mt"));
                                int mDisplayS=(int) ((millisUntilFinished / 1000))-(mDisplayHours*60*60)-mDisplayM*60;
                                textSec.setText(String.valueOf(mDisplayS+"s"));
                            }
                            else if (mDisplayHours<96){
                                textDay.setText("3 d");
                                int mDisplayH=mDisplayHours-72;
                                textHr.setText(String.valueOf(mDisplayH+"hr"));
                                int mDisplayM= (int) ((millisUntilFinished / 1000) /60)-(4320+(mDisplayH*60));
                                textMt.setText(String.valueOf(mDisplayM+"m"));
                                int mDisplayS=(int) ((millisUntilFinished / 1000))-(mDisplayHours*60*60)-mDisplayM*60;
                                textSec.setText(String.valueOf(mDisplayS+"s"));
                            }
                            else if (mDisplayHours<120){
                                textDay.setText("4 d");
                                int mDisplayH=mDisplayHours-96;
                                textHr.setText(String.valueOf(mDisplayH+"hr"));
                                int mDisplayM= (int) ((millisUntilFinished / 1000) /60)-(5760+(mDisplayH*60));
                                textMt.setText(String.valueOf(mDisplayM+"m"));
                                int mDisplayS=(int) ((millisUntilFinished / 1000))-(mDisplayHours*60*60)-mDisplayM*60;
                                textSec.setText(String.valueOf(mDisplayS+"s"));
                            }
                            else if (mDisplayHours<144){
                                textDay.setText("5 d");
                                int mDisplayH=mDisplayHours-120;
                                textHr.setText(String.valueOf(mDisplayH+"hr"));
                                int mDisplayM= (int) ((millisUntilFinished / 1000) /60)-(7200+(mDisplayH*60));
                                textMt.setText(String.valueOf(mDisplayM+"m"));
                                int mDisplayS=(int) ((millisUntilFinished / 1000))-(mDisplayHours*60*60)-mDisplayM*60;
                                textSec.setText(String.valueOf(mDisplayS+"s"));
                            }
                            else if (mDisplayHours<168){
                                textDay.setText("6 d");
                                int mDisplayH=mDisplayHours-144;
                                textHr.setText(String.valueOf(mDisplayH+"hr"));
                                int mDisplayM= (int) ((millisUntilFinished / 1000) /60)-(8640+(mDisplayH*60));
                                textMt.setText(String.valueOf(mDisplayM+"m"));
                                int mDisplayS=(int) ((millisUntilFinished / 1000))-(mDisplayHours*60*60)-mDisplayM*60;
                                textSec.setText(String.valueOf(mDisplayS+"s"));
                            }
                        }
                        else {
                            textDay.setText("0 d");
                            textHr.setText(String.valueOf(mDisplayHours+"hr"));
                            int mDisplayM= (int) ((millisUntilFinished / 1000) /60)-(mDisplayHours*60);
                            textMt.setText(String.valueOf(mDisplayM+"m"));
                            int mDisplayS=(int) ((millisUntilFinished / 1000))-(mDisplayHours*3600)-mDisplayM*60;
                            textSec.setText(String.valueOf(mDisplayS+"s"));
                        }
                        cSpro.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFinish() {
                        SimpleDateFormat df=new SimpleDateFormat("dd-MMM-yyyy");
                        final String fd=df.format(c.getTime());
                        mRef = FirebaseDatabase.getInstance().getReference();
                        String day=getNameOfDay(cur_year,dayOfYr);
                        mRef.child("Token Id").orderByChild("docKey").equalTo(uid+fd+"("+day.substring(0,3)+")1").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                                    DocConsultationStartedView fragment=new DocConsultationStartedView();
                                    Bundle b=new Bundle();
                                    b.putString("active",active);
                                    b.putString("tk",tk);
                                    fragment.setArguments(b);
                                    ft.replace(R.id.activity_consult_starts,fragment);
                                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                    ft.addToBackStack(null);
                                    ft.commit();
                                }
                                else {
                                    textSec.setVisibility(View.GONE);
                                    textMt.setVisibility(View.GONE);
                                    textHr.setVisibility(View.GONE);
                                    pro.setVisibility(View.GONE);
                                    textDay.setVisibility(View.GONE);
                                    strt.setVisibility(View.GONE);
                                    cSpro.setVisibility(View.GONE);
                                    sry.setVisibility(View.VISIBLE);
                                    noBk.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mRef.child("Token Id").orderByChild("docKey").equalTo(uid+fd+"("+day.substring(0,3)+")1").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                                    DocConsultationStartedView fragment=new DocConsultationStartedView();
                                    Bundle b=new Bundle();
                                    b.putString("active",active);
                                    b.putString("tk",tk);
                                    fragment.setArguments(b);
                                    ft.replace(R.id.activity_consult_starts,fragment);
                                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                    ft.addToBackStack(null);
                                    ft.commit();
                                }
                                else {
                                    textSec.setVisibility(View.GONE);
                                    textMt.setVisibility(View.GONE);
                                    textHr.setVisibility(View.GONE);
                                    pro.setVisibility(View.GONE);
                                    textDay.setVisibility(View.GONE);
                                    strt.setVisibility(View.GONE);
                                    sry.setVisibility(View.VISIBLE);
                                    noBk.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
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
        String days[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);

        return days[dayIndex - 1];
    }
    public void notification(){
        mRef.child("Doctor Notification").child(uid).addValueEventListener(new ValueEventListener() {
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