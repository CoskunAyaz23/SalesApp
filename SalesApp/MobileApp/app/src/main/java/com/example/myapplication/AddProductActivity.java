package com.example.myapplication;

import android.os.AsyncTask; // Arka planda işlemleri yapmak için kullanılıyor
import android.os.Bundle; // Aktivite yaşam döngüsü için gerekli
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast; // Kısa mesaj göstermek için
import androidx.appcompat.app.AppCompatActivity;
import java.io.PrintWriter; // Verileri yazmak için
import java.net.Socket; // Ağ bağlantıları için

public class AddProductActivity extends AppCompatActivity {

    private EditText productNameEditText;
    private EditText productDescriptionEditText;
    private Button saveButton;

    private static final String SERVER_IP = "IP";
    private static final int SERVER_PORT = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        productNameEditText = findViewById(R.id.product_name);
        productDescriptionEditText = findViewById(R.id.product_description);
        saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });
    }

    private void addProduct() {
        String name = productNameEditText.getText().toString().trim(); // Boşlukları temizliyoruz
        String description = productDescriptionEditText.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Ürün adı ve açıklaması boş olamaz.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AddProductTask().execute(name, description);
    }

    private class AddProductTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String name = params[0]; // ürün adı
            String description = params[1]; // ürün açıklaması

            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) { // Sunucuya bağlanıyoruz
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true); // Veriyi göndermek için
                writer.println("ADD_PRODUCT," + name + "," + description); // Ürün ekleme komutunu gönderiyoruz
                return "Ürün eklendi";
            } catch (Exception e) {
                e.printStackTrace();
                return "Hata oluştu";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(AddProductActivity.this, result, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
