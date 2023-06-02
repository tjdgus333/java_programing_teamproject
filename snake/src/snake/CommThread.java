package snake;

import java.io.*;
import java.net.Socket;

public class CommThread extends Thread {
    private Socket soc;
    private int id;
    private Server server;

    public CommThread(Socket soc, int id, Server server) {
        this.soc = soc;
        this.id = id;
        this.server = server;
    }

    public void sendData(Object data) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(soc.getOutputStream());
            oos.writeObject(data);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        while (true) {
            try {
                ObjectInputStream ois = new ObjectInputStream(soc.getInputStream());
                Object data = ois.readObject();
                server.sendData(data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
