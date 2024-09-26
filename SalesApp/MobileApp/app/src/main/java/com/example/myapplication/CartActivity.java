package com.example.myapplication;

import android.content.DialogInterface; // Diyalog arayüzleri için gerekli
import android.content.SharedPreferences; // Kalıcı veri saklamak için
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter; // Liste verilerini göstermek için
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog; // Alert diyaloğu için
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList; // Dinamik dizi oluşturmak için
import java.util.HashSet; // Set veri yapısı için
import java.util.List; // Liste arayüzü
import java.util.Set; // Set arayüzü

public class CartActivity extends AppCompatActivity {

    private ListView cartListView;
    private Button sendButton;
    private ArrayAdapter<String> cartAdapter;
    private List<String> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartListView = findViewById(R.id.cart_list_view);
        sendButton = findViewById(R.id.send_button);

        loadCartItems();

        sendButton.setOnClickListener(v -> sendProductsToSentProducts());

        cartListView.setOnItemClickListener((parent, view, position, id) -> {
            showRemoveConfirmationDialog(position);
        });
    }

    private void loadCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("cart_prefs", MODE_PRIVATE);
        Set<String> cartSet = sharedPreferences.getStringSet("cart_items", new HashSet<>()); // Sepet setini alıyoruz
        cartItems = new ArrayList<>(cartSet); // Set'i listeye çeviriyoruz

        // ArrayAdapter ile sepet öğelerini listeye bağlıyoruz
        cartAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cartItems);
        cartListView.setAdapter(cartAdapter);
    }

    private void sendProductsToSentProducts() {
        SharedPreferences sharedPreferences = getSharedPreferences("sent_product_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> sentProductSet = new HashSet<>(sharedPreferences.getStringSet("sent_product_items", new HashSet<>()));

        // Sepetteki her ürünü gönderilen ürünler setine ekliyoruz
        for (String item : cartItems) {
            sentProductSet.add(item);
        }

        editor.putStringSet("sent_product_items", sentProductSet); // Gönderilen ürünleri kaydediyoruz
        editor.apply();

        clearCart();

        Toast.makeText(this, "Ürünler gönderildi.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void clearCart() {
        SharedPreferences sharedPreferences = getSharedPreferences("cart_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void showRemoveConfirmationDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Silme Onayı")
                .setMessage("Bu ürünü silmek istediğinize emin misiniz?")
                .setPositiveButton("Evet", (dialog, which) -> removeProductFromCart(position))
                .setNegativeButton("Hayır", null)
                .show();
    }

    private void removeProductFromCart(int position) {
        String itemToRemove = cartItems.get(position);
        cartItems.remove(position);
        cartAdapter.notifyDataSetChanged();

        // Sepetteki verileri güncelle
        SharedPreferences sharedPreferences = getSharedPreferences("cart_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> cartSet = new HashSet<>(sharedPreferences.getStringSet("cart_items", new HashSet<>()));
        cartSet.remove(itemToRemove);
        editor.putStringSet("cart_items", cartSet); // Güncellenmiş seti kaydet
        editor.apply();

        Toast.makeText(this, "Ürün sepetten silindi.", Toast.LENGTH_SHORT).show();
    }
}