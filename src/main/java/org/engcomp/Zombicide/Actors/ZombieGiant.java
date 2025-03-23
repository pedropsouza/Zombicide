package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Game;
import org.engcomp.Zombicide.Interaction;

public class ZombieGiant extends Zombie {
    public ZombieGiant(Game owner) {
        super(owner);
        this.textRepr = "Giant Zombie";
    }

    @Override
    protected boolean interactionFilter(Interaction i) {
        return i instanceof Interaction.EnterCombat;
    }
}
