package immortal.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Server server;
    private Socket socket;
    private PrintWriter textOutput;
    private BufferedReader textInput;

    ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;

        try {
            textInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            textOutput = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.println("Client connected: " + socket.getPort());
        sendMessage(new Message(this, "Connected to server on port: " + socket.getPort()));

        this.start();
    }

    @Override
    public void run() {
        String data;

        try {
            while((data = textInput.readLine()) != null) {
                server.pushMessage(new Message(this, data));
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    void sendMessage(Message message) {
        textOutput.println(message.data);
    }

    String getClientName() {
        return String.valueOf(socket.getPort());
    }
}
