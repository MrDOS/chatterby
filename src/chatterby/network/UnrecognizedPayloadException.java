package chatterby.network;

/**
 * Thrown when a payload cannot be recognized.
 * 
 * @author scoleman
 * @version 1.0.0
 * @since 1.0.0
 */
public class UnrecognizedPayloadException extends Exception
{
    private static final long serialVersionUID = 1L;

    public UnrecognizedPayloadException()
    {
        super();
    }

    public UnrecognizedPayloadException(String message)
    {
        super(message);
    }

    public UnrecognizedPayloadException(Throwable throwable)
    {
        super(throwable);
    }
}