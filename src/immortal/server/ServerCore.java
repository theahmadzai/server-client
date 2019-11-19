package immortal.server;

import javax.sound.sampled.*;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerCore {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new LinkedList<ClientHandler>();
    private Queue<Message> dataQueue = new LinkedBlockingQueue<Message>();

    public ServerCore(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on: " + serverSocket);

        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            String data = null;
            while(sc.hasNext()) {
                data = sc.nextLine();
                try {
                    for(ClientHandler client : clients) {
                        client.sendMessage(new Message(client, data));
                    }
                } catch(IOException ex) {}
            }
        }).start();

        new Thread(() -> {
            try {
                DatagramSocket ds = new DatagramSocket(5858);

                AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
                SourceDataLine speaker = AudioSystem.getSourceDataLine(format);

                if(!AudioSystem.isLineSupported(speaker.getLineInfo())) {
                    throw new Exception("TargetDataLine is not supported");
                }

                speaker.open();
                speaker.start();

                DatagramPacket packet = null;
                byte[] data = new byte[speaker.getBufferSize() / 5];

                while(true) {
                    packet = new DatagramPacket(data, data.length);
                    ds.receive(packet);
                    speaker.write(data, 0, data.length);
                    System.out.println(data);
                    data = new byte[speaker.getBufferSize() / 5];
                }
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        while(!serverSocket.isClosed()) {
            clients.add(new ClientHandler(this, serverSocket.accept()));
        }
    }

    public void pushMessage(Message message) {
        dataQueue.add(message);
        System.out.println(message.client.getClientName() + ": " + message.data);
    }
}
