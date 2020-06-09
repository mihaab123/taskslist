package com.mihailovalex.ecommerce.buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mihailovalex.ecommerce.R;
import com.mihailovalex.ecommerce.prevalent.Prevalent;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {
    private String check;
    private TextView tvPassword, tvQuestionTitle;
    private EditText phoneNumber, question1, question2;
    private Button btnVerify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        check = getIntent().getStringExtra("check");
        tvPassword = findViewById(R.id.tvPassword);
        tvQuestionTitle = findViewById(R.id.title_questions);
        phoneNumber = findViewById(R.id.reset_phone);
        question1 = findViewById(R.id.question_1);
        question2 = findViewById(R.id.question_2);
        btnVerify = findViewById(R.id.verify_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        phoneNumber.setVisibility(View.GONE);
        if(check.equals("settings")){
            tvPassword.setText("Set questions");
            tvQuestionTitle.setText("Please set Answer for the questions");
            dislpayPreviosAnswers();
            btnVerify.setText("Set");
            btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAnswers();
                }
            });
        }else if(check.equals("login")){
            phoneNumber.setVisibility(View.VISIBLE);
            btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyUser();
                }
            });
        }
    }

    private void verifyUser() {
        final String phone = phoneNumber.getText().toString();
        final String answer1 = question1.getText().toString().toLowerCase();
        final String answer2 = question2.getText().toString().toLowerCase();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(phone);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String mPhone = dataSnapshot.child("phone").getValue().toString();
                    if(phone.equals(mPhone)){
                        if(dataSnapshot.hasChild("Security questions")){
                            String answ1 = dataSnapshot.child("Security questions").child("answer1").getValue().toString();
                            String answ2 = dataSnapshot.child("Security questions").child("answer2").getValue().toString();
                            if(!answ1.equals(answer1)){
                                Toast.makeText(ResetPasswordActivity.this,"Your first answer is wrong.",Toast.LENGTH_SHORT).show();
                            }else if(!answ2.equals(answer2)){
                                Toast.makeText(ResetPasswordActivity.this,"Your second answer is wrong.",Toast.LENGTH_SHORT).show();
                            }else {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("New password");

                                final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                newPassword.setHint("Write new password here ...");
                                builder.setView(newPassword);
                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {
                                        if(!newPassword.getText().toString().equals("")){
                                            ref.child("password").setValue(newPassword.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(ResetPasswordActivity.this,"password change successfully",Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                });

                                        }
                                    }
                                });
                                builder.setNegativeButton("Cansel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.show();
                            }
                        }
                    } else{
                        Toast.makeText(ResetPasswordActivity.this,"This user phone number not exists.",Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(ResetPasswordActivity.this,"This user phone number not exists.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAnswers() {
        String answer1 = question1.getText().toString().toLowerCase();
        String answer2 = question2.getText().toString().toLowerCase();
        if(answer1.equals("")&&answer2.equals("")){
            Toast.makeText(ResetPasswordActivity.this,"Please answer both questions.",Toast.LENGTH_SHORT).show();
        }else{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(Prevalent.currentUser.getPhone());
            HashMap<String,Object> userData = new HashMap<>();
            userData.put("answer1",answer1);
            userData.put("answer2",answer2);
            ref.child("Security questions").updateChildren(userData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this,"You have set questions",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
        }

    }
    private void dislpayPreviosAnswers() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(Prevalent.currentUser.getPhone());

        ref.child("Security questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String answ1 = dataSnapshot.child("answer1").getValue().toString();
                String answ2 = dataSnapshot.child("answer2").getValue().toString();

                question1.setText(answ1);
                question2.setText(answ2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
