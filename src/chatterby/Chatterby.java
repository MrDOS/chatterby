package chatterby;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

import chatterby.messages.Message;
import chatterby.network.MessageManager;

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

    public static void main(String[] args) throws Exception
    {
        MessageManager manager = null;
        try
        {
            manager = new MessageManager();
            manager.start();
        }
        catch (IOException e)
        {
            LOGGER.severe("Chatterby couldn't get prepared to send messages! Make sure nothing else is occupying port "
                    + MessageManager.CONNECT_PORT + ".");
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);
        String line;
        while (scanner.hasNextLine() && (line = scanner.nextLine()) != null && !line.equals("exit"))
            manager.send(new Message("Chatterby User", line, null));
        scanner.close();

        manager.interrupt();
    }
}