package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Game;

import javax.swing.*;

public class Wall extends GameObj {
    public Wall(Game owner) {
        super(owner);
        this.hasCollision = true;
        this.textRepr = "Wall";
        this.imgRepr = new ImageIcon("assets/brick_wall.scaled.png");
    }
}
