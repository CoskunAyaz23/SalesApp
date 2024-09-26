package com.example.myapplication;

import java.io.*; // Giriş/çıkış işlemleri için
import java.net.*; // Ağa erişim için
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private static final int PORT = 12345;
    private static final String PRODUCT_FILE = "products.txt";
    private List<Product> products;

    public ProductService() {
        products = loadProductsFromFile();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Sunucu başlatıldı, port: " + PORT);
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("Yeni bir istemci bağlandı.");
                    handleClient(socket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) throws IOException {
        InputStream input = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

        String request = reader.readLine();
        System.out.println("İstek alındı: " + request);

        if (request.startsWith("ADD_PRODUCT,")) {
            String[] parts = request.split(",");
            if (parts.length == 3) {
                int newId = products.size() + 1;
                String name = parts[1];
                String description = parts[2];
                products.add(new Product(newId, name, description));
                writeProductsToFile();
                writer.println("PRODUCT_ADDED");
                System.out.println("Ürün eklendi: " + name);
            } else {
                writer.println("INVALID_PRODUCT_DATA");
            }
        } else if (request.equals("GET_PRODUCTS")) {
            for (Product product : products) {
                writer.println(product);
            }
            writer.println("END");
        } else if (request.startsWith("GET_PRODUCT,")) {
            String[] parts = request.split(",");
            int id = Integer.parseInt(parts[1]);
            Product product = getProductById(id);
            if (product != null) {
                writer.println(product);
            } else {
                writer.println("PRODUCT_NOT_FOUND");
            }
        } else if (request.startsWith("DELETE_PRODUCT,")) {
            String[] parts = request.split(",");
            int idToDelete = Integer.parseInt(parts[1]);
            deleteProductById(idToDelete);
            writer.println("PRODUCT_DELETED");
        } else {
            writer.println("INVALID_REQUEST");
        }
    }

    private Product getProductById(int id) {
        for (Product product : products) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }

    private List<Product> loadProductsFromFile() {
        List<Product> productList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String description = parts[2];
                productList.add(new Product(id, name, description));
            }
        } catch (IOException e) {
            System.err.println("Ürün dosyası okunurken hata oluştu: " + e.getMessage());
        }
        return productList;
    }

    private void writeProductsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCT_FILE))) {
            for (Product product : products) {
                writer.println(product.getId() + "," + product.getName() + "," + product.getDescription());
            }
        } catch (IOException e) {
            System.err.println("Ürün dosyası yazılırken hata oluştu: " + e.getMessage());
        }
    }

    private void deleteProductById(int id) {
        products.removeIf(product -> product.getId() == id);
        writeProductsToFile();
    }

    public static void main(String[] args) {
        new ProductService().startServer();
    }
}