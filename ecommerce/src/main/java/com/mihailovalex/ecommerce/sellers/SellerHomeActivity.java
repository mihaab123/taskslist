package com.mihailovalex.ecommerce.sellers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mihailovalex.ecommerce.R;
import com.mihailovalex.ecommerce.buyers.MainActivity;
import com.mihailovalex.ecommerce.model.Products;
import com.mihailovalex.ecommerce.viewholder.SellerViewHolder;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SellerHomeActivity extends AppCompatActivity {
    private RecyclerView productList;
    private DatabaseReference unverifiedProductsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);

        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productList = findViewById(R.id.seller_product_list);
        productList.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent = null;
                        switch (item.getItemId()) {
                            case R.id.navigation_home:

                                break;
                            case R.id.navigation_add:
                                intent =new Intent(SellerHomeActivity.this, SellerProductCategoryActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                break;
                            case R.id.navigation_logout:
                                final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                mAuth.signOut();
                                intent =new Intent(SellerHomeActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                break;
                        }
                        return false;
                    }
                });
       /* // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_add, R.id.navigation_logout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(unverifiedProductsRef.orderByChild("sId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()), Products.class)
                .build();
        FirebaseRecyclerAdapter<Products, SellerViewHolder> adapter
                = new FirebaseRecyclerAdapter<Products, SellerViewHolder>(options) {
            @NonNull
            @Override
            public SellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_items_view,parent,false);
                SellerViewHolder holder = new SellerViewHolder(view);
                return holder;
            }

            @Override
            protected void onBindViewHolder(@NonNull SellerViewHolder sellerViewHolder, int i, @NonNull Products products) {
                sellerViewHolder.txtProductName.setText(products.getPname());
                sellerViewHolder.txtProductDescription.setText(products.getDescription());
                sellerViewHolder.txtProductPrice.setText("Price = " + products.getPrice() + "$");
                sellerViewHolder.txtProductState.setText("State = " + products.getProductState());

                Picasso.get().load(products.getImage()).into(sellerViewHolder.imageView);
                final Products itemClick = products;
                sellerViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        final String productId = itemClick.getPid();
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Yes",
                                        "No"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(SellerHomeActivity.this);
                        builder.setTitle("Do you want delete this product?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0){
                                    deleteProduct(productId);
                                }
                                if (i==1){

                                }
                            }
                        });
                        builder.show();
                    }
                });

            }
        };
        productList.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteProduct(String productId) {
        unverifiedProductsRef.child(productId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SellerHomeActivity.this,"That item has been deleted.",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}