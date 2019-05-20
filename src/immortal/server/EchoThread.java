package immortal.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

class EchoThread extends Thread {
    private Socket socket = null;
    private OutputStreamWriter outputStream = null;
    private InputStreamReader inputStream = null;
    private Scanner scanner;

    EchoThread(Socket socket, Scanner scanner) throws Exception {
        this.socket = socket;
        this.scanner = scanner;
        System.out.println("Client connected to server on socket: " + socket);

        outputStream = new OutputStreamWriter(socket.getOutputStream());
        inputStream = new InputStreamReader(socket.getInputStream());
    }

    public void run() {
        Scanner sc = this.scanner;
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
                    sc.close();
                }
            } catch(Exception ex) { }
        }).start();
    }
}
