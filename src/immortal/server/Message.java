package immortal.server;

public class Message {
    public ClientHandler client;
    public String data;

    public Message(ClientHandler client, String data) {
        this.client = client;
        this.data = data;
    }
}
