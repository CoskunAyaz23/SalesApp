package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private ListView productListView;
    private List<Product> productList;
    private static final String SERVER_IP = "IP";
    private static final int SERVER_PORT = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        productListView = findViewById(R.id.product_list_view);

        loadProducts();

        productListView.setOnItemClickListener((parent, view, position, id) -> {
            Product selectedProduct = productList.get(position);
            showProductOptionsDialog(selectedProduct);
        });
    }

    // Ürünleri sunucudan yüklemek için metot
    private void loadProducts() {
        new FetchProductsTask().execute();
    } // AsyncTask başlat

    // Ürünleri arka planda yüklemek için AsyncTask
    private class FetchProductsTask extends AsyncTask<Void, Void, List<Product>> {
        @Override
        protected List<Product> doInBackground(Void... voids) {
            List<Product> productList = new ArrayList<>();
            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                writer.println("GET_PRODUCTS");
                String response;
                while (!(response = reader.readLine()).equals("END")) {
                    String[] data = response.split(",");
                    Product product = new Product(Integer.parseInt(data[0]), data[1], data[2]);
                    productList.add(product);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ProductListActivity.this, "Ürünleri yüklerken hata oluştu.", Toast.LENGTH_SHORT).show();
            }
            return productList;
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            productList = products;
            if (productList.isEmpty()) {
                Toast.makeText(ProductListActivity.this, "Hiç ürün yok.", Toast.LENGTH_SHORT).show();
            } else {
                ArrayAdapter<Product> adapter = new ArrayAdapter<>(ProductListActivity.this, android.R.layout.simple_list_item_1, productList);
                productListView.setAdapter(adapter);
            }
        }
    }

    private void showProductOptionsDialog(final Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Ürün Seçenekleri")
                .setItems(new CharSequence[]{"Ürün Detayları", "Ürünü Sil"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                            intent.putExtra("product_id", product.getId());
                            startActivity(intent);
                            break;
                        case 1:
                            showDeleteConfirmationDialog(product);
                            break;
                    }
                })
                .show();
    }

    private void showDeleteConfirmationDialog(final Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Ürünü Sil")
                .setMessage("Bu ürünü silmek istediğinizden emin misiniz?")
                .setPositiveButton("Sil", (dialog, which) -> {
                    new DeleteProductTask().execute(product);
                })
                .setNegativeButton("İptal", null)
                .show();
    }

    // Ürünü silmek için AsyncTask
    private class DeleteProductTask extends AsyncTask<Product, Void, Void> {
        @Override
        protected Void doInBackground(Product... products) {
            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println("DELETE_PRODUCT," + products[0].getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadProducts();
            Toast.makeText(ProductListActivity.this, "Ürün silindi.", Toast.LENGTH_SHORT).show();
        }
    }
}