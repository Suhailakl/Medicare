package com.jrs.medicare;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by SUHAIL on 9/16/2017.
 */

public class BookDocFragment extends Fragment {
    TextView drName,hspName,adrs,dpmt,numbr,days,time,pu_time;
    String id,usrname,back;
    TextView tk,p_date;
    private Spinner date;
    DatabaseReference dataRef,rootRef,docRef;
    private Button tok;
    private PopupWindow popup;
    private LayoutInflater inflater;
    private LinearLayout layout;
    private ViewGroup viewGroup;
    private FirebaseAuth mAuth;
    private int tokNo=0;
    private ProgressBar bkPro,imgPro;
    private ImageView img;
    private FrameLayout mLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.book_doc,container,false);
        getActivity().setTitle(R.string.bkDoc);
        layout= (LinearLayout) v.findViewById(R.id.book_doc);
        inflater= (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popup=inflater.inflate(R.layout.token_submit_popup,null);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popup,650,450, focusable);
        tk= (TextView) popup.findViewById(R.id.tkno);
        p_date= (TextView) popup.findViewById(R.id.pu_date);
        pu_time= (TextView) popup.findViewById(R.id.pu_time);
        bkPro= (ProgressBar) v.findViewById(R.id.bkDoc_progress);
        imgPro= (ProgressBar) v.findViewById(R.id.book_doc_img_pro);
        mLayout= (FrameLayout) v.findViewById(R.id.book_doc_frame);
        mLayout.getForeground().setAlpha(0);
        bkPro.setVisibility(View.GONE);
        popup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FragmentHome fh=new FragmentHome();
                FragmentTransaction ft=getFragmentManager().beginTransaction();
                ft.replace(R.id.content_user_home,fh);
                ft.commit();
                popupWindow.dismiss();
                return true;
            }
        });
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mLayout.getForeground().setAlpha(0);
            }
        });
        if (container!=null){
            container.removeAllViews();
        }

        mAuth=FirebaseAuth.getInstance();
        final String user=mAuth.getCurrentUser().getUid();
        rootRef=FirebaseDatabase.getInstance().getReference().child("Usernames");
        rootRef.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usrname=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        drName= (TextView) v.findViewById(R.id.tk_name);
        adrs= (TextView) v.findViewById(R.id.tk_ads);
        hspName= (TextView) v.findViewById(R.id.tk_hspname);
        dpmt= (TextView) v.findViewById(R.id.tk_dpt);
        numbr= (TextView) v.findViewById(R.id.tk_num);
        days= (TextView) v.findViewById(R.id.tk_days);
        time= (TextView) v.findViewById(R.id.tk_time);
        date= (Spinner) v.findViewById(R.id.tk_date);
        tok= (Button) v.findViewById(R.id.take_tok);
        img= (ImageView) v.findViewById(R.id.book_doc_img);
        Bundle b=this.getArguments();
        if (getArguments()!=null){
            id=b.getString("uid");
            back=b.getString("back");
            back=b.getString("notBack");
        }
        dataRef= FirebaseDatabase.getInstance().getReference();
        docRef= FirebaseDatabase.getInstance().getReference().child("Docters");
        dataRef.child("Docters").child(id).addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String dpt = dataSnapshot.child("department").getValue().toString();
                String hsp = dataSnapshot.child("working hospital").getValue().toString();
                String ads = dataSnapshot.child("address").getValue().toString();
                String num = dataSnapshot.child("mobile number").getValue().toString();
                final String tokTot = (String) dataSnapshot.child("total tokens").getValue();
                final String timeStart = (String) dataSnapshot.child("consultation starts at").getValue();
                final String timeEnd = (String) dataSnapshot.child("consultation ends at").getValue();
                time.setText(timeStart + " To " + timeEnd);
                drName.setText("Dr." + name);
                dpmt.setText(dpt);
                hspName.setText("Works At " + hsp);
                adrs.setText(ads);
                numbr.setText("+91 " + num);
                if (!dataSnapshot.child("profile pic").exists()){
                    if (dataSnapshot.child("gender").getValue().toString().trim().equals("Male")){
                        img.setImageResource(R.mipmap.ic_male_doc);
                        imgPro.setVisibility(View.GONE);
                    }
                    else {
                        img.setImageResource(R.mipmap.ic_female_doc);
                        imgPro.setVisibility(View.GONE);
                    }
                }
                else {
                    Glide.with(getActivity()).load(dataSnapshot.child("profile pic").getValue().toString()).into(img);
                    imgPro.setVisibility(View.GONE);
                }
                String day1 = (String) dataSnapshot.child("day1").getValue();
                String day2 = (String) dataSnapshot.child("day2").getValue();
                String day3 = (String) dataSnapshot.child("day3").getValue();
                String day4 = (String) dataSnapshot.child("day4").getValue();
                String day5 = (String) dataSnapshot.child("day5").getValue();
                String day6 = (String) dataSnapshot.child("day6").getValue();
                String day7 = (String) dataSnapshot.child("day7").getValue();
                tok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tok.setVisibility(View.GONE);
                        bkPro.setVisibility(View.VISIBLE);
                        final String selectedDate = date.getSelectedItem().toString();
                        p_date.setText("Today");
                        int s = Integer.parseInt(tokTot);
                        if (dataSnapshot.child("Bookings").child(selectedDate).exists()) {
                            if (dataSnapshot.child("Bookings").child(selectedDate).getChildrenCount() < s) {
                                dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String key = (String) dataSnapshot.child("Booked Keys").child(selectedDate).child(id).child(usrname).getValue();
                                        if (key == null) {
                                            tok.setVisibility(View.GONE);
                                            long t = dataSnapshot.child("Docters").child(id).child("Bookings").child(selectedDate).getChildrenCount() + 1;
                                            dataRef.child("Users").child(user).child("booked docters").child(id).child(selectedDate).setValue(t);
                                            String pkey = dataRef.child("Docters").child(id).child("Bookings").child(selectedDate).push().getKey();
                                            dataRef.child("Token Id").child(pkey).child("token").setValue(String.valueOf(t));
                                            dataRef.child("Token Id").child(pkey).child("doctor").setValue(dataSnapshot.child("Docters").child(id).child("name").getValue().toString());
                                            dataRef.child("Token Id").child(pkey).child("time1").setValue(dataSnapshot.child("Docters").child(id).child("consultation starts at").getValue().toString());
                                            dataRef.child("Token Id").child(pkey).child("time2").setValue(dataSnapshot.child("Docters").child(id).child("consultation ends at").getValue().toString());
                                            dataRef.child("Token Id").child(pkey).child("city").setValue(dataSnapshot.child("Docters").child(id).child("city").getValue().toString());
                                            dataRef.child("Token Id").child(pkey).child("doctorId").setValue(id);
                                            int day=Integer.parseInt(selectedDate.substring(0,2));
                                            int y=Integer.parseInt(selectedDate.substring(7,11));
                                            String mo=selectedDate.substring(3,6);
                                            int m=BookingDetails.getMonth(mo);
                                            dataRef.child("Token Id").child(pkey).child("userId").setValue(usrname+y+m+day);
                                            dataRef.child("Token Id").child(pkey).child("uid").setValue(user);
                                            dataRef.child("Token Id").child(pkey).child("key").setValue(usrname + selectedDate);
                                            dataRef.child("Token Id").child(pkey).child("docKey").setValue(id + selectedDate+String.valueOf(t));
                                            dataRef.child("Token Id").child(pkey).child("doctorKey").setValue(id + selectedDate);
                                            dataRef.child("Token Id").child(pkey).child("date").setValue(selectedDate);
                                            dataRef.child("Booked Keys").child(selectedDate).child(id).child(usrname).setValue(pkey);
                                            dataRef.child("Docters").child(id).child("Bookings").child(selectedDate).child(pkey).setValue(usrname);
                                            bkPro.setVisibility(View.GONE);
                                            popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
                                            mLayout.getForeground().setAlpha(220);
                                            tk.setText(String.valueOf(t));
                                            p_date.setText(selectedDate);
                                            pu_time.setText(timeStart + " To " + timeEnd);
                                        } else {
                                            bkPro.setVisibility(View.GONE);
                                            tok.setVisibility(View.VISIBLE);
                                            Toast.makeText(getActivity(), "Already Booked", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                Calendar c=Calendar.getInstance();
                                bkPro.setVisibility(View.GONE);
                                tok.setVisibility(View.VISIBLE);
                                DatabaseReference docN=dataRef.child("Doctor Notification").child(id).push().getRef();
                                docN.child("doctorId").setValue(id);
                                docN.child("tokFullActive").setValue("yes");
                                docN.child("date").setValue(selectedDate);
                                docN.child("time").setValue(c.getTimeInMillis());
                                Toast.makeText(getActivity(), "Token Completed,try for another day", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    dataRef.child("Users").child(user).child("booked docters").child(id).child(selectedDate).setValue("1");
                                    String pkey = dataRef.child("Docters").child(id).child("Bookings").child(selectedDate).push().getKey();
                                    dataRef.child("Booked Keys").child(selectedDate).child(id).child(usrname).setValue(pkey);
                                    dataRef.child("Docters").child(id).child("Bookings").child(selectedDate).child(pkey).setValue(usrname);
                                    dataRef.child("Token Id").child(pkey).child("token").setValue(String.valueOf("1"));
                                    dataRef.child("Token Id").child(pkey).child("doctor").setValue(dataSnapshot.child("Docters").child(id).child("name").getValue().toString());
                                    dataRef.child("Token Id").child(pkey).child("time1").setValue(dataSnapshot.child("Docters").child(id).child("consultation starts at").getValue().toString());
                                    dataRef.child("Token Id").child(pkey).child("time2").setValue(dataSnapshot.child("Docters").child(id).child("consultation ends at").getValue().toString());
                                    dataRef.child("Token Id").child(pkey).child("city").setValue(dataSnapshot.child("Docters").child(id).child("city").getValue().toString());
                                    dataRef.child("Token Id").child(pkey).child("doctorId").setValue(id);
                                    int day=Integer.parseInt(selectedDate.substring(0,2));
                                    int y=Integer.parseInt(selectedDate.substring(7,11));
                                    String mo=selectedDate.substring(3,6);
                                    int m=BookingDetails.getMonth(mo);
                                    dataRef.child("Token Id").child(pkey).child("userId").setValue(usrname+y+m+day);
                                    dataRef.child("Token Id").child(pkey).child("date").setValue(selectedDate);
                                    dataRef.child("Token Id").child(pkey).child("key").setValue(usrname + selectedDate);
                                    dataRef.child("Token Id").child(pkey).child("docKey").setValue(id + selectedDate+"1");
                                    dataRef.child("Token Id").child(pkey).child("doctorKey").setValue(id + selectedDate);
                                    dataRef.child("Token Id").child(pkey).child("uid").setValue(user);
                                    bkPro.setVisibility(View.GONE);
                                    popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
                                    mLayout.getForeground().setAlpha(220);
                                    tk.setText("1");
                                    p_date.setText(selectedDate);
                                    pu_time.setText(timeStart + " To " + timeEnd);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    }
                });
                days.setText(null);
                if (day1 != null) {
                    days.setText(day1);
                }
                if (day2 != null) {
                    if (day1 != null) {
                        days.append("," + day2);
                    } else {
                        days.append(day2);
                    }
                }
                if (day3 != null) {
                    if (day1 != null || day2 != null) {
                        days.append("," + day3);
                    } else {
                        days.append(day3);
                    }
                }
                if (day4 != null) {
                    if (day1 != null || day2 != null || day3 != null) {
                        days.append("," + day4);
                    } else {
                        days.append(day4);
                    }
                }
                if (day5 != null) {
                    if (day1 != null || day2 != null || day3 != null || day4 != null) {
                        days.append("," + day5);
                    } else {
                        days.append(day5);
                    }
                }
                if (day6 != null) {
                    if (day1 != null || day2 != null || day3 != null || day4 != null || day5 != null) {
                        days.append("," + day6);
                    } else {
                        days.append(day6);
                    }
                }
                if (day7 != null) {
                    if (day1 != null || day2 != null || day3 != null || day4 != null || day5 != null || day6 != null) {
                        days.append("," + day7);
                    } else {
                        days.append(day7);
                    }
                }

                List<String> list = new ArrayList<>();
                Calendar c = Calendar.getInstance();
                int cur_year = c.get(Calendar.YEAR);
                int uday = c.get(Calendar.DAY_OF_YEAR);
                for (int i = uday + 1; i < uday + 60; i++) {
                    if (i>365){
                        cur_year=(c.get(Calendar.YEAR))+1;
                        int day=uday+60-365;
                        c.set(Calendar.YEAR,cur_year);
                        for (int j=1;j<day;j++){
                            if (getNameOfDay(cur_year, j).equals(day1)) {
                                c.set(Calendar.DAY_OF_YEAR, j);
                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                String formattedDate = df.format(c.getTime());
                                list.add(formattedDate + "(Sun)");
                            } else if (getNameOfDay(cur_year, j).equals(day2)) {
                                c.set(Calendar.DAY_OF_YEAR, j);
                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                String formattedDate = df.format(c.getTime());
                                list.add(formattedDate + "(Mon)");
                            } else if (getNameOfDay(cur_year, j).equals(day3)) {
                                c.set(Calendar.DAY_OF_YEAR, j);
                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                String formattedDate = df.format(c.getTime());
                                list.add(formattedDate + "(Tue)");
                            } else if (getNameOfDay(cur_year, j).equals(day4)) {
                                c.set(Calendar.DAY_OF_YEAR, j);
                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                String formattedDate = df.format(c.getTime());
                                list.add(formattedDate + "(Wed)");
                            } else if (getNameOfDay(cur_year, j).equals(day5)) {
                                c.set(Calendar.DAY_OF_YEAR, j);
                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                String formattedDate = df.format(c.getTime());
                                list.add(formattedDate + "(Thu)");
                            } else if (getNameOfDay(cur_year, j).equals(day6)) {
                                c.set(Calendar.DAY_OF_YEAR, j);
                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                String formattedDate = df.format(c.getTime());
                                list.add(formattedDate + "(Fri)");
                            } else if (getNameOfDay(cur_year, j).equals(day7)) {
                                c.set(Calendar.DAY_OF_YEAR, j);
                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                String formattedDate = df.format(c.getTime());
                                list.add(formattedDate + "(Sat)");
                            }
                        }
                        break;
                    }
                    if (getNameOfDay(cur_year, i).equals(day1)) {
                        c.set(Calendar.DAY_OF_YEAR, i);
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c.getTime());
                        list.add(formattedDate + "(Sun)");
                    } else if (getNameOfDay(cur_year, i).equals(day2)) {
                        c.set(Calendar.DAY_OF_YEAR, i);
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c.getTime());
                        list.add(formattedDate + "(Mon)");
                    } else if (getNameOfDay(cur_year, i).equals(day3)) {
                        c.set(Calendar.DAY_OF_YEAR, i);
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c.getTime());
                        list.add(formattedDate + "(Tue)");
                    } else if (getNameOfDay(cur_year, i).equals(day4)) {
                        c.set(Calendar.DAY_OF_YEAR, i);
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c.getTime());
                        list.add(formattedDate + "(Wed)");
                    } else if (getNameOfDay(cur_year, i).equals(day5)) {
                        c.set(Calendar.DAY_OF_YEAR, i);
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c.getTime());
                        list.add(formattedDate + "(Thu)");
                    } else if (getNameOfDay(cur_year, i).equals(day6)) {
                        c.set(Calendar.DAY_OF_YEAR, i);
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c.getTime());
                        list.add(formattedDate + "(Fri)");
                    } else if (getNameOfDay(cur_year, i).equals(day7)) {
                        c.set(Calendar.DAY_OF_YEAR, i);
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c.getTime());
                        list.add(formattedDate + "(Sat)");
                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    date.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return v;
    }
    public String getNameOfDay(int year, int dayOfYear) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
        String days[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);

        return days[dayIndex - 1];
    }

}
