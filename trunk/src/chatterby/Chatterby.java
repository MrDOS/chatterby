package chatterby;

import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JFrame;

import chatterby.network.MessageListener;
import chatterby.network.MessageManager;
import chatterby.ui.ChatterbyFrame;

/**
 * Chatterby entry point.
 * 
 * @author scoleman
 * @version 1.0.0
 * @since 1.0.0
 */
public class Chatterby
{
    private static final Logger LOGGER = Logger.getLogger(MessageManager.class.getName());

    private final MessageManager manager;
    private final MessageListener listener;

    public Chatterby() throws IOException
    {
        this.manager = new MessageManager();
        this.manager.start();

        ChatterbyFrame frame = new ChatterbyFrame(manager);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.listener = new MessageListener(frame);
        this.listener.start();

        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception
    {
        try
        {
            new Chatterby();
        }
        catch (IOException e)
        {
            LOGGER.severe("Chatterby couldn't get prepared to send messages! Make sure nothing else is occupying port "
                    + MessageManager.CONNECT_PORT + ".");
            System.exit(1);
        }
    }
}