package immortal.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private ServerCore server;
    private Socket socket;
    private InputStreamReader inputStream;
    private OutputStreamWriter outputStream;

    ClientHandler(ServerCore server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.inputStream = new InputStreamReader(socket.getInputStream());
        this.outputStream = new OutputStreamWriter(socket.getOutputStream());

        System.out.println("Client connected: " + socket.getPort());
        sendMessage(new Message(this, "Connected to server on port: " + socket.getPort()));

        this.start();
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(inputStream);
        String data;

        try {
            while((data = br.readLine()) != null) {
                server.pushMessage(new Message(this, data));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    void sendMessage(Message message) throws IOException {
        outputStream.write(message.data + "\n");
        outputStream.flush();
    }

    String getClientName() {
        return String.valueOf(socket.getPort());
    }
}
