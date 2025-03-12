package org.engcomp.Zombicide.Actors;

public class Wall extends GameObj {
    public Wall() {
        super();
        this.hasCollision = true;
        this.textRepr = "Wall";
    }
}
