package immortal.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class HeartbeatThread extends Thread {
    private boolean tryToReconnect = true;
    private Socket socket = null;
    private Thread heartbeatThread;

    public HeartbeatThread() throws Exception {
        connect();

        (heartbeatThread = new Thread(){
            public void run() {
                while(tryToReconnect) {
                    try {
                        socket.getOutputStream().write(555);
                        sleep(5000);
                    } catch(Exception ex) {
                        System.out.println(ex.getMessage());
                        connect();
                    }
                }
            }
        }).start();

        OutputStreamWriter outputStream = new OutputStreamWriter(socket.getOutputStream());
        InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());

        Scanner sc = new Scanner(System.in);
        BufferedReader bf = new BufferedReader(inputStream);

        new Thread(() -> {
            try {
                String data;
                while((data = bf.readLine()) != null) {
                    System.out.println(socket.getInetAddress() +":"+ socket.getPort() + ": " + data);
                }
            } catch(Exception ex) { }
        }).start();

        new Thread(() -> {
            try {
                String data;
                while(sc.hasNext()) {
                    data = sc.nextLine();
                    outputStream.write(data + "\n");
                    outputStream.flush();
                }
            } catch(Exception ex) { }
        }).start();
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 5959);
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
