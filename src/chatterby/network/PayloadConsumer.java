package chatterby.network;

import chatterby.messages.Message;

/**
 * A consumer of recieved messages.
 * 
 * @author scoleman
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PayloadConsumer
{
    public void consume(Message message);
}