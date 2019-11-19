package immortal.server;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.*;

class ServerCore {
    private List<ClientHandler> clients = new LinkedList<>();

    ServerCore(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on: " + serverSocket);

        new Thread(this::textInput).start();

        new Thread(this::audioInput).start();

        while(!serverSocket.isClosed()) {
            clients.add(new ClientHandler(this, serverSocket.accept()));
        }
    }

    private void audioInput() {
        try {
            DatagramSocket ds = new DatagramSocket(5858);

            AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
            SourceDataLine speaker = AudioSystem.getSourceDataLine(format);

            if (!AudioSystem.isLineSupported(speaker.getLineInfo())) {
                throw new Exception("TargetDataLine is not supported");
            }

            speaker.open();
            speaker.start();

            DatagramPacket packet;
            byte[] data = new byte[speaker.getBufferSize() / 5];

            while (true) {
                packet = new DatagramPacket(data, data.length);
                ds.receive(packet);
                speaker.write(data, 0, data.length);
                System.out.println(Arrays.toString(data));
                data = new byte[speaker.getBufferSize() / 5];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void textInput() {
        Scanner sc = new Scanner(System.in);
        String data;
        while (sc.hasNext()) {
            data = sc.nextLine();
            try {
                for (ClientHandler client : clients) {
                    client.sendMessage(new Message(client, data));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void pushMessage(Message message) {
        System.out.println(message.client.getClientName() + ": " + message.data);
    }
}
