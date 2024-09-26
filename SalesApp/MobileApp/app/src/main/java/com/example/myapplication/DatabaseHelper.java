package com.example.myapplication;

import android.content.ContentValues; // Veri eklemek için gerekli sınıf
import android.content.Context; // Uygulama bağlamı için
import android.database.Cursor; // Veri okuma için
import android.database.sqlite.SQLiteDatabase; // SQLite veritabanı sınıfı
import android.database.sqlite.SQLiteOpenHelper; // SQLite veritabanı yönetimi için yardımcı sınıf
import android.util.Log; // Log kaydı için
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USERNAME + " TEXT PRIMARY KEY,"
                + COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase(); // Yazma izni ile veritabanına erişiyoruz
        ContentValues values = new ContentValues(); // Yeni içerik değerleri oluştur
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PASSWORD, user.getPassword());

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase(); // Okuma izni ile veritabanına erişiyoruz
        String[] columns = { COLUMN_USERNAME };
        String selection = COLUMN_USERNAME + "=?" + " AND " + COLUMN_PASSWORD + "=?";
        String[] selectionArgs = { username, password };
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>(); // Kullanıcı listesi oluştur
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, null); // Tüm kullanıcıları al
        if (cursor.moveToFirst()) { // Cursor ilk elemana git
            try {
                int usernameIndex = cursor.getColumnIndexOrThrow(COLUMN_USERNAME);
                int passwordIndex = cursor.getColumnIndexOrThrow(COLUMN_PASSWORD);
                do {
                    String username = cursor.getString(usernameIndex);
                    String password = cursor.getString(passwordIndex);
                    userList.add(new User(username, password));
                } while (cursor.moveToNext()); // Sonraki elemana geç
            } catch (IllegalArgumentException e) {
                Log.e("DatabaseHelper", "Column not found", e);
            }
        }
        cursor.close();
        db.close();
        return userList;
    }

    public void deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, COLUMN_USERNAME + "=?", new String[]{username});
        db.close();
    }
}