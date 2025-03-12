package org.engcomp.Zombicide.Actors;

public abstract class Zombie extends ActorObj {
    public Zombie() {
        super();
        this.hasCollision = true;
        this.hasRun = true;
        this.health = 2;
        this.textRepr = "abstract Zombie";
    }

    @Override
    public void run() {

    }
}
