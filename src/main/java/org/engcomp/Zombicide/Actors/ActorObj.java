package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Game;
import org.engcomp.Zombicide.GridLoc;

public abstract class ActorObj extends GameObj {
    protected int health = 5;
    ///  how many cells can this actor move per tick
    protected int speed = 1;
    protected int perception = 1;
    protected int concealment = 0;

    public ActorObj(Game owner) {
        super(owner);
        this.hasCollision = true;
        owner.getActors().add(this);
    }

    public boolean insideMoveDist(GridLoc loc) {
        return this.loc.taxiCabDistance(loc) <= this.speed;
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

    public boolean isDead() {
        return getHealth() <= 0;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
