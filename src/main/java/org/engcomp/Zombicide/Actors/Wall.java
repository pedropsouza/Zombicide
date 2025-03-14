package org.engcomp.Zombicide.Actors;

import javax.swing.*;

public class Wall extends GameObj {
    public Wall() {
        super();
        this.hasCollision = true;
        this.textRepr = "Wall";
        this.imgRepr = new ImageIcon("assets/brick_wall.scaled.png");
    }
}
