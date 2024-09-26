package com.example.myapplication;

import android.content.ContentValues; // İçerik değerlerini yönetmek için
import android.content.Context; // Android bağlamı için
import android.database.Cursor; // Veritabanı sorgularını almak için
import android.database.sqlite.SQLiteDatabase; // SQLite veritabanı ile çalışmak için
import android.database.sqlite.SQLiteOpenHelper; // SQLite veritabanı açma ve güncelleme için
import android.util.Log; // Logcat için log mesajlarını yazmak için
import java.util.ArrayList; // Dinamik dizi için
import java.util.List; // Liste arayüzü için

public class ProductDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "product_database.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";

    public ProductDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("ALTER TABLE " + TABLE_PRODUCTS + " ADD COLUMN " + COLUMN_DESCRIPTION + " TEXT");
        }
    }

    public long addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase(); // Yazma için veritabanını aç
        ContentValues values = new ContentValues(); // İçerik değerlerini tutmak için
        values.put(COLUMN_NAME, product.getName());
        values.put(COLUMN_DESCRIPTION, product.getDescription());

        long result = db.insert(TABLE_PRODUCTS, null, values);

        if (result != -1) {
            Log.d("ProductDatabaseHelper", "Ürün başarıyla eklendi: " + product.getName());
        } else {
            Log.e("ProductDatabaseHelper", "Ürün ekleme başarısız: " + product.getName());
        }

        db.close();
        return result;
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>(); // Ürün listesini tutmak için
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getReadableDatabase(); // Okuma için veritabanını aç
        Cursor cursor = db.rawQuery(selectQuery, null); // Sorguyu çalıştır

        if (cursor.moveToFirst()) { // İlk satıra git
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                Product product = new Product(id, name, description);
                productList.add(product);
            } while (cursor.moveToNext()); // Sonraki satıra geç
        }

        Log.d("ProductDatabaseHelper", "Veritabanından ürünler yüklendi: " + productList.size() + " adet ürün");

        cursor.close();
        db.close();
        return productList;
    }

    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int productId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            cursor.close();
            return new Product(productId, name, description);
        }
        return null;
    }

    public void deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}