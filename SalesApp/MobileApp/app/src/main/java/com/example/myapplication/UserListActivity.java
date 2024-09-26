package com.example.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private ListView userListView;
    private DatabaseHelper databaseHelper;
    private ArrayAdapter<User> userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        userListView = findViewById(R.id.user_list_view);
        databaseHelper = new DatabaseHelper(this);

        loadUsers();

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = (User) userAdapter.getItem(position);
                if (selectedUser != null) {
                    showDeleteConfirmationDialog(selectedUser);
                } else {
                    Log.e("UserListActivity", "Selected user is null");
                }
            }
        });
    }

    private void loadUsers() {
        try {
            List<User> userList = databaseHelper.getAllUsers();
            if (userList != null) {
                userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
                userListView.setAdapter(userAdapter);
            } else {
                Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("UserListActivity", "Error loading users", e);
            Toast.makeText(this, "Error loading users", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog(final User user) {
        new AlertDialog.Builder(this)
                .setTitle("Kullanıcıyı Sil")
                .setMessage("Bu kullanıcıyı silmek istediğinizden emin misiniz?")
                .setPositiveButton("Sil", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            databaseHelper.deleteUser(user.getUsername());
                            loadUsers();
                        } catch (Exception e) {
                            Log.e("UserListActivity", "Error deleting user", e);
                            Toast.makeText(UserListActivity.this, "Error deleting user", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("İptal", null)
                .show();
    }
}