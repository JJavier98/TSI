package practica_busqueda;

import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

import java.util.*;

public class Node {
    private Node parent;                // parent of the current node
    private ArrayList<Node> children;   // children of the current node
    private Observation obs;            // coordinates and type

    private int f; // Total cost
    private int g; // Current cost
    private int h; // Heuristic cost
    private int objective_distance;     // distance to objective
    private boolean free_an_enemy;      // this position frees an enemy
    private boolean im_an_enemy;        // this position is an enemy
    private boolean i_could_be_an_enemy;// this position could be occupied by an enemy
    private boolean rock_on_top;        // there is a rock on top of this box

    public Node(int _x, int _y, int _g, Node _parent, Observation objective, Set<String> freeEnemies, ArrayList<Observation>[] enemies, StateObservation stateObs)
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
        objective_distance = 0;
        if(objective != null)
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
        else if(freeEnemies != null)
            {
                free_an_enemy = freeEnemies.contains(_x + ":" + _y);
                //System.out.println(free_an_enemy);
            }

        // Indicate if there is a rock on top
        if(_y-1 >= 0 && stateObs.getObservationGrid()[_x][_y-1].size() > 0)
            rock_on_top = (stateObs.getObservationGrid()[_x][_y-1].get(0).itype == 7);

        // Increase h according to some events
        if( type == ObservationType.WALL ||
            type == ObservationType.BOULDER ||
                im_an_enemy)
            h += 90;
        if(rock_on_top)
            h += 5;
        if(free_an_enemy || i_could_be_an_enemy)
            h += 20;
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

    public Node(){}

    public boolean roundAnEnemy(int x, int y, ArrayList<Observation>[] enemies, StateObservation stateObs)
    {
        for (ArrayList<Observation> v_enem:enemies)
        {
            for (Observation enem:v_enem)
            {
                if( (enem.getX()+1 == x && enem.getY() == y) ||
                    (enem.getX()-1 == x && enem.getY() == y) ||
                    (enem.getX() == x && enem.getY()+1 == y) ||
                    (enem.getX() == x && enem.getY()-1 == y)  )
                {
                    return true;
                }
            }
        }

        return false;
    }

    public  boolean isAnEnemy(int x, int y, ArrayList<Observation>[] enemies, StateObservation stateObs)
    {
        ArrayList<core.game.Observation> aux = stateObs.getObservationGrid()[x][y];

        if(aux.size()>0)
            return aux.get(0).itype == 10 || aux.get(0).itype == 10;
        else
            return false;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////// GETTERS ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Node getParent() {
        return parent;
    }

    public ArrayList<Node> getChildren() { return children; }

    public Observation getObs() { return obs; }

    public int getF() { return f; }

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
        this.f = g+h;
    }

    public void setH(int h) {
        this.h = h;
        this.f = g+h;
    }

    public void setObjective_distance(int objective_distance) {
        h = h - this.objective_distance;
        this.objective_distance = objective_distance;
        h = h+objective_distance;
    }

    public void setFree_an_enemy(boolean free_an_enemy) {
        if(this.free_an_enemy)
            h = h-20;

        this.free_an_enemy = free_an_enemy;

        if(this.free_an_enemy)
            h = h+20;
    }

    public void setIm_an_enemy(boolean im_an_enemy) {
        if(this.im_an_enemy)
            h = h-90;

        this.im_an_enemy = im_an_enemy;

        if(this.im_an_enemy)
            h = h+90;
    }

    public void setI_could_be_an_enemy(boolean i_could_be_an_enemy) {
        if(this.i_could_be_an_enemy)
            h = h-20;

        this.i_could_be_an_enemy = i_could_be_an_enemy;

        if(this.i_could_be_an_enemy)
            h = h+20;
    }

    public void setRock_on_top(boolean rock_on_top) {
        this.rock_on_top = rock_on_top;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////// BFS /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ArrayList<String> expandToSand(ArrayList<Observation>[] enemies, StateObservation stateObs)
    {
        NodeComparator comparator = new NodeComparator();
        int max_x = stateObs.getObservationGrid().length;
        int max_y = stateObs.getObservationGrid()[0].length;
        ArrayList<String> nodesColindantToEnemy = new ArrayList<String>();
        boolean Visited[][] = new boolean[max_x][max_y];                               // CLOSED list
        PriorityQueue<Node> toVisit = new PriorityQueue<Node>(comparator);                // OPEN list

        toVisit.add(this);

        while(!toVisit.isEmpty())
        {
            Node n = toVisit.poll();
            if( (n.getObs().getType() == ObservationType.GROUND)  ||
                (n.getObs().getType() == ObservationType.BOULDER) ||
                (n.getObs().getType() == ObservationType.GEM) ||
                (n.getObs().getType() == ObservationType.WALL))
            {
                String str = n.getObs().getX() + ":" + n.getObs().getY();
                nodesColindantToEnemy.add(str);
            }
            else
            {
                int x = n.getObs().getX()+1;
                int y = n.getObs().getY();
                if(x < max_x && !Visited[x][y])
                    toVisit.add(new Node(x, y, 0, null, null, null, enemies, stateObs));
                x = n.getObs().getX();
                y = n.getObs().getY()-1;
                if(y >= 0 && !Visited[x][y])
                    toVisit.add(new Node(x, y, 0, null, null, null, enemies, stateObs));
                x = n.getObs().getX()-1;
                y = n.getObs().getY();
                if(x >= 0 && !Visited[x][y])
                    toVisit.add(new Node(x, y, 0, null, null, null, enemies, stateObs));
                x = n.getObs().getX();
                y = n.getObs().getY()+1;
                if(y < max_y && !Visited[x][y])
                    toVisit.add(new Node(x, y, 0, null, null, null, enemies, stateObs));
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
        if(n1.getF() > n2.getF())
            return 1;
        else if(n1.getF() < n2.getF())
            return -1;
        return 0;
    }
}