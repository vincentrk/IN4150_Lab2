import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Created by vincent on 11/17/16.
 */
public class MMAProc extends UnicastRemoteObject implements MMAInterface, Runnable
{
    private int[] requestSet;
    private int procID;
    private int time;
    private PriorityQueue<Timestamp> buffer;
    private int numGrants;
    private boolean granted;
    private boolean inquiring;
    private boolean postponed;
    private boolean requesting;
    private Timestamp currentGrant;
    private int numOfIterations;

    public MMAProc(int procID, int[] requestSet,int numOfIterations) throws RemoteException
    {
        this.procID = procID;
        this.time = 0;
        this.buffer = new PriorityQueue<Timestamp>();
        this.requestSet = requestSet;
        numGrants = 0;
        this.numOfIterations=numOfIterations;
        // boolean value that tells if this process has already granted permission to another process
        granted = false;
        inquiring = false;
        postponed = false;
        requesting = false;
        currentGrant = new Timestamp(-1,-1);
    }

    public void run()
    {
        for(int i=0;i<numOfIterations;i++)
        {
            waitTime(getRandTime());
            System.out.println("ProcID: " + procID + ", time " + time);
            if(!requesting && Math.random() > 0.8)
            {
                System.out.println("Proc" + procID + " requests to go into its critical section from " + requestSetToString(requestSet) + " at time " + time);
                sendRequest(new Timestamp(time, procID));
            }
        }
        System.out.println("Proc" + procID + " completed at time " + time);
        //return;
    }

    public void waitTime(int time)
    {
        try
        {
            Thread.sleep(time);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public int getRandTime()
    {
        return ((int)(Math.random() * 1000));
    }

    // Sends request messages to all processes in the request set and increments time
    public void sendRequest(Timestamp reqTime)
    {
        requesting = true;
        numGrants = 0;
        for(int i=0;i<requestSet.length;i++)
        {
            Message req  = new Message(reqTime, messageType.REQUEST);
            System.out.println("Proc" + procID + " sends REQUEST message to proc" + requestSet[i] + " at time " + time);
            sendMessage(requestSet[i], req);
        }
        time++;
    }

    public void sendMessage(int procID, Message message)
    {
        try
        {
            MMAInterface Rcv = (MMAInterface) Naming.lookup("rmi://localhost/MMAProc" + procID);
            Rcv.receiveMessage(message);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void receiveMessage(Message message)
    {
        System.out.println("Proc" + procID + " receives " + message.getType() + " message from proc" +
                message.getTimestamp().getId() + " at time " + message.getTimestamp().getTime());
        switch (message.getType())
        {
            case GRANT:
                numGrants++;
                if(numGrants >= requestSet.length)
                {
                    postponed = false;
                    executeCS();
                    sendRelease();
                    numGrants = 0;
                }
                break;
            case REQUEST:
                if(!granted)
                {
                    currentGrant = message.getTimestamp();
                    granted = true;
                    sendGrant(currentGrant.getId());
                }
                else
                {
                    buffer.add(message.getTimestamp());
                    Timestamp v = buffer.peek();
                    if((currentGrant.compareTo(message.getTimestamp()) < 0) || (v.compareTo(message.getTimestamp()) < 0))
                    {
                        sendPostpone(message.getTimestamp().getId());
                    }
                    else if(!inquiring)
                    {
                        inquiring = true;
                        sendInquire(currentGrant.getId());
                    }
                }
                break;
            case POSTPONE:
                postponed = true;
                break;
            case INQUIRE:
                if(postponed)
                {
                    numGrants--;
                    sendRelinquish(message.getTimestamp().getId());
                }
                break;
            case RELEASE:
                granted = false;
                inquiring = false;
                if(!buffer.isEmpty())
                {
                    granted = true;
                    currentGrant = buffer.remove();
                    sendGrant(currentGrant.getId());
                }
                break;
            case RELINQUISH:
                inquiring = false;
                granted = false;
                buffer.add(currentGrant);
                currentGrant = buffer.remove();
                granted = true;
                sendGrant(currentGrant.getId());
                break;
            default:
                System.out.println("Error message type not found");
                return;
        }
        time = Math.max(time, message.getTimestamp().getTime()) + 1;
    }

    private void sendGrant(int sendId)
    {
        System.out.println("Proc" + procID + " sends GRANT message to proc" + sendId + " at time " + time);
        granted = true;
        Message grantMessage = new Message(new Timestamp(time, procID), messageType.GRANT);
        sendMessage(sendId, grantMessage);
        time++;
    }

    private void sendInquire(int sendId)
    {
        System.out.println("Proc" + procID + " sends INQUIRE message to proc" + sendId + " at time " + time);
        Message inqMessage = new Message(new Timestamp(time, procID), messageType.INQUIRE);
        sendMessage(sendId, inqMessage);
        time++;
    }

    private void sendPostpone(int sendId)
    {
        System.out.println("Proc" + procID + " sends POSTPONE message to proc" + sendId + " at time " + time);
        Message postponeMessage = new Message(new Timestamp(time, procID), messageType.POSTPONE);
        sendMessage(sendId, postponeMessage);
        time++;
    }

    private void sendRelease()
    {
        for(int j=0; j<requestSet.length; j++)
        {
            System.out.println("Proc" + procID + " sends RELEASE message to proc" + requestSet[j] + " at time " + time);
            Message release = new Message(new Timestamp(time, procID), messageType.RELEASE);
            sendMessage(requestSet[j], release);
        }
        time++;
    }

    private void sendRelinquish(int sendId)
    {
        System.out.println("Proc" + procID + " sends RELINQUISH message to proc" + sendId + " at time " + time);
        Message reqMessage = new Message(new Timestamp(time, procID), messageType.RELINQUISH);
        sendMessage(sendId, reqMessage);
        time++;
    }

    private void executeCS()
    {
        System.out.println("================================================");
        System.out.println("Process " + procID + " ENTERS critical section");
        System.out.println("================================================");
        waitTime(getRandTime() * 2);
        System.out.println("================================================");
        System.out.println("Process " + procID + " LEAVES critical section");
        System.out.println("================================================");
        requesting = false;
    }

    private String requestSetToString(int[] requestSet)
    {
        String reqset = "[";
        for(int i=0; i<requestSet.length; i++)
        {
            reqset += requestSet[i];
            if(i < requestSet.length-1)
            {
                reqset += ", ";
            }
        }
        reqset += "]";
        return reqset;
    }

}