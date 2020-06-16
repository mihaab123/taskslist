package com.mihailovalex.ecommerce.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mihailovalex.ecommerce.Interface.ItemClickListner;
import com.mihailovalex.ecommerce.R;
import com.mihailovalex.ecommerce.buyers.CartActivity;
import com.mihailovalex.ecommerce.buyers.HomeActivity;
import com.mihailovalex.ecommerce.buyers.ProductDetailsActivity;
import com.mihailovalex.ecommerce.model.Cart;
import com.mihailovalex.ecommerce.model.Products;
import com.mihailovalex.ecommerce.prevalent.Prevalent;
import com.mihailovalex.ecommerce.viewholder.CartViewHolder;
import com.mihailovalex.ecommerce.viewholder.ProductViewHolder;
import com.squareup.picasso.Picasso;

public class AdminCheckNewProductActivity extends AppCompatActivity {
    private RecyclerView productList;
    private DatabaseReference unverifiedProductsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_new_product);
        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productList = findViewById(R.id.approve_product_list);
        productList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {

        super.onStart();
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(unverifiedProductsRef.orderByChild("productState").equalTo("Not Aprroved"), Products.class)
                .build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter
                = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull Products products) {
                productViewHolder.txtProductName.setText(products.getPname());
                productViewHolder.txtProductDescription.setText(products.getDescription());
                productViewHolder.txtProductPrice.setText("Price = " + products.getPrice() + "$");
                Picasso.get().load(products.getImage()).into(productViewHolder.imageView);
                final Products itemClick = products;
                productViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                    public void onClick(View view) {
                        final String productId = itemClick.getPid();
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Yes",
                                        "No"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminCheckNewProductActivity.this);
                        builder.setTitle("Do you want approved this product?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0){
                                    changeProductState(productId);
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

    private void changeProductState(String productId) {

        unverifiedProductsRef.child(productId).child("productState")
            .setValue("Approved")
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(AdminCheckNewProductActivity.this,"That item has been approved, and it is now available for sale.",Toast.LENGTH_SHORT).show();
                }
            });
    }
}