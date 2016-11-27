import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by vincent on 11/17/16.
 */
public class Main
{
    private static final int NUM_PROCESSES = 2;

    public static void main(String[] args) throws RemoteException
    {
        try
        {
            LocateRegistry.createRegistry(1099);
            System.out.println("Created Registry");
        }
        catch(RemoteException e)
        {
            System.out.println("Already Running Binding");
        }

        for(int i=0;i<NUM_PROCESSES;i++)
        {
            MMAProc p = new MMAProc(i, NUM_PROCESSES);
            try
            {
                Naming.rebind("rmi://localhost:1099/MMAProc"+i, p);
                new Thread(p).start();
            }
            catch (Exception ex)
            {
                System.out.println(ex);
            }
        }
    }
}
