import org.json.JSONObject;

import java.util.ArrayList;

public class Game {
    private static Game instance;

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public void startLobbyListener() {
        Thread thread = new Thread(() -> {
            System.out.println("Game Listener Started");
            while (true) {
                try {
                    Thread.sleep(100);
                    for (String lobbyid : Main.lobbydata.keySet()) {
                        JSONObject jsonobject = Main.lobbydata.get(lobbyid);
                        if (!jsonobject.has("client1") && !jsonobject.has("client2")) {
                            Main.lobbydata.remove(lobbyid);
                        } else {
                            if (!jsonobject.has("started") && jsonobject.has("client1") && jsonobject.has("client2")) {
                                jsonobject.put("started", true);
                                Main.getSocketwithid(jsonobject.getString("client1")).senddata(jsonobject.getString("client1"), "start");
                                System.out.println("Started Lobby: " + lobbyid);
                            }
                        }
                        Main.lobbydata.replace(lobbyid, jsonobject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
