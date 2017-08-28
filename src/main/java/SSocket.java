import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SSocket implements Runnable {
    private Socket socket;

    public SSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            if (socket.isClosed()) {
                Main.clients.remove(socket);
            } else {
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();


                ObjectOutputStream mapOutputStream = new ObjectOutputStream(out);
                ObjectInputStream mapInputStream = new ObjectInputStream(in);


                HashMap<String, String> map = null;
                while (true) {
                    Thread.sleep(100);
                    map = (HashMap<String, String>)mapInputStream.readObject();
                    System.out.println("Request: " + Arrays.toString(map.keySet().toArray())+ Arrays.toString(map.values().toArray()));
                    map.put("status", "200");
                    mapOutputStream.writeObject(map);
                    mapOutputStream.flush();
                }
            }
        } catch (Exception e) {
            Main.clients.remove(socket);
            System.out.println(e.getMessage());
        }
    }
}
