package immortal.client;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.xml.transform.Source;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {


    private Client() throws Exception {
        Socket socket = new Socket("localhost", 5959);
        System.out.println("client started on: " + socket);

        OutputStreamWriter outputStream = new OutputStreamWriter(socket.getOutputStream());
        InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());

        Scanner sc = new Scanner(System.in);
        BufferedReader bf = new BufferedReader(inputStream);

        new Thread(() -> {
            try {
                String data;
                while((data = bf.readLine()) != null) {
                    System.out.println(socket.getInetAddress() +":"+ socket.getPort() + ": " + data);
                }
            } catch(Exception ex) { }
        }).start();

        new Thread(() -> {
            try {
                String data;
                while(sc.hasNext()) {
                    data = sc.nextLine();
                    outputStream.write(data + "\n");
                    outputStream.flush();
                }
            } catch(Exception ex) { }
        }).start();

        new Thread(() -> {
            try {
                DatagramSocket ds = new DatagramSocket();

                AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
                TargetDataLine microphone = AudioSystem.getTargetDataLine(format);

                if(!AudioSystem.isLineSupported(microphone.getLineInfo())) {
                    throw new Exception("TargetDataLine is not supported");
                }

                microphone.open();
                microphone.start();

                byte[] data = new byte[microphone.getBufferSize() / 5];
                int readBytes = 0;

                while(true) {
                    readBytes = microphone.read(data, 0, data.length);
                    DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("localhost"), 5858);
                    ds.send(packet);
                }

            } catch (SocketException | LineUnavailableException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        new Client();
    }

//    public static void main(String[] args) throws Exception {
//        JFrame frame = new JFrame("Client");
//        frame.setLayout(new FlowLayout());
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//        frame.pack();
//
//        JButton button = new JButton("Play");
//        frame.add(button);
//
//        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
//
//        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
//
//        for(Mixer.Info mixerInfo : mixerInfos) {
//            System.out.println(mixerInfo);
//        }
//
//        final TargetDataLine microphone = AudioSystem.getTargetDataLine(format);
//        final SourceDataLine speaker = AudioSystem.getSourceDataLine(format);
//
//        if(!AudioSystem.isLineSupported(microphone.getLineInfo())) {
//            throw new Exception("TargetDataLine is not supported");
//        }
//
//        microphone.open();
//
//        AudioInputStream audioInputStream = new AudioInputStream(microphone);
//        final ByteArrayOutputStream b = new ByteArrayOutputStream();
//
//        button.addActionListener(e -> {
//            if(button.getText() == "Play") {
//                microphone.start();
//                button.setText("Stop");
//
//                new Thread(() -> {
//                    try {
//                        b.write(audioInputStream.readAllBytes());
//                        System.out.println("Stopped Recording");
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                });
//                return;
//            }
//
//            try {
//                byte[] audioData = b.toByteArray();
//                ByteArrayInputStream ba = new ByteArrayInputStream(audioData);
//                AudioInputStream audioInp = new AudioInputStream(ba, format, audioData.length / audioInputStream.getFormat().getFrameSize());
//                AudioSystem.write(AudioSystem.getAudioInputStream(audioInp),AudioFileFormat.Type.WAVE, new File("sound.wav"));
//            } catch (IOException | UnsupportedAudioFileException ex) {
//                ex.printStackTrace();
//            }
//
//            microphone.stop();
//            button.setText("Play");
//        });
//
////        while(soundThread.isAlive());
//
//
//        System.out.println("Listening!");
//        Thread.sleep(500000);
//        microphone.close();
//        System.out.println("Ended test");
//
////        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
////        TargetDataLine microphone;
////        SourceDataLine speakers;
////        try {
////            microphone = AudioSystem.getTargetDataLine(format);
////
////            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
////            microphone = (TargetDataLine) AudioSystem.getLine(info);
////            microphone.open(format);
////
////            ByteArrayOutputStream out = new ByteArrayOutputStream();
////            int numBytesRead;
////            int CHUNK_SIZE = 1024;
////            byte[] data = new byte[microphone.getBufferSize() / 5];
////            microphone.start();
////
////            int bytesRead = 0;
////            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
////            speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
////            speakers.open(format);
////            speakers.start();
////            while (bytesRead < 100000) {
////                numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
////                bytesRead += numBytesRead;
////                // write the mic data to a stream for use later
////                out.write(data, 0, numBytesRead);
////                // write mic data to stream for immediate playback
////                speakers.write(data, 0, numBytesRead);
////            }
////            speakers.drain();
////            speakers.close();
////            microphone.close();
////        } catch (LineUnavailableException e) {
////            e.printStackTrace();
////        }
//    }
}
