package chatfx;

/**
 *
 * @author frair
 */
public class ChatMessage implements java.io.Serializable {
    public String messageType;
    public String message;

    public ChatMessage(String messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }
    
}
