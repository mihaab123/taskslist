package com.mihailovalex.lapitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;

import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "MyChat" ;
    @BindView(R.id.reg_display_name)
    TextInputLayout displayName;
    @BindView(R.id.reg_email)
    TextInputLayout email;
    @BindView(R.id.reg_password)
    TextInputLayout password;
    @BindView(R.id.register_toolbar)
    Toolbar toolbar;

    private FirebaseAuth mAuth;
    private ProgressDialog progressBar;
    private DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.create_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = new ProgressDialog(this);
        progressBar.setTitle(getString(R.string.reg_title));
        progressBar.setMessage(getString(R.string.reg_message));
        progressBar.setCanceledOnTouchOutside(false);
    }
    @OnClick(R.id.reg_create_button)
    public void createAccount() {
        String textDisplayName = displayName.getEditText().getText().toString();
        String textEmail = email.getEditText().getText().toString();
        String textPassword = password.getEditText().getText().toString();
        if(!TextUtils.isEmpty(textDisplayName)&&!TextUtils.isEmpty(textEmail)&&!TextUtils.isEmpty(textPassword)){
            progressBar.show();
            mAuth.createUserWithEmailAndPassword(textEmail,textPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                String uId = currentUser.getUid();
                                final HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("name",textDisplayName);
                                userMap.put("image","default");
                                userMap.put("thumb_image","default");
                                userMap.put("status","New");
                                userMap.put("device_token", FirebaseInstanceId.getInstance().getToken());
                                myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);
                                myRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressBar.dismiss();
                                            Intent reg_intent = new Intent(RegisterActivity.this,MainActivity.class);
                                            reg_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(reg_intent);
                                            finish();
                                        }else {
                                            progressBar.hide();
                                            Toast.makeText(RegisterActivity.this, R.string.reg_error,
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });



                            } else {
                                progressBar.hide();
                                Toast.makeText(RegisterActivity.this, R.string.reg_error,
                                        Toast.LENGTH_SHORT).show();

                            }
                        }});

        }

    }
}