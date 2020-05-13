package com.mihailovalex.reminder_room.ui.birthdays;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mihailovalex.reminder_room.MainActivity;
import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.ViewModelFactory;
import com.mihailovalex.reminder_room.adapter.BirthdayAdapter;
import com.mihailovalex.reminder_room.data.Item;
import com.mihailovalex.reminder_room.databinding.FragmentBirthdaysBinding;
import com.mihailovalex.reminder_room.ui.addeditbirthday.AddEditBirthdayActivity;
import com.mihailovalex.reminder_room.ui.addedittask.AddEditTaskActivity;
import com.mihailovalex.reminder_room.ui.addedittask.AddEditTaskFragment;

import java.util.ArrayList;

public class BirthdayFragment extends Fragment {

    private RecyclerView recyclerView;
    private BirthdayAdapter adapter;
    public MainActivity activity;
    private BirthdayViewModel birthdayViewModel;
    protected SearchView searchView;

    private FragmentBirthdaysBinding birthdaysFragBinding;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity()!=null){
            activity = (MainActivity) getActivity();
        }

        setupFab();

        setupListAdapter();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(getActivity().getApplication());

        birthdayViewModel =
                ViewModelProviders.of(getActivity(), factory).get(BirthdayViewModel.class);
        birthdayViewModel.setFiltering();
        birthdaysFragBinding = FragmentBirthdaysBinding.inflate(inflater, container, false);
        birthdaysFragBinding.setViewmodel(birthdayViewModel);

        // Subscribe to "open task" event
        birthdayViewModel.getOpenBirthdayEvent().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(@Nullable Long birthdayId) {
                if (birthdayId != null) {
                    openBirthdayDetails(birthdayId);
                }
            }
        });

        // Subscribe to "new task" event
        birthdayViewModel.getNewBirthdayEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void _) {
                addNewBirthday();
            }
        });
        // Subscribe to "search task" event
        birthdayViewModel.searchString.setValue("");
        birthdayViewModel.getSearchString().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String taskId) {
                //if (!taskId.isEmpty()) {
                birthdayViewModel.loadBirthdays(true);
                //}
            }
        });
        setHasOptionsMenu(true); // It's important here
        return birthdaysFragBinding.getRoot();
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()){
                    birthdayViewModel.searchString.setValue("");
                } else {
                    birthdayViewModel.searchString.setValue("%"+newText+"%");
                }
                return true;
            }
        });
    }
    public void openBirthdayDetails(long birthdayId) {
        Intent intent = new Intent(getActivity(), AddEditBirthdayActivity.class);
        intent.putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, birthdayId);
        startActivityForResult(intent, AddEditBirthdayActivity.REQUEST_CODE);

    }
    public void addNewBirthday() {
        Intent intent = new Intent(getActivity(), AddEditBirthdayActivity.class);
        startActivityForResult(intent, AddEditBirthdayActivity.REQUEST_CODE);
    }


    @Override
    public void onResume() {
        super.onResume();
        birthdayViewModel.start();
    }

    private void setupListAdapter() {
        recyclerView = birthdaysFragBinding.recyclerViewBirthday;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BirthdayAdapter(
                new ArrayList<Item>(0),
                birthdayViewModel
        );
        recyclerView.setAdapter(adapter);
    }

    private void setupFab() {
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birthdayViewModel.addNewBirthday();
            }
        });
        fab.setVisibility(View.VISIBLE);
    }
}
