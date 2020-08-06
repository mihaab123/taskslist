package com.mihailovalex.lapitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    private DatabaseReference myRef;
    FirebaseUser currentUser;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.settings_display_name)
    TextView displayName;
    @BindView(R.id.setting_status)
    TextView statusText;
    private static final int GALARY_PICK = 1;
    private StorageReference UserImagesRef;
    private ProgressDialog progressBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        progressBar = new ProgressDialog(this);
        progressBar.setTitle(getString(R.string.image_title));
        progressBar.setMessage(getString(R.string.reg_message));
        progressBar.setCanceledOnTouchOutside(false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        UserImagesRef = FirebaseStorage.getInstance().getReference().child("profile_images");
        myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String image = snapshot.child("image").getValue().toString();
                String thumb_image = snapshot.child("thumb_image").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                displayName.setText(name);
                statusText.setText(status);
                if(!image.equals("default")){
                    Picasso.get().load(image).placeholder(R.drawable.blank_profile).into(profileImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @OnClick(R.id.setting_change_status_button)
    public void changeStatusButton() {
        Intent reg_intent = new Intent(SettingsActivity.this,StatusActivity.class);
        reg_intent.putExtra("status",statusText.getText().toString());
        startActivity(reg_intent);
    }

    @OnClick(R.id.setting_change_image_button)
    public void changeImageButton() {
        /*// start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);*/
        Intent gallaryIntent = new Intent();
        gallaryIntent.setType("image/*");
        gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallaryIntent,getString(R.string.select_image)),GALARY_PICK);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }*/
        if (requestCode==GALARY_PICK  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            Uri ImageUri = data.getData();
            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(ImageUri)
                    .setAspectRatio(1,1) // circle
                    .start(this);

            //InputProductImage.setImageURI(ImageUri);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressBar.show();
                Uri resultUri = result.getUri();
                File thumb_file = new File(resultUri.getPath());
                try {
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75).compressToBitmap(thumb_file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //thumb_bitmap.com

                final StorageReference userImg = UserImagesRef.child(currentUser.getUid()+".jpg");
                final UploadTask uploadTask = userImg.putFile(resultUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful())
                                {
                                    throw task.getException();

                                }

                                return userImg.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful())
                                {
                                    myRef.child("image").setValue(task.getResult().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(SettingsActivity.this,"Success uploading",Toast.LENGTH_SHORT).show();
                                                        progressBar.dismiss();
                                                    }else {
                                                        Toast.makeText(SettingsActivity.this,"Error uploading",Toast.LENGTH_SHORT).show();
                                                        progressBar.hide();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                    }
                });
                /*userImg.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                        myRef.child("image").setValue(userImg.getDownloadUrl().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(SettingsActivity.this,"Success uploading",Toast.LENGTH_SHORT).show();
                                            progressBar.dismiss();
                                        }else {
                                            Toast.makeText(SettingsActivity.this,"Error uploading",Toast.LENGTH_SHORT).show();
                                            progressBar.hide();
                                        }
                                    }
                                });
                    }
                });*/
                     /*   .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            myRef.child("image").setValue(task.getResult().getStorage().getDownloadUrl().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(SettingsActivity.this,"Success uploading",Toast.LENGTH_SHORT).show();
                                        progressBar.dismiss();
                                    }else {
                                        Toast.makeText(SettingsActivity.this,"Error uploading",Toast.LENGTH_SHORT).show();
                                        progressBar.hide();
                                    }
                                }
                            });

                            //Toast.makeText(SettingsActivity.this,"Working",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SettingsActivity.this,"Error uploading",Toast.LENGTH_SHORT).show();
                            progressBar.hide();
                        }
                    }
                });*/

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}