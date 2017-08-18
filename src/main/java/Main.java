import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class Main {
    private static PriorityQueue<Object> socketMap;
    public static ObservableList<Socket> clients = FXCollections.observableArrayList();
    public static ObservableList<Socket> allclients = FXCollections.observableArrayList();
    private static ServerSocket ss;

    public static void main(String[] args) {
        System.out.println("Server starting...");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Closing connections...");
            try {
                for (Socket client : allclients) {
                    client.close();
                }
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "Shutdown-thread"));
        clients.addListener((ListChangeListener<Socket>) c -> {
            c.next();
            if (c.wasAdded()) {
                System.out.println("Client connected: " + clients.size());
            } else if (c.wasRemoved()) {
                System.out.println("Client disconnected: " + clients.size());
            } else {
                System.out.println("Error on Client");
            }

        });
        int port = 666; //random port number
        try {

            ss = new ServerSocket(port);
            System.out.println("Waiting for a client....");


            while (true) {
                Socket socket = ss.accept();
                if (socket != null && socket.isConnected()) {
                    clients.add(socket);
                    allclients.add(socket);
                }
                for (Socket client : clients) {
                    if (socket != null && socket.isConnected()) {
                        SSocket sSocket = new SSocket(client);
                        Thread t = new Thread(sSocket);
                        t.start();
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
