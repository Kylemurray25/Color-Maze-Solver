import java.util.ArrayList;

public class Node implements Comparable<Node>
{
    public int totalCost,x,y,cost;
    public ArrayList<Edge> neighbors;
    public Node previous;
    public boolean visited,start,end;
    
    //Combined Node class from Dijkstra's Algorithm and the Maze Solver
    public Node(int initX, int initY)
    {
        start=false;
        end=false;
        x=initX;
        y=initY;
        totalCost = Integer.MAX_VALUE;
        cost=0;
        neighbors = new ArrayList<Edge>();
        previous = null;
        visited = false;
    }
    
    public void addEdges(Edge... edgeArray)
    {
        for (Edge e : edgeArray)
            neighbors.add(e);
    }
    
    public String toString()
    {
        return "[" + x+","+y + ":" + totalCost + "]";
    } 
    
    public int compareTo(Node other)
    {
        if ( this.totalCost < other.totalCost )
            return -1;   // this < other
        else if ( this.totalCost == other.totalCost )
            return 0;    // this = other
        else 
            return 1;    // this > other
    }
}