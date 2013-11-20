package chatterby.ui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import chatterby.messages.Message;
import chatterby.network.MessageManager;
import chatterby.network.PayloadConsumer;

public class ChatterbyFrame extends JFrame implements PayloadConsumer
{
    private static final long serialVersionUID = 1L;

    private JTextArea messages;

    public ChatterbyFrame(MessageManager manager)
    {
        this.setTitle("Chatterby");
        this.setMinimumSize(new Dimension(600, 400));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        this.setContentPane(content);

        messages = new JTextArea(5, 20);
        messages.setEditable(false);

        JScrollPane messageScroll = new JScrollPane(messages);
        messageScroll.setMinimumSize(new Dimension(600, 400));
        this.add(messageScroll);
    }

    @Override
    public void consume(Message message)
    {
        messages.setText(messages.getText() + "\n" + message.getUsername() + ": " + message.getMessage());
    }
}