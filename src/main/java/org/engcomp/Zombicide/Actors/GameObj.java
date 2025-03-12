package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.GridLoc;

public abstract class GameObj {
    protected boolean hasCollision = false;
    protected boolean hasRun = false;
    protected String textRepr = "abstract GameActor";

    public GridLoc getLoc() {
        return loc;
    }

    public void setLoc(GridLoc loc) {
        this.loc = loc;
    }

    protected GridLoc loc = null;
    public void run() {};

    @Override
    public String toString() {
        return this.textRepr;
    }
}
