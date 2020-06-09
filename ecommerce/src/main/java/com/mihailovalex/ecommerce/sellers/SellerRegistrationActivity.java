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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mihailovalex.ecommerce.R;
import com.mihailovalex.ecommerce.buyers.MainActivity;
import com.mihailovalex.ecommerce.buyers.SearchProductsActivity;

import java.util.HashMap;

public class SellerRegistrationActivity extends AppCompatActivity {
    private Button allreadyHaveAccount, sellerRegister;
    private EditText sellerName, sellerPhone, sellerEmail,sellerPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);
        mAuth = FirebaseAuth.getInstance();
        allreadyHaveAccount = findViewById(R.id.seller_allready_have_account_btn);
        allreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerRegistrationActivity.this, SellerLoginActivity.class);
                startActivity(intent);
            }
        });
        sellerName = findViewById(R.id.seller_name);
        sellerPhone = findViewById(R.id.seller_phone);
        sellerPassword = findViewById(R.id.seller_password);
        sellerEmail = findViewById(R.id.seller_email);
        sellerRegister = findViewById(R.id.seller_register_btn);
        sellerRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerSeller();
            }
        });
        loadingBar = new ProgressDialog(this);
    }

    private void registerSeller() {
        final String name = sellerName.getText().toString();
        final String phone = sellerPhone.getText().toString();
        final String email = sellerEmail.getText().toString();
        final String password = sellerPassword.getText().toString();
        if(!name.equals("")&&!phone.equals("")&&!email.equals("")&&!password.equals("")){
            loadingBar.setTitle("Create seller account");
            loadingBar.setMessage("Please wait, while we checking credential.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            String uId = mAuth.getCurrentUser().getUid();
                            HashMap<String,Object> userData = new HashMap<>();
                            userData.put("uId",uId);
                            userData.put("email",email);
                            userData.put("name",name);
                            userData.put("phone",phone);
                            userData.put("password",password);
                            rootRef.child("Sellers").child(uId).updateChildren(userData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(SellerRegistrationActivity.this, "You are registered successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent =new Intent(SellerRegistrationActivity.this, SellerHomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                        }
                    }
                });
            loadingBar.dismiss();
        }else {
            Toast.makeText(this, "Please complete the registration form", Toast.LENGTH_SHORT).show();
        }
    }
}