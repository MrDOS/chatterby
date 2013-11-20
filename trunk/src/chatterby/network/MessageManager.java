package chatterby.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import chatterby.messages.Message;

/**
 * Manage transmission of messages.
 * 
 * @author scoleman
 * @version 1.0.0
 * @since 1.0.0
 */
public class MessageManager extends Thread
{
    public static final int CONNECT_PORT = 6689;
    public static final String MULTICAST_ADDRESS = "224.0.66.89";

    private static final Logger LOGGER = Logger.getLogger(MessageManager.class.getName());

    private final LinkedBlockingQueue<Message> queue;
    private final MulticastSocket socket;
    private final InetAddress group;

    /**
     * @throws IOException when the socket cannot be established
     */
    public MessageManager() throws IOException
    {
        this.queue = new LinkedBlockingQueue<Message>();
        this.socket = new MulticastSocket(MessageManager.CONNECT_PORT);
        this.group = InetAddress.getByName(MessageManager.MULTICAST_ADDRESS);
    }

    public void send(Message message) throws InterruptedException
    {
        this.queue.put(message);
    }

    @Override
    public void run()
    {
        LOGGER.info("Starting up transmission thread.");

        while (!Thread.interrupted())
        {
            try
            {
                Message next = this.queue.take();

                LOGGER.info("Sending message.");
                byte[] payload = Payloads.wrapPayload(next.payload(), PayloadType.MESSAGE);

                DatagramPacket packet = new DatagramPacket(
                        payload, payload.length,
                        this.group, MessageManager.CONNECT_PORT);

                try
                {
                    this.socket.send(packet);
                }
                catch (IOException e)
                {
                    LOGGER.severe("Could not send packet: " + e.getMessage());
                }
            }
            catch (InterruptedException e)
            {
                break;
            }
        }

        LOGGER.info("Halting transmission.");
        this.socket.close();
    }
}