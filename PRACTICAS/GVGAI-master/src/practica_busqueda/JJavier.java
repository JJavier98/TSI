package practica_busqueda;

import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Random;
import tools.Vector2d;

/*  Agente de prueba que usa los métodos de BaseAgent para obtener información
    sobre el entorno y asigna varias probabilidades en función de esta información
    a las posibles acciones.
*/
public class JJavier extends BaseAgent{
    private ArrayList<Types.ACTIONS> lista_acciones; // Conjunto de acciones posibles
    private Random generador;

    public JJavier(StateObservation stateObservation, ElapsedCpuTimer elapsedTimer){
        super(stateObservation, elapsedTimer);
        generador = new Random();

        lista_acciones = new ArrayList();
        lista_acciones.add(Types.ACTIONS.ACTION_UP);
        lista_acciones.add(Types.ACTIONS.ACTION_DOWN);
        lista_acciones.add(Types.ACTIONS.ACTION_RIGHT);
        lista_acciones.add(Types.ACTIONS.ACTION_LEFT);
    }

    public int getBoxType(int x, int y, StateObservation stateObs)
    {
        return stateObs.getObservationGrid()[x][y].get(0).itype;
    }

    public ArrayList<Observation> getBoxesFreeEnemies(StateObservation stateObs)
    {
        ArrayList<Vector2d> boxesFreeEnemies = new ArrayList<Vector2d>();
        ArrayList<Observation>[] enemies = getEnemiesList(stateObs);

        for (ArrayList<Observation> enem : enemies)
        {
            int x = enem.get(1).getX();
            int y = enem.get(1).getY();

            Node ini_node = new Node(x,y,0,0,null);
        }

        System.out.println(enemies[0].get(1));
        System.out.println(enemies[1].get(1));
        //System.out.println(stateObs.getObservationGrid()[1][1].get(0).obsID);
        return  enemies[0];
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){

        ArrayList<Observation> muro = getWallsList(stateObs);
        //System.out.println(getPlayer(stateObs).getManhattanDistance(getExit(stateObs)));
        getBoxesFreeEnemies(stateObs);

        return Types.ACTIONS.ACTION_NIL;
    }
}
