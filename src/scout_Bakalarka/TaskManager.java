package scout_Bakalarka;

import sk.hackcraft.bwu.Game;
import sk.hackcraft.bwu.Graphics;
import sk.hackcraft.bwu.Vector2D;

import java.util.ArrayList;

/**
 * TaskManager manages given tasks depending on task priority and assigns available scouting units to them.
 */
public class TaskManager {

    private ArrayList<Task> tasks;


    /* ------------------- Constructors ------------------- */

    public TaskManager() {
        tasks=new ArrayList<>();
    }


    /* ------------------- Real-time management methods ------------------- */

    public void manageAll(ActionManager pActionManager, MapManager pMapManager, Game pGame) {
        manageTasks(pActionManager, pMapManager, pGame);
    }

    public void manageTasks(ActionManager pActionManager, MapManager pMapManager, Game pGame) {
        for(Task t:tasks) {
            if(t.getState()==Task.FINISHED) {
                if(!t.isPermanent()) {
                    tasks.remove(t);
                } else {
                    t.setPriority(0);
                    t.setScoutingUnit(null);
                    t.setTimeLimit(-1);
                    t.setState(Task.WAITING);
                }
            } else if(t.getState()==Task.INPROGRESS&&t.getTimeLimit()==0) {
//                t.setState(Task.WAITING);
//
//                pActionManager.returnHome(t.getScoutingUnit(),pMapManager,pGame);
//
//                t.setScoutingUnit(null);
//                t.setPriority(10);
//                t.setTimeLimit(-1);
            }
            t.manageAll(pActionManager, pMapManager, pGame);
        }
    }

    public void manageTaskAssigning(UnitManager pUnitManager, MapManager pMapManager) {
        if(pUnitManager.getScoutingUnits().size()>0&&tasks.size()>0) {
            Task t=getHighestPriorityTask();
            ScoutingUnit bestUnit;
            if(t!=null) {
                if(t.getPriority()>=t.getPriorityLevel()) {
                    bestUnit=pUnitManager.getClosestScoutingUnit(t, pMapManager, true);
                    if(bestUnit.hasOrder()) {
                        for(Task tsk: tasks) {
                            if(tsk.getScoutingUnit()==bestUnit) {
                                if(t.getPriority()>tsk.getPriority()) {
                                    tsk.getScoutingUnit().setHasOrder(false);
                                    tsk.getScoutingUnit().setPath(new ArrayList<>());
                                    assignUnitToTask(t, bestUnit);


                                    tsk.setScoutingUnit(null);
                                    tsk.setTimeLimit(-1);
                                    tsk.setState(Task.WAITING);
                                    return;
                                }
                            }
                        }
                    } else {
                        assignUnitToTask(t,bestUnit);
                    }
                } else if(t.getPriority()>15){
                    assignBestUnit(t,pUnitManager,pMapManager,false);
                }
            }
        }
    }


    /* ------------------- Top-level methods ------------------- */

    public void assignBestUnit(Task pTask, UnitManager pUnitManager, MapManager pMapManager, boolean pInterrupt) {
        ScoutingUnit sc = pUnitManager.getClosestScoutingUnit(pTask,pMapManager,pInterrupt);
        if(sc!=null) {
            assignUnitToTask(pTask,sc);
        }
    }

    public void assignUnitToTask(Task pTask, ScoutingUnit pScoutingUnit) {
        if(pScoutingUnit!=null) {
            pTask.setScoutingUnit(pScoutingUnit);
        }
    }

    public Task getHighestPriorityTask() {
            if(tasks.size()>0) {
            double priority=0;
            int index=-1;
            for(int i=0;i<tasks.size();i++) {
                if(tasks.get(i).getState()==Task.WAITING||tasks.get(i).getState()==Task.FAILED) {
                    if(tasks.get(i).getPriority()>priority) {
                        priority=tasks.get(i).getPriority();
                        index=i;
                    }
                }
            }
            if(index!=-1&&tasks.get(index).getState()!=Task.INPROGRESS) {
                return tasks.get(index);
            }
        }
        return null;
    }


    /* ------------------- Data structure operation methods ------------------- */

    public void addTask(Task pTask) {
        tasks.add(pTask);
    }

    public void removeTask(Task pTask) {
        tasks.remove(pTask);
    }


    /* ------------------- Drawing functions ------------------- */

    public void drawTaskList(Graphics pGraphics) {
        int z=110;
        pGraphics.drawText(new Vector2D(450, 10), "--- Task list ---");
        if(tasks.size()>0) {
            for(int i=0;i<tasks.size();i++) {
                pGraphics.drawText(new Vector2D(450, 25+(i*z)), (i+1)+". "+tasks.get(i).toString());
            }
        }
    }


    /* ------------------- Getters and setters ------------------- */

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

}
