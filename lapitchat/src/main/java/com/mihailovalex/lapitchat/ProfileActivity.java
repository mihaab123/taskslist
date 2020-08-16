package com.mihailovalex.lapitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {
    private DatabaseReference myRef;
    private DatabaseReference myFriend1ReqRef;
    private DatabaseReference myFriendsRef;
    private DatabaseReference myNotificatioRef;
    FirebaseUser currentUser;
    @BindView(R.id.profile_image1)
    ImageView profileImage;
    @BindView(R.id.profile_display_name)
    TextView displayName;
    @BindView(R.id.profile_status)
    TextView statusText;
    @BindView(R.id.profile_total_friends)
    TextView totalFriendsText;
    @BindView(R.id.proffle_send_request)
    Button profileSendRequest;
    @BindView(R.id.proffle_decline_request)
    Button profileDelcineRequest;
    private ProgressDialog progressBar;
    private String current_state;
    private String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        progressBar = new ProgressDialog(this);
        progressBar.setTitle(getString(R.string.profile_title));
        progressBar.setMessage(getString(R.string.reg_message));
        progressBar.setCanceledOnTouchOutside(false);
        user_id = getIntent().getStringExtra("user_id");
        current_state = "not_friend";
        progressBar.show();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        myFriend1ReqRef = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        myFriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        myNotificatioRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String image = snapshot.child("image").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                displayName.setText(name);
                statusText.setText(status);
                if(!image.equals("default")){
                    Picasso.get().load(image).placeholder(R.drawable.blank_profile).into(profileImage);
                }
                myFriend1ReqRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(user_id)){
                            String req_type =snapshot.child(user_id).child("request_type").getValue().toString();
                            if (req_type.equals("received")){

                                current_state = "req_received";
                                profileSendRequest.setText(R.string.accept_friend_request);
                                profileDelcineRequest.setVisibility(View.VISIBLE);
                                profileDelcineRequest.setEnabled(true);
                            } else if (req_type.equals("sent")){

                                current_state = "req_sent";
                                profileSendRequest.setText(R.string.cancel_friend_request);
                                profileDelcineRequest.setVisibility(View.INVISIBLE);
                                profileDelcineRequest.setEnabled(false);
                            }
                        }else {
                            myFriendsRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(user_id)){
                                        current_state = "friends";
                                        profileSendRequest.setText(R.string.unfriend_request);
                                        profileDelcineRequest.setVisibility(View.INVISIBLE);
                                        profileDelcineRequest.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        progressBar.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.dismiss();
            }
        });
    }
    @OnClick(R.id.proffle_send_request)
    public void sendRequestButton() {
        profileSendRequest.setEnabled(false);
        if(current_state.equals("not_friend")){
            myFriend1ReqRef.child(currentUser.getUid()).child(user_id).child("request_type")
                    .setValue("sent")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                myFriend1ReqRef.child(user_id).child(currentUser.getUid()).child("request_type")
                                    .setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            HashMap<String,String> notitficationData = new HashMap<>();
                                            notitficationData.put("from", currentUser.getUid());
                                            notitficationData.put("type", "request");
                                            myNotificatioRef.child(user_id).push().setValue(notitficationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    current_state = "req_sent";
                                                    profileSendRequest.setText(R.string.cancel_friend_request);
                                                    profileDelcineRequest.setVisibility(View.INVISIBLE); // проверить и настроить правильно
                                                    profileDelcineRequest.setEnabled(false);
                                                    Toast.makeText(ProfileActivity.this,"Uploading request",Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    });
                            }else {
                                Toast.makeText(ProfileActivity.this,"Error uploading request",Toast.LENGTH_SHORT).show();
                            }
                            profileSendRequest.setEnabled(true);

                        }
                    });
        } else if(current_state.equals("req_sent")){
            myFriend1ReqRef.child(currentUser.getUid()).child(user_id).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            myFriend1ReqRef.child(user_id).child(currentUser.getUid()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            current_state = "not_friend";
                                            profileSendRequest.setText(R.string.send_friend_request);
                                            profileDelcineRequest.setVisibility(View.INVISIBLE);
                                            profileDelcineRequest.setEnabled(false);
                                        }
                                    });
                        }
                    });
            profileSendRequest.setEnabled(true);
        } else if(current_state.equals("req_received")){
            String currentTime = DateFormat.getDateTimeInstance().format(new Date());
            myFriendsRef.child(currentUser.getUid()).child(user_id).setValue(currentTime).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    myFriendsRef.child(user_id).child(currentUser.getUid()).setValue(currentTime).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            myFriend1ReqRef.child(currentUser.getUid()).child(user_id).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            myFriend1ReqRef.child(user_id).child(currentUser.getUid()).removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            current_state = "friends";
                                                            profileSendRequest.setText(R.string.unfriend_request);
                                                            profileDelcineRequest.setVisibility(View.INVISIBLE);
                                                            profileDelcineRequest.setEnabled(false);
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
                }
            });
            profileSendRequest.setEnabled(true);
        }else if(current_state.equals("friends")){
            myFriendsRef.child(currentUser.getUid()).child(user_id).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            myFriendsRef.child(user_id).child(currentUser.getUid()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            current_state = "not_friend";
                                            profileSendRequest.setText(R.string.send_friend_request);
                                        }
                                    });
                        }
                    });
            profileSendRequest.setEnabled(true);
        }
    }
    @OnClick(R.id.proffle_decline_request)
    public void declineRequestButton() {

    }
}