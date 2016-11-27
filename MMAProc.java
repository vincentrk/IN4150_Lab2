import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Created by vincent on 11/17/16.
 */
public class MMAProc extends UnicastRemoteObject implements MMAInterface, Runnable
{
    private int numProcesses;
    private int procID;
    private int time;
    private PriorityQueue<Message> buffer;
    private TreeMap<Timestamp, Integer> acks;

    public MMAProc(int procID, int numProcesses) throws RemoteException // TODO this is not ideal better to implement try/catch
    {
        this.procID = procID;
        this.time = 0;
        this.buffer = new PriorityQueue<Message>();
        this.numProcesses = numProcesses;
        acks = new TreeMap<Timestamp, Integer>();
    }


    public void run()
    {
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

//    public void broadcastMessage(Message message)
//    {
//        try
//        {
//            for(int i=0;i<numProcesses;i++)
//            {
//                sendMessage(i, message);
//            }
//            time++;
//        } catch (Exception ex)
//        {
//            System.out.println(ex);
//        }
//    }

    public void sendMessage(int procID, Message message)
    {
        try
        {
            TMOInterface Rcv = (TMOInterface) Naming.lookup("rmi://localhost/TMOProc" + procID);
            Rcv.receiveMessage(message);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void receiveMessage(Message message)
    {
//        if(message.getType() == messageType.Message)
//        {
//            System.out.println("Proc" + procID + " receiving message from " + message.getTimestamp());
//            this.buffer.add(message);
//            Message ack = new Message(new Timestamp(message.getTimestamp()), messageType.ACK, "");
//            if(!acks.containsKey(message.getTimestamp()))
//            {
//                acks.put(message.getTimestamp(), 0);
//            }
//            broadcastMessage(ack);
//        }
//        else if(message.getType() == messageType.ACK)
//        {
//            System.out.println("Proc" + procID + " receiving ACK for " + message.getTimestamp());
//            if(acks.containsKey(message.getTimestamp()))
//            {
//                acks.put(message.getTimestamp(), acks.get(message.getTimestamp()) + 1);
//            }
//            else
//            {
//                acks.put(message.getTimestamp(), 1);
//            }
//
//            if((acks.get(message.getTimestamp()) >= numProcesses) && (buffer.peek().getTimestamp().equals(message.getTimestamp())))
//            {
//                deliverMessage();
//            }
//
//        }
//        else if(message.getType() == messageType.ERR)
//        {
//            System.out.println("Message Error: " + message.getMessage());
//        }
//        else
//        {
//            System.out.println("Error: MessageType not found.");
//        }
    }

//    public void deliverMessage()
//    {
//        if(buffer.isEmpty())
//        {
//            return;
//        }
//
//
//        Message deliv = buffer.remove();
//        acks.remove(deliv.getTimestamp());
//        //System.out.println("Process " + this.procID + " Read message " + deliv.getMessage() );
//        time = Math.max(time, deliv.getTimestamp().getTime()) + 1;
//
//        System.out.println("=======================================");
//        System.out.println("PROCESS " + procID + " DELIVERED MESSAGE: " + deliv);
//        System.out.println("=======================================");
//
//        // check if deliver message again?
//        if(!buffer.isEmpty())
//        {
//            Timestamp temp = buffer.peek().getTimestamp();
//            if((acks.containsKey(temp)) && (acks.get(temp) >= numProcesses))
//            {
//                deliverMessage();
//            }
//        }
//    }
}