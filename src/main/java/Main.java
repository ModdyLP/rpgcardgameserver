import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Main {
    private static PriorityQueue<Object> socketMap;
    public static ObservableList<Socket> clients = FXCollections.observableArrayList();
    public static ObservableMap<String, SSocket> clientwithid = FXCollections.observableHashMap();
    private static HashMap<String, JSONObject> lobbydata = new HashMap<>();
    private static ServerSocket ss;

    public static void main(String[] args) {
        System.out.println("Server starting...");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Closing connections...");
            try {
                for (SSocket client : clientwithid.values()) {
                    client.getSocket().close();
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
    public static JSONObject getLobbyData(String lobbyid) {
        if (lobbydata.containsKey(lobbyid)) {
            return lobbydata.get(lobbyid);
        } else {
            JSONObject dataobject = new JSONObject();
            lobbydata.put(lobbyid, dataobject);
            return dataobject;
        }
    }
    public static void addSocketwithid(String id, SSocket socket) {
        clientwithid.put(id, socket);
    }
    public static SSocket getSocketwithid(String id) {
        return clientwithid.get(id);
    }
    public static void senddatatosocket(String id, String clientid) {
        JSONObject data = getLobbyData(id);
        if (data.has("client1") && clientid.equals(data.getString("client1"))) {
            clientwithid.get(data.getString("client2")).senddata(data.getString("client2"), "disconnect");
            clientwithid.remove(clientid);
            lobbydata.remove(id);
        } else if (data.has("client2") && clientid.equals(data.getString("client2"))) {
            clientwithid.get(data.getString("client1")).senddata(data.getString("client1"), "disconnect");
            clientwithid.remove(clientid);
            lobbydata.remove(id);
        }
    }
}
