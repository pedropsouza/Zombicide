package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Game;

import javax.swing.*;

public class Floor extends GameObj {
    public Floor(Game owner) {
        super(owner);
        this.hasCollision = false;
        this.textRepr = "Floor";
        this.imgRepr = new ImageIcon("assets/cement_floor.scaled.png");
    }
}
