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
		ArrayList<Observation>[] enemies = getEnemiesList(stateObs);

		for (ArrayList<Observation> v_enem:enemies)
		{
			for (Observation enem:v_enem)
			{
				if( enem.getX() == x && enem.getY() == y )
				{
					return true;
				}
			}
		}

		return false;
	}

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){

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

			if (roundAnEnemy(next_x, next_y, stateObs) || isAnEnemy(next_x, next_y, stateObs))
			{
				movements.clear();
				path.clear();
				action_to_do = Types.ACTIONS.ACTION_NIL;
			}
		}

		if(path.isEmpty())
		{
			// System.out.println("HE LLEGADO AQUÍ 3");
			// System.out.println(getRemainingGems(stateObs));
			if(getRemainingGems(stateObs) > 0)
			{
				// System.out.println("HE LLEGADO AQUÍ 4");
				ArrayList<ArrayList<Node>> multiPaths = new ArrayList<ArrayList<Node>>();
				for (Observation gem: getGemsList(stateObs))
				{
					multiPaths.add(pathFinding(gem, stateObs));
				}

				boolean containsRockOnTop = false;
				boolean perfectPath = false;
				int indexPerfectPath = 0;
				for (int i = 0; i < multiPaths.size() && !perfectPath; i++)
				{
					for (int j = 0; j < multiPaths.get(i).size() && !containsRockOnTop; j++)
					{
						if(multiPaths.get(i).get(j).isRock_on_top())
							containsRockOnTop = true; // break
					}
					if(!containsRockOnTop)
					{
						perfectPath = true; // break
						indexPerfectPath = i;
					}
				}
				// System.out.println("HE LLEGADO AQUÍ 1");
				path = multiPaths.get(indexPerfectPath);
			}
		}
		else
		{
			// System.out.println("HE LLEGADO AQUÍ 2");
			path = pathFinding(getExit(stateObs), stateObs);
		}

		if(movements.isEmpty())
		{
			int x_player = getPlayer(stateObs).getX();
			int y_player = getPlayer(stateObs).getY();
			//////////////////////////////////// FIX ////////////////////////////////////////////////
			// System.out.println(path.size());
			/////////////////////////////////////////////////////////////////////////////////////////
			int x_box = path.get(0).getObs().getX();
			int y_box = path.get(0).getObs().getY();

			if (x_box < x_player)            // MOVE TO THE LEFT
			{
				if (stateObs.getAvatarOrientation() == new Vector2d(-1, 0))      // LOOKS TO THE LEFT
				{
					movements.add(Types.ACTIONS.ACTION_LEFT);
				}
                else if (stateObs.getAvatarOrientation() == new Vector2d(1, 0)) // LOOKS TO THE RIGHT
				{
					movements.add(Types.ACTIONS.ACTION_LEFT);
					movements.add(Types.ACTIONS.ACTION_LEFT);
				}
                else if (stateObs.getAvatarOrientation() == new Vector2d(0, -1)) // LOOKS TO THE TOP
				{
					movements.add(Types.ACTIONS.ACTION_LEFT);
					movements.add(Types.ACTIONS.ACTION_LEFT);
				}
                else if (stateObs.getAvatarOrientation() == new Vector2d(0, 1)) // LOOKS TO THE BOTTOM
				{
                    movements.add(Types.ACTIONS.ACTION_LEFT);
					movements.add(Types.ACTIONS.ACTION_LEFT);
				}
			}
            else if (x_box > x_player)        // MOVE TO THE RIGHT
			{
				if (stateObs.getAvatarOrientation() == new Vector2d(-1, 0))      // LOOKS TO THE LEFT
				{
                    movements.add(Types.ACTIONS.ACTION_RIGHT);
                    movements.add(Types.ACTIONS.ACTION_RIGHT);
				}
                else if (stateObs.getAvatarOrientation() == new Vector2d(1, 0)) // LOOKS TO THE RIGHT
				{
                    movements.add(Types.ACTIONS.ACTION_RIGHT);
				}
                else if (stateObs.getAvatarOrientation() == new Vector2d(0, -1)) // LOOKS TO THE TOP
				{
                    movements.add(Types.ACTIONS.ACTION_RIGHT);
                    movements.add(Types.ACTIONS.ACTION_RIGHT);
				}
                else if (stateObs.getAvatarOrientation() == new Vector2d(0, 1)) // LOOKS TO THE BOTTOM
				{
                    movements.add(Types.ACTIONS.ACTION_RIGHT);
                    movements.add(Types.ACTIONS.ACTION_RIGHT);
				}
			}
            else if (y_box < y_player)        // MOVE TO THE TOP
			{
				if (stateObs.getAvatarOrientation() == new Vector2d(-1, 0))      // LOOKS TO THE LEFT
				{
                    movements.add(Types.ACTIONS.ACTION_UP);
                    movements.add(Types.ACTIONS.ACTION_UP);
				}
                else if (stateObs.getAvatarOrientation() == new Vector2d(1, 0)) // LOOKS TO THE RIGHT
				{
                    movements.add(Types.ACTIONS.ACTION_UP);
                    movements.add(Types.ACTIONS.ACTION_UP);
				}
                else if (stateObs.getAvatarOrientation() == new Vector2d(0, -1)) // LOOKS TO THE TOP
				{
                    movements.add(Types.ACTIONS.ACTION_UP);
				}
                else if (stateObs.getAvatarOrientation() == new Vector2d(0, 1)) // LOOKS TO THE BOTTOM
				{
                    movements.add(Types.ACTIONS.ACTION_UP);
                    movements.add(Types.ACTIONS.ACTION_UP);
				}
			}
            else if (y_box > y_player)        // MOVE TO THE BOTTOM
			{
				if (stateObs.getAvatarOrientation() == new Vector2d(-1, 0))      // LOOKS TO THE LEFT
				{
                    movements.add(Types.ACTIONS.ACTION_DOWN);
                    movements.add(Types.ACTIONS.ACTION_DOWN);
				}
                else if (stateObs.getAvatarOrientation() == new Vector2d(1, 0)) // LOOKS TO THE RIGHT
				{
                    movements.add(Types.ACTIONS.ACTION_DOWN);
                    movements.add(Types.ACTIONS.ACTION_DOWN);
				}
                else if (stateObs.getAvatarOrientation() == new Vector2d(0, -1)) // LOOKS TO THE TOP
				{
                    movements.add(Types.ACTIONS.ACTION_DOWN);
                    movements.add(Types.ACTIONS.ACTION_DOWN);
				}
                else if (stateObs.getAvatarOrientation() == new Vector2d(0, 1)) // LOOKS TO THE BOTTOM
				{
                    movements.add(Types.ACTIONS.ACTION_DOWN);
				}
			}

            Types.ACTIONS last_action = movements.peek();

			for (int i = 1; i < path.size(); i++)
			{
				if(last_action == Types.ACTIONS.ACTION_DOWN)
				{
					y_player++;
				}
				else if(last_action == Types.ACTIONS.ACTION_UP)
				{
					y_player--;
				}
				else if(last_action == Types.ACTIONS.ACTION_LEFT)
				{
					x_player--;
				}
				else if(last_action == Types.ACTIONS.ACTION_RIGHT)
				{
					x_player++;
				}

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

		action_to_do = movements.poll();
		boolean keep_out = false;

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

			if(keep_out)
			{

			}
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
        for (int i = 0; i < max_x; i++) {
            for (int j = 0; j < max_y; j++) {
                Visited[i][j] = null;
            }
        }

        toVisit.add(n_inicial);

		boolean objectiveFound = false;
		while(!objectiveFound)
        {

        	//System.out.println(toVisit.size());
            Node n_current = toVisit.poll();
			//System.out.println(toVisit.size());

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
                if(x < max_x && Visited[x][y] == null)
                {
                    Node child = new Node(x, y, n_current.getG()+1, n_current, objective, nodesAdjacentToEnemy, enemies, stateObs);
                    toVisit.add(child);
					//System.out.println("a");
                    //n_current.addChildren(child);
                }
                else if(x < max_x && Visited[x][y] != null)
                {
                    Node child = new Node(x, y, n_current.getG()+1, n_current, objective, nodesAdjacentToEnemy, enemies, stateObs);
                    if(Visited[x][y].getF() > child.getF())
                    {
                        toVisit.add(child);
						//System.out.println("b");
                        //n_current.addChildren(child);
                    }
                }

                // TOP
                x = n_current.getObs().getX();
                y = n_current.getObs().getY()-1;
                if(y >= 0 && Visited[x][y] == null)
                {
                    Node child = new Node(x, y, n_current.getG()+1, n_current, objective, nodesAdjacentToEnemy, enemies, stateObs);
                    toVisit.add(child);
                    //n_current.addChildren(child);
                }
                else if(y >= 0 && Visited[x][y] != null)
                {
                    Node child = new Node(x, y, n_current.getG()+1, n_current, objective, nodesAdjacentToEnemy, enemies, stateObs);
                    if(Visited[x][y].getF() > child.getF())
                    {
                        toVisit.add(child);
                        //n_current.addChildren(child);
                    }
                }

                // LEFT
                x = n_current.getObs().getX()-1;
                y = n_current.getObs().getY();
                if(x >= 0 && Visited[x][y] == null)
                {
                    Node child = new Node(x, y, n_current.getG()+1, n_current, objective, nodesAdjacentToEnemy, enemies, stateObs);
                    toVisit.add(child);
                    //n_current.addChildren(child);
                }
                else if(x >= 0 && Visited[x][y] != null)
                {
                    Node child = new Node(x, y, n_current.getG()+1, n_current, objective, nodesAdjacentToEnemy, enemies, stateObs);
                    if(Visited[x][y].getF() > child.getF())
                    {
                        toVisit.add(child);
                        //n_current.addChildren(child);
                    }
                }

				// BOTTOM
                x = n_current.getObs().getX();
                y = n_current.getObs().getY()+1;
                if(y < max_y && Visited[x][y] == null)
                {
                    Node child = new Node(x, y, n_current.getG()+1, n_current, objective, nodesAdjacentToEnemy, enemies, stateObs);
                    toVisit.add(child);
                    //n_current.addChildren(child);
                }
                else if(y < max_y && Visited[x][y] != null)
                {
                    Node child = new Node(x, y, n_current.getG()+1, n_current, objective, nodesAdjacentToEnemy, enemies, stateObs);
                    if(Visited[x][y].getF() > child.getF())
                    {
                        toVisit.add(child);
                        //n_current.addChildren(child);
                    }
                }

            }
            if(Visited[n_current.getObs().getX()][n_current.getObs().getY()] != null)
            {
				if (Visited[n_current.getObs().getX()][n_current.getObs().getY()].getF() < n_current.getF())
					Visited[n_current.getObs().getX()][n_current.getObs().getY()] = n_current;
			}
            else
				Visited[n_current.getObs().getX()][n_current.getObs().getY()] = n_current;

			Scanner sc = new Scanner(System.in);
			//int n = sc.nextInt();
        }

		Deque<Node> stack = new ArrayDeque<Node>();
		Node aux = new Node(n_fin);

		while(aux.getParent() != null)
		{
			stack.push(aux);
			aux = aux.getParent();
		}

		while (!stack.isEmpty())
		{
			path.add(stack.pop());
		}
        return path;
    }
}
