package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Game;
import org.engcomp.Zombicide.Interaction;

public class ZombieRunner extends Zombie {
    public ZombieRunner(Game game) {
        super(game);
        this.textRepr = "Runner Zombie";
        this.speed = 2;
    }
}
