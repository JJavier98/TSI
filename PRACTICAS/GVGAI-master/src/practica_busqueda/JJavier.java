package practica_busqueda;

import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;
import java.util.Scanner;
import java.nio.file.Path;
import java.util.*;
import tools.Vector2d;

import javax.swing.plaf.nimbus.State;

/*  Agente de prueba que usa los métodos de BaseAgent para obtener información
    sobre el entorno y asigna varias probabilidades en función de esta información
    a las posibles acciones.
*/

public class JJavier extends BaseAgent{
    private ArrayList<Types.ACTIONS> lista_acciones; // Conjunto de acciones posibles
    private Random generador;
    private ArrayList<Node> path = new ArrayList<Node>();
    private Queue<Types.ACTIONS> movements = new ArrayDeque<Types.ACTIONS>();
    boolean keep_out = false;
    boolean print = true;

    public JJavier(StateObservation stateObservation, ElapsedCpuTimer elapsedTimer){
        super(stateObservation, elapsedTimer);
        generador = new Random();

        lista_acciones = new ArrayList();
        lista_acciones.add(Types.ACTIONS.ACTION_UP);
        lista_acciones.add(Types.ACTIONS.ACTION_DOWN);
        lista_acciones.add(Types.ACTIONS.ACTION_RIGHT);
        lista_acciones.add(Types.ACTIONS.ACTION_LEFT);
    }

    public boolean gotRockOnTop(int x, int y, StateObservation stateObs)
    {
    	if(y-1 < 0)
        	return stateObs.getObservationGrid()[x][y-1].get(0).itype == 7;
    	else
    		return false;
    }

    public boolean roundAnEnemy(int x, int y, StateObservation stateObs)
	{
		ArrayList<Observation>[] enemies = getEnemiesList(stateObs);

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

	public  boolean isAnEnemy(int x, int y, StateObservation stateObs)
	{
		ArrayList<core.game.Observation> aux = stateObs.getObservationGrid()[x][y];

		if(aux.size()>0)
			return aux.get(0).itype == 10 || aux.get(0).itype == 10;
		else
			return false;
	}

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer)
    {
        if(keep_out)
            path.clear();

        System.out.println("FFFFFFFFFFFFFIIIIIIIIIIIIIIIIIIINNNNNNNNNNNNNN  1");
		Types.ACTIONS action_to_do;
    	if(!movements.isEmpty())
    	{
			action_to_do = movements.peek();

			int next_x = getPlayer(stateObs).getX();
			int next_y = getPlayer(stateObs).getY();

			if (action_to_do == Types.ACTIONS.ACTION_LEFT)      // LOOKS TO THE LEFT
			{
				next_x--;
			}
            else if (action_to_do == Types.ACTIONS.ACTION_RIGHT) // LOOKS TO THE RIGHT
			{
				next_x++;
			}
            else if (action_to_do == Types.ACTIONS.ACTION_UP) // LOOKS TO THE TOP
			{
				next_y--;
			}
            else if (action_to_do == Types.ACTIONS.ACTION_DOWN) // LOOKS TO THE BOTTOM
			{
				next_y++;
			}

			if (roundAnEnemy(next_x, next_y, stateObs) || isAnEnemy(next_x, next_y, stateObs) )
			{
				movements.clear();
				path.clear();
				action_to_do = Types.ACTIONS.ACTION_NIL;
			}
		}
        System.out.println("FFFFFFFFFFFFFIIIIIIIIIIIIIIIIIIINNNNNNNNNNNNNN 2");

		if(path.isEmpty() && movements.isEmpty())
		{
			if(getRemainingGems(stateObs) != 0)
			{
				
				ArrayList<Observation> gems = getGemsList(stateObs);
				boolean fin = false;

				while (!fin)
				{
                    int min_dist = 999;
                    Observation chosen_gem = gems.get(0);
                    System.out.println("buclaso 2");
					for (Observation gem : gems) {
						int new_dist = getPlayer(stateObs).getManhattanDistance(gem);
						if (new_dist < min_dist) {
							chosen_gem = gem;
							min_dist = new_dist;
						}
					}
					path = pathFinding(chosen_gem, stateObs);
					gems.remove(chosen_gem);
					if (path != null)
						fin = true;
				}
			}
            else
            {
                path = pathFinding(getExit(stateObs), stateObs);
            }
		}
        System.out.println("FFFFFFFFFFFFFIIIIIIIIIIIIIIIIIIINNNNNNNNNNNNNN 3");

        if(keep_out)
        {
            Node n_next = path.get(0);
            int x_player = getPlayer(stateObs).getX();
			int y_player = getPlayer(stateObs).getY();

            if(n_next.getObs().getX() != x_player -1 &&
					n_next.getObs().getX() != x_player +1)
			{
				movements.clear();
				boolean fixed = false;

				while (!fixed) {
					if (x_player + 1 < stateObs.getObservationGrid().length) {
						ArrayList<core.game.Observation> aux = stateObs.getObservationGrid()[x_player + 1][y_player];
						if (aux.size() > 0) {
							if (aux.get(0).itype != 0 && aux.get(0).itype != 7 &&
									aux.get(0).itype != 10 && aux.get(0).itype != 11) {
								movements.add(Types.ACTIONS.ACTION_RIGHT);
								movements.add(Types.ACTIONS.ACTION_RIGHT);
								fixed = true;
							}
						} else {
							movements.add(Types.ACTIONS.ACTION_RIGHT);
							movements.add(Types.ACTIONS.ACTION_RIGHT);
							fixed = true;
						}
					}

					if (x_player - 1 < stateObs.getObservationGrid().length && !fixed) {
						ArrayList<core.game.Observation> aux = stateObs.getObservationGrid()[x_player - 1][y_player];
						if (aux.size() > 0) {
							if (aux.get(0).itype != 0 && aux.get(0).itype != 7 &&
									aux.get(0).itype != 10 && aux.get(0).itype != 11) {
								movements.add(Types.ACTIONS.ACTION_RIGHT);
								movements.add(Types.ACTIONS.ACTION_RIGHT);
								fixed = true;
							}
						} else {
							movements.add(Types.ACTIONS.ACTION_RIGHT);
							movements.add(Types.ACTIONS.ACTION_RIGHT);
							fixed = true;
						}
					}

					if (!fixed)
					{
						movements.add(Types.ACTIONS.ACTION_DOWN);
						y_player--;
					}
				}
			}
        }

		for (Node n:path)
		{
			System.out.println(n.getObs().getX());
			System.out.println(n.getObs().getY());
            System.out.println();
		}
        System.out.println("FFFFFFFFFFFFFIIIIIIIIIIIIIIIIIIINNNNNNNNNNNNNN 4");

		if(movements.isEmpty() && !path.isEmpty())
		{
			int x_player = getPlayer(stateObs).getX();
			int y_player = getPlayer(stateObs).getY();
			double x_look = stateObs.getAvatarOrientation().x;
			double y_look = stateObs.getAvatarOrientation().y;
			int x_box = path.get(0).getObs().getX();
			int y_box = path.get(0).getObs().getY();
			//path.remove(0);

			if (x_box < x_player)            // MOVE TO THE LEFT
			{
				if (stateObs.getAvatarOrientation().x == -1 &&
                    stateObs.getAvatarOrientation().y == 0)      // LOOKS TO THE LEFT
				{
					movements.add(Types.ACTIONS.ACTION_LEFT);
				}
                else if (stateObs.getAvatarOrientation().x == 1 &&
                    stateObs.getAvatarOrientation().y == 0) // LOOKS TO THE RIGHT
				{
					movements.add(Types.ACTIONS.ACTION_LEFT);
					movements.add(Types.ACTIONS.ACTION_LEFT);
				}
                else if (stateObs.getAvatarOrientation().x == 0 &&
                    stateObs.getAvatarOrientation().y == -1) // LOOKS TO THE TOP
				{
					movements.add(Types.ACTIONS.ACTION_LEFT);
					movements.add(Types.ACTIONS.ACTION_LEFT);
				}
                else if (stateObs.getAvatarOrientation().x == 0 &&
                    stateObs.getAvatarOrientation().y == 1) // LOOKS TO THE BOTTOM
				{
                    movements.add(Types.ACTIONS.ACTION_LEFT);
					movements.add(Types.ACTIONS.ACTION_LEFT);
				}
			}
            else if (x_box > x_player)        // MOVE TO THE RIGHT
			{
				if (stateObs.getAvatarOrientation().x == -1 &&
                    stateObs.getAvatarOrientation().y == 0)      // LOOKS TO THE LEFT
				{
                    movements.add(Types.ACTIONS.ACTION_RIGHT);
                    movements.add(Types.ACTIONS.ACTION_RIGHT);
				}
                else if (stateObs.getAvatarOrientation().x == 1 &&
                    stateObs.getAvatarOrientation().y == 0) // LOOKS TO THE RIGHT
				{
                    movements.add(Types.ACTIONS.ACTION_RIGHT);
				}
                else if (stateObs.getAvatarOrientation().x == 0 &&
                    stateObs.getAvatarOrientation().y == -1) // LOOKS TO THE TOP
				{
                    movements.add(Types.ACTIONS.ACTION_RIGHT);
                    movements.add(Types.ACTIONS.ACTION_RIGHT);
				}
                else if (stateObs.getAvatarOrientation().x == 0 &&
                    stateObs.getAvatarOrientation().y == 1) // LOOKS TO THE BOTTOM
				{
                    movements.add(Types.ACTIONS.ACTION_RIGHT);
                    movements.add(Types.ACTIONS.ACTION_RIGHT);
				}
			}
            else if (y_box < y_player)        // MOVE TO THE TOP
			{
				if (stateObs.getAvatarOrientation().x == -1 &&
                    stateObs.getAvatarOrientation().y == 0)      // LOOKS TO THE LEFT
				{
                    movements.add(Types.ACTIONS.ACTION_UP);
                    movements.add(Types.ACTIONS.ACTION_UP);
				}
                else if (stateObs.getAvatarOrientation().x == 1 &&
                    stateObs.getAvatarOrientation().y == 0) // LOOKS TO THE RIGHT
				{
                    movements.add(Types.ACTIONS.ACTION_UP);
                    movements.add(Types.ACTIONS.ACTION_UP);
				}
                else if (stateObs.getAvatarOrientation().x == 0 &&
                    stateObs.getAvatarOrientation().y == -1) // LOOKS TO THE TOP
				{
                    movements.add(Types.ACTIONS.ACTION_UP);
				}
                else if (stateObs.getAvatarOrientation().x == 0 &&
                    stateObs.getAvatarOrientation().y == 1) // LOOKS TO THE BOTTOM
				{
                    movements.add(Types.ACTIONS.ACTION_UP);
                    movements.add(Types.ACTIONS.ACTION_UP);
				}
			}
            else if (y_box > y_player)        // MOVE TO THE BOTTOM
			{
				if (stateObs.getAvatarOrientation().x == -1 &&
                    stateObs.getAvatarOrientation().y == 0)      // LOOKS TO THE LEFT
				{
                    movements.add(Types.ACTIONS.ACTION_DOWN);
                    movements.add(Types.ACTIONS.ACTION_DOWN);
				}
                else if (stateObs.getAvatarOrientation().x == 1 &&
                    stateObs.getAvatarOrientation().y == 0) // LOOKS TO THE RIGHT
				{
                    movements.add(Types.ACTIONS.ACTION_DOWN);
                    movements.add(Types.ACTIONS.ACTION_DOWN);
				}
                else if (stateObs.getAvatarOrientation().x == 0 &&
                    stateObs.getAvatarOrientation().y == -1) // LOOKS TO THE TOP
				{
                    movements.add(Types.ACTIONS.ACTION_DOWN);
                    movements.add(Types.ACTIONS.ACTION_DOWN);
				}
                else if (stateObs.getAvatarOrientation().x == 0 &&
                    stateObs.getAvatarOrientation().y == 1) // LOOKS TO THE BOTTOM
				{
                    movements.add(Types.ACTIONS.ACTION_DOWN);
				}
			}
            System.out.println("FFFFFFFFFFFFFIIIIIIIIIIIIIIIIIIINNNNNNNNNNNNNN 5");

			//.out.println(movements);
            Types.ACTIONS last_action = movements.peek();
           // .out.println(movements);

			for (int i = 1; i < path.size(); i++)
			{
				if(last_action == Types.ACTIONS.ACTION_DOWN)
				{
					y_player++;
				}
				else if(last_action == Types.ACTIONS.ACTION_UP)
				{
					if(y_player-2 >= 0)
					{
						ArrayList<core.game.Observation> aux = stateObs.getObservationGrid()[x_player][y_player-2];
						if(aux.size() > 0)
						{
							if(aux.get(0).itype != 7)
							{
								y_player--;
							}
						}
					}
				}
				else if(last_action == Types.ACTIONS.ACTION_LEFT)
				{
					x_player--;
				}
				else if(last_action == Types.ACTIONS.ACTION_RIGHT)
				{
					x_player++;
				}
				System.out.println(x_player);
				System.out.println(y_player);

				x_box = path.get(i).getObs().getX();
				y_box = path.get(i).getObs().getY();

				if (x_box < x_player)            // MOVE TO THE LEFT
				{
					if(last_action == Types.ACTIONS.ACTION_LEFT)      // LOOKS TO THE LEFT
					{
						movements.add(Types.ACTIONS.ACTION_LEFT);
					}
					else if(last_action == Types.ACTIONS.ACTION_RIGHT) // LOOKS TO THE RIGHT
					{
						movements.add(Types.ACTIONS.ACTION_LEFT);
						movements.add(Types.ACTIONS.ACTION_LEFT);
					}
					else if(last_action == Types.ACTIONS.ACTION_UP) // LOOKS TO THE TOP
					{
						movements.add(Types.ACTIONS.ACTION_LEFT);
						movements.add(Types.ACTIONS.ACTION_LEFT);
					}
					else if(last_action == Types.ACTIONS.ACTION_DOWN) // LOOKS TO THE BOTTOM
					{
						movements.add(Types.ACTIONS.ACTION_LEFT);
						movements.add(Types.ACTIONS.ACTION_LEFT);
					}
                    last_action = Types.ACTIONS.ACTION_LEFT;
				}
				else if (x_box > x_player)        // MOVE TO THE RIGHT
				{
					if(last_action == Types.ACTIONS.ACTION_LEFT)      // LOOKS TO THE LEFT
					{
						movements.add(Types.ACTIONS.ACTION_RIGHT);
						movements.add(Types.ACTIONS.ACTION_RIGHT);
					}
					else if(last_action == Types.ACTIONS.ACTION_RIGHT) // LOOKS TO THE RIGHT
					{
						movements.add(Types.ACTIONS.ACTION_RIGHT);
					}
					else if(last_action == Types.ACTIONS.ACTION_UP) // LOOKS TO THE TOP
					{
						movements.add(Types.ACTIONS.ACTION_RIGHT);
						movements.add(Types.ACTIONS.ACTION_RIGHT);
					}
					else if(last_action == Types.ACTIONS.ACTION_DOWN) // LOOKS TO THE BOTTOM
					{
						movements.add(Types.ACTIONS.ACTION_RIGHT);
						movements.add(Types.ACTIONS.ACTION_RIGHT);
					}
                    last_action = Types.ACTIONS.ACTION_RIGHT;
				}
				else if (y_box < y_player)        // MOVE TO THE TOP
				{
					if(last_action == Types.ACTIONS.ACTION_LEFT)      // LOOKS TO THE LEFT
					{
						movements.add(Types.ACTIONS.ACTION_UP);
						movements.add(Types.ACTIONS.ACTION_UP);
					}
					else if(last_action == Types.ACTIONS.ACTION_RIGHT) // LOOKS TO THE RIGHT
					{
						movements.add(Types.ACTIONS.ACTION_UP);
						movements.add(Types.ACTIONS.ACTION_UP);
					}
					else if(last_action == Types.ACTIONS.ACTION_UP) // LOOKS TO THE TOP
					{
						movements.add(Types.ACTIONS.ACTION_UP);
					}
					else if(last_action == Types.ACTIONS.ACTION_DOWN) // LOOKS TO THE BOTTOM
					{
						movements.add(Types.ACTIONS.ACTION_UP);
						movements.add(Types.ACTIONS.ACTION_UP);
					}
                    last_action = Types.ACTIONS.ACTION_UP;
				}
				else if (y_box > y_player)        // MOVE TO THE BOTTOM
				{
					if(last_action == Types.ACTIONS.ACTION_LEFT)      // LOOKS TO THE LEFT
					{
						movements.add(Types.ACTIONS.ACTION_DOWN);
						movements.add(Types.ACTIONS.ACTION_DOWN);
					}
					else if(last_action == Types.ACTIONS.ACTION_RIGHT) // LOOKS TO THE RIGHT
					{
						movements.add(Types.ACTIONS.ACTION_DOWN);
						movements.add(Types.ACTIONS.ACTION_DOWN);
					}
					else if(last_action == Types.ACTIONS.ACTION_UP) // LOOKS TO THE TOP
					{
						movements.add(Types.ACTIONS.ACTION_DOWN);
						movements.add(Types.ACTIONS.ACTION_DOWN);
					}
					else if(last_action == Types.ACTIONS.ACTION_DOWN) // LOOKS TO THE BOTTOM
					{
						movements.add(Types.ACTIONS.ACTION_DOWN);
					}
                    last_action = Types.ACTIONS.ACTION_DOWN;
				}
			}
		}
        System.out.println("FFFFFFFFFFFFFIIIIIIIIIIIIIIIIIIINNNNNNNNNNNNNN 6");
		if(print) {
			System.out.println(movements);
			System.out.println();
			print = false;
		}
		action_to_do = movements.poll();
        if(movements.isEmpty()) {
			path.clear();
			print = true;
		}

		int next_x = getPlayer(stateObs).getX();
		int next_y = getPlayer(stateObs).getY();

		if(action_to_do == Types.ACTIONS.ACTION_LEFT)      // LOOKS TO THE LEFT
		{
			next_x--;
		}
		else if(action_to_do == Types.ACTIONS.ACTION_RIGHT) // LOOKS TO THE RIGHT
		{
			next_x++;
		}
		else if(action_to_do == Types.ACTIONS.ACTION_UP) // LOOKS TO THE TOP
		{
			keep_out = true;
			next_y--;
		}
		else if(action_to_do == Types.ACTIONS.ACTION_DOWN) // LOOKS TO THE BOTTOM
		{
			next_y++;
		}

		if( gotRockOnTop(next_x, next_y, stateObs) )
		{
			movements.clear();
			path.clear();
		}
        else
        {
            keep_out = false;
        }

        return action_to_do;
    }

