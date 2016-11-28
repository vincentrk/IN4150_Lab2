import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Created by vincent on 11/17/16.
 */
public class MMAProc extends UnicastRemoteObject implements MMAInterface, Runnable
{
    private HashSet<Integer> requestSet;
    private int procID;
    private int time;
    private PriorityQueue<Timestamp> buffer;
    private int numGrants;
    private boolean granted;
    private boolean inquiring;
    private boolean postponed;
    private Timestamp currentGrant;

    public MMAProc(int procID, HashSet<Integer> requestSet) throws RemoteException // TODO this is not ideal better to implement try/catch
    {
        this.procID = procID;
        this.time = 0;
        this.buffer = new PriorityQueue<Timestamp>();
        this.requestSet = requestSet;
        numGrants = 0;
        // Tells if can currently grant permission
        granted = false;
        inquiring = false;
        postponed = false;
        currentGrant = new Timestamp(-1,-1);
    }


    public void run()
    {
        // TODO
//        while(true)
//        {
//            waitTime(getRandTime());
//            System.out.println("ProcID: " + procID + ", time " + time);
//            if(Math.random() > 0.5)
//            {
//                Message m = new Message(new Timestamp(time, procID), messageType.Message, "Broadcast from " + procID + " at local time " + time);
//                System.out.println("Proc" + procID + " broadcast message at time: " + time);
//                broadcastMessage(m);
//            }
//        }
//        //return;
    }

    public void printTest()
    {
        System.out.println("THIS IS THE TEST FOR PROC: " + procID);
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

    public void sendRequest()
    {
        numGrants = 0;
        try
        {
            Integer[] reqSet = ((Integer[])(requestSet.toArray()));
            for(int i=0;i<reqSet.length;i++)
            {
                Message req  = new Message(new Timestamp(time, procID), messageType.REQUEST);
                sendMessage(reqSet[i], req);
            }
            time++;
        } catch (Exception ex)
        {
            System.out.println(ex);
        }
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
        switch (message.getType())
        {
            case GRANT:
                numGrants++;
                if(numGrants >= requestSet.size())
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
                    sendGrant(currentGrant.getId());
                    granted = true;
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
                // TODO
                break;
            case RELEASE:
                granted = false;
                inquiring = false;
                if(!buffer.isEmpty())
                {
                    currentGrant = buffer.remove();
                    granted = true;
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
        granted = true;
        Message grantMessage = new Message(new Timestamp(time, procID), messageType.GRANT);
        sendMessage(sendId, grantMessage);
    }

    private void sendInquire(int sendId)
    {
        Message inqMessage = new Message(new Timestamp(time, procID), messageType.INQUIRE);
        sendMessage(sendId, inqMessage);
    }

    private void sendPostpone(int sendId)
    {
        Message postponeMessage = new Message(new Timestamp(time, procID), messageType.POSTPONE);
        sendMessage(sendId, postponeMessage);
    }

    private void sendRelease()
    {
        Integer[] reqSet = (Integer[])(requestSet.toArray());
        for(int j=0; j<reqSet.length; j++)
        {
            Message release = new Message(new Timestamp(time, procID), messageType.RELEASE);
            sendMessage(reqSet[j], release);
        }
    }

    private void executeCS()
    {
        System.out.println("================================================");
        System.out.println("Process" + procID + " entering critical section");
        System.out.println("================================================");
        waitTime(getRandTime());
    }

}