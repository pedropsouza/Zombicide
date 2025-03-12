package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.GridLoc;

public abstract class ActorObj extends GameObj {
    protected boolean hasCollision = true;
    protected boolean hasRun = true;
    protected int health = 5;
    ///  how many cells can this actor move per tick
    protected int speed = 1;
    protected int perception = 1;
    protected int concealment = 0;

    public boolean insideMoveDist(GridLoc loc) {
        return this.loc.taxiCabDistance(loc) <= this.speed;
    }
    public boolean canMove(GridLoc loc) {
        var occ = loc.getOccupant();
        var occupied = occ != null;
        var isFloor = occupied && occ instanceof Floor;
        return (!occupied | isFloor) && insideMoveDist(loc);
    }

    public boolean canInteract(GridLoc loc) {
        var occ = loc.getOccupant();
        var occupied = occ != null;
        var isChest = occupied && occ instanceof Chest;
        var isZombie = occupied && occ instanceof Zombie;
        boolean v = isChest || isZombie;
        return canMove(loc) || (v && insideMoveDist(loc));
    }
}
