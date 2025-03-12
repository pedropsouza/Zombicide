package org.engcomp.Zombicide.Actors;

public class Wall extends GameActor {
    public Wall() {
        super();
        this.hasCollision = true;
        this.textRepr = "Wall";
    }
}
