package immortal.server;

import immortal.audio.Audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new LinkedList<>();

    private Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        new Thread(this::textInput).start();

        new Thread(this::audioInput).start();

        System.out.println("Server started on: " + serverSocket);

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
    }

    private void audioInput() {
        try {
            SourceDataLine speaker = AudioSystem.getSourceDataLine(Audio.FORMAT);

            if (!AudioSystem.isLineSupported(speaker.getLineInfo())) {
                throw new IOException("TargetDataLine is not supported");
            }

            speaker.open();
            speaker.start();

            byte[] buffer = new byte[Audio.BUFFER_SIZE];

            DatagramSocket udp = new DatagramSocket(5858);
            DatagramPacket packet = new DatagramPacket(buffer, Audio.BUFFER_SIZE);

            while (true) {
                udp.receive(packet);
                speaker.write(buffer, 0, Audio.BUFFER_SIZE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
