package com.example.myapplication;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SentProductsActivity extends AppCompatActivity {

    private ListView sentProductListView;
    private ArrayAdapter<String> sentProductAdapter;
    private List<String> sentProductItems;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_products);

        sentProductListView = findViewById(R.id.sent_products_list_view);
        sharedPreferences = getSharedPreferences("sent_product_prefs", MODE_PRIVATE);

        loadSentProductItems();

        sentProductListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedProduct = sentProductItems.get(position);
                confirmDelete(selectedProduct);
            }
        });
    }

    private void loadSentProductItems() {
        Set<String> sentProductSet = sharedPreferences.getStringSet("sent_product_items", new HashSet<>());
        sentProductItems = new ArrayList<>(sentProductSet);

        if (sentProductItems.isEmpty()) {
            Toast.makeText(this, "Gönderilen ürün yok.", Toast.LENGTH_SHORT).show();
        }

        // Adapter'i oluştur ve ListView'e ata
        sentProductAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sentProductItems);
        sentProductListView.setAdapter(sentProductAdapter);
    }

    private void confirmDelete(String product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Silme Onayı");
        builder.setMessage(product + " ürününü silmek istediğinize emin misiniz?");

        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteSentProduct(product);
            }
        });

        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteSentProduct(String product) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> sentProductSet = new HashSet<>(sharedPreferences.getStringSet("sent_product_items", new HashSet<>()));

        if (sentProductSet != null) {
            sentProductSet.remove(product);
            editor.putStringSet("sent_product_items", sentProductSet);
            editor.apply();
        }

        loadSentProductItems();
        Toast.makeText(this, product + " silindi.", Toast.LENGTH_SHORT).show();
    }
}