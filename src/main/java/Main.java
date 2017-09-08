import javafx.collections.*;
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
    public static ObservableMap<String, JSONObject> lobbydata = FXCollections.observableHashMap();
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
        lobbydata.addListener((MapChangeListener<String, JSONObject>) change -> {
            if (change.wasAdded()) {
                System.out.println("New Lobby identified: " + change.getKey());
            } else if (change.wasRemoved()){
                System.out.println("Lobby has Lost all Players: "+change.getKey());
            } else {
                System.out.println("something else happend: "+change.getKey());
            }
        });
        int port = 666; //random port number
        try {
            Game.getInstance().startLobbyListener();
            ss = new ServerSocket(port);
            System.out.println("Waiting for a client....");


            while (true) {
                Socket socket = ss.accept();
                if (socket != null && socket.isConnected()) {
                    clients.add(socket);
                    SSocket sSocket = new SSocket(socket);
                    Thread t = new Thread(sSocket);
                    t.start();
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
            if (data.has("client2")) {
                clientwithid.get(data.getString("client2")).senddata(data.getString("client2"), "disconnect");
            } else {
                System.out.println("Client 1 disconnect Request: Client 2 is already disconnected");
            }
            clientwithid.remove(clientid);
            lobbydata.get(id).remove("client1");
        } else if (data.has("client2") && clientid.equals(data.getString("client2"))) {
            if (data.has("client1")) {
                clientwithid.get(data.getString("client1")).senddata(data.getString("client1"), "disconnect");
            } else {
                System.out.println("Client 2 disconnect Request: Client 1 is already disconnected");
            }
            clientwithid.remove(clientid);
            lobbydata.get(id).remove("client2");
        }
    }
}
