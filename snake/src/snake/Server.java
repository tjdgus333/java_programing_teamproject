package snake;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Server {
    private static ArrayList<Thread> arr = new ArrayList<Thread>();
    private ServerSocket ss;
    public void sendData(Object data) {
        Iterator<Thread> iter = arr.iterator();
        while (iter.hasNext()) {
            Thread t = iter.next();
            if (t.isAlive()) {
                try {
                    CommThread ct = (CommThread) t;
                    ct.sendData(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Server() {
        try {
            ss = new ServerSocket(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int id = 0;
        Server server = new Server();
        while (true) {
            try {
                Socket soc = server.ss.accept();
                System.out.println("new connection arrived" + " " + id);
                Thread t = new CommThread(soc, id++, server);
                t.start();
                arr.add(t);
                Iterator<Thread> iter = arr.iterator();
                while (iter.hasNext()) {
                    t = iter.next();
                    if (!t.isAlive()) {
                        iter.remove();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
