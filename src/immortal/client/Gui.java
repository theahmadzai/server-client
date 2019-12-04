package immortal.client;

import org.w3c.dom.Text;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Gui extends JFrame {
    private JTextField ipAddressTextField;
    private JButton connectButton;
    private JPanel rootPanel;
    private JTextField textField1;
    private JButton button1;
    private JList list1;
    private JTextPane textPane1;
    private Client client;

    public Gui() {
        setTitle("Client Application");
        setSize(new Dimension(500, 500));
        setMinimumSize(new Dimension(500, 500));
        add(rootPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        connectButton.addActionListener(this::connectServer);
        button1.addActionListener(e -> {
            if(client == null) {
                sendMessage("You are not connected!", Color.RED);
                return;
            }
            client.sendMessage();
        });

        setVisible(true);
        pack();
    }

    private void sendMessage(String message, Color color) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet attributes = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);

        textPane1.setCaretPosition(textPane1.getDocument().getLength());
        textPane1.setCharacterAttributes(attributes, false);
        textPane1.replaceSelection(message + "\n");
    }

    private void connectServer(ActionEvent e) {
        client = new Client(ipAddressTextField.getText(), 5959, textPane1, textField1);
    }

    public static void main(String[] args) {
        new Gui();
    }
}
