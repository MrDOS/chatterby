package chatterby.network;

import chatterby.messages.Message;

/**
 * Utility methods for handling payloads.
 * 
 * @author scoleman
 * @since 1.0.0
 * @version 1.0.0
 */
public class Payloads
{
    private static final byte[] MAGIC = new byte[] {(byte) 'B', (byte) 'Y'};
    private static final byte VERSION = (byte) 0x01;
    public static final int HEADER_LENGTH = MAGIC.length + 1 + 1;

    /**
     * Parse a transmission with header.
     * 
     * @param header the full transmission
     * @return a parsed payload message
     * @throws UnrecognizedPayloadException
     */
    public static Payload parse(byte[] header) throws UnrecognizedPayloadException
    {
        int i;
        for (i = 0; i < MAGIC.length; i++)
            if (header[i] != MAGIC[i])
                throw new UnrecognizedPayloadException("Mismatching magic bytes");
        if (header[i++] != VERSION)
            throw new UnrecognizedPayloadException("Unknown payload version");

        if (header[i] == PayloadType.MESSAGE.getTypeIndicator())
            return Message.parseMessage(Payloads.unwrapPayload(header));

        throw new UnrecognizedPayloadException("Unrecognized payload type");
    }

    /**
     * Wrap a payload in the transmission.
     * 
     * @param payload the payload
     * @param type the type of payload
     * @return the wrapped payload
     * @see PayloadType
     */
    public static byte[] wrapPayload(byte[] payload, PayloadType type)
    {
        byte[] header = new byte[payload.length + Payloads.HEADER_LENGTH];

        int i;
        for (i = 0; i < MAGIC.length; i++)
            header[i] = MAGIC[i];
        header[i++] = VERSION;
        header[i++] = type.getTypeIndicator();

        System.arraycopy(payload, 0, header, i, payload.length);
        return header;
    }

    /**
     * Unpack the payload from a transmission with header.
     * 
     * @param header the full transmission with header
     * @return the payload
     */
    public static byte[] unwrapPayload(byte[] header)
    {
        byte[] payload = new byte[header.length - Payloads.HEADER_LENGTH];

        System.arraycopy(header, Payloads.HEADER_LENGTH, payload, 0, payload.length);
        return payload;
    }
}