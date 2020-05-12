package com.mihailovalex.firebase_organazer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MyLogs";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private EditText etLogin;
    private EditText etPassword;
    private Button bnSign;
    private Button bnRegistration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser!=null){
                    Intent intent = new Intent(MainActivity.this, ListTasksActivity.class);
                    startActivityForResult(intent, 0);
                }else {

                }
            }
        };
        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        bnSign = findViewById(R.id.bnSign);
        bnRegistration = findViewById(R.id.bnRegistration);
        bnSign.setOnClickListener(this);
        bnRegistration.setOnClickListener(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            Intent intent = new Intent(MainActivity.this, ListTasksActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bnSign:
                signing(etLogin.getText().toString(),etPassword.getText().toString());
                break;
            case R.id.bnRegistration:
                registration(etLogin.getText().toString(),etPassword.getText().toString());
                break;
        }
    }

    private void registration(String login, String pass) {
        mAuth.createUserWithEmailAndPassword(login, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(MainActivity.this, "Регистрация провалена", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signing(String login, String pass) {
        mAuth.signInWithEmailAndPassword(login, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Авторизация успешна", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(MainActivity.this, "Авторизация провалена", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
