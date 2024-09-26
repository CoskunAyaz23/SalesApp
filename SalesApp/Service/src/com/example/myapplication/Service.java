package com.example.myapplication;

public class Service {
    public static void main(String[] args) {
        new Thread(() -> new UserService().startServer()).start(); // UserService için
        new Thread(() -> new ProductService().startServer()).start(); // ProductService için
    }
}