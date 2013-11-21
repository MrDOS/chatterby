package chatterby.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import chatterby.messages.Message;
import chatterby.network.MessageManager;
import chatterby.network.PayloadConsumer;

/**
 * Top-level GUI component.
 * 
 * @author scoleman
 * @version 1.0.0
 * @since 1.0.0
 */
public class ChatterbyFrame extends JFrame implements PayloadConsumer
{
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(ChatterbyFrame.class.getName());

    private final MessageManager manager;

    private JTextPane messages;
    private JTextField messageInput;

    private String messagesText;

    public ChatterbyFrame(MessageManager manager)
    {
        this.manager = manager;

        this.setTitle("Chatterby");
        this.setMinimumSize(new Dimension(600, 400));

        /* Layout management */
        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());
        this.setContentPane(content);

        GridBagConstraints c = new GridBagConstraints();

        /* Incoming messages */
        messages = new JTextPane();
        messages.setContentType("text/html");
        messages.setEditable(false);

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.add(new JScrollPane(messages), c);

        /* User message */
        messageInput = new JTextField(0);
        messageInput.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ChatterbyFrame.this.sendMessage(messageInput.getText());
                messageInput.setText("");
            }
        });

        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.weighty = 0.0;
        this.add(messageInput, c);
    }

    @Override
    public void consume(Message message)
    {
        if (this.messagesText == null)
            this.messagesText = "";
        else
            this.messagesText += "<br>";
        this.messagesText += "<b>" + message.getUsername() + "</b>: " + message.getMessage() + "\n";
        messages.setText("<html><body>" + this.messagesText + "</body></html>");
        messages.setCaretPosition(messages.getDocument().getLength());
    }

    /**
     * Send out a message or parse a command.
     * 
     * @param message the user message
     */
    private void sendMessage(String message)
    {
        if (message.equals("/quit") || message.startsWith("/quit "))
        {
            this.setVisible(false);
            this.dispose();
        }
        else
        {
            try
            {
                this.manager.send(new Message("Chatterby User", null, message));
            }
            catch (InterruptedException e)
            {
                LOGGER.warning("Failed to send message.");
            }
        }
    }

    private void refreshMessages()
    {
    }
}