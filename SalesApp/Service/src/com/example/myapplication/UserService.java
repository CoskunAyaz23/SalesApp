package com.example.myapplication;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private static final int PORT = 12346;
    private List<User> users;

    public UserService() {
        users = new ArrayList<>();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Kullanıcı servisi başlatıldı, port: " + PORT);
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

        if (request.startsWith("ADD_USER,")) {
            String[] parts = request.split(",");
            if (parts.length == 3) {
                String username = parts[1];
                String password = parts[2];
                users.add(new User(username, password));
                writer.println("Kullanıcı eklendi.");
                System.out.println("Kullanıcı eklendi: " + username);
            } else {
                writer.println("Geçersiz kullanıcı adı veya şifre!");
            }
        } else if (request.equals("GET_USERS")) {
            for (User user : users) {
                writer.println(user);
            }
            writer.println("END");
        } else {
            writer.println("INVALID_REQUEST");
        }
    }

    public static void main(String[] args) {
        new UserService().startServer();
    }
}