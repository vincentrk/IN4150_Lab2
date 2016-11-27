import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by vincent on 11/27/16.
 */
enum messageType {REQUEST, GRANT, POSTPONE, INQUIRE, RELEASE, RELINQUISH}
public class Message implements Serializable, Comparable<Message>
{
    private Timestamp timestamp;
    private messageType type;

    public Message(int time, int senderID, messageType type)
    {
        this.timestamp = new Timestamp(time, senderID);
        this.type = type;
    }

    public Message(Timestamp timestamp, messageType type)
    {
        this.timestamp = timestamp;
        this.type = type;
    }

    public String toString()
    {
        return "Message timestamp: " + timestamp + ", Message Type: " + type;
    }

    public boolean equals(Object obj)
    {
        if(!(obj instanceof Message))
            return false;
        return ((this.timestamp.equals(((Message)(obj)).timestamp)) && (this.type == ((Message)(obj)).type));
    }

    public int compareTo(Message that)
    {
        return this.timestamp.compareTo(that.timestamp);
    }

    public messageType getType() {return type;}
    
    public Timestamp getTimestamp() {return timestamp;}
}
