package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences; // Uygulama içi kalıcı veri saklama için
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader; // Giriş akışını okuma için
import java.io.IOException; // Giriş/çıkış hatalarını yönetmek için
import java.io.InputStreamReader; // Giriş akışını karakter akışına dönüştürmek için
import java.io.PrintWriter; // Çıkış akışını yazmak için
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView productNameTextView;
    private TextView productDescriptionTextView;
    private EditText quantityEditText;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String serverAddress = "IP";
    private static final int SERVER_PORT = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productNameTextView = findViewById(R.id.product_name);
        productDescriptionTextView = findViewById(R.id.product_description);
        quantityEditText = findViewById(R.id.quantity_edit_text);
        sharedPreferences = getSharedPreferences("cart_prefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Intent intent = getIntent();
        int productId = intent.getIntExtra("product_id", -1);

        if (productId != -1) {
            loadProductDetails(productId);
        } else {
            displayProductNotFound();
        }

        setupAddToCartButton();
    }

    // Ürün detaylarını sunucudan yüklemek için
    private void loadProductDetails(int productId) {
        new Thread(() -> { // Yeni bir iş oluştur
            try (Socket socket = new Socket(serverAddress, SERVER_PORT); // Sunucuya bağlan
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // Çıkış
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) { // Giriş

                // Ürün bilgilerini almak için istek gönder
                out.println("GET_PRODUCT," + productId);
                String response = in.readLine(); // Sunucudan yanıt al
                if (response != null && !response.equals("PRODUCT_NOT_FOUND")) { // Geçerli yanıt kontrolü
                    String[] parts = response.split(","); // Yanıtı parçala
                    String name = parts[1];
                    String description = parts[2];
                    runOnUiThread(() -> { // UI üzerinde güncelleme yap
                        productNameTextView.setText(name);
                        productDescriptionTextView.setText(description);
                    });
                } else {
                    runOnUiThread(this::displayProductNotFound);
                }
            } catch (IOException e) {
                e.printStackTrace(); // Hata durumunda hata çıktısını göster
            }
        }).start();
    }

    private void displayProductNotFound() {
        Toast.makeText(this, "Ürün bulunamadı.", Toast.LENGTH_SHORT).show();
    }

    private void setupAddToCartButton() {
        findViewById(R.id.add_to_cart_button).setOnClickListener(v -> {
            String quantity = quantityEditText.getText().toString();
            if (!quantity.isEmpty()) {
                Set<String> cartItems = sharedPreferences.getStringSet("cart_items", new HashSet<>()); // Sepet öğelerini al
                cartItems.add(productNameTextView.getText() + " - " + quantity);
                editor.putStringSet("cart_items", cartItems); // Yeni sepet öğelerini kaydet
                editor.apply();
                Toast.makeText(this, "Ürün sepete eklendi.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lütfen bir miktar girin.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}