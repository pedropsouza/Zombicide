package org.engcomp.Zombicide.Actors;

import javax.swing.*;

public abstract class Zombie extends ActorObj {
    public Zombie() {
        super();
        this.hasCollision = true;
        this.hasRun = true;
        this.health = 2;
        this.textRepr = "abstract Zombie";
        this.imgRepr = new ImageIcon("assets/zombies/idle.gif");
    }

    @Override
    public void run() {

    }
}
