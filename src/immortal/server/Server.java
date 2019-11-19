package immortal.server;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Server {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new LinkedList<>();

    private Server(int port) throws IOException {
         serverSocket = new ServerSocket(port);

        new Thread(this::textInput).start();

        new Thread(this::audioInput).start();

        System.out.println("Server started on: " + serverSocket);

        while(!serverSocket.isClosed()) {
            clients.add(new ClientHandler(this, serverSocket.accept()));
        }
    }

    private void audioInput() {
        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);

        try {
            SourceDataLine speaker = AudioSystem.getSourceDataLine(format);

            if (!AudioSystem.isLineSupported(speaker.getLineInfo())) {
                throw new IOException("TargetDataLine is not supported");
            }

            speaker.open();
            speaker.start();

            DatagramSocket datagramSocket = new DatagramSocket(5858);
            DatagramPacket packet;

            byte[] data = new byte[speaker.getBufferSize() / 5];

            while (true) {
                packet = new DatagramPacket(data, data.length);
                datagramSocket.receive(packet);
                speaker.write(data, 0, data.length);
                System.out.println(Arrays.toString(data));
                data = new byte[speaker.getBufferSize() / 5];//test
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

    public static void main(String[] args) throws Exception {
        new Server(5959);
    }
}
