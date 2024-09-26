package com.example.myapplication;

import android.content.Intent; // Başka aktivitelere geçiş yapmak için gerekli
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader; // Sunucudan veri okumak için
import java.io.IOException; // Giriş/Çıkış hatalarını yakalamak için
import java.io.InputStreamReader; // Akıştan veri okumak için
import java.io.PrintWriter; // Sunucuya veri yazmak için
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button addButton;
    private Button viewUsersButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        addButton = findViewById(R.id.add_button);
        viewUsersButton = findViewById(R.id.view_users_button);

        databaseHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (databaseHelper.checkUser(username, password)) {
                    Toast.makeText(MainActivity.this, "Giriş başarılı!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Geçersiz kullanıcı adı veya şifre!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                new AddUserTask().execute(username, password);
            }
        });

        viewUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserListActivity.class);
                startActivity(intent);
            }
        });
    }

    // Kullanıcı ekleme işlemi için AsyncTask
    private class AddUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0]; // Kullanıcı adını alıyoruz
            String password = params[1]; // Şifreyi alıyoruz

            try (Socket socket = new Socket("IP", 12346)) { // Sunucuya bağlanıyoruz
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true); // Veri göndermek için PrintWriter
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Sunucudan veri almak için BufferedReader

                writer.println("ADD_USER," + username + "," + password); // Kullanıcı ekleme isteğini gönderiyoruz
                String response = reader.readLine(); // Sunucudan gelen yanıtı alıyoruz

                if ("Kullanıcı eklendi.".equals(response)) {
                    User newUser = new User(username, password);
                    databaseHelper.addUser(newUser);
                }

                return response;
            } catch (IOException e) {
                e.printStackTrace();
                return "UserService'e bağlanırken hata oluştu.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }
}