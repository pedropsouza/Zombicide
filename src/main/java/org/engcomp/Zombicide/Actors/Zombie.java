package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Game;
import org.engcomp.Zombicide.GridLoc;
import org.engcomp.Zombicide.Interaction;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public abstract class Zombie extends ActorObj {
    private boolean alerted = true;
    private boolean inCombat = false;
    public Zombie(Game owner) {
        super(owner);
        this.hasRun = true;
        this.health = 2;
        this.textRepr = "abstract Zombie";
        this.imgRepr = new ImageIcon(Objects.requireNonNull(getClass().getResource("Zombie/idle.gif")));
    }

    @Override
    public Interaction run() {
        if (isInCombat()) { return null; }
        var inters = possibleInteractions();
        if (alerted) {
            if (inters.peek() instanceof Interaction.EnterCombat) {
                return inters.remove();
            }
            return runTowardsPlayer(inters);
        } else {
            // random move?
        }
        return null;
    }

    private PriorityQueue<Interaction> possibleInteractions() {
        Set<GridLoc> neighs = getOwner().getBoard().getOrthogonals(getLoc());
        PriorityQueue<Interaction> inters = new PriorityQueue<>((l,r) -> {
            return -l.compareTo(r); // reverse ordering, from highest to lowest
        });
        for (var neigh : neighs) {
            inters.addAll(
                    neigh.possibleInteractions(this)
                            .stream()
                            .filter(this::interactionFilter)
                            .toList()
            );
        }
        System.out.println("possible interactions for " + this + ": " + inters);
        return inters;
    }

    /// Used to restrict movement for the Giant
    protected boolean interactionFilter(Interaction i) {
        return true;
    }

    private Interaction runTowardsPlayer(PriorityQueue<Interaction> inters) {
        Interaction.MoveToLoc desiredMove = null;
        for (var move : inters.stream().flatMap(i ->
            (i instanceof Interaction.MoveToLoc)? Stream.of((Interaction.MoveToLoc)i) : Stream.empty()
        ).toList()) {
            var newDist = move.getTargetLoc().getPlayerDistance();
            var oldDist = (desiredMove != null) ? desiredMove.getTargetLoc().getPlayerDistance() : Integer.MAX_VALUE;
            if (newDist < oldDist) {
                desiredMove = move;
            }
        }
        System.out.println(this + " @ loc " + getLoc() + " wants to " + desiredMove);
        return desiredMove;
    }

    public boolean isInCombat() {
        return inCombat;
    }

    public void setInCombat(boolean inCombat) {
        this.inCombat = inCombat;
    }
}
