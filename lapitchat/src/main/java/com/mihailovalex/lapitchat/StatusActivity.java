package com.mihailovalex.lapitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StatusActivity extends AppCompatActivity {
    @BindView(R.id.status_change_toolbar)
    Toolbar toolbar;
    @BindView(R.id.status_status)
    TextInputLayout statusText;
    private DatabaseReference myRef;
    FirebaseUser currentUser;
    private ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        ButterKnife.bind(this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        /*myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.child("status").getValue().toString();
                statusText.getEditText().setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        statusText.getEditText().setText(getIntent().getStringExtra("status"));

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.account_status);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = new ProgressDialog(this);
        progressBar.setTitle(getString(R.string.status_title));
        progressBar.setMessage(getString(R.string.reg_message));
        progressBar.setCanceledOnTouchOutside(false);
    }
    @OnClick(R.id.status_change_button)
    public void changeStatus() {
        progressBar.show();
        String status = statusText.getEditText().getText().toString();
        myRef.child("status").setValue(status).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressBar.dismiss();

                }else {
                    progressBar.hide();
                    Toast.makeText(StatusActivity.this, R.string.reg_error,
                            Toast.LENGTH_SHORT).show();

                }

            }
        });
    }
}