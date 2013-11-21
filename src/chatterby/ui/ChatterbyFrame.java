package chatterby.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

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

    private final ReentrantLock messagesLock = new ReentrantLock();

    private DefaultListModel<String> usernames = new DefaultListModel<>();
    private HashSet<String> tags = new HashSet<>();
    private LinkedList<Message> messages = new LinkedList<>();

    private JTextPane messagePane;
    private JTextField messageField;
    private JButton sendButton;
    private JList<String> knownUsernames;
    private JTextField usernameField;

    private SimpleDateFormat sendDateFormat;
    private SimpleAttributeSet sendDateAttributeSet;
    private SimpleAttributeSet usernameAttributeSet;

    public ChatterbyFrame(MessageManager manager)
    {
        this.manager = manager;

        this.setTitle("Chatterby");
        this.setMinimumSize(new Dimension(768, 512));

        this.initLayout();
        this.initMessageComponents();
    }

    private void initLayout()
    {
        /* Layout management */
        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());
        this.setContentPane(content);

        GridBagConstraints c = new GridBagConstraints();

        /* Incoming messages */
        messagePane = new JTextPane();
        messagePane.setEditable(false);

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 2;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.add(new JScrollPane(messagePane), c);

        /* User message */
        messageField = new JTextField(0);
        messageField.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ChatterbyFrame.this.sendMessage();
            }
        });

        c.gridx = 1;
        c.gridy = 2;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.weighty = 0.0;
        this.add(messageField, c);

        /* Send button */
        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ChatterbyFrame.this.sendMessage();
            }
        });

        c.gridx = 2;
        c.gridy = 2;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0;
        c.weighty = 0.0;
        this.add(sendButton, c);

        /* Username list */
        knownUsernames = new JList<String>(this.usernames);

        c.gridx = 3;
        c.gridy = 0;
        c.gridheight = 2;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.0;
        c.weighty = 0.0;
        this.add(new JScrollPane(knownUsernames), c);

        /* Username */
        usernameField = new JTextField("Chatterby User");

        c.gridx = 3;
        c.gridy = 2;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.weighty = 0.0;
        this.add(usernameField, c);
    }

    private void initMessageComponents()
    {
        this.sendDateFormat = new SimpleDateFormat("h:mm:ss a");

        this.sendDateAttributeSet = new SimpleAttributeSet();
        StyleConstants.setFontSize(this.sendDateAttributeSet,
                (int) (StyleConstants.getFontSize(this.sendDateAttributeSet) * .75));

        this.usernameAttributeSet = new SimpleAttributeSet();
        StyleConstants.setBold(this.usernameAttributeSet, true);

        SimpleAttributeSet noteAttributeSet = new SimpleAttributeSet();
        StyleConstants.setItalic(noteAttributeSet, true);
        StyleConstants.setForeground(noteAttributeSet, Color.GRAY);

        try
        {
            this.messagePane.getDocument().insertString(0, "Welcome to Chatterby!\n", noteAttributeSet);
        }
        catch (BadLocationException e)
        {
        }
    }

    @Override
    public void consume(Message message)
    {
        this.messagesLock.lock();

        if (!this.usernames.contains(message.getUsername()))
            this.usernames.addElement(message.getUsername());
        this.tags.add(message.getTag());
        this.messages.add(message);

        this.appendMessage(message);

        this.messagesLock.unlock();
    }

    /**
     * Send out a message or parse a command.
     * 
     * @param message the user message
     */
    private void sendMessage()
    {
        String message = messageField.getText().trim();

        if (message.length() == 0)
            return;

        messageField.setText("");

        if (message.equals("/quit") || message.startsWith("/quit "))
        {
            this.setVisible(false);
            this.dispose();
        }
        else
        {
            try
            {
                this.manager.send(new Message(usernameField.getText(), null, message));
            }
            catch (InterruptedException e)
            {
                LOGGER.warning("Failed to send message.");
            }
        }
    }

    private void appendMessage(Message message)
    {
        StyledDocument doc = messagePane.getStyledDocument();

        StyleConstants.setForeground(this.usernameAttributeSet,
                Colorizer.colorize(message.getUsername().trim()));

        try
        {
            doc.insertString(doc.getLength(),
                    this.sendDateFormat.format(message.getSendDate()) + "\t",
                    this.sendDateAttributeSet);
            doc.insertString(doc.getLength(),
                    message.getUsername() + ":",
                    this.usernameAttributeSet);
            doc.insertString(doc.getLength(),
                    " " + message.getMessage() + "\n",
                    null);
        }
        catch (BadLocationException e)
        {
            /* Because we're inserting at the end of the document as indicated
             * by the document itself, we will (presumably) never try to insert
             * at a bad location. */
        }

        messagePane.setCaretPosition(messagePane.getDocument().getLength());
    }

    private void refreshMessages()
    {
        this.messagesLock.lock();
        this.messagesLock.unlock();
    }
}