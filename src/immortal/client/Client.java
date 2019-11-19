package immortal.client;

import javax.sound.sampled.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private PrintWriter textOutput;
    private BufferedReader textInput;

    private Client(String ip, int port) throws Exception {
        socket = new Socket(ip, port);
        textOutput = new PrintWriter(socket.getOutputStream(), true);
        textInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        new Thread(this::textInput).start();

        new Thread(this::textOutput).start();

        new Thread(this::audioOutput).start();

        System.out.println("client started on: " + socket);
    }

    private void textInput() {
        String data;

        try {
            while((data = textInput.readLine()) != null) {
                System.out.println(socket.getInetAddress() +":"+ socket.getPort() + ": " + data);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void textOutput() {
        Scanner sc = new Scanner(System.in);
        String data;

        try {
            while((data = sc.nextLine()) != null) {
                textOutput.println(data);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void audioOutput() {
        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);

        try {
            TargetDataLine microphone = AudioSystem.getTargetDataLine(format);

            if (!AudioSystem.isLineSupported(microphone.getLineInfo())) {
                throw new Exception("TargetDataLine is not supported");
            }

            microphone.open();
            microphone.start();

            byte[] data = new byte[microphone.getBufferSize() / 5];
            DatagramSocket ds = new DatagramSocket();

            while (true) {
                microphone.read(data, 0, data.length);
                DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("localhost"), 5858);
                ds.send(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new Client("localhost", 5959);
    }
}
