package scout_Bakalarka;

import sk.hackcraft.bwu.Game;

/**
 * Task represents exact scouting task specified by actionID (see ActionManager). Realization of task is completely
 * independent and automatic.
 */
public class Task {

    public static final int WAITING=0;
    public static final int INPROGRESS=1;
    public static final int FAILED=2;
    public static final int FINISHED=3;

    private double priority;

    private int priorityLevel;

    private int timeLimit=-1;

    private int state;

    private int actionID;

    private int safetyLevel;

    private boolean permanent;

    private ScoutingUnit scoutingUnit;


    /* ------------------- Constructors ------------------- */

    public Task(int pStartingPriority,int pPriorityLevel, int pActionID, boolean pPermanent) {
        this.priority=pStartingPriority;
        this.priorityLevel=pPriorityLevel;
        this.actionID=pActionID;
        this.safetyLevel=ScoutAI.SAFETY_LEVEL;
        this.permanent=pPermanent;
        state=Task.WAITING;
    }


    /* ------------------- Real-time management ------------------- */

    public void manageAll(ActionManager pActionManager, MapManager pMapManager, Game pGame) {
        increaseActualPriority();
        timeLimitSetup();
        manageTimeLimit(pGame);
        manageTaskRealization(pActionManager, pMapManager, pGame);
    }

    public void manageTaskRealization(ActionManager pActionManager, MapManager pMapManager, Game pGame) {
        if(scoutingUnit!=null&&!scoutingUnit.getUnit().isExists()) {
            state=Task.FAILED;
            timeLimit=-1;
            scoutingUnit=null;
        }
        if((state==Task.WAITING||state==Task.FAILED)&&scoutingUnit!=null) {
            state = Task.INPROGRESS;
            pActionManager.startAction(this, pMapManager, pGame);
        } else if(state==Task.INPROGRESS&&timeLimit==0) {
            pActionManager.returnHome(scoutingUnit,pMapManager,pGame);
            scoutingUnit=null;
            timeLimit=-1;
            state=Task.WAITING;
        } else if(state==Task.INPROGRESS&&scoutingUnit!=null) {
            if(actionID!=7) {
                if (scoutingUnit.isFinishedOrder()) {
                    state = Task.FINISHED;
                    pActionManager.returnHome(scoutingUnit,pMapManager,pGame);
                    scoutingUnit=null;
                    timeLimit=-1;
                }
            } else {
                if(scoutingUnit.getScoutingArea().size()==0) {
                    state = Task.FINISHED;
                    pActionManager.returnHome(scoutingUnit,pMapManager,pGame);
                    priority=0;
                    scoutingUnit=null;
                    timeLimit=-1;
                }
            }
        }
    }

    public void manageTimeLimit(Game pGame) {
        if(pGame.getFrameCount()%35==0) {
            if(timeLimit>0) {
                timeLimit--;
            }
        }
    }


    /* ------------------- Main functionality methods ------------------- */

    public void increaseActualPriority() {
        if(priority<priorityLevel) {
            priority+=1/(double)100;
        }
    }

    public String toString() {
        String act="";
        switch (actionID) {
            case 0:act="SCOUT_BASE";
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6: act="SCOUT_EXPAND";
                break;
            case 7: act="SCOUT_ARMY";
                break;
            case 8: act="RETURN_HOME";
                break;
        }
        String s="";
        s+="<Task>"+"\n";
        s+="   Action         = "+act+"\n";
        s+="   Act. priority  = "+String.format("%.3g%n", getPriority());
        s+="   Priority level = "+getPriorityLevel()+"\n";
        s+="   Reserved time =  "+getTimeLimit()+"\n";
        switch (state) {
            case 0: s+="   Task state     = Waiting \n";
                break;
            case 1: s+="   Task state     = In progress \n";
                break;
            case 2: s+="   Task state     = Failed \n";
                break;
            case 3: s+="   Task state     = Finished \n";
                break;
        }
        if(getScoutingUnit()!=null) {
            s+="   Assigned unit  = "+getScoutingUnit().getUnit().getType().getName()+"\n";
        } else {
            s+="   Assigned unit  = NONE \n";
        }

        s+="   Permanent      = "+isPermanent()+"\n";
        return s;
    }

    public void timeLimitSetup() {
        if(timeLimit==-1&&scoutingUnit!=null&&scoutingUnit.getPath().size()>0) {
            timeLimit=scoutingUnit.getPath().size()/4;
        }
    }


    /* ------------------- Getters and setters ------------------- */

    public double getPriority() {
        return priority;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public ScoutingUnit getScoutingUnit() {
        return scoutingUnit;
    }

    public void setScoutingUnit(ScoutingUnit pScoutingUnit) {
        this.scoutingUnit = pScoutingUnit;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getSafetyLevel() {
        return safetyLevel;
    }

    public void setSafetyLevel(int safetyLevel) {
        this.safetyLevel = safetyLevel;
    }

    public int getActionID() {
        return actionID;
    }

    public void setActionID(int actionID) {
        this.actionID = actionID;
    }
}
