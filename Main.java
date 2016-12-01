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
   // private static final int NUM_PROCESSES = 4;

    public static void main(String[] args) throws RemoteException
    {
        System.out.print(args.length);
        if(args.length!=3) {
            System.out.println("Invalid Arguments enter current process id, Number of Process, and number of iterations");
            return;
        }
        int thisProcId=Integer.parseInt(args[0]);
        int NUM_PROCESSES=Integer.parseInt(args[1]);
        int numberOfIterations=Integer.parseInt(args[2]);


        try
        {
            LocateRegistry.createRegistry(1099);
            System.out.println("Created Registry");
        }
        catch(RemoteException e)
        {
            System.out.println("Already Running Binding");
        }

//        ArrayList<HashSet<Integer>> requestSets = createRequestSets(NUM_PROCESSES);
        int[][] requestSets = createRequestSets(NUM_PROCESSES);
     //   printRequestSets(requestSets);

        // create, bind, and start the processes

            MMAProc p = new MMAProc(thisProcId, requestSets[thisProcId],numberOfIterations);
            try
            {
                Naming.rebind("rmi://localhost:1099/MMAProc"+thisProcId, p);
                p.run();
            }
            catch (Exception ex)
            {
                System.out.println(ex);
            }
    }

    // Create all request sets using an adjusted grid from the lecture slides
    // Always fulfills [M1] and [M2]. Fulfills [M3] and [M4] when the number of processes is square.
    private static int[][] createRequestSets(int num_procs)
    {
        int[][] requestSets = new int[num_procs][];
        int maxRowSize = (int)(Math.round(Math.sqrt(num_procs)));

        for(int ndx=0; ndx<num_procs; ndx++)
        {
            HashSet<Integer> reqSet = new HashSet<Integer>();

            // Figure out which row and column this process is in
            int x = (int)(ndx/maxRowSize);
            int y = ndx%maxRowSize;

            // add all of the column to the request set
            for(int i=0; i<maxRowSize+1; i++)
            {
                int temp = (maxRowSize * i) + y;
                if(temp < num_procs)
                {
                    reqSet.add(temp);
                }
            }
            // add all of the row to the request set
            for(int i=0; i<maxRowSize; i++)
            {
                int temp = (maxRowSize * x) + i;
                if(temp < num_procs)
                {
                    reqSet.add(temp);
                }
                else
                {
                    // if the number of processes is not square then the last part of the previous row is used to fill
                    // in the row that flows over the square.
                    reqSet.add(temp - maxRowSize);
                }
            }
            int[] reqArr = new int[reqSet.size()];
            int i=0;
            for(Integer num : reqSet)
            {
                reqArr[i] = (int)(num);
                i++;
            }
            requestSets[ndx] = reqArr;
        }
        return requestSets;
    }

    private static void printRequestSets(int[][] arr)
    {
        System.out.println("REQUEST SETS:");
        System.out.print("[");
        for(int i=0; i<arr.length; i++)
        {
            System.out.print("[");
            for(int j=0; j<arr[i].length; j++)
            {
                System.out.print(arr[i][j]);
                if(j < arr[i].length-1)
                {
                    System.out.print(", ");
                }
            }
            System.out.print("]");
            if(i < arr.length-1)
            {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
}
