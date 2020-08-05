package com.mihailovalex.lapitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {
    @BindView(R.id.all_users_toolbar)
    Toolbar toolbar;
    @BindView(R.id.all_users_list)
    RecyclerView recyclerView;
    private DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.all_users);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(myRef.orderByChild("Users"), Users.class)
                        .build();
        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder usersViewHolder, int i, @NonNull Users users) {
                usersViewHolder.txtName.setText(users.getName());
                usersViewHolder.txtStatus.setText(users.getStatus());
                Picasso.get().load(users.getImage()).into(usersViewHolder.imageView);
                usersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*if (type.equals("Admin")){
                            Intent intent =new Intent(HomeActivity.this, AdminMaintainProductsActivity.class);
                            intent.putExtra("pid",model.getPid());
                            startActivity(intent);
                        }
                        else {
                            Intent intent =new Intent(HomeActivity.this, ProductDetailsActivity.class);
                            intent.putExtra("pid",model.getPid());
                            startActivity(intent);
                        }*/

                    }
                });
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_item, parent, false);
                UsersViewHolder holder = new UsersViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    public class UsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View mView;
        @BindView(R.id.all_users_name)
        public TextView txtName;
        @BindView(R.id.all_users_status)
        public TextView txtStatus;
        @BindView(R.id.profile_image)
        public CircleImageView imageView;
        public ItemClickListner listner;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setItemClickListner(ItemClickListner listner)
        {
            this.listner = listner;
        }

        @Override
        public void onClick(View view)
        {
            listner.onClick(view, getAdapterPosition(), false);
        }
    }
}