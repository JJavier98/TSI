package practica_busqueda;

import core.game.StateObservation;
import ontology.Types;
import org.jetbrains.annotations.NotNull;
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
    Observation obs;            // coordinates and type

    int f, g, h;                // algorithm functions
    int objective_distance;     // distance to objective
    boolean free_an_enemy;      // this position frees an enemy
    boolean im_an_enemy;        // this position is an enemy
    boolean i_could_be_an_enemy;// this position could be occupied by an enemy
    boolean rock_on_top;        // there is a rock on top of this box

    public Node(int _x, int _y, int _g, Node _parent, Observation objective, ArrayList<Vector2d> freeEnemies, StateObservation stateObs)
    {
        // Designate box type
        ObservationType type = ObservationType.BAT;
        if(stateObs.getObservationGrid()[_x][_y].isEmpty())
            type = null;
        else {
            int itype = stateObs.getObservationGrid()[_x][_y].get(0).itype;
            switch (itype) {
                case (0):
                    type = ObservationType.WALL;
                    break;
                case (4):
                    type = ObservationType.GROUND;
                    break;
                case (7):
                    type = ObservationType.BOULDER;
                    break;
                case (6):
                    type = ObservationType.GEM;
                    break;
                case (11):
                    type = ObservationType.BAT;
                    break;
                case (10):
                    type = ObservationType.SCORPION;
                    break;
                case (1):
                    type = ObservationType.PLAYER;
                    break;
                case (5):
                    type = ObservationType.EXIT;
                    break;
            }
        }
        // Indicate coordinates and type of the current box
        obs = new Observation(_x, _y, type);
        // Calculate the very first h
        objective_distance = obs.getManhattanDistance(objective);
        // Assigment of the real cost
        g = _g;
        // Assigment of base h
        h = objective_distance;

         // Designate if the box houses an enemy
        im_an_enemy = false;
        free_an_enemy = false;
        i_could_be_an_enemy = false;
        if(obs.getType() == ObservationType.BAT || obs.getType() == ObservationType.SCORPION)
            im_an_enemy = true;
        else // Identify if the box is near an enemy
            if( stateObs.getObservationGrid()[_x+1][_y].get(0).itype == 11 ||
            stateObs.getObservationGrid()[_x+1][_y].get(0).itype == 10 ||
            stateObs.getObservationGrid()[_x-1][_y].get(0).itype == 11 ||
            stateObs.getObservationGrid()[_x-1][_y].get(0).itype == 10 ||
            stateObs.getObservationGrid()[_x][_y+1].get(0).itype == 11 ||
            stateObs.getObservationGrid()[_x][_y+1].get(0).itype == 10 ||
            stateObs.getObservationGrid()[_x][_y-1].get(0).itype == 11 ||
            stateObs.getObservationGrid()[_x][_y-1].get(0).itype == 10)
            {
                i_could_be_an_enemy = true;
            }
            else
            {
                // Designate if the box could free an enemy
                for (Vector2d freeEnemy : freeEnemies)
                {
                    if(freeEnemy.x == obs.getX() && freeEnemy.y == obs.getY())
                    {
                        free_an_enemy = true;
                    }
                }
            }

        // Indicate if there is a rock on top
        rock_on_top = false;
        if(stateObs.getObservationGrid()[_x][_y-1].get(0).itype == 7)
            rock_on_top = true;

        // Increase h according to some events
        if(type == ObservationType.WALL ||
                (type == ObservationType.EXIT && objective.getType() == ObservationType.GEM) ||
                type == ObservationType.BOULDER ||
                im_an_enemy)
            h += 90;
        if(rock_on_top)
            h += 10;
        if(free_an_enemy || i_could_be_an_enemy)
            h += 50;
        f = g+h;
        parent = _parent;
    }

    public Node(Node n)
    {
        obs = new Observation(n.obs.getX(), n.obs.getY(), n.obs.getType());
        g = n.g;
        h = n.h;
        f = n.f;
        parent = n.parent;
        children = n.children;
        rock_on_top = n.rock_on_top;
        i_could_be_an_enemy = n.i_could_be_an_enemy;
        free_an_enemy = n.free_an_enemy;
        objective_distance = n.objective_distance;
        im_an_enemy = n.im_an_enemy;
    }
    public void actualizar(Observation objective, ArrayList<Vector2d> freeEnemies, StateObservation stateObs)
    {
    	int _x = obs.getX();
    	int _y = obs.getY();
        // Designate box type
        ObservationType type = ObservationType.BAT;
        if(stateObs.getObservationGrid()[_x][_y].isEmpty())
            type = null;
        else {
            int itype = stateObs.getObservationGrid()[_x][_y].get(0).itype;
            switch (itype) {
                case (0):
                    type = ObservationType.WALL;
                    break;
                case (4):
                    type = ObservationType.GROUND;
                    break;
                case (7):
                    type = ObservationType.BOULDER;
                    break;
                case (6):
                    type = ObservationType.GEM;
                    break;
                case (11):
                    type = ObservationType.BAT;
                    break;
                case (10):
                    type = ObservationType.SCORPION;
                    break;
                case (1):
                    type = ObservationType.PLAYER;
                    break;
                case (5):
                    type = ObservationType.EXIT;
                    break;
            }
        }
        // Indicate coordinates and type of the current box
        Observation observ = getObs();
        obs = new Observation(observ.getX(), observ.getY(), type);
        // Calculate the very first h
        objective_distance = obs.getManhattanDistance(objective);
        // Assigment of base h
        h = objective_distance;

         // Designate if the box houses an enemy
        im_an_enemy = false;
        free_an_enemy = false;
        i_could_be_an_enemy = false;
        if(obs.getType() == ObservationType.BAT || obs.getType() == ObservationType.SCORPION)
            im_an_enemy = true;
        else // Identify if the box is near an enemy
            if( stateObs.getObservationGrid()[_x+1][_y].get(0).itype == 11 ||
                stateObs.getObservationGrid()[_x+1][_y].get(0).itype == 10 ||
                stateObs.getObservationGrid()[_x-1][_y].get(0).itype == 11 ||
                stateObs.getObservationGrid()[_x-1][_y].get(0).itype == 10 ||
                stateObs.getObservationGrid()[_x][_y+1].get(0).itype == 11 ||
                stateObs.getObservationGrid()[_x][_y+1].get(0).itype == 10 ||
                stateObs.getObservationGrid()[_x][_y-1].get(0).itype == 11 ||
                stateObs.getObservationGrid()[_x][_y-1].get(0).itype == 10)
            {
                i_could_be_an_enemy = true;
            }
            else
            {
                // Designate if the box could free an enemy
                for (Vector2d freeEnemy : freeEnemies)
                {
                    if(freeEnemy.x == obs.getX() && freeEnemy.y == obs.getY())
                    {
                        free_an_enemy = true;
                    }
                }
            }

        // Indicate if there is a rock on top
        rock_on_top = false;
        if(stateObs.getObservationGrid()[_x][_y-1].get(0).itype == 7)
            rock_on_top = true;

        // Increase h according to some events
        if(type == ObservationType.WALL ||
                (type == ObservationType.EXIT && objective.getType() == ObservationType.GEM) ||
                type == ObservationType.BOULDER ||
                im_an_enemy)
            h += 90;
        if(rock_on_top)
            h += 10;
        if(free_an_enemy || i_could_be_an_enemy)
            h += 50;
        f = g+h;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////// GETTERS ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Node getParent() {
        return parent;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public Observation getObs() {
        return obs;
    }

    public int getF() {
        return f;
    }

    public int getG() {
        return g;
    }

    public int getH() {
        return h;
    }

    public int getObjective_distance() {
        return objective_distance;
    }

    public boolean isFree_an_enemy() {
        return free_an_enemy;
    }

    public boolean isIm_an_enemy() {
        return im_an_enemy;
    }

    public boolean isI_could_be_an_enemy() {
        return i_could_be_an_enemy;
    }

    public boolean isRock_on_top() {
        return rock_on_top;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////// SETTERS ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }

    public void addChildren(Node child) {
        children.add(child);
    }

    public void setObs(Observation obs) { this.obs = obs; }

    public void setF(int f) {
        this.f = f;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setH(int h) {
        this.h = h;
    }

    public void setObjective_distance(int objective_distance) {
        this.objective_distance = objective_distance;
    }

    public void setFree_an_enemy(boolean free_an_enemy) {
        this.free_an_enemy = free_an_enemy;
    }

    public void setIm_an_enemy(boolean im_an_enemy) {
        this.im_an_enemy = im_an_enemy;
    }

    public void setI_could_be_an_enemy(boolean i_could_be_an_enemy) {
        this.i_could_be_an_enemy = i_could_be_an_enemy;
    }

    public void setRock_on_top(boolean rock_on_top) {
        this.rock_on_top = rock_on_top;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////// BFS /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ArrayList<Vector2d> expandToSand(StateObservation stateObs)
    {
        NodeComparator comparator = new NodeComparator();
        int max_x = stateObs.getObservationGrid().length;
        int max_y = stateObs.getObservationGrid()[0].length;
        ArrayList<Vector2d> nodesColindantToEnemy = new ArrayList<Vector2d>();
        boolean Visited[][] = new boolean[max_x][max_y];                                  // CLOSED list
        PriorityQueue<Node> toVisit = new PriorityQueue<Node>(comparator);                // OPEN list

        for (int i = 0; i < max_x ; ++i)
        {
            for (int j = 0 ; j < max_y ; ++j)
            {
                Visited[i][j] = false;
            }
        }

        toVisit.add(this);

        while(!toVisit.isEmpty())
        {
            Node n = toVisit.poll();
            if( (n.getObs().getType() == ObservationType.GROUND)  ||
                (n.getObs().getType() == ObservationType.BOULDER) ||
                (n.getObs().getType() == ObservationType.WALL))
            {
                nodesColindantToEnemy.add(new Vector2d(n.getObs().getX(), n.getObs().getY()));
            }
            else
            {
                int x = n.getObs().getX()+1;
                int y = n.getObs().getY();
                if(x < max_x && !Visited[x][y])
                    toVisit.add(new Node(x, y, 0, null, null, null, stateObs));
                x = n.getObs().getX();
                y = n.getObs().getY()-1;
                if(y >= 0 && !Visited[x][y])
                    toVisit.add(new Node(x, y, 0, null, null, null, stateObs));
                x = n.getObs().getX()-1;
                y = n.getObs().getY();
                if(x >= 0 && !Visited[x][y])
                    toVisit.add(new Node(x, y, 0, null, null, null, stateObs));
                x = n.getObs().getX();
                y = n.getObs().getY()+1;
                if(y < max_y && !Visited[x][y])
                    toVisit.add(new Node(x, y, 0, null, null, null, stateObs));
            }
            Visited[n.getObs().getX()][n.getObs().getY()] = true;
        }
        return nodesColindantToEnemy;
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