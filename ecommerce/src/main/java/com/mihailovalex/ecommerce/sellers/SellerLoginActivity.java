package com.mihailovalex.ecommerce.sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mihailovalex.ecommerce.R;
import com.mihailovalex.ecommerce.buyers.MainActivity;

public class SellerLoginActivity extends AppCompatActivity {
    private Button  sellerLogin;
    private EditText sellerEmail,sellerPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);
        mAuth = FirebaseAuth.getInstance();

        sellerPassword = findViewById(R.id.seller_login_password);
        sellerEmail = findViewById(R.id.seller_login_email);
        sellerLogin = findViewById(R.id.seller_login_btn);
        sellerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSeller();
            }
        });
        loadingBar = new ProgressDialog(this);
    }

    private void loginSeller() {
        final String email = sellerEmail.getText().toString();
        final String password = sellerPassword.getText().toString();
        if(!email.equals("")&&!password.equals("")){
            loadingBar.setTitle("Check seller account");
            loadingBar.setMessage("Please wait, while we checking credential.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loadingBar.dismiss();
                        if (task.isSuccessful()){
                            //Toast.makeText(SellerLoginActivity.this, "You are login successfully", Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
        }else {
            Toast.makeText(this, "Please complete login form", Toast.LENGTH_SHORT).show();
        }
    }
}