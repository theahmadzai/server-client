package immortal.server;

import immortal.audio.Audio;
import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private List<ClientHandler> clients = new LinkedList<>();

    private Server(int port) {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on: " + serverSocket);

            new Thread(this::textInput).start();
            new Thread(this::audioInput).start();

            ExecutorService pool = Executors.newFixedThreadPool(3);

            while(!serverSocket.isClosed()) {
                try {
                    ClientHandler client = new ClientHandler(this, serverSocket.accept());
                    clients.add(client);
                    pool.execute(client);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void audioInput() {
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

    private void textInput() {
        Scanner sc = new Scanner(System.in);
        String data;

        while (sc.hasNext()) {
            data = sc.nextLine();
            for (ClientHandler client : clients) {
                client.sendMessage(new Message(client, data));
            }
        }
    }

    void pushMessage(Message message) {
        System.out.println(message.client.getClientName() + ": " + message.data);
    }

    public static void main(String[] args) {
        new Server(5959);
    }
}