    public ArrayList<Node> pathFinding(Observation objective, StateObservation stateObs)
    {
        NodeComparator comparator = new NodeComparator();
        int max_x = stateObs.getObservationGrid().length;
        int max_y = stateObs.getObservationGrid()[0].length;
        ArrayList<Observation>[] enemies = getEnemiesList(stateObs);
        ArrayList<Vector2d> nodesAdjacentToEnemy = new ArrayList<Vector2d>();
        Node n_fin = new Node();

		for (ArrayList<Observation> enem : enemies)
        {
            Node n_enemy = new Node(enem.get(1).getX(), enem.get(1).getY(), 0, null, null, null, enemies, stateObs);
            ArrayList<Vector2d> aux = n_enemy.expandToSand(enemies, stateObs);

            nodesAdjacentToEnemy.addAll(aux);
        }

        Node n_inicial = new Node(getPlayer(stateObs).getX(), getPlayer(stateObs).getY(), 0, null, objective, nodesAdjacentToEnemy, enemies,  stateObs);

        Node[][] Visited = new Node[max_x][max_y];                           // CLOSED list
        PriorityQueue<Node> toVisit = new PriorityQueue<Node>(comparator);   // OPEN list

        toVisit.add(n_inicial);

		boolean objectiveFound = false;
		while(!objectiveFound && !toVisit.isEmpty())
        {

            Node n_current = toVisit.poll();

            if( n_current.getObs().getX() == objective.getX()  &&
                n_current.getObs().getY() == objective.getY())
            {
                n_fin = new Node(n_current);
                objectiveFound = true;
            }
            else
            {
            	// RIGHT
                int x = n_current.getObs().getX()+1;
                int y = n_current.getObs().getY();
                if( x < max_x )
                    if( stateObs.getObservationGrid()[x][y].size() > 0 )
                        if( stateObs.getObservationGrid()[x][y].get(0).itype != 0 &&
                            stateObs.getObservationGrid()[x][y].get(0).itype != 7)
                        {
                            if(Visited[x][y] == null)
                            {
                                Node child = new Node(x, y, n_current.getG()+1, n_current, objective, nodesAdjacentToEnemy, enemies, stateObs);
                                toVisit.add(child);
                                //n_current.addChildren(child);
                            }
                            else
                            {
                                Node child = new Node(x, y, n_current.getG()+1, n_current, objective, nodesAdjacentToEnemy, enemies, stateObs);
                                if(Visited[x][y].getF() > child.getF())
                                {
                                    toVisit.add(child);
                                    //n_current.addChildren(child);
                                }
                            }
                        }

                // TOP
                x = n_current.getObs().getX();
                y = n_current.getObs().getY()-1;
                if( y >= 0 )
                    if( stateObs.getObservationGrid()[x][y].size() > 0 )
                        if( stateObs.getObservationGrid()[x][y].get(0).itype != 0 &&
                            stateObs.getObservationGrid()[x][y].get(0).itype != 7)
                        {
                            if(Visited[x][y] == null)
                            {
                                Node child = new Node(x, y, n_current.getG()+1, n_current, objective, nodesAdjacentToEnemy, enemies, stateObs);
                                toVisit.add(child);
                                //n_current.addChildren(child);
                            }
                            else
                            {
                                Node child = new Node(x, y, n_current.getG()+1, n_current, objective, nodesAdjacentToEnemy, enemies, stateObs);
                                if(Visited[x][y].getF() > child.getF())
                                {
                                    toVisit.add(child);
                                    //n_current.addChildren(child);
                                }
                            }
                        }

                // LEFT
                x = n_current.getObs().getX()-1;
                y = n_current.getObs().getY();
                if( x >= 0 )
                    if( stateObs.getObservationGrid()[x][y].size() > 0 )
                        if( stateObs.getObservationGrid()[x][y].get(0).itype != 0 &&
                            stateObs.getObservationGrid()[x][y].get(0).itype != 7)
                        {
                            if(Visited[x][y] == null)
                            {
                                Node child = new Node(x, y, n_current.getG()+1, n_current, objective, nodesAdjacentToEnemy, enemies, stateObs);
                                toVisit.add(child);
                                //n_current.addChildren(child);
                            }
                            else
                            {
                                Node child = new Node(x, y, n_current.getG()+1, n_current, objective, nodesAdjacentToEnemy, enemies, stateObs);
                                if(Visited[x][y].getF() > child.getF())
                                {
                                    toVisit.add(child);
                                    //n_current.addChildren(child);
                                }
                            }
                        }

				// BOTTOM
                x = n_current.getObs().getX();
                y = n_current.getObs().getY()+1;
                if( y < max_y )
                    if( stateObs.getObservationGrid()[x][y].size() > 0 )
                        if( stateObs.getObservationGrid()[x][y].get(0).itype != 0 &&
                            stateObs.getObservationGrid()[x][y].get(0).itype != 7)
                        {
                            if(Visited[x][y] == null)
                            {
                                Node child = new Node(x, y, n_current.getG()+1, n_current, objective, nodesAdjacentToEnemy, enemies, stateObs);
                                toVisit.add(child);
                                //n_current.addChildren(child);
                            }
                            else
                            {
                                Node child = new Node(x, y, n_current.getG()+1, n_current, objective, nodesAdjacentToEnemy, enemies, stateObs);
                                if(Visited[x][y].getF() > child.getF())
                                {
                                    toVisit.add(child);
                                    //n_current.addChildren(child);
                                }
                            }
                        }
            }
            if(Visited[n_current.getObs().getX()][n_current.getObs().getY()] != null)
            {
				if (Visited[n_current.getObs().getX()][n_current.getObs().getY()].getF() > n_current.getF())
					Visited[n_current.getObs().getX()][n_current.getObs().getY()] = n_current;
			}
            else
				Visited[n_current.getObs().getX()][n_current.getObs().getY()] = n_current;

			//int n = sc.nextInt();
        }

		Deque<Node> stack = new ArrayDeque<Node>();
		if(objectiveFound) {
			Node aux = new Node(n_fin);

			while (aux.getParent() != null) {
				stack.push(aux);
				aux = aux.getParent();
			}

			while (!stack.isEmpty()) {
				System.out.println("eh");
				Node n = (stack.pop());
				path.add(n);
			}
			return path;
		}
        else
            return null;
    }
}
