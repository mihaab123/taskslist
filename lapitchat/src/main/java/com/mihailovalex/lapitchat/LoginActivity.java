package com.mihailovalex.lapitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MyChat" ;
    @BindView(R.id.log_email)
    TextInputLayout email;
    @BindView(R.id.log_password)
    TextInputLayout password;
    @BindView(R.id.login_toolbar)
    Toolbar toolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.login_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = new ProgressDialog(this);
        progressBar.setTitle(getString(R.string.log_title));
        progressBar.setMessage(getString(R.string.reg_message));
        progressBar.setCanceledOnTouchOutside(false);
    }
    @OnClick(R.id.login_button)
    public void loginAccount() {

        String textEmail = email.getEditText().getText().toString();
        String textPassword = password.getEditText().getText().toString();
        if(!TextUtils.isEmpty(textEmail)&&!TextUtils.isEmpty(textPassword)){
            progressBar.show();
            mAuth.signInWithEmailAndPassword(textEmail, textPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.dismiss();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                myRef.child(mAuth.getCurrentUser().getUid()).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent log_intent = new Intent(LoginActivity.this,MainActivity.class);
                                        log_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(log_intent);
                                        finish();
                                    }
                                });

                            } else {
                                progressBar.hide();
                                Toast.makeText(LoginActivity.this, R.string.reg_error,
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }
}