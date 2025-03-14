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
        var occupants = loc.getOccupants();
        var occupied = !occupants.isEmpty();
        var anyHasCollision = occupied && occupants.stream().anyMatch(o -> o.hasCollision);
        return (!occupied | !anyHasCollision) && insideMoveDist(loc);
    }

    public boolean canInteract(GridLoc loc) {
        var occupants = loc.getOccupants();
        var occupied = !occupants.isEmpty();
        var hasChest = occupied && occupants.stream().anyMatch(o -> o instanceof Chest);
        var hasZombie = occupied && occupants.stream().anyMatch(o -> o instanceof Zombie);
        boolean v = hasZombie || hasChest;
        return canMove(loc) || (v && insideMoveDist(loc));
    }

    public int getPerception() {
        return perception;
    }

    public void setPerception(int perception) {
        this.perception = perception;
    }

    public int getConcealment() {
        return concealment;
    }

    public void setConcealment(int concealment) {
        this.concealment = concealment;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
