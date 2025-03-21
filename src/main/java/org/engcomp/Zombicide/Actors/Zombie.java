package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Game;
import org.engcomp.Zombicide.GridLoc;
import org.engcomp.Zombicide.Interaction;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public abstract class Zombie extends ActorObj {
    private boolean alerted = true;
    private boolean inCombat = false;
    public Zombie(Game owner) {
        super(owner);
        this.hasRun = true;
        this.health = 2;
        this.textRepr = "abstract Zombie";
        this.imgRepr = new ImageIcon("assets/zombies/idle.gif");
    }

    @Override
    public Interaction run() {
        if (alerted) { return runTowardsPlayer(); }
        return null;
    }

    private Interaction runTowardsPlayer() {
        final java.util.List<Dimension> neighOffs = List.of(
                new Dimension(1,0),
                new Dimension(-1,0),
                new Dimension(0,1),
                new Dimension(0,-1)
        );
        List<GridLoc> neighs = new ArrayList<>(4);
        for (var neighOff : neighOffs) {
            var loc = getOwner()
                    .getBoard()
                    .get(getLoc().getCol() + neighOff.width,
                            getLoc().getRow() + neighOff.height);
            if (loc != null) {
                neighs.add(loc);
            }
        }
        PriorityQueue<Interaction> possInter = new PriorityQueue<>((l,r) -> {
            return -l.compareTo(r); // reverse ordering, from highest to lowest
        });
        Interaction.MoveToLoc desiredMove = null;
        for (var loc : neighs) {
            possInter.addAll(loc.possibleInteractions(this));
        }
        if (possInter.isEmpty()) return null;
        System.out.println("possibilities: " + possInter);
        var maxPrio = possInter.peek().getPriority();
        possInter.removeIf(i -> i.getPriority() != maxPrio);
        System.out.println("after culling: " + possInter);
        for (var i : possInter) {
            switch (i) {
                case Interaction.MoveToLoc move: {
                    var newDist = move.getTargetLoc().getPlayerDistance();
                    var oldDist = (desiredMove != null) ? desiredMove.getTargetLoc().getPlayerDistance() : Integer.MAX_VALUE;
                    if (newDist < oldDist) {
                        desiredMove = move;
                    }
                    break;
                }
                case Interaction.EnterCombat combat:
                    return combat;
                default: break;
            }
             //neighs.get(getOwner().getRand().nextInt(neighs.size())));
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
