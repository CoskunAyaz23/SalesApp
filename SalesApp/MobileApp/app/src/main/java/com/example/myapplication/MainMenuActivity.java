package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button addProductButton = findViewById(R.id.add_product_button);
        Button productListButton = findViewById(R.id.product_list_button);
        Button cartButton = findViewById(R.id.cart_button);
        Button sentProductsButton = findViewById(R.id.sent_products_button);

        addProductButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, AddProductActivity.class);
            startActivity(intent);
        });

        productListButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, ProductListActivity.class);
            startActivity(intent);
        });

        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, CartActivity.class);
            startActivity(intent);
        });

        sentProductsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, SentProductsActivity.class);
            startActivity(intent);
        });
    }
}