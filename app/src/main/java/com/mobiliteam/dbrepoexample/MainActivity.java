package com.mobiliteam.dbrepoexample;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.stmt.PreparedQuery;
import com.mobiliteam.dbrepo.IDatabaseRepository;
import com.mobiliteam.dbrepoexample.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private IDatabaseRepository dbRepo;

    private RecyclerView recyclerView;
    private UserAdapter mAdapter;
    private List<User> mUsers;
    public static final int REQUEST_CREATE_RECORD = 123;
    public static final int REQUEST_UPDATE_RECORD = 456;
    private static int selectedItemPosition = 0;
    private MaterialToolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerview);
        toolbar = findViewById(R.id.topAppBar);

        dbRepo = ApplicationEx.getInstance().getDbRepo();

        //seedData();
        FloatingActionButton buttonAdd = findViewById(R.id.fab);
        buttonAdd.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
            activityResultLaunch.launch(intent);
        });

        setListener();
        setupRecyclerView();

    }

    private void setListener(){
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.clear){
                confirmDelete();
                return true;
            }
            return false;
        });
    }

    private void confirmDelete(){
        if (mUsers == null || mUsers.size() == 0){
            Toast.makeText(this, "No data!", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete all");
        builder.setMessage("Are you sure?");
        // Add the buttons
        builder.setPositiveButton(R.string.ok, (DialogInterface.OnClickListener) (dialog, id) -> {
            // User clicked OK button
            dialog.dismiss();
            clearData();
        });
        builder.setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) (dialog, id) -> {
            // User cancelled the dialog
            dialog.dismiss();
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void clearData(){
        dbRepo.deleteAll(User.class, mUsers);
        mUsers.clear();
        mAdapter.clear();

    }

    private final ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {

                if (result.getData() != null){
                    int userID = result.getData().getIntExtra("UserID", 0);
                    User user = dbRepo.getById(User.class, userID);
                    if (result.getResultCode() == REQUEST_CREATE_RECORD) {
                        mAdapter.addItem(user);
                    } else if(result.getResultCode() == REQUEST_UPDATE_RECORD) {
                        mAdapter.updateItem(user, selectedItemPosition);
                    }

                }
                selectedItemPosition = 0;
            });

    private void setupRecyclerView(){
        mUsers = dbRepo.getList(User.class);
        mAdapter = new UserAdapter(mUsers, itemListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
    }

    private final UserAdapter.UserItemListener itemListener = new UserAdapter.UserItemListener() {
        @Override
        public void onListItemClick(int position, User user) {
            Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
            intent.putExtra("UserID", user.getId());
            activityResultLaunch.launch(intent);
            selectedItemPosition = position;
        }

        @Override
        public void onDelete(int position, User user) {
            delete(position, user);
        }
    };

    private List<User> findAll() {
        PreparedQuery<User> preparedQuery = null;
        try {
            preparedQuery = dbRepo.getQueryBuilder(User.class)
                    .where()
                    .eq("id", 38)
                    .prepare();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (List<User>) dbRepo.findAll(User.class, preparedQuery);
    }


    private void delete(int position, User user) {
        dbRepo.delete(User.class, user);
        mUsers.remove(position);
        mAdapter.notifyItemRemoved(position);
        mAdapter.notifyItemRangeChanged(position, mUsers.size());
    }


    private void seedData() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User user = new User();
            user.setName("User " + i);
            user.setEmail("example_" + i + "@test.com");
            user.setMobile("900000000" + i);
            users.add(user);
            //iDatabaseRepository.add(User.class, user);
        }
        dbRepo.add(User.class, users);
    }

}
