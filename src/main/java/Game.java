public class Game {
    private static Game instance;

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }
    public void startLobbyListener() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Game Listener Started");
                while(true) {
                    Main.lobbydata.forEach((lobbyid, jsonobect) -> {
                        if (!jsonobect.has("started") && jsonobect.has("client1") && jsonobect.has("client2")) {
                            jsonobect.put("started", true);
                            Main.getSocketwithid(jsonobect.getString("client1")).senddata(jsonobect.getString("client1"), "start");
                            System.out.println("Started Lobby: "+lobbyid);
                        }
                    });
                }
            }
        });
        thread.start();
    }
}
