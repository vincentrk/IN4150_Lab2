import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by vincent on 11/17/16.
 */
public interface MMAInterface extends Remote
{
    public void receiveMessage(Message message) throws java.rmi.RemoteException;
}
