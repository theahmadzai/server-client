package immortal.client;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Gui extends JFrame {
    private JTextField ipAddressTextField;
    private JButton connectButton;
    private JPanel rootPanel;
    private JTextField messageTextField;
    private JButton sendButton;
    private JList sessionList;
    private JTextPane messagesTextPane;
    private Client client;

    public Gui(Client client) {
        setTitle("Client Application");
        setSize(new Dimension(500, 500));
        setMinimumSize(new Dimension(500, 500));
        add(rootPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.client = client;
        System.out.println(this.client);

        connectButton.addActionListener(e -> client.connect(ipAddressTextField.getText(), 5959));
        sendButton.addActionListener(e -> sendMessage(messageTextField.getText()));
        messageTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage(messageTextField.getText());
                }
            }
        });

        setVisible(true);
        pack();
    }

    public void showMessage(String message, Color color) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet attributes = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);

        messagesTextPane.setCaretPosition(messagesTextPane.getDocument().getLength());
        messagesTextPane.setCharacterAttributes(attributes, false);
        messagesTextPane.replaceSelection(message + "\n");
    }

    private void sendMessage(String message) {
        if(!client.isConnected()) {
            showMessage("You are not connected!", Color.RED);
            return;
        }

        client.textStreamOut(message);
        messageTextField.setText("");
    }
}
