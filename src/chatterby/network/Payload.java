package chatterby.network;

/**
 * A network-transferrable message.
 * 
 * @author scoleman
 */
public interface Payload
{
    /**
     * @return a byte representation of the message
     */
    public byte[] payload();
}