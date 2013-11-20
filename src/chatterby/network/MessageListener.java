package chatterby.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Logger;

import chatterby.messages.Message;
import chatterby.messages.MessageConsumer;

/**
 * Listens for messages from other clients.
 * 
 * @author scoleman
 * @version 1.0.0
 * @since 1.0.0
 */
public class MessageListener extends Thread
{
    private static final Logger LOGGER = Logger.getLogger(MessageManager.class.getName());

    private static final int PAYLOAD_SIZE = 4096;

    private final MessageConsumer consumer;
    private final MulticastSocket socket;
    private final InetAddress group;

    public MessageListener(MessageConsumer consumer) throws IOException
    {
        this.consumer = consumer;
        this.socket = new MulticastSocket(MessageManager.CONNECT_PORT);
        this.group = InetAddress.getByName(MessageManager.MULTICAST_ADDRESS);
        this.socket.joinGroup(group);
    }

    @Override
    public void run()
    {
        LOGGER.info("Starting up listener thread.");

        while (!Thread.interrupted())
        {
            byte[] payload = new byte[PAYLOAD_SIZE];
            DatagramPacket packet = new DatagramPacket(
                    payload, payload.length,
                    this.group, MessageManager.CONNECT_PORT);

            try
            {
                this.socket.receive(packet);
            }
            catch (IOException e)
            {
                LOGGER.severe("Could not receive packet: " + e.getMessage());
            }

            try
            {
                Payload received = Payloads.parse(packet.getData());
                if (received instanceof Message)
                    consumer.consume((Message) received);
            }
            catch (UnrecognizedPayloadException e)
            {
                LOGGER.severe("Could not parse packet: " + e.getMessage());
            }
        }

        LOGGER.info("Halting listening.");
        this.socket.close();
    }
}