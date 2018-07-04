package com.jrs.medicare;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Timer;


public class BookingDetails extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference dataRef;
    private FirebaseAuth mAuth;
    private ArrayList<String> uids;
    private String user, usrnm, d;
    private String mm, curDate, tk;
    private int hr, hour, ddd, mnt, yr;
    private String mnth[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private static HashSet<String> hs = new HashSet<String>();
    private ArrayList<String> docList = new ArrayList<>();
    private Query mQuery;
    private TextView noBook, cnsltDate, dctName, crntToken, yrTkn, strsIn,strtsIn,yrTime;
    private String cnsltTime, cnsltEnd;
    private ProgressBar progress;
    private Timer t = new Timer();
    private Handler h = new Handler();
    private ArrayList<String> dates;
    private LayoutInflater inflater;
    public  FrameLayout mLayout;
    private PopupWindow popupWindow;
    private int mt, hrE, mtE, actHrInMt;
    private Calendar c,ca;
    private long daytime;
    private CountDownTimer timer;

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uids = new ArrayList<>();
        dataRef.child("Usernames").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usrnm = dataSnapshot.getValue().toString();
                mQuery = dataRef.child("Token Id").orderByChild("userId").startAt(usrnm).endAt(usrnm + "\uf8ff");
                recyclerView.addItemDecoration(new VerticalItemDecoration(20, false));
                final FirebaseRecyclerAdapter<DocTok, DocTokViewHolder> adapter = new FirebaseRecyclerAdapter<DocTok, DocTokViewHolder>(
                        DocTok.class,
                        R.layout.token_recycler_view,
                        DocTokViewHolder.class,
                        mQuery
                )

                {

                    @Override
                    public void onDataChanged() {
                        if (getItemCount() == 0) {
                            progress.setVisibility(View.GONE);
                            noBook.setVisibility(View.VISIBLE);
                        }
                        super.onDataChanged();
                    }

                    @Override
                    public void cleanup() {
                        super.cleanup();
                    }

                    @Override
                    public void onViewRecycled(DocTokViewHolder holder) {
                        super.onViewRecycled(holder);
                    }

                    @Override
                    protected void populateViewHolder(final DocTokViewHolder viewHolder, final DocTok model, final int position) {
                        viewHolder.setDoctor("Dr." + model.getDoctor());
                        viewHolder.setDate(model.getDate());
                        viewHolder.setTime1(model.getTime1());
                        viewHolder.setTime2(model.getTime2());
                        viewHolder.setToken(model.getToken());
                        tk = model.getToken();
                        final String docId = model.getDoctorId();
                        progress.setVisibility(View.GONE);
                        dates = new ArrayList<String>();
                        dates.add(model.getDate());
                        if (!removeDates(model.getDate(), model.getTime2())) {
                            DatabaseReference mRef = getRef(position);
                            final String key = mRef.getKey();
                            final int tk = Integer.parseInt(model.getToken());
                            dataRef.child("Token Id").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final String uid = (String) dataSnapshot.child(key).child("userId").getValue();
                                    final String date = (String) dataSnapshot.child(key).child("date").getValue();
                                    final String doc = (String) dataSnapshot.child(key).child("doctorId").getValue();
                                    dataRef.child("Token Id").orderByChild("docKey").equalTo(doc + date).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            ArrayList<String> list = new ArrayList<String>();
                                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                String token = (String) data.child("token").getValue();
                                                if (Integer.parseInt(token) > tk) {
                                                    dataRef.child("Token Id").child(data.getKey()).child("token").setValue(String.valueOf(Integer.parseInt(token) - 1));
                                                }
                                            }
                                            dataRef.child("Booked Keys").child(date).child(doc).child(uid).removeValue();
                                            dataRef.child("Docters").child(doc).child("Bookings").child(date).child(key).removeValue();
                                            dataRef.child("Users").child(user).child("booked docters").child(doc).child(date).removeValue();
                                            dataRef.child("Token Id").child(key).removeValue();
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
                        }
                        DatabaseReference mRef = getRef(position);
                        final String key = mRef.getKey();
                        viewHolder.cncel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                progress.setVisibility(View.VISIBLE);
                                mLayout.getForeground().setAlpha(220);
                                final int tk = Integer.parseInt(model.getToken());
                                dataRef.child("Token Id").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userId = (String) dataSnapshot.child(key).child("uid").getValue();
                                        final String date = (String) dataSnapshot.child(key).child("date").getValue();
                                        final String doc = (String) dataSnapshot.child(key).child("doctorId").getValue();
                                        dataRef.child("Token Id").orderByChild("doctorKey").equalTo(doc + date).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                ArrayList<String> list = new ArrayList<String>();
                                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                    String token = (String) data.child("token").getValue();
                                                    if (Integer.parseInt(token) > tk) {
                                                        dataRef.child("Token Id").child(data.getKey()).child("token").setValue(String.valueOf(Integer.parseInt(token) - 1));
                                                    }
                                                }
                                                dataRef.child("Usernames").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String uid=dataSnapshot.getValue().toString();
                                                        dataRef.child("Booked Keys").child(date).child(doc).child(uid).removeValue();
                                                        dataRef.child("Docters").child(doc).child("Bookings").child(date).child(key).removeValue();
                                                        dataRef.child("Users").child(user).child("booked docters").child(doc).child(date).removeValue();
                                                        dataRef.child("Token Id").child(key).removeValue();
                                                        Toast.makeText(getActivity(), "Booking Cancelled", Toast.LENGTH_SHORT).show();
                                                        progress.setVisibility(View.GONE);
                                                        mLayout.getForeground().setAlpha(0);
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
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        });
                        yrTkn.setText("Your Token  : " + model.getToken());
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                mLayout.getForeground().setAlpha(220);
                                popupWindow.showAtLocation(mLayout, Gravity.CENTER, 0, 0);
                                d = model.getDate().substring(0, 11);
                                String s = model.getDoctorId() + model.getDate();
                                consultTime(docId,d,s,model.getToken());

                            }
                        });
                    }
                };
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (in.isActive()) {
            in.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        final View v = inflater.inflate(R.layout.activity_booking_details, container, false);
        if (container != null) {
            container.removeAllViews();
        }
        getActivity().setTitle(R.string.bkngs);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        FragmentHome fh = new FragmentHome();
                        ft.replace(R.id.content_user_home, fh);
                        ft.addToBackStack(null);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                        return true;
                    }
                }
                return false;
            }
        });
        noBook = (TextView) v.findViewById(R.id.noBook);
        noBook.setVisibility(View.INVISIBLE);
        recyclerView = (RecyclerView) v.findViewById(R.id.book_detail_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();
        c = Calendar.getInstance();
        ca=Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat dd = new SimpleDateFormat("dd");
        SimpleDateFormat mmm = new SimpleDateFormat("MMM");
        ddd = Integer.parseInt(dd.format(c.getTime()));
        curDate = df.format(c.getTime());
        mm = mmm.format(c.getTime());
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
        hour = c.get(Calendar.HOUR_OF_DAY);
        mnt = c.get(Calendar.MINUTE);
        yr = c.get(Calendar.YEAR);
        dataRef = FirebaseDatabase.getInstance().getReference();
        mLayout = (FrameLayout) v.findViewById(R.id.activity_booking_details_frame);
        mLayout.getForeground().setAlpha(0);
        progress = (ProgressBar) v.findViewById(R.id.rec_progress);
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popUp = inflater.inflate(R.layout.popup_token_details, null);
        crntToken = (TextView) popUp.findViewById(R.id.popUp_crnt_tkn);
        yrTkn = (TextView) popUp.findViewById(R.id.popUp_yr_tkn);
        crntToken.setVisibility(View.GONE);
        strsIn = (TextView) popUp.findViewById(R.id.popUp_strs_in);
        strtsIn= (TextView) popUp.findViewById(R.id.strtsIn);
        yrTime= (TextView) popUp.findViewById(R.id.popUp_yr_time);
        yrTime.setVisibility(View.GONE);
        popupWindow = new PopupWindow(popUp, 650, 750, true);
        popUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mLayout.getForeground().setAlpha(0);
                popupWindow.dismiss();
                BookingDetails bd = new BookingDetails();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.activity_booking_details, bd);
                ft.commit();
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
        return v;
    }

    public static class DocTokViewHolder extends RecyclerView.ViewHolder {
        TextView tkno, docName, cDate, time;
        private Button cncel;

        public DocTokViewHolder(final View itemView) {
            super(itemView);
            tkno = (TextView) itemView.findViewById(R.id.rec_tkno);
            docName = (TextView) itemView.findViewById(R.id.rec_name);
            cDate = (TextView) itemView.findViewById(R.id.rec_date);
            time = (TextView) itemView.findViewById(R.id.rec_time);
            cncel = (Button) itemView.findViewById(R.id.rec_btn);
        }
        public void setToken(String token) {
            tkno.setText(token);
        }

        public void setDoctor(String doctor) {
            docName.setText(doctor);
        }

        public void setDate(String date) {
            cDate.setText(date);
        }

        public void setTime1(String time1) {
            time.setText(time1);
        }

        public void setTime2(String time2) {
            time.append(" To " + time2);
        }


    }

    public boolean removeDates(String s, String st) {
            if (Integer.parseInt(s.substring(0, 2)) > ddd && s.substring(3, 6).equals(mm)) {
                return true;
            }
                /*else if (s.substring(0, 11).equals(curDate)) {
                    int hour = 0;
                    if (st.contains("P") && !st.equals("12PM")) {
                        hour = Integer.parseInt(st.substring(0, 1)) + 12;
                    } else if (st.contains("A") || st.equals("12PM")) {
                        hour = Integer.parseInt(st.substring(0, 1));
                    } else if (st.equals("12AM")) {
                        hour = Integer.parseInt(st.substring(0, 1)) + 12;
                    }*/
                if (s.substring(0, 11).equals(curDate)) {
                    if (st.length() == 3) {
                        if (st.contains("AM")) {
                            hr = Integer.valueOf(st.substring(0, 1));
                            mt = 0;

                        } else {
                            hr = Integer.valueOf(st.substring(0, 1)) + 12;
                            mt = 0;
                        }
                    } else if (st.length() == 6) {
                        if (st.contains("AM")) {
                            hr = Integer.valueOf(st.substring(0, 1));
                            mt = Integer.valueOf(st.substring(2, 4));
                        } else {
                            hr = Integer.valueOf(st.substring(0, 1)) + 12;
                            mt = Integer.valueOf(st.substring(2, 4));
                        }
                    } else if (st.length() == 7) {
                        if (st.contains("AM")) {
                            hr = Integer.valueOf(st.substring(0, 2));
                            mt = Integer.valueOf(st.substring(3, 5));
                        } else {
                            hr = Integer.valueOf(st.substring(0, 2)) + 12;
                            mt = Integer.valueOf(st.substring(3, 5));
                        }
                    } else {
                        if (st.contains("AM")) {
                            hr = Integer.valueOf(st.substring(0, 2));
                            mt = 0;
                        } else {
                            hr = Integer.valueOf(st.substring(0, 2)) + 12;
                            mt = 0;
                        }
                    }
                    if (hour < hr) {
                        return true;
                    } else if (hour == hr) {
                        if (mt != 0) {
                            if (mnt < mt) {
                                return true;
                            }

                        }
                    }
                }

        for (int k = 0; k < 12; k++) {
            if (mnth[k].equals(mm) && !mnth[k].equals("Dec")) {
                if (s.substring(3, 6).equals(mnth[k + 1])) {
                    return true;
                }
            }
        }
        for (int k = 0; k < 12; k++) {
            if (mnth[k].equals(mm) && !mnth[k].equals("Nov") && !mnth[k].equals("Dec")) {
                if (s.substring(3, 6).equals(mnth[k + 2])) {
                    return true;
                }
            }
        }
        return false;
    }

    public void consultTime(final String uid, final String date, final String s, final String d) {
        dataRef.child("Docters").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cnsltTime = dataSnapshot.child("consultation starts at").getValue().toString();
                cnsltEnd = dataSnapshot.child("consultation ends at").getValue().toString();
                if (cnsltTime.length() == 3) {
                    if (cnsltTime.contains("AM")) {
                        hr = Integer.valueOf(cnsltTime.substring(0, 1));
                        mt = 0;

                    } else {
                        hr = Integer.valueOf(cnsltTime.substring(0, 1)) + 12;
                        mt = 0;
                    }
                } else if (cnsltTime.length() == 6) {
                    if (cnsltTime.contains("AM")) {
                        hr = Integer.valueOf(cnsltTime.substring(0, 1));
                        mt = Integer.valueOf(cnsltTime.substring(2, 4));
                    } else {
                        hr = Integer.valueOf(cnsltTime.substring(0, 1)) + 12;
                        mt = Integer.valueOf(cnsltTime.substring(2, 4));
                    }
                } else if (cnsltTime.length() == 7) {
                    if (cnsltTime.contains("AM")) {
                        hr = Integer.valueOf(cnsltTime.substring(0, 2));
                        mt = Integer.valueOf(cnsltTime.substring(3, 5));
                    } else {
                        hr = Integer.valueOf(cnsltTime.substring(0, 2)) + 12;
                        mt = Integer.valueOf(cnsltTime.substring(3, 5));
                    }
                } else {
                    if (cnsltTime.contains("AM")) {
                        hr = Integer.valueOf(cnsltTime.substring(0, 2));
                        mt = 0;
                    } else {
                        hr = Integer.valueOf(cnsltTime.substring(0, 2)) + 12;
                        mt = 0;
                    }
                }
                if (cnsltEnd.length() == 3) {
                    if (cnsltEnd.contains("AM")) {
                        hrE = Integer.valueOf(cnsltEnd.substring(0, 1));
                        mtE = 0;

                    } else {
                        hrE = Integer.valueOf(cnsltEnd.substring(0, 1)) + 12;
                        mtE = 0;
                    }
                } else if (cnsltEnd.length() == 6) {
                    if (cnsltEnd.contains("AM")) {
                        hrE = Integer.valueOf(cnsltEnd.substring(0, 1));
                        mtE = Integer.valueOf(cnsltEnd.substring(2, 4));
                    } else {
                        hrE = Integer.valueOf(cnsltEnd.substring(0, 1)) + 12;
                        mtE = Integer.valueOf(cnsltEnd.substring(2, 4));
                    }
                } else if (cnsltEnd.length() == 7) {
                    if (cnsltEnd.contains("AM")) {
                        hrE = Integer.valueOf(cnsltEnd.substring(0, 2));
                        mtE = Integer.valueOf(cnsltEnd.substring(3, 5));
                    } else {
                        hrE = Integer.valueOf(cnsltEnd.substring(0, 2)) + 12;
                        mtE = Integer.valueOf(cnsltEnd.substring(3, 5));
                    }
                } else {
                    if (cnsltEnd.contains("AM")) {
                        hrE = Integer.valueOf(cnsltEnd.substring(0, 2));
                        mtE = 0;
                    } else {
                        hrE = Integer.valueOf(cnsltEnd.substring(0, 2)) + 12;
                        mtE = 0;
                    }
                }
                int day = Integer.parseInt(date.substring(0, 2));
                int y = Integer.parseInt(date.substring(7, 11));
                String mo = date.substring(3, 6);
                int m = getMonth(mo);
                long cur = c.getTimeInMillis();
                c.set(Calendar.DAY_OF_MONTH, day);
                c.set(Calendar.MONTH, m);
                c.set(Calendar.YEAR, y);
                c.set(Calendar.HOUR_OF_DAY, hr);
                c.set(Calendar.MINUTE, mt);
                long cT = c.getTimeInMillis();
                c.set(Calendar.DAY_OF_MONTH, day);
                c.set(Calendar.MONTH, m);
                c.set(Calendar.YEAR, y);
                c.set(Calendar.HOUR_OF_DAY, hrE);
                c.set(Calendar.MINUTE, mtE);
                long cE = c.getTimeInMillis();
                if (cE > cur) {
                    daytime = cT - cur;
                }
                timer = new CountDownTimer(daytime, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        int mDisplayHours = (int) ((millisUntilFinished / 1000) / 3600);
                        if (mDisplayHours < 24) {
                            strsIn.setText("0 d  ");
                            strsIn.append(String.valueOf(mDisplayHours + "hr  "));
                            int mDisplayM = (int) ((millisUntilFinished / 1000) / 60) - (mDisplayHours * 60);
                            strsIn.append(String.valueOf(mDisplayM + "m  "));
                            int mDisplayS = (int) ((millisUntilFinished / 1000)) - (mDisplayHours * 3600) - mDisplayM * 60;
                            strsIn.append(String.valueOf(mDisplayS + "s "));
                        } else if (mDisplayHours < 48) {
                            strsIn.setText("1 d ");
                            int mDisplayH = mDisplayHours - 24;
                            strsIn.append(String.valueOf(mDisplayH + "hr  "));
                            int mDisplayM = (int) ((millisUntilFinished / 1000) / 60) - (1440 + (mDisplayH * 60));
                            strsIn.append(String.valueOf(mDisplayM + "m  "));
                            int mDisplayS = (int) ((millisUntilFinished / 1000)) - (mDisplayHours * 60 * 60) - mDisplayM * 60;
                            strsIn.append(String.valueOf(mDisplayS + "s  "));
                        } else if (mDisplayHours < 72) {
                            strsIn.setText("2 d  ");
                            int mDisplayH = mDisplayHours - 48;
                            strsIn.append(String.valueOf(mDisplayH + "hr  "));
                            int mDisplayM = (int) ((millisUntilFinished / 1000) / 60) - (2880 + (mDisplayH * 60));
                            strsIn.append(String.valueOf(mDisplayM + "mt  "));
                            int mDisplayS = (int) ((millisUntilFinished / 1000)) - (mDisplayHours * 60 * 60) - mDisplayM * 60;
                            strsIn.append(String.valueOf(mDisplayS + "s  "));
                        } else if (mDisplayHours < 96) {
                            strsIn.setText("3 d  ");
                            int mDisplayH = mDisplayHours - 72;
                            strsIn.append(String.valueOf(mDisplayH + "hr  "));
                            int mDisplayM = (int) ((millisUntilFinished / 1000) / 60) - (4320 + (mDisplayH * 60));
                            strsIn.append(String.valueOf(mDisplayM + "m  "));
                            int mDisplayS = (int) ((millisUntilFinished / 1000)) - (mDisplayHours * 60 * 60) - mDisplayM * 60;
                            strsIn.append(String.valueOf(mDisplayS + "s  "));
                        } else if (mDisplayHours < 120) {
                            strsIn.setText("4 d  ");
                            int mDisplayH = mDisplayHours - 96;
                            strsIn.append(String.valueOf(mDisplayH + "hr  "));
                            int mDisplayM = (int) ((millisUntilFinished / 1000) / 60) - (5760 + (mDisplayH * 60));
                            strsIn.append(String.valueOf(mDisplayM + "m  "));
                            int mDisplayS = (int) ((millisUntilFinished / 1000)) - (mDisplayHours * 60 * 60) - mDisplayM * 60;
                            strsIn.append(String.valueOf(mDisplayS + "s  "));
                        } else if (mDisplayHours < 144) {
                            strsIn.setText("5 d  ");
                            int mDisplayH = mDisplayHours - 120;
                            strsIn.append(String.valueOf(mDisplayH + "hr  "));
                            int mDisplayM = (int) ((millisUntilFinished / 1000) / 60) - (7200 + (mDisplayH * 60));
                            strsIn.append(String.valueOf(mDisplayM + "m  "));
                            int mDisplayS = (int) ((millisUntilFinished / 1000)) - (mDisplayHours * 60 * 60) - mDisplayM * 60;
                            strsIn.append(String.valueOf(mDisplayS + "s  "));
                        } else if (mDisplayHours < 168) {
                            strsIn.setText("6 d  ");
                            int mDisplayH = mDisplayHours - 144;
                            strsIn.append(String.valueOf(mDisplayH + "hr  "));
                            int mDisplayM = (int) ((millisUntilFinished / 1000) / 60) - (8640 + (mDisplayH * 60));
                            strsIn.append(String.valueOf(mDisplayM + "m  "));
                            int mDisplayS = (int) ((millisUntilFinished / 1000)) - (mDisplayHours * 60 * 60) - mDisplayM * 60;
                            strsIn.append(String.valueOf(mDisplayS + "s  "));
                        } else {
                            strsIn.setText("Weeks Later");
                        }

                    }

                    @Override
                    public void onFinish() {
                        if (d.equals("1")){
                            Toast.makeText(getActivity(), "Your consultation time started", Toast.LENGTH_LONG).show();
                            crntToken.setText("Current Token  : ");
                            crntToken.append("1");
                            yrTime.setVisibility(View.VISIBLE);
                        }
                        crntToken.setVisibility(View.VISIBLE);
                        dataRef.child("Token Id").orderByChild("doctorKey").equalTo(s).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {

                                    if (data.hasChild("active")) {
                                        String tok = data.child("token").getValue().toString();
                                        crntToken.setText("Current Token  : ");
                                        crntToken.append(tok);
                                        if (Integer.parseInt(d)<Integer.parseInt(tok)){
                                            Toast.makeText(getActivity(), "Your Consultation completed", Toast.LENGTH_LONG).show();
                                            popupWindow.dismiss();
                                            mLayout.setAlpha(0);

                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //strsIn.setVisibility(View.GONE);
                       // else if (Integer.parseInt(dKey)>)
                        endTime(uid);

                    }
                };
                timer.start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static int getMonth(String m) {
        int mnth = 0;
        if (m.equals("Feb")) {
            mnth = 1;
        } else if (m.equals("Mar")) {
            mnth = 2;
        } else if (m.equals("Apr")) {
            mnth = 3;
        } else if (m.equals("May")) {
            mnth = 4;
        } else if (m.equals("Jun")) {
            mnth = 5;
        } else if (m.equals("Jul")) {
            mnth = 6;
        } else if (m.equals("Aug")) {
            mnth = 7;
        } else if (m.equals("Sep")) {
            mnth = 8;
        } else if (m.equals("Oct")) {
            mnth = 9;
        } else if (m.equals("Nov")) {
            mnth = 10;
        } else if (m.equals("Dec")) {
            mnth = 11;
        }
        return mnth;
    }

    public void endTime(String uid) {
        strtsIn.setText("Ends In...");
        dataRef.child("Docters").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String star = dataSnapshot.child("consultation starts at").getValue().toString();
                String en = dataSnapshot.child("consultation ends at").getValue().toString();
                int hr, mt = 0;
                long start = 0, end;
                start = (ca.get(Calendar.HOUR_OF_DAY) * (1000 * 60 * 60)) + (ca.get(Calendar.MINUTE)) * (1000 * 60);
                int h, m = 0;
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
                }   else if (en.length()==7) {
                    if (en.contains("AM")) {
                        h = Integer.valueOf(en.substring(0, 2));
                        m = Integer.valueOf(en.substring(3, 5));
                    } else {
                        h = Integer.valueOf(en.substring(0, 2)) + 12;
                        m = Integer.valueOf(en.substring(3, 5));
                    }
                }
                else
                    {
                        if (en.contains("AM")) {
                            h = Integer.valueOf(en.substring(0, 2));
                            m = 0;
                        } else {
                            h = Integer.valueOf(en.substring(0, 2)) + 12;
                            m = 0;
                        }
                }
                end = (h * 1000 * 60 * 60) + (m * 1000 * 60);
                final long time = (end - start);
                CountDownTimer timer = new CountDownTimer(time, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long hr = (millisUntilFinished / 1000) / 3600;
                        long mt = ((millisUntilFinished / 1000) / 60) - (hr * 60);
                        long se = (millisUntilFinished / 1000) - (hr * 60 * 60) - (mt * 60);
                        strsIn.setText(hr + "h:" + mt + "m" + ":" + se + "s");
                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(getActivity(), "Consultation Completed", Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                        mLayout.setAlpha(0);
                    }
                };
                timer.start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

