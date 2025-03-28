package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Damage;
import org.engcomp.Zombicide.Game;
import org.engcomp.Zombicide.GridLoc;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public abstract class ActorObj extends GameObj {
    private static int serialNumCounter = 0;
    private final int serialNum;
    protected int health = 5;
    ///  how many cells can this actor move per tick
    protected int speed = 1;
    protected int perception = 1;
    protected int concealment = 0;
    private boolean inCombat = false;
    protected Set<Damage> negatedDamages = new HashSet<>();

    protected Set<Consumer<ActorObj>> changedCallbacks = new HashSet<>();

    public ActorObj(Game game) {
        super(game);
        this.serialNum = serialNumCounter++;
        this.hasCollision = true;
        game.getActors().add(this);
    }

    ///  Returns the damage dealt
    public int dealDamage(Damage d) {
        if (negatedDamages.contains(d)) { return 0; }
        var prev = getHealth();
        setHealth(prev - d.strength);
        return d.strength;
    }

    public int getPerception() {
        return perception;
    }

    public void setPerception(int perception) {
        this.perception = perception;
        reportChange();
    }

    public int getConcealment() {
        return concealment;
    }

    public void setConcealment(int concealment) {
        this.concealment = concealment;
        reportChange();
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
        reportChange();
    }

    public boolean isDead() {
        return getHealth() <= 0;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        reportChange();
    }

    protected int getSerialNum() {
        return serialNum;
    }

    public boolean isInCombat() {
        return inCombat;
    }

    public void setInCombat(boolean inCombat) {
        this.inCombat = inCombat;
        reportChange();
    }

    public void addChangedCallback(Consumer<ActorObj> callback) {
        changedCallbacks.add(callback);
    }

    public void removeChangedCallback(Consumer<ActorObj> callback) {
        changedCallbacks.remove(callback);
    }

    public void reportChange() {
        changedCallbacks.forEach(c -> c.accept(this));
    }

    @Override
    public String toString() {
        return super.toString() + " #" + getSerialNum();
    }

    public void moveTo(GridLoc loc) {
        var board = getGame().getBoard();
        var prevLoc = getLoc();
        prevLoc.mutateOcuppants(occupants -> {
            occupants.remove(this);
        });
        loc.mutateOcuppants(occupants -> {
            occupants.add(this);
        });
    }
}
