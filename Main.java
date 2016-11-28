import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by vincent on 11/17/16.
 */
public class Main
{
    private static final int NUM_PROCESSES = 13;

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

        ArrayList<HashSet<Integer>> requestSets = createRequestSets(NUM_PROCESSES);

        System.out.println(requestSets);

        for(int i=0;i<NUM_PROCESSES;i++)
        {
            MMAProc p = new MMAProc(i, requestSets.get(i));
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

    private static ArrayList<HashSet<Integer>> createRequestSets(int num_procs)
    {
        ArrayList<HashSet<Integer>> requestSets = new ArrayList<HashSet<Integer>>();
        int maxRowSize = (int)(Math.round(Math.sqrt(num_procs)));

        for(int ndx=0; ndx<num_procs; ndx++)
        {
            HashSet<Integer> reqSet = new HashSet<Integer>();

            int x = (int)(ndx/maxRowSize);
            int y = ndx%maxRowSize;

            for(int i=0; i<maxRowSize+1; i++)
            {
                int temp = (maxRowSize * i) + y;
                if(temp < num_procs)
                {
                    reqSet.add(temp);
                }
            }
            for(int i=0; i<maxRowSize; i++)
            {
                int temp = (maxRowSize * x) + i;
                if(temp < num_procs)
                {
                    reqSet.add(temp);
                }
                else
                {
                    reqSet.add(temp - maxRowSize);
                }
            }
            requestSets.add(reqSet);
        }
        return requestSets;
    }
}
