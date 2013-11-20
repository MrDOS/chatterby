package chatterby.messages;

/**
 * A consumer of recieved messages.
 * 
 * @author scoleman
 * @version 1.0.0
 * @since 1.0.0
 */
public interface MessageConsumer
{
    public void consume(Message message);
}