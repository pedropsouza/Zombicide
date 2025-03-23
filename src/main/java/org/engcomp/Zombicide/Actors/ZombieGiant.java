package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Damage;
import org.engcomp.Zombicide.Game;
import org.engcomp.Zombicide.Interaction;

import java.util.List;

public class ZombieGiant extends Zombie {
    public ZombieGiant(Game game) {
        super(game);
        this.textRepr = "Giant Zombie";
        this.attackDamage = Damage.Critical;
        this.health = 3;
        this.negatedDamages.addAll(List.of(Damage.BareHand, Damage.Blunt, Damage.Critical));
    }

    @Override
    protected boolean interactionFilter(Interaction i) {
        return i instanceof Interaction.EnterCombat;
    }
}
