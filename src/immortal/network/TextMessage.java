package immortal.network;

public class TextMessage {
    private TextTransfer client;
    private String text;

    private TextMessage(Builder builder) {
        if (builder.client == null) {
            throw new IllegalArgumentException("Null client value given!");
        }

        this.client = builder.client;
        this.text = builder.text;
    }

    public TextTransfer getClient() {
        return client;
    }

    public String getText() {
        return text;
    }

    public static final class Builder {
        private String text;
        private TextTransfer client;

        public Builder withClient(TextTransfer client) {
            this.client = client;
            return this;
        }

        public Builder withText(String text) {
            this.text = text;
            return this;
        }

        public TextMessage build() {
            return new TextMessage(this);
        }
    }

}
