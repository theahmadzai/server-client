package immortal.client;

import immortal.audio.Audio;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

class Client {
    private Socket socket;
    private BufferedReader textInput;
    private PrintWriter textOutput;
    private JTextField textInputComponent;
    private JTextPane textOutputComponent;

    // USE SINGLETON SO CAN'T CONNECT TWO TIMES
    Client(String ip, int port, JTextPane a, JTextField b) {
        textOutputComponent = a;
        textInputComponent = b;

        try {
            socket = new Socket(ip, port);

            textInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            textOutput = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            return;
        }

        new Thread(this::textInput).start();

        new Thread(this::textOutput).start();

        new Thread(this::audioInput).start();

        new Thread(this::audioOutput).start();

        textOutputComponent.setText("client started on: " + socket);
    }

    private void textInput() {
        String data;

        try {
            while((data = textInput.readLine()) != null) {
                textOutputComponent.setText(socket.getInetAddress() +":"+ socket.getPort() + ": " + data);
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private void textOutput() {
        textOutput.println(textInputComponent.getText());
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

    void sendMessage() {
        textOutput();
    }
}
