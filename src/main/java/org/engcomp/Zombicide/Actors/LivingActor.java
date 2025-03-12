package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.GridLoc;

public abstract class LivingActor extends GameActor {
    protected boolean hasCollision = true;
    protected boolean hasRun = true;
    protected int health = 5;
    ///  how many cells can this actor move per tick
    protected int speed = 1;
    protected int perception = 1;
    protected int concealment = 0;

    public boolean canMove(GridLoc loc) {
        var occupied = loc.getOccupant() != null;
        var distanceTaxicab = this.loc.taxiCabDistance(loc);
        return !occupied && distanceTaxicab <= speed;
    }
}
