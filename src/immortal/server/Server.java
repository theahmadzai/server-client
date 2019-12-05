package immortal.server;

import immortal.audio.Audio;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class Server extends Thread {
    private ServerSocket serverSocket;
    private Map<String, ClientHandler> clients = new HashMap<>();
    private Gui gui;

    public Server(Gui gui, int port) {
        this.gui = gui;

        try {
            serverSocket = new ServerSocket(port);
            gui.showMessage("Server started on: " + serverSocket, Color.GREEN);

//            new Thread(this::audioInput).start();
            new Thread(this::listenClients).start();


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void listenClients() {
        while (!serverSocket.isClosed()) {
            try {
                ClientHandler clientHandler = new ClientHandler(this, serverSocket.accept());
                clients.put(clientHandler.getUsername(), clientHandler);

                broadCastText("<ADD USERNAME>" + clients.keySet());
                gui.updateSessionsList(clients.keySet().toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void log(String message) {
        gui.showMessage(message, Color.PINK);
    }

    public void broadCastText(String text) {
        for(ClientHandler client : clients.values()) {
            client.textStreamOut(text);
        }
    }

    private void streamAudio() {
        String clientAddress = "localhost";
        int clientPort = 5757;

        byte[] buffer = new byte[Audio.BUFFER_SIZE];

        try {
            DatagramSocket udpR = new DatagramSocket(5858);
            DatagramPacket packetR = new DatagramPacket(buffer, Audio.BUFFER_SIZE);
            DatagramSocket udpS = new DatagramSocket();
            DatagramPacket packetS = new DatagramPacket(buffer, Audio.BUFFER_SIZE, InetAddress.getByName(clientAddress), clientPort);

            while (true) {
                udpR.receive(packetR);
                udpS.send(packetS);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Gui();
    }
}
