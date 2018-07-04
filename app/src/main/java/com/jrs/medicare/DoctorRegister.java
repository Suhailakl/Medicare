package com.jrs.medicare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

import static com.jrs.medicare.R.id.doc_rdo__gndr;

public class DoctorRegister extends AppCompatActivity {
    //private Firebase mfire;
    ArrayList<String> items=new ArrayList<>();
    SpinnerDialog spinnerDialog;
    int textlength=0;
    private EditText selectedItems;
    private EditText doc_name,doc_city,doc_hsptl_name,doc_adrs,doc_qua,doc_regno,doc_dpmt,doc_cnslt_start,doc_cnslt_time_end;
    private EditText doc_tok,doc_email,doc_pwd,doc_cnfm_pwd,doc_no;
    private String docName,docGen,docCity,docHsptl,docAdrs,docQua,docReg,docDpmt,docCnsltTimeStart,docNo,docCnsltTimeEnd,docToken,docPwd,docCnfrmPwd,docEmail,docPhone;
    private String docSun,docMon,docTue,docWed,docThi,docFri,docSat,docAm,docPm;
    private RadioGroup doc_gen,doc_ver;
    private RadioButton doc_male,doc_female,doc_r_mail,doc_r_phone;
    private Button register;
    private ProgressDialog progress;
    private DatabaseReference dataref,ref;
    private FirebaseAuth mAuth;
    private Toast to,alt;
    private Spinner s1,s2;
    private Calendar c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);
        getSupportActionBar().setTitle("Doctor Registration");
        c=Calendar.getInstance();
        mAuth=FirebaseAuth.getInstance();
        dataref= FirebaseDatabase.getInstance().getReference().child("Temporary Doctors");
        ref=FirebaseDatabase.getInstance().getReference();
        progress=new ProgressDialog(this);
        progress.setCanceledOnTouchOutside(false);
        selectedItems= (EditText) findViewById(R.id.doc_city);
        doc_name= (EditText) findViewById(R.id.doc_name);
        doc_adrs= (EditText) findViewById(R.id.doc_adrs);
        doc_gen= (RadioGroup) findViewById(doc_rdo__gndr);
        doc_male= (RadioButton) findViewById(R.id.doc_rdo_male);
        doc_female= (RadioButton) findViewById(R.id.doc_rdo_female);
        doc_city= (EditText) findViewById(R.id.doc_city);
        doc_hsptl_name= (EditText) findViewById(R.id.hsptl_name);
        doc_qua= (EditText) findViewById(R.id.doc_edu_qua);
        doc_regno= (EditText) findViewById(R.id.doc_regno);
        doc_dpmt= (EditText) findViewById(R.id.doc_dprtmnt);
        doc_cnslt_start= (EditText) findViewById(R.id.cnsult_start);
        doc_cnslt_time_end= (EditText) findViewById(R.id.cnsult_end);
        doc_tok= (EditText) findViewById(R.id.no_Of_tokens);
        doc_email= (EditText) findViewById(R.id.doc_email);
        doc_pwd= (EditText) findViewById(R.id.doc_pwd);
        doc_cnfm_pwd= (EditText) findViewById(R.id.doc_confm_pwd);
        doc_r_phone= (RadioButton) findViewById(R.id.doc_radio_phone);
        doc_r_mail= (RadioButton) findViewById(R.id.doc_radio_mail);
        doc_ver= (RadioGroup) findViewById(R.id.doc_radioGr);
        doc_no= (EditText) findViewById(R.id.doc_mob_no);
        register= (Button) findViewById(R.id.doc_register);
        addItemOnSpinner();
        addListnerOnRegisterBtn();
        to=Toast.makeText(this,"Successfully Registered",Toast.LENGTH_SHORT);
        alt=Toast.makeText(this,"User Already Exists",Toast.LENGTH_SHORT);
      // spinnerDialog=new SpinnerDialog(DoctorRegister.this,items,"Select or Search City");// With No Animation
        spinnerDialog=new SpinnerDialog(DoctorRegister.this,items,"Select or Search City",R.style.DialogAnimations_SmileWindow);// With 	Animation
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                Toast.makeText(DoctorRegister.this, item , Toast.LENGTH_SHORT).show();
                selectedItems.setText(item );
            }
        });
        selectedItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemsToSpinner();
                spinnerDialog.showSpinerDialog();
            }
        });
        doc_ver.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId==R.id.doc_radio_mail){
                    doc_pwd.setVisibility(View.VISIBLE);
                    doc_cnfm_pwd.setVisibility(View.VISIBLE);
                }
                else {
                    doc_pwd.setVisibility(View.GONE);
                    doc_cnfm_pwd.setVisibility(View.GONE);
                }
            }
        });
        addListnerOnRegisterBtn();
    }
    public void addItemOnSpinner(){
        s1= (Spinner) findViewById(R.id.cnsltAmStart);
        s2= (Spinner) findViewById(R.id.cnsltAmEnd);
        List<String> list=new ArrayList<>();
        list.add("AM");
        list.add("PM");
        ArrayAdapter<String> dataAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(dataAdapter);
        s2.setAdapter(dataAdapter);

        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                docAm=s1.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                docPm=s2.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    public void addListnerOnRegisterBtn(){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                register();
            }
        });
    }
    public void register(){
        initialize();
        if (!validate()) {
            Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
        } else {
            progress.setMessage("Signing Up ...");
            progress.show();
            int selectedId =doc_ver.getCheckedRadioButtonId();
            switch (selectedId) {
                case R.id.doc_radio_mail:
                    onMailSignUpSuccess();
                    break;
                case R.id.doc_radio_phone:
                    ref.child("Id").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(docNo)){
                                progress.dismiss();
                                Toast.makeText(DoctorRegister.this, "Doctor Already Exists,Please SignIn", Toast.LENGTH_SHORT).show();
                                doc_no.setError("Mobile Number Already Exists");
                            }
                            else {
                                phoneAuth();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    break;
            }
        }
    }
   public void phoneAuth(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+docNo, 60, TimeUnit.SECONDS, DoctorRegister.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(DoctorRegister.this, "Verification Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String otpId) {
                super.onCodeAutoRetrievalTimeOut(otpId);
                Toast.makeText(DoctorRegister.this, "Timeout", Toast.LENGTH_SHORT).show();
            }

           @Override
            public void onCodeSent(String otpId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(otpId, forceResendingToken);
                String otpCode=otpId;
                Intent intent=new Intent(DoctorRegister.this,otpVerify.class);
                Bundle b=new Bundle();
                b.putString("docname",docName);
                b.putString("docgen",docGen);
                b.putString("docadrs",docAdrs);
                b.putString("docmobno",docNo);
                b.putString("docmail",docEmail);
                b.putString("code",otpCode);
                b.putString("docqua",docQua);
               b.putString("docReg",docReg);
               b.putString("docDpmt",docDpmt);
               b.putString("docHsptl",docHsptl);
               b.putString("docCity",docCity);
               b.putString("docCnsltTimeStart",docCnsltTimeStart);
               b.putString("docCnsltTimeEnd",docCnsltTimeEnd);
               b.putString("docSun",docSun);
               b.putString("docMon",docMon);
               b.putString("docTue",docTue);
               b.putString("docWed",docWed);
               b.putString("docThi",docThi);
               b.putString("docFri",docFri);
               b.putString("docSat",docSat);
               b.putString("docTok",docToken);
                intent.putExtras(b);
                startActivity(intent);
               finish();
            }
        });
    }


            public void onMailSignUpSuccess() {
                mAuth.createUserWithEmailAndPassword(docEmail, docPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            read();
                            to.show();
                            sendEmailVerification();
                            progress.dismiss();
                        } else {
                            alt.show();
                            progress.dismiss();
                        }
                    }
                });
            }

            public void sendEmailVerification() {
                final FirebaseUser user = mAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(DoctorRegister.this, MailVerify.class));
                                finish();
                                Toast.makeText(DoctorRegister.this, "Check Mail for verification", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            public void read() {
                String user_id = mAuth.getCurrentUser().getUid();
                final DatabaseReference current_user = dataref.child(user_id);
                current_user.child("uid").setValue(user_id);
                current_user.child("mailActive").setValue("no");
                current_user.child("name").setValue(docName);
                current_user.child("gender").setValue(docGen);
                current_user.child("address").setValue(docAdrs);
                current_user.child("mobile number").setValue(docNo);
                current_user.child("email").setValue(docEmail);
                current_user.child("day1").setValue(docSun);
                current_user.child("day2").setValue(docMon);
                current_user.child("day3").setValue(docTue);
                current_user.child("day4").setValue(docWed);
                current_user.child("day5").setValue(docThi);
                current_user.child("day6").setValue(docFri);
                current_user.child("day7").setValue(docSat);
                current_user.child("education qualification").setValue(docQua);
                current_user.child("department").setValue(docDpmt);
                current_user.child("registration number").setValue(docReg);
                current_user.child("working hospital").setValue(docHsptl);
                current_user.child("city").setValue(docCity);
                current_user.child("consultation starts at").setValue(docCnsltTimeStart);
                current_user.child("consultation ends at").setValue(docCnsltTimeEnd);
                current_user.child("total tokens").setValue(docToken);
                current_user.child("timeStamp").setValue(c.getTimeInMillis());
            }

            public boolean validate() {
                boolean valid = true;
                if (docName.isEmpty() || docName.length() > 32) {
                    doc_name.setError("Please Enter a Valid Name");
                    valid = false;
                }

                if (docAdrs.isEmpty()) {
                    doc_adrs.setError("Please Enter a Valid Address");
                    valid = false;
                }
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if ((docEmail.isEmpty()) || (!docEmail.matches(emailPattern))) {
                    doc_email.setError("Please Enter a Valid Email Address");
                    valid = false;
                }
                if (doc_pwd.isShown()) {
                    if (docPwd.isEmpty() || docPwd.length() < 8) {
                        doc_pwd.setError("Password Must Be 8 Characters");
                        valid = false;
                    }
                    if (!docCnfrmPwd.equals(docPwd)) {
                        doc_cnfm_pwd.setError("Password Must Be Same");
                        doc_pwd.setError("Password Must Be Same");
                        valid = false;
                    }
                    if ((docPwd.isEmpty()) && (docCnfrmPwd.isEmpty())) {
                        doc_pwd.setError("Password did n't be empty");
                        doc_cnfm_pwd.setError("Password did n't be empty");
                        valid = false;
                    }
                }
                if (docNo.isEmpty()) {
                    doc_no.setError("Invalid Mobile Number");
                    valid = false;
                }
                if (docQua.isEmpty()) {
                    doc_qua.setError("Invalid Qualification Details");
                    valid = false;
                }
                if (docReg.isEmpty()) {
                    doc_regno.setError("Invalid Register Number");
                    valid = false;
                }
                if (docDpmt.isEmpty()) {
                    doc_dpmt.setError("Please Type Your Medical Department");
                    valid = false;
                }
                if (docHsptl.isEmpty()) {
                    doc_hsptl_name.setError("Plase Type A Valid Hospital Name");
                    valid = false;
                }
                if (docCity.isEmpty()) {
                    doc_city.setError("Invalid City");
                    valid = false;
                }
                if (docCnsltTimeStart.isEmpty() || (docCnsltTimeEnd.isEmpty())) {
                    doc_no.setError("Consult Time Did Not Be Empty");
                    valid = false;
                }
                if (docToken.isEmpty()) {
                    doc_tok.setError("Invalid Token");
                    valid = false;
                }
                return valid;
            }

            public void initialize() {
                docName = doc_name.getText().toString().trim();
                int selectedId = doc_gen.getCheckedRadioButtonId();
                switch (selectedId) {
                    case R.id.doc_rdo_male:
                        docGen = "Male";
                        break;
                    case R.id.doc_rdo_female:
                        docGen = "Female";
                        break;
                }
                docQua = doc_qua.getText().toString().trim();
                docReg = doc_regno.getText().toString().trim();
                docDpmt = doc_dpmt.getText().toString().trim();
                docHsptl = doc_hsptl_name.getText().toString().trim();
                docCity = doc_city.getText().toString().trim();
                docAdrs = doc_adrs.getText().toString().trim();
                docCnsltTimeStart = doc_cnslt_start.getText().toString().trim()+""+docAm;
                docCnsltTimeEnd = doc_cnslt_time_end.getText().toString().trim()+""+docPm;
                docToken = doc_tok.getText().toString().trim();
                docPwd = doc_pwd.getText().toString().trim();
                docCnfrmPwd = doc_cnfm_pwd.getText().toString().trim();
                docNo = doc_no.getText().toString().trim();
                docEmail = doc_email.getText().toString().trim();
            }
    public void onCheckboxClicked(View view){
        switch (view.getId()){
            case R.id.sunday:
                if(((CheckBox) view).isChecked()) {
                    docSun = "Sunday";
                }
                else
                {
                    docSun=null;
                }
                break;
            case R.id.monday:
                if (((CheckBox) view).isChecked()){
                    docMon="Monday";
                }
                else {
                    docMon = null;
                }
                break;
            case R.id.tuesday:
                if (((CheckBox) view).isChecked()){
                    docTue="Tuesday";
                }
                else
                {
                    docTue=null;
                }
                break;
            case R.id.wednsaday:
                if (((CheckBox) view).isChecked()){
                    docWed="Wednesday";
                }
                else {
                    docWed=null;
                }
                break;

            case R.id.thirsday:
                if (((CheckBox) view).isChecked()){
                    docThi="Thursday";
                }
                else {
                    docThi=null;
                }
                break;

            case R.id.friday:
                if (((CheckBox) view).isChecked()){
                    docFri="Friday";
                }
                else {
                    docFri=null;
                }
                break;
            case R.id.saturday:
                if (((CheckBox) view).isChecked()){
                    docSat="Saturday";
                }else
                {
                    docSat=null;
                }
                break;
        }
    }

            public void addItemsToSpinner() {
                items.add("Enter New City");
                items.add("Mumbai");
                items.add("Delhi");
                items.add("Bengaluru");
                items.add("Hyderabad");
                items.add("Ahmedabad");
                items.add("Chennai");
                items.add("Kolkata");
                items.add("Surat");
                items.add("Pune");
                items.add("Jaipur");
                items.add("Lucknow");
                items.add("Kanpur");
            }
        }

