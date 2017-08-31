import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SSocket implements Runnable {
    private SSocket instance;
    private Socket socket;
    private JSONObject sendjson = new JSONObject();
    private String clientid;
    private String lobbyid;
    InputStream in;
    OutputStream out;
    PrintWriter outstr;
    BufferedReader instr;

    public SSocket(Socket socket) {
        this.instance = this;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            if (socket.isClosed()) {
                Main.clients.remove(socket);
            } else {
                in =  socket.getInputStream();
                out = socket.getOutputStream();
                outstr = new PrintWriter(out);
                instr = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));


                System.out.println("Socket init and connected");
                while (true) {
                    Thread.sleep(200);
                    String line = instr.readLine();
                    if (line.equals("")) {
                        System.out.println("Request without object");
                        break;
                    }
                    JSONObject json = new JSONObject(line);
                    if (json.has("lobbyid")) {
                        JSONObject data = Main.getLobbyData(json.getString("lobbyid"));
                        lobbyid = json.getString("lobbyid");
                        if (json.has("client1")) {
                            data.put("client1", json.get("client1"));
                            sendjson.put("client1", data.get("client1"));
                            Main.addSocketwithid(data.getString("client1"), this.instance);
                            if (data.has("client2")) {
                                sendjson.put("client2", data.get("client2"));
                            }
                            clientid = data.getString("client1");
                        } else if (json.has("client2")) {
                            data.put("client2", json.get("client2"));
                            sendjson.put("client2", data.get("client2"));
                            Main.addSocketwithid(data.getString("client2"), this.instance);
                            if (data.has("client1")) {
                                sendjson.put("client1", data.get("client1"));
                            }
                            clientid = data.getString("client2");
                        }
                    }
                    sendjson.put("status", 200);
                    System.out.println("Response: " + sendjson.toString());
                    outstr.println(sendjson.toString());
                    outstr.flush();
                }
            }
        } catch (Exception e) {
            Main.clients.remove(socket);
            Main.senddatatosocket(lobbyid, clientid);
            System.out.println(e.getMessage());
        }
    }
    public void senddata(String clientid, String action) {
        System.out.println("Sending data to "+clientid);
        sendjson.put(action, clientid);
        outstr.println(sendjson.toString());
        outstr.flush();
    }
    public Socket getSocket() {
        return this.socket;
    }
}
