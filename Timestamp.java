import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by vincent on 11/27/16.
 */
public class Timestamp implements Serializable, Comparable<Timestamp>
{
    private int time;
    private int id;

    public Timestamp(int time, int id)
    {
        this.time = time;
        this.id = id;
    }

    public Timestamp(Timestamp timestamp)
    {
        this.time = timestamp.time;
        this.id = timestamp.id;
    }

    public int getTime() {return time;}

    public int getId() {return id;}

    public String toString()
    {
        return "Time " + time + ", ID: " + id;
    }

    public boolean equals(Object obj)
    {
        if(!(obj instanceof Timestamp))
            return false;
        return ((this.time == ((Timestamp)(obj)).time) && (this.id == ((Timestamp)(obj)).id));
    }

    public int compareTo(Timestamp that)
    {
        if(this.time == that.time)
        {
            return this.id - that.id;
        }
        else
        {
            return this.time-that.time;
        }
    }



}
