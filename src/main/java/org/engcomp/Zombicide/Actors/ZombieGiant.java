package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Game;
import org.engcomp.Zombicide.Interaction;

public class ZombieGiant extends Zombie {
    public ZombieGiant(Game game) {
        super(game);
        this.textRepr = "Giant Zombie";
        this.attackStrength = 2;
        this.health = 3;
    }

    @Override
    protected boolean interactionFilter(Interaction i) {
        return i instanceof Interaction.EnterCombat;
    }
}
