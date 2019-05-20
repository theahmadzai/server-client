package immortal.server;

import java.net.ServerSocket;
import java.util.Scanner;

public class Server {
    private Server() throws Exception {
        ServerSocket serverSocket = null;

        System.out.println("Server Started: 5959");
        serverSocket = new ServerSocket(5959);

        Scanner sc = new Scanner(System.in);

        while(true) {
            new EchoThread(serverSocket.accept(), sc).start();
        }
    }

    public static void main(String[] args) throws Exception {
        new Server();
    }
}
