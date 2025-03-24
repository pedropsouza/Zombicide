package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Damage;
import org.engcomp.Zombicide.Game;
import org.engcomp.Zombicide.GridLoc;
import org.engcomp.Zombicide.Interaction;
import org.engcomp.Zombicide.utils.Pair;

import javax.swing.*;
import java.util.*;
import java.util.stream.Stream;

public abstract class Zombie extends ActorObj {
    private boolean alerted = true;
    protected Damage attackDamage = Damage.BareHand;

    public Zombie(Game game) {
        super(game);
        this.hasRun = true;
        this.health = 2;
        this.textRepr = "abstract Zombie";
        this.imgRepr = new ImageIcon(Objects.requireNonNull(getClass().getResource("Zombie/idle.gif")));
        this.negatedDamages.add(Damage.BareHand);
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
        PriorityQueue<Interaction> inters = new PriorityQueue<>((l,r) -> {
            return -l.compareTo(r); // reverse ordering, from highest to lowest
        });

        var board = getGame().getBoard();
        var sizeHeuristic = board.getCols()*board.getRows();
        Set<GridLoc> neighs = new HashSet<>(sizeHeuristic);
        Queue<Pair<Integer, GridLoc>> queue = new ArrayDeque<>(sizeHeuristic/10);
        queue.add(new Pair<>(0, getLoc()));

        GridLoc ourLoc = getLoc();
        while(!queue.isEmpty()) {
            var pair = queue.remove();
            int dist = pair.l;
            GridLoc loc = pair.r;
            // stop one step before our limit, since we add neighbouring
            // locations which will probably be +1 dist
            var occupiedByNonPlayer = loc != ourLoc && loc.isExclusivelyOccupied() && !loc.isPlayerHere();
            if (dist > speed || occupiedByNonPlayer) continue;
            neighs.add(loc);
            for (var neigh : board.getOrthogonals(loc)) {
                if (!neighs.contains(neigh)) {
                    queue.add(new Pair<>(dist + 1, neigh));
                }
            }
        }
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
        return desiredMove;
    }

    public Damage getAttackDamage() {
        return attackDamage;
    }
}
