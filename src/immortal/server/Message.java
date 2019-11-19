package immortal.server;

class Message {
    ClientHandler client;
    String data;

    Message(ClientHandler client, String data) {
        this.client = client;
        this.data = data;
    }
}
