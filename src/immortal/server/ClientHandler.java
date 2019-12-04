package immortal.server;

import immortal.network.TextChannel;
import immortal.network.TextTransfer;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements TextChannel {
    private Server server;
    private Socket socket;
    private TextTransfer textTransfer;

    ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;

        try {
            textTransfer = new TextTransfer(socket);
            textTransfer.addChannel(this);

            textTransfer.sendText("Connected to server on port [" + socket.getPort() + "]");
        } catch (IOException e) {
            e.printStackTrace();
        }

        textStreamIn("Client connected: " + socket.getPort());
    }

    @Override
    public void textStreamIn(String text) {
        server.broadCastText(socket.getPort() + ": " + text);
    }

    @Override
    public void textStreamOut(String text) {
        textTransfer.sendText(text);
    }
}
