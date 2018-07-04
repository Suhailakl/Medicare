package com.jrs.medicare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import java.util.List;

/**
 * Created by SUHAIL on 1/9/2018.
 */

public class DoctorBookedPatients extends Fragment {
    private FirebaseAuth mAuth;
    private String user;
    private DatabaseReference dataRef;
    private Query mQery;
    private RecyclerView recyclerView;
    private TextView count,noBook;
    private ProgressBar progress;
    private MenuItem item;
    private Spinner spin;
    private String d;
    private FrameLayout frame;
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
        View v=inflater.inflate(R.layout.booked_patients,container,false);
        getActivity().setTitle(R.string.bkdPtnts);
        setHasOptionsMenu(true);
        progress= (ProgressBar) v.findViewById(R.id.doc_bkPt_pro);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();
        dataRef = FirebaseDatabase.getInstance().getReference();
        recyclerView= (RecyclerView) v.findViewById(R.id.doc_bkPt_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        spin= (Spinner) v.findViewById(R.id.doc_bkPt_spinner);
        frame= (FrameLayout) v.findViewById(R.id.doc_bkPt_frame);
        frame.getForeground().setAlpha(0);
        noBook= (TextView) v.findViewById(R.id.doc_bkPt_noBook);
        noBook.setVisibility(View.GONE);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                        StartConsult fh=new StartConsult();
                        ft.replace(R.id.content_doc_home,fh);
                        ft.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE);
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
        dataRef.child("Docters").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String day1 = (String) dataSnapshot.child("day1").getValue();
                String day2 = (String) dataSnapshot.child("day2").getValue();
                String day3 = (String) dataSnapshot.child("day3").getValue();
                String day4 = (String) dataSnapshot.child("day4").getValue();
                String day5 = (String) dataSnapshot.child("day5").getValue();
                String day6 = (String) dataSnapshot.child("day6").getValue();
                String day7 = (String) dataSnapshot.child("day7").getValue();
                List<String> list = new ArrayList<>();
                Calendar c = Calendar.getInstance();
                int cur_year = c.get(Calendar.YEAR);
                int uday = c.get(Calendar.DAY_OF_YEAR);
                for (int i = uday; i < uday + 60; i++)

                {
                    if (i > 365) {
                        cur_year = (c.get(Calendar.YEAR)) + 1;
                        int day = uday + 60 - 365;
                        c.set(Calendar.YEAR, cur_year);
                        for (int j = 1; j < day; j++) {
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
                    spin.setAdapter(adapter);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, final View view, int position, long id) {
                progress.setVisibility(View.VISIBLE);
                d=spin.getSelectedItem().toString();
                mQery=dataRef.child("Token Id").orderByChild("docKey").startAt(user+d).endAt(user+d+"\uf8ff");
                FirebaseRecyclerAdapter<DocTok,DocTokBookedHolder> adapter=new FirebaseRecyclerAdapter<DocTok, DocTokBookedHolder>(
                        DocTok.class,
                        R.layout.booked_patients_recycler,
                        DocTokBookedHolder.class,
                        mQery
                ) {
                    @Override
                    protected void populateViewHolder(final DocTokBookedHolder viewHolder, final DocTok model, final int position) {
                    String uid=model.getUid();
                        viewHolder.itemView.setVisibility(View.GONE);
                        viewHolder.setTk(model.getToken()+".");
                        dataRef.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                viewHolder.setName(dataSnapshot.child("name").getValue().toString());
                                viewHolder.setMobNo(dataSnapshot.child("mobile number").getValue().toString());
                                viewHolder.setUsrName("Id : "+dataSnapshot.child("username").getValue().toString());
                                progress.setVisibility(View.GONE);
                                viewHolder.itemView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        DatabaseReference ref=getRef(position);
                        final String key=ref.getKey();
                        viewHolder.btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                progress.setVisibility(View.VISIBLE);
                                frame.getForeground().setAlpha(220);
                                final int tk = Integer.parseInt(model.getToken());
                                dataRef.child("Token Id").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String date = (String) dataSnapshot.child(key).child("date").getValue();
                                        final String doc = (String) dataSnapshot.child(key).child("doctorId").getValue();
                                        final String userId= (String) dataSnapshot.child(key).child("uid").getValue();
                                        dataRef.child("Token Id").orderByChild("doctorKey").equalTo(doc + date).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                    String token = (String) data.child("token").getValue();
                                                    if (Integer.parseInt(token) > tk) {
                                                        dataRef.child("Token Id").child(data.getKey()).child("token").setValue(String.valueOf(Integer.parseInt(token) - 1));
                                                        dataRef.child("Token Id").child(data.getKey()).child("docKey").setValue(user+date+String.valueOf(Integer.parseInt(token) - 1));
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
                                                        frame.getForeground().setAlpha(0);
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
                    }
                    @Override
                    public void onDataChanged() {
                        if (getItemCount() == 0) {
                            progress.setVisibility(View.GONE);
                            noBook.setVisibility(View.VISIBLE);
                        }
                        else {
                            progress.setVisibility(View.GONE);
                            noBook.setVisibility(View.GONE);
                        }
                        super.onDataChanged();
                    }
                };

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        super.onViewCreated(view, savedInstanceState);
        }

    public void notification(){
        dataRef.child("Doctor Notification").child(user).addValueEventListener(new ValueEventListener() {
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
                    if (data.hasChild("dId")){
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
    public String getNameOfDay(int year, int dayOfYear) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
        String days[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);

        return days[dayIndex - 1];
    }
    public static class DocTokBookedHolder extends RecyclerView.ViewHolder {
        TextView name, usrName, mobNo,tk;
        private Button btn;

        public DocTokBookedHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.doc_bkPt_name);
            usrName = (TextView) itemView.findViewById(R.id.doc_bkPt_usrName);
            mobNo = (TextView) itemView.findViewById(R.id.doc_bkPt_no);
            btn = (Button) itemView.findViewById(R.id.doc_bkPt_btn_cncl);
            tk= (TextView) itemView.findViewById(R.id.doc_bkPt__tk);
        }

        public void setName(String s) {
            name.setText(s);
        }

        public void setUsrName(String s) {
            usrName.setText(s);
        }

        public void setMobNo(String s) {
            mobNo.setText(s);
        }

        public void setTk(String s) {
            tk.setText(s);
        }
    }
}
