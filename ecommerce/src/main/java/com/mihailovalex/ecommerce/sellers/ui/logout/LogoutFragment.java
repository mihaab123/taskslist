package com.mihailovalex.ecommerce.sellers.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mihailovalex.ecommerce.admin.AdminMaintainProductsActivity;
import com.mihailovalex.ecommerce.buyers.HomeActivity;
import com.mihailovalex.ecommerce.buyers.MainActivity;
import com.mihailovalex.ecommerce.R;

public class LogoutFragment extends Fragment {

    private LogoutViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(LogoutViewModel.class);
        View root = inflater.inflate(R.layout.fragment_logout, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
//        Intent intent =new Intent(getContext(), MainActivity.class);
  //      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    //    startActivity(intent);
        return root;


    }
}