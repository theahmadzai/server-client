package immortal.client;

import java.io.BufferedReader;
import java.net.Socket;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Client {

    private Client() throws Exception {
        Socket socket = new Socket("localhost", 5959);

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
    }

    public static void main(String[] args) throws Exception {
        new Client();
    }
}
