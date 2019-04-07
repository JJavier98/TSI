package practica_busqueda;

import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

import java.util.ArrayList;
import java.util.Random;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class Node {
    Node parent;                // parent of the current node
    ArrayList<Node> children;   // children of the current node
    int f, g, h;                // algorithm functions
    int x, y;                   // position coordinates
    int objective_distance;     // distance to objective
    boolean free_an_enemy;         // this position frees an enemy
    boolean im_an_enemy;        // this position is an enemy
    boolean i_could_be_an_enemy;// this position could be occupied by an enemy

    public Node(int _x, int _y, int g, int h, Node parent)
    {
        x = _x;
        y = _y;
        f = g+h;
    }

    public ArrayList<Vector2d> expandToSand(Node node_ini, StateObservation stateObs)
    {
        NodeComparator comparator = new NodeComparator();
        int max_x = stateObs.getObservationGrid().length;
        int max_y = stateObs.getObservationGrid()[0].length;
        boolean Visited[][] = new boolean[max_x][max_y];                                // CLOSED list
        PriorityQueue<Node> toVisit = new PriorityQueue<Node>(comparator);      // OPEN list

        for (int i = 0; i < max_x ; ++i)
        {
            for (int j = 0 ; j < max_y ; ++j)
            {
                Visited[i][j] = false;
            }
        }

        toVisit.add(node_ini);

        while(!toVisit.isEmpty())
        {

        }
    }
}

class NodeComparator implements Comparator<Node>
{
    public int compare(Node n1, Node n2)
    {
        if(n1.f > n2.f)
            return 1;
        else if(n1.f < n2.f)
            return -1;
        return 0;
    }
}