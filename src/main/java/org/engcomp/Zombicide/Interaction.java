package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.*;

import java.util.List;

public abstract class Interaction implements Comparable<Interaction> {
    protected int priority = 0; // Higher is more important
    protected Interaction(int priority) { this.priority = priority; }
    public int getPriority() { return this.priority; }
    public abstract void run(Game game);

    @Override
    public String toString() {
        return "Generic Interaction";
    }

    @Override
    public int compareTo(Interaction interaction) {
        return Integer.compare(this.priority, interaction.priority);
    }

    public static class MoveToLoc extends Interaction {
        private final ActorObj actor;
        private final GridLoc loc;
        public MoveToLoc(ActorObj actor, GridLoc loc) {
            super(0);
            this.actor = actor;
            this.loc = loc;
        }

        @Override
        public void run(Game game) {
            actor.moveTo(loc);
        }

        public ActorObj getActor() { return actor; }
        public GridLoc getTargetLoc() {
            return loc;
        }

        @Override
        public String toString() {
            return "Move to Location " + loc.getCol() + "," + loc.getRow();
        }
    }

    public static final class Flee extends MoveToLoc {
        public Flee(Player p, GridLoc loc) {
            super(p, loc);
        }
    }

    public static final class EnterCombat extends Interaction {
        private final ActorObj attacker;
        private final ActorObj victim;
        public EnterCombat(ActorObj attacker, ActorObj victim) {
            super(2);
            this.attacker = attacker;
            this.victim = victim;
        }

        @Override
        public void run(Game game) {
            if (attacker instanceof Zombie) {
                game.combat((Zombie) attacker, false);
            } else {
                game.combat((Zombie) victim, true);
            }
        }

        @Override
        public String toString() {
            return "Enter combat between attacker " + attacker + " and victim " + victim;
        }
    }

    public static final class OpenChest extends Interaction {
        private final Chest chest;
        public OpenChest(Chest chest) {
            super(1);
            this.chest = chest;
        }

        @Override
        public void run(Game game) {
            var board = game.getBoard();
            var player = board.getPlayer();
            var chest = getChest();
            player.moveTo(chest.getLoc());
            chest.getLoc().mutateOcuppants(occupants -> {
                occupants.remove(chest);
            });
            game.chestEncounter(chest);
        }

        public Chest getChest() {
            return this.chest;
        }

        @Override
        public String toString() {
            return "Open chest " + getChest();
        }
    }
}
