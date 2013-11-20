package chatterby.network;

/**
 * Possible types of payloads.
 * 
 * @author scoleman
 * @version 1.0.0
 * @since 1.0.0
 */
public enum PayloadType
{
    MESSAGE((byte) 'm');

    private final byte typeIndicator;

    private PayloadType(byte typeIndicator)
    {
        this.typeIndicator = typeIndicator;
    }

    public byte getTypeIndicator()
    {
        return this.typeIndicator;
    }
}