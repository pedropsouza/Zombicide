package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Game;
import org.engcomp.Zombicide.Interaction;

public class ZombieRegular extends Zombie {
    public ZombieRegular(Game owner) {
        super(owner);
        this.textRepr = "Zombie";
    }

    @Override
    public Interaction run() {
        return super.run();
    }
}
