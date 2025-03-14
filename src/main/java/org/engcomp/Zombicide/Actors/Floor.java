package org.engcomp.Zombicide.Actors;

import javax.swing.*;

public class Floor extends GameObj {
    public Floor() {
        super();
        this.hasCollision = false;
        this.textRepr = "Floor";
        this.imgRepr = new ImageIcon("assets/cement_floor.scaled.png");
    }
}
