import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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

                DataInputStream dIn = new DataInputStream(in);
                DataOutputStream dOut = new DataOutputStream(out);


                String line = null;
                while (true) {
                    line = dIn.readUTF();
                    System.out.println("Request: " + line);
                    dOut.writeUTF("\nResponse: 200");
                    dOut.flush();
                }
            }
        } catch (Exception e) {
            Main.clients.remove(socket);
        }
    }
}
