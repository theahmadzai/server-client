package immortal.client;

import immortal.audio.Audio;
import immortal.network.TextChannel;
import immortal.network.TextTransfer;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class Client implements TextChannel {
    private Socket socket;
    private TextTransfer textTransfer;
    private Gui gui;

    private Client() {
        gui = new Gui(this);
//        new Thread(this::audioInput).start();
//        new Thread(this::audioOutput).start();
    }

    public void connect(String ip, int port) {
        if (isConnected()) {
            gui.showMessage("Already connected", Color.RED);
            return;
        }

        try {
            socket = new Socket(ip, port);
            textTransfer = new TextTransfer(socket);
            textTransfer.addChannel(this);

            gui.showMessage("client started on: " + socket, Color.GREEN);
        } catch (IOException ex) {
            gui.showMessage(ex.getMessage(), Color.RED);
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    @Override
    public void textStreamIn(String text) {
        if(text.startsWith("<ADD USERNAME>")) {
            gui.addUser(text.substring("<SET USERNAME>".length()));
            return;
        }
        gui.showMessage(text, Color.BLACK);
    }

    @Override
    public void textStreamOut(String text) {
        textTransfer.sendText(text);
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
        new Client();
    }
}
