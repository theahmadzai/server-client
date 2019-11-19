package immortal.client;

import javax.sound.sampled.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader textInput;
    private PrintWriter textOutput;

    private Client(String ip, int port) {
        try {
            socket = new Socket(ip, port);

            textInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            textOutput = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

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
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private void textOutput() {
        Scanner sc = new Scanner(System.in);
        String data;

        while((data = sc.nextLine()) != null) {
            textOutput.println(data);
        }
    }

    private void audioOutput() {
        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);

        try {
            TargetDataLine microphone = AudioSystem.getTargetDataLine(format);

            if (!AudioSystem.isLineSupported(microphone.getLineInfo())) {
                throw new IOException("TargetDataLine is not supported");
            }

            microphone.open();
            microphone.start();

            DatagramSocket ds = new DatagramSocket();

            byte[] data = new byte[microphone.getBufferSize() / 5];

            while (true) {
                microphone.read(data, 0, data.length);
                DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("localhost"), 5858);
                ds.send(packet);
            }
        } catch (IOException | LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new Client("localhost", 5959);
    }
}
