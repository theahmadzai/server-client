package immortal.client;

import immortal.audio.Audio;

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

        new Thread(this::audioInput).start();

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

    private void audioInput() {
        try {
            SourceDataLine speaker = AudioSystem.getSourceDataLine(Audio.FORMAT);

            if (!AudioSystem.isLineSupported(speaker.getLineInfo())) {
                throw new IOException("TargetDataLine is not supported");
            }

            speaker.open();
            speaker.start();

            byte[] buffer = new byte[Audio.BUFFER_SIZE];

            DatagramSocket udp = new DatagramSocket(5757);
            DatagramPacket packet = new DatagramPacket(buffer, Audio.BUFFER_SIZE);

            while (true) {
                udp.receive(packet);
                speaker.write(buffer, 0, Audio.BUFFER_SIZE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void audioOutput() {
        String serverAddress = "localhost";
        int serverPort = 5858;

        try {
            TargetDataLine microphone = AudioSystem.getTargetDataLine(Audio.FORMAT);

            if (!AudioSystem.isLineSupported(microphone.getLineInfo())) {
                throw new IOException("TargetDataLine is not supported");
            }

            microphone.open();
            microphone.start();

            byte[] buffer = new byte[Audio.BUFFER_SIZE];

            DatagramSocket udp = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(buffer, Audio.BUFFER_SIZE, InetAddress.getByName(serverAddress), serverPort);

            while (true) {
                microphone.read(buffer, 0, Audio.BUFFER_SIZE);
                udp.send(packet);
            }
        } catch (IOException | LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client("localhost", 5959);
    }
}
