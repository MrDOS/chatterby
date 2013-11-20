package chatterby.messages;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.sql.Date;

import chatterby.network.Payload;
import chatterby.network.UnrecognizedPayloadException;

/**
 * A user message.
 * 
 * @author scoleman
 * @version 1.0.0
 * @since 1.0.0
 */
public class Message implements Payload
{
    /**
     * The minimum length of a message payload.
     */
    private static final int MIN_LENGTH = 4 + 8 + 4 + 4;

    private final String username;
    private final Date sendDate;
    private final String tag;
    private final String message;

    /**
     * Construct the message.
     * 
     * @param username an arbitrary username
     * @param message the message body
     * @param sendDate the date at which the message was sent
     * @param tag an arbitrary topic tag; may be null
     */
    public Message(String username, Date sendDate, String tag, String message)
    {
        this.username = username;
        this.sendDate = sendDate;
        this.tag = tag;
        this.message = message;
    }

    /**
     * Construct the message.
     * 
     * @param username an arbitrary username
     * @param message the message body
     * @param tag an arbitrary topic tag; may be null
     */
    public Message(String username, String tag, String message)
    {
        this.username = username;
        this.sendDate = new Date(System.currentTimeMillis());
        this.tag = tag;
        this.message = message;
    }

    public String getUsername()
    {
        return this.username;
    }

    public Date getSendDate()
    {
        return this.sendDate;
    }

    public String getTag()
    {
        return this.tag;
    }

    public String getMessage()
    {
        return this.message;
    }

    /**
     * Parse a message into a byte payload.
     * 
     * @return the payload
     */
    public byte[] payload()
    {
        byte[] usernameBytes = null;
        byte[] tagBytes = null;
        byte[] messageBytes = null;

        try
        {
            usernameBytes = this.username.getBytes("UTF8");
            messageBytes = this.message.getBytes("UTF8");

            if (this.tag == null)
                tagBytes = new byte[0];
            else
                tagBytes = this.tag.getBytes("UTF8");
        }
        catch (UnsupportedEncodingException e)
        {
            /* If we can't encode to UTF-8, we're pretty well boned. */
        }

        byte[] payload = new byte[
                4 + usernameBytes.length /* length + size of username */
                        + 8 /* send date */
                        + 4 + tagBytes.length /* length + size of tag */
                        + 4 + messageBytes.length /* length + size of message */
                ];
        ByteBuffer.wrap(payload)
                .putInt(usernameBytes.length).put(usernameBytes)
                .putLong(this.sendDate.getTime())
                .putInt(tagBytes.length).put(tagBytes)
                .putInt(messageBytes.length).put(messageBytes);

        return payload;
    }

    /**
     * Parse a byte payload into a message.
     * 
     * @param payload the payload
     * @return the message
     * @throws UnrecognizedPayloadException
     */
    public static Message parseMessage(byte[] payload) throws UnrecognizedPayloadException
    {
        if (payload.length < Message.MIN_LENGTH)
            throw new UnrecognizedPayloadException("Payload identifies as a message, but is too short");
        ByteBuffer buffer = ByteBuffer.wrap(payload);

        byte[] usernameBytes = new byte[buffer.getInt()];
        buffer.get(usernameBytes, 0, usernameBytes.length);

        long sendTimestamp = buffer.getLong();

        byte[] tagBytes = new byte[buffer.getInt()];
        buffer.get(tagBytes, 0, tagBytes.length);

        byte[] messageBytes = new byte[buffer.getInt()];
        buffer.get(messageBytes, 0, messageBytes.length);

        try
        {
            return new Message(new String(usernameBytes, "UTF8"),
                    new Date(sendTimestamp),
                    (tagBytes.length > 0) ? null : new String(tagBytes, "UTF8"),
                    new String(messageBytes, "UTF8"));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new UnrecognizedPayloadException(e);
        }
    }
}