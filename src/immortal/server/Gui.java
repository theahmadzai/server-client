package immortal.server;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;

public class Gui extends JFrame {
    private JPanel rootPanel;
    private JTextField portTextField;
    private JButton startButton;
    private JTextPane logsTextPane;
    private JList<String> sessionsList;
    DefaultListModel<String> dlm = new DefaultListModel<String>();
    private Server server;

    public Gui() {
        setTitle("Client Application");
        setSize(new Dimension(500, 500));
        setMinimumSize(new Dimension(500, 500));
        add(rootPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.server = server;
        sessionsList.setModel(dlm);

        startButton.addActionListener(e -> {
            if (server == null) {
                try {
                    server = new Server(this, Integer.parseInt(portTextField.getText()));
                } catch(Exception ex) {
                    showMessage(ex.getMessage(), Color.RED);
                }
                return;
            }

            showMessage("Server already started!", Color.RED);
        });

        setVisible(true);
        pack();
    }

    public void updateSessionsList(String usersList) {
        dlm.removeAllElements();

        String[] users = usersList.substring(1, usersList.length()-1).split(", ");
        for(String user : users) {
            dlm.addElement(user);
        }
    }

    public void showMessage(String message, Color color) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet attributes = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);

        logsTextPane.setCaretPosition(logsTextPane.getDocument().getLength());
        logsTextPane.setCharacterAttributes(attributes, false);
        logsTextPane.replaceSelection(message + "\n");
    }
}
