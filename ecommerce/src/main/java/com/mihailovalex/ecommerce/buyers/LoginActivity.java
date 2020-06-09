package com.mihailovalex.ecommerce.buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mihailovalex.ecommerce.R;
import com.mihailovalex.ecommerce.admin.AdminCategoryActivity;
import com.mihailovalex.ecommerce.model.User;
import com.mihailovalex.ecommerce.prevalent.Prevalent;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private EditText inputPhone;
    private EditText inputPassword;
    private ProgressDialog loadingBar;
    private CheckBox chbRememberMe;
    private TextView adminLink, notAdminLink,forgetPasswordLink;
    private String parentDBName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton =findViewById(R.id.login_btn);
        inputPhone =findViewById(R.id.login_phone_number_input);
        inputPassword =findViewById(R.id.login_password_input);
        adminLink = findViewById(R.id.admin_panel_link);
        notAdminLink = findViewById(R.id.not_admin_panel_link);
        forgetPasswordLink = findViewById(R.id.forget_password_link);
        loadingBar = new ProgressDialog(this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        chbRememberMe = findViewById(R.id.chRememberMe);
        Paper.init(this);
        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login admin");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                parentDBName = "Admins";
            }
        });
        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                parentDBName = "Users";
            }
        });
        forgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });
    }

    private void loginUser() {

        String phone = inputPhone.getText().toString();
        String password = inputPassword.getText().toString();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please enter phone",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
        }else {
            loadingBar.setTitle("Login account");
            loadingBar.setMessage("Please wait, while we checking credential.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            validatePhoneNNumber(phone,password);
        }
    }

    private void validatePhoneNNumber(final String phone, final String password) {
        if (chbRememberMe.isChecked()){
            Paper.book().write(Prevalent.userPhoneKey,phone);
            Paper.book().write(Prevalent.userPasswordKey,password);
        }
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDBName).child(phone).exists()){
                    User userData = dataSnapshot.child(parentDBName).child(phone).getValue(User.class);
                    if(userData.getPhone().equals(phone) && userData.getPassword().equals(password)){
                        if (parentDBName.equals("Admins")){
                            Toast.makeText(LoginActivity.this,"Logged successfully",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);
                        }else  if (parentDBName.equals("Users")){
                            Toast.makeText(LoginActivity.this,"Logged successfully",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            Prevalent.currentUser = userData;
                            startActivity(intent);
                        }
                    }else {
                        Toast.makeText(LoginActivity.this,"Wrong phone or password",Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }else {
                    Toast.makeText(LoginActivity.this,"Phone "+phone+ " not find",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
