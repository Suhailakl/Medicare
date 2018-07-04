package com.jrs.medicare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class SendTestResult extends Fragment{
    private static final int PICK_IMAGE_REQUEST =234 ;
    private Spinner spin,dateSpin;
    private EditText sbjct;
    private Button choose,upld;
    private DatabaseReference dataRef;
    private FirebaseAuth mAuth;
    private String user;
    private long count;
    private Uri filePath,downUri;
    private StorageReference sRef;
    private ImageView image;
    private LinearLayout l1,l3;
    private RelativeLayout l2,l4;
    private Calendar c;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_REQUEST&&resultCode== Activity.RESULT_OK&&data!=null&&data.getData()!=null){
            filePath=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),filePath);
                image.setImageBitmap(bitmap);
                image.setVisibility(View.VISIBLE);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container!=null){
            container.removeAllViews();
        }
        getActivity().setTitle(R.string.tstRslt);
        InputMethodManager in=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        View v= inflater.inflate(R.layout.activity_send_test_result,container,false);
        sRef= FirebaseStorage.getInstance().getReference();
        spin= (Spinner) v.findViewById(R.id.tstSndSpinner);
        dateSpin= (Spinner) v.findViewById(R.id.cnsltSpin);
        sbjct= (EditText) v.findViewById(R.id.tstSndSbjct);
        choose= (Button) v.findViewById(R.id.chooseBtn);
        upld= (Button) v.findViewById(R.id.upldBtn);
        image= (ImageView) v.findViewById(R.id.test_image);
        dataRef= FirebaseDatabase.getInstance().getReference();
        c=Calendar.getInstance();
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser().getUid();
        l1= (LinearLayout) v.findViewById(R.id.send_test_result_l1);
        l2= (RelativeLayout) v.findViewById(R.id.send_test_result_l2);
        l3= (LinearLayout) v.findViewById(R.id.send_test_result_l3);
        l4= (RelativeLayout) v.findViewById(R.id.send_test_result_l4);
        l1.setVisibility(View.GONE);
        l3.setVisibility(View.GONE);
        l4.setVisibility(View.GONE);
        l2.setVisibility(View.GONE);
        spinnerAdapt();
        upld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
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
    public void spinnerAdapt(){
        final List<String> dateList=new ArrayList<>();
        final List<String> list=new ArrayList<>();
        final   List<String> uidArray=new ArrayList<>();
        dataRef.child("Medical Record").orderByChild("uid").equalTo(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),R.layout.spinner,list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin.setAdapter(adapter);
                count=dataSnapshot.getChildrenCount();
                for (final DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.exists()) {
                        if (!list.contains(data.child("doctor").getValue().toString())) {
                            list.add(data.child("doctor").getValue().toString());
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
                if (list.isEmpty()){
                    l2.setVisibility(View.VISIBLE);
                    l2.setGravity(RelativeLayout.CENTER_IN_PARENT);
                }
                else {
                    l1.setVisibility(View.VISIBLE);
                    l3.setVisibility(View.VISIBLE);
                    l4.setVisibility(View.VISIBLE);
                   // showFileChooser();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dateList.clear();

                final String s = spin.getSelectedItem().toString();
                dataRef.child("Medical Record").orderByChild("spinnerKey").equalTo(s+user).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayAdapter<String> dateAdapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,dateList);
                        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dateSpin.setAdapter(dateAdapter);
                        for (DataSnapshot data:dataSnapshot.getChildren()) {
                            dateList.add(data.child("date").getValue().toString());
                            dateAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void showFileChooser(){
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select An Image"),PICK_IMAGE_REQUEST);
    }
    private void uploadFile(){
        if (filePath!=null&&dateSpin.getSelectedItem()!=null) {
            final ProgressDialog pd = new ProgressDialog(getActivity());
            pd.setTitle("Uploading...");
            pd.show();

            dataRef.child("Medical Record").orderByChild("spinnerKey").equalTo(spin.getSelectedItem().toString() + user).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    for (final DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data.child("date").getValue().toString().equals(dateSpin.getSelectedItem().toString())) {
                            final String path = data.getKey();
                            if (!data.child("Test Results").child("image1").exists()) {
                                StorageReference storageReference = sRef.child("Test Results/" + path + "/" + "img1.jpg");
                                storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downUri = taskSnapshot.getDownloadUrl();
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image1").child("img1").setValue(downUri.toString());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image1").child("sbj1").setValue(sbjct.getText().toString().trim());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image1").child("time1").setValue(c.getTimeInMillis());
                                        long time=c.getTimeInMillis();
                                        DatabaseReference mRef=dataRef.child("Test Results").push().getRef();
                                        mRef.child("image").setValue(downUri.toString());
                                        mRef.child("subject").setValue(sbjct.getText().toString().trim());
                                        mRef.child("time").setValue(time);
                                        mRef.child("key").setValue(data.child("doctorId").getValue().toString()+time);
                                        mRef.child("uid").setValue(user);
                                        mRef.child("name").setValue(data.child("patient").getValue().toString());
                                        mRef.child("docKey").setValue(data.child("docKey").getValue().toString());
                                        mRef.child("date").setValue(data.child("date").getValue().toString());
                                        mRef.child("dateKey").setValue(data.child("doctorId").getValue().toString()+data.child("date").getValue().toString());
                                        mRef.child("mRkey").setValue(path);
                                        DatabaseReference pRef=dataRef.child("Doctor Notification").child(data.child("doctorId").getValue().toString()).push().getRef();
                                        pRef.child("uid").setValue(user);
                                        Calendar ca=Calendar.getInstance();
                                        pRef.child("time").setValue(ca.getTimeInMillis());
                                        pRef.child("dId").setValue(data.child("doctorId").getValue().toString());
                                        pRef.child("tActive").setValue("Yes");
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Image Successfully Sent to doctor", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        pd.setMessage((int) progress + "% Uploaded...");

                                    }
                                });
                            }
                            else if (!data.child("Test Results").child("image2").exists()){
                                StorageReference storageReference = sRef.child("Test Results/" + path + "/" + "img2.jpg");
                                storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downUri = taskSnapshot.getDownloadUrl();
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image2").child("img2").setValue(downUri.toString());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image2").child("sbj2").setValue(sbjct.getText().toString().trim());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image2").child("time2").setValue(c.getTimeInMillis());
                                        long time=c.getTimeInMillis();
                                        DatabaseReference mRef=dataRef.child("Test Results").push().getRef();
                                        mRef.child("image").setValue(downUri.toString());
                                        mRef.child("subject").setValue(sbjct.getText().toString().trim());
                                        mRef.child("time").setValue(time);
                                        mRef.child("key").setValue(data.child("doctorId").getValue().toString()+time);
                                        mRef.child("uid").setValue(user);
                                        mRef.child("name").setValue(data.child("patient").getValue().toString());
                                        mRef.child("docKey").setValue(data.child("docKey").getValue().toString());
                                        mRef.child("date").setValue(data.child("date").getValue().toString());
                                        mRef.child("mRkey").setValue(path);
                                        mRef.child("dateKey").setValue(data.child("doctorId").getValue().toString()+data.child("date").getValue().toString());
                                        DatabaseReference pRef=dataRef.child("Doctor Notification").child(data.child("doctorId").getValue().toString()).push().getRef();
                                        pRef.child("uid").setValue(user);
                                        Calendar ca=Calendar.getInstance();
                                        pRef.child("time").setValue(ca.getTimeInMillis());
                                        pRef.child("dId").setValue(data.child("doctorId").getValue().toString());
                                        pRef.child("tActive").setValue("Yes");
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Image Successfully Sent to doctor", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        pd.setMessage((int) progress + "% Uploaded...");

                                    }
                                });
                            }
                            else if (!data.child("Test Results").child("image3").exists()){
                                StorageReference storageReference = sRef.child("Test Results/" + path + "/" + "img3.jpg");
                                storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downUri = taskSnapshot.getDownloadUrl();

                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image3").child("img3").setValue(downUri.toString());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image3").child("sbj3").setValue(sbjct.getText().toString().trim());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image3").child("time3").setValue(c.getTimeInMillis());
                                        long time=c.getTimeInMillis();
                                        DatabaseReference mRef=dataRef.child("Test Results").push().getRef();
                                        mRef.child("image").setValue(downUri.toString());
                                        mRef.child("subject").setValue(sbjct.getText().toString().trim());
                                        mRef.child("time").setValue(time);
                                        mRef.child("key").setValue(data.child("doctorId").getValue().toString()+time);
                                        mRef.child("uid").setValue(user);
                                        mRef.child("name").setValue(data.child("patient").getValue().toString());
                                        mRef.child("docKey").setValue(data.child("docKey").getValue().toString());
                                        mRef.child("date").setValue(data.child("date").getValue().toString());
                                        mRef.child("mRkey").setValue(path);
                                        mRef.child("dateKey").setValue(data.child("doctorId").getValue().toString()+data.child("date").getValue().toString());
                                        DatabaseReference pRef=dataRef.child("Doctor Notification").child(data.child("doctorId").getValue().toString()).push().getRef();
                                        pRef.child("uid").setValue(user);
                                        Calendar ca=Calendar.getInstance();
                                        pRef.child("time").setValue(ca.getTimeInMillis());
                                        pRef.child("dId").setValue(data.child("doctorId").getValue().toString());
                                        pRef.child("tActive").setValue("Yes");
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Image Successfully Sent to doctor", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        pd.setMessage((int) progress + "% Uploaded...");

                                    }
                                });
                            }
                            else if (!data.child("Test Results").child("image4").exists()){
                                StorageReference storageReference = sRef.child("Test Results/" + path + "/" + "img4.jpg");
                                storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downUri = taskSnapshot.getDownloadUrl();

                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image4").child("img4").setValue(downUri.toString());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image4").child("sbj4").setValue(sbjct.getText().toString().trim());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image4").child("time4").setValue(c.getTimeInMillis());
                                        long time=c.getTimeInMillis();
                                        DatabaseReference mRef=dataRef.child("Test Results").push().getRef();
                                        mRef.child("image").setValue(downUri.toString());
                                        mRef.child("subject").setValue(sbjct.getText().toString().trim());
                                        mRef.child("time").setValue(time);
                                        mRef.child("key").setValue(data.child("doctorId").getValue().toString()+time);
                                        mRef.child("uid").setValue(user);
                                        mRef.child("name").setValue(data.child("patient").getValue().toString());
                                        mRef.child("docKey").setValue(data.child("docKey").getValue().toString());
                                        mRef.child("date").setValue(data.child("date").getValue().toString());
                                        mRef.child("mRkey").setValue(path);
                                        mRef.child("dateKey").setValue(data.child("doctorId").getValue().toString()+data.child("date").getValue().toString());
                                        DatabaseReference pRef=dataRef.child("Doctor Notification").child(data.child("doctorId").getValue().toString()).push().getRef();
                                        pRef.child("uid").setValue(user);
                                        Calendar ca=Calendar.getInstance();
                                        pRef.child("time").setValue(ca.getTimeInMillis());
                                        pRef.child("dId").setValue(data.child("doctorId").getValue().toString());
                                        pRef.child("tActive").setValue("Yes");
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Image Successfully Sent to doctor", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        pd.setMessage((int) progress + "% Uploaded...");

                                    }
                                });
                            }
                            else if (!data.child("Test Results").child("image5").exists()){
                                StorageReference storageReference = sRef.child("Test Results/" + path + "/" + "img5.jpg");
                                storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downUri = taskSnapshot.getDownloadUrl();

                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image5").child("img5").setValue(downUri.toString());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image5").child("sbj5").setValue(sbjct.getText().toString().trim());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image5").child("time5").setValue(c.getTimeInMillis());
                                        long time=c.getTimeInMillis();
                                        DatabaseReference mRef=dataRef.child("Test Results").push().getRef();
                                        mRef.child("image").setValue(downUri.toString());
                                        mRef.child("subject").setValue(sbjct.getText().toString().trim());
                                        mRef.child("time").setValue(time);
                                        mRef.child("key").setValue(data.child("doctorId").getValue().toString()+time);
                                        mRef.child("uid").setValue(user);
                                        mRef.child("name").setValue(data.child("patient").getValue().toString());
                                        mRef.child("docKey").setValue(data.child("docKey").getValue().toString());
                                        mRef.child("date").setValue(data.child("date").getValue().toString());
                                        mRef.child("mRkey").setValue(path);
                                        mRef.child("dateKey").setValue(data.child("doctorId").getValue().toString()+data.child("date").getValue().toString());
                                        DatabaseReference pRef=dataRef.child("Doctor Notification").child(data.child("doctorId").getValue().toString()).push().getRef();
                                        pRef.child("uid").setValue(user);
                                        Calendar ca=Calendar.getInstance();
                                        pRef.child("time").setValue(ca.getTimeInMillis());
                                        pRef.child("dId").setValue(data.child("doctorId").getValue().toString());
                                        pRef.child("tActive").setValue("Yes");
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Image Successfully Sent to doctor", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        pd.setMessage((int) progress + "% Uploaded...");

                                    }
                                });
                            }
                            else if (!data.child("Test Results").child("image6").exists()){
                                StorageReference storageReference = sRef.child("Test Results/" + path + "/" + "img6.jpg");
                                storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downUri = taskSnapshot.getDownloadUrl();

                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image6").child("img6").setValue(downUri.toString());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image6").child("sbj6").setValue(sbjct.getText().toString().trim());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image6").child("time6").setValue(c.getTimeInMillis());
                                        long time=c.getTimeInMillis();
                                        DatabaseReference mRef=dataRef.child("Test Results").push().getRef();
                                        mRef.child("image").setValue(downUri.toString());
                                        mRef.child("subject").setValue(sbjct.getText().toString().trim());
                                        mRef.child("time").setValue(time);
                                        mRef.child("key").setValue(data.child("doctorId").getValue().toString()+time);
                                        mRef.child("uid").setValue(user);
                                        mRef.child("name").setValue(data.child("patient").getValue().toString());
                                        mRef.child("docKey").setValue(data.child("docKey").getValue().toString());
                                        mRef.child("date").setValue(data.child("date").getValue().toString());
                                        mRef.child("mRkey").setValue(path);
                                        mRef.child("dateKey").setValue(data.child("doctorId").getValue().toString()+data.child("date").getValue().toString());
                                        DatabaseReference pRef=dataRef.child("Doctor Notification").child(data.child("doctorId").getValue().toString()).push().getRef();
                                        pRef.child("uid").setValue(user);
                                        Calendar ca=Calendar.getInstance();
                                        pRef.child("time").setValue(ca.getTimeInMillis());
                                        pRef.child("dId").setValue(data.child("doctorId").getValue().toString());
                                        pRef.child("tActive").setValue("Yes");
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Image Successfully Sent to doctor", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        pd.setMessage((int) progress + "% Uploaded...");

                                    }
                                });
                            }
                            else if (!data.child("Test Results").child("image7").exists()){
                                StorageReference storageReference = sRef.child("Test Results/" + path + "/" + "img7.jpg");
                                storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downUri = taskSnapshot.getDownloadUrl();

                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image7").child("img7").setValue(downUri.toString());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image7").child("sbj7").setValue(sbjct.getText().toString().trim());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image7").child("time7").setValue(c.getTimeInMillis());
                                        long time=c.getTimeInMillis();
                                        DatabaseReference mRef=dataRef.child("Test Results").push().getRef();
                                        mRef.child("image").setValue(downUri.toString());
                                        mRef.child("subject").setValue(sbjct.getText().toString().trim());
                                        mRef.child("time").setValue(time);
                                        mRef.child("key").setValue(data.child("doctorId").getValue().toString()+time);
                                        mRef.child("uid").setValue(user);
                                        mRef.child("name").setValue(data.child("patient").getValue().toString());
                                        mRef.child("docKey").setValue(data.child("docKey").getValue().toString());
                                        mRef.child("date").setValue(data.child("date").getValue().toString());
                                        mRef.child("mRkey").setValue(path);
                                        mRef.child("dateKey").setValue(data.child("doctorId").getValue().toString()+data.child("date").getValue().toString());
                                        DatabaseReference pRef=dataRef.child("Doctor Notification").child(data.child("doctorId").getValue().toString()).push().getRef();
                                        pRef.child("uid").setValue(user);
                                        Calendar ca=Calendar.getInstance();
                                        pRef.child("time").setValue(ca.getTimeInMillis());
                                        pRef.child("dId").setValue(data.child("doctorId").getValue().toString());
                                        pRef.child("tActive").setValue("Yes");
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Image Successfully Sent to doctor", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        pd.setMessage((int) progress + "% Uploaded...");

                                    }
                                });
                            }
                            else if (!data.child("Test Results").child("image8").exists()){
                                StorageReference storageReference = sRef.child("Test Results/" + path + "/" + "img8.jpg");
                                storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downUri = taskSnapshot.getDownloadUrl();

                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image8").child("img8").setValue(downUri.toString());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image8").child("sbj8").setValue(sbjct.getText().toString().trim());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image8").child("time8").setValue(c.getTimeInMillis());
                                        long time=c.getTimeInMillis();
                                        DatabaseReference mRef=dataRef.child("Test Results").push().getRef();
                                        mRef.child("image").setValue(downUri.toString());
                                        mRef.child("subject").setValue(sbjct.getText().toString().trim());
                                        mRef.child("time").setValue(time);
                                        mRef.child("key").setValue(data.child("doctorId").getValue().toString()+time);
                                        mRef.child("uid").setValue(user);
                                        mRef.child("name").setValue(data.child("patient").getValue().toString());
                                        mRef.child("docKey").setValue(data.child("docKey").getValue().toString());
                                        mRef.child("date").setValue(data.child("date").getValue().toString());
                                        mRef.child("mRkey").setValue(path);
                                        mRef.child("dateKey").setValue(data.child("doctorId").getValue().toString()+data.child("date").getValue().toString());
                                        DatabaseReference pRef=dataRef.child("Doctor Notification").child(data.child("doctorId").getValue().toString()).push().getRef();
                                        pRef.child("uid").setValue(user);
                                        Calendar ca=Calendar.getInstance();
                                        pRef.child("time").setValue(ca.getTimeInMillis());
                                        pRef.child("dId").setValue(data.child("doctorId").getValue().toString());
                                        pRef.child("tActive").setValue("Yes");
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Image Successfully Sent to doctor", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        pd.setMessage((int) progress + "% Uploaded...");

                                    }
                                });
                            }
                            else if (!data.child("Test Results").child("image9").exists()){
                                StorageReference storageReference = sRef.child("Test Results/" + path + "/" + "img9.jpg");
                                storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downUri = taskSnapshot.getDownloadUrl();

                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image9").child("img9").setValue(downUri.toString());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image9").child("sbj9").setValue(sbjct.getText().toString().trim());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image9").child("time9").setValue(c.getTimeInMillis());
                                        long time=c.getTimeInMillis();
                                        DatabaseReference mRef=dataRef.child("Test Results").push().getRef();
                                        mRef.child("image").setValue(downUri.toString());
                                        mRef.child("subject").setValue(sbjct.getText().toString().trim());
                                        mRef.child("time").setValue(time);
                                        mRef.child("key").setValue(data.child("doctorId").getValue().toString()+time);
                                        mRef.child("uid").setValue(user);
                                        mRef.child("name").setValue(data.child("patient").getValue().toString());
                                        mRef.child("docKey").setValue(data.child("docKey").getValue().toString());
                                        mRef.child("date").setValue(data.child("date").getValue().toString());
                                        mRef.child("mRkey").setValue(path);
                                        mRef.child("dateKey").setValue(data.child("doctorId").getValue().toString()+data.child("date").getValue().toString());
                                        DatabaseReference pRef=dataRef.child("Doctor Notification").child(data.child("doctorId").getValue().toString()).push().getRef();
                                        pRef.child("uid").setValue(user);
                                        Calendar ca=Calendar.getInstance();
                                        pRef.child("time").setValue(ca.getTimeInMillis());
                                        pRef.child("dId").setValue(data.child("doctorId").getValue().toString());
                                        pRef.child("tActive").setValue("Yes");
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Image Successfully Sent to doctor", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        pd.setMessage((int) progress + "% Uploaded...");

                                    }
                                });
                            }
                            else if (!data.child("Test Results").child("image10").exists()){
                                StorageReference storageReference = sRef.child("Test Results/" + path + "/" + "img10.jpg");
                                storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downUri = taskSnapshot.getDownloadUrl();

                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image10").child("img10").setValue(downUri.toString());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image10").child("sbj10").setValue(sbjct.getText().toString().trim());
                                        dataRef.child("Medical Record").child(path).child("Test Results").child("image10").child("time10").setValue(c.getTimeInMillis());
                                        long time=c.getTimeInMillis();
                                        DatabaseReference mRef=dataRef.child("Test Results").push().getRef();
                                        mRef.child("image").setValue(downUri.toString());
                                        mRef.child("subject").setValue(sbjct.getText().toString().trim());
                                        mRef.child("time").setValue(time);
                                        mRef.child("key").setValue(data.child("doctorId").getValue().toString()+time);
                                        mRef.child("uid").setValue(user);
                                        mRef.child("name").setValue(data.child("patient").getValue().toString());
                                        mRef.child("docKey").setValue(data.child("docKey").getValue().toString());
                                        mRef.child("date").setValue(data.child("date").getValue().toString());
                                        mRef.child("mRkey").setValue(path);
                                        mRef.child("dateKey").setValue(data.child("doctorId").getValue().toString()+data.child("date").getValue().toString());
                                        DatabaseReference pRef=dataRef.child("Doctor Notification").child(data.child("doctorId").getValue().toString()).push().getRef();
                                        pRef.child("uid").setValue(user);
                                        Calendar ca=Calendar.getInstance();
                                        pRef.child("time").setValue(ca.getTimeInMillis());
                                        pRef.child("dId").setValue(data.child("doctorId").getValue().toString());
                                        pRef.child("tActive").setValue("Yes");
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Image Successfully Sent to doctor", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        pd.setMessage((int) progress + "% Uploaded...");

                                    }
                                });
                            }
                            else {
                                Toast.makeText(getActivity(), "Uploading Failed.!You Can't send more than 10 images", Toast.LENGTH_LONG).show();
                            pd.dismiss();
                            }
                        }
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else if (dateSpin.getSelectedItem()==null){
            Toast.makeText(getActivity(), "You haven't Consulted Yet", Toast.LENGTH_SHORT).show();
        }
          else {
            Toast.makeText(getActivity(), "No File Selected", Toast.LENGTH_SHORT).show();
        }

    }
}
