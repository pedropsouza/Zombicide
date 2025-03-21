package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class Game extends JFrame {
    public enum GameStage {
        PLAYER_TURN,
        PLAYER_ANIMATION,
        AI_TURN,
        AI_ANIMATION
    }
    protected JButton loadBtn;
    protected GridLayout btnGridLayout;
    protected GameBoard board = null;
    protected CombatWin combatWin = null;
    protected Random rand = new Random();
    protected List<ActorObj> actors = new ArrayList<>();
    protected boolean debug = false;
    protected GameStage stage;
    protected Timer animationTimer;

    public Game(String map, int playerPerception) {
        super("Zombicide");
        loadBtn = new JButton("load");
        setSize(800, 800);
        setMaximumSize(new Dimension(800, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        var path = FileSystems.getDefault().getPath(".", map);
        board = GameBoard.load(this, path);
        board.stream().forEach(e -> add(e.val));
        board.getPlayer().setPerception(playerPerception);
        System.out.println(board);
        btnGridLayout = new GridLayout(board.getRows(), board.getCols());
        setLayout(btnGridLayout);
        calcDistances();
        updateBtns();
        revalidate();
        repaint();
        setVisible(true);
    }

    public void finishTurn() {
        calcDistances(board.getPlayer().getLoc());
        for (ActorObj actor : actors) {
            Interaction i = actor.run();
            if (i == null) continue;
            System.out.println("got interaction " + i);
            i.run(this);
        }
        updateBtns();
        revalidate();
        repaint();
    }

    public void calcDistances() {
        calcDistances(board.getPlayer().getLoc());
    }
    public void calcDistances(GridLoc startLoc) {
        assert startLoc != null;
        board.stream().forEach(loc -> loc.val.setPlayerDistance(Integer.MAX_VALUE));
        final List<Dimension> neighOffs = List.of(
                new Dimension(1,0),
                new Dimension(-1,0),
                new Dimension(0,1),
                new Dimension(0,-1)
        );

        var setSizeHeuristic = board.getCols()*board.getRows();
        Set<GridLoc> visited = new HashSet<>(setSizeHeuristic);
        Queue<Pair<Integer, GridLoc>> queue = new ArrayDeque<>(setSizeHeuristic/10);
        queue.add(new Pair<>(0, startLoc));

        while(!queue.isEmpty()) {
            var pair = queue.remove();
            int dist = pair.l;
            GridLoc loc = pair.r;
            visited.add(loc);

            if (loc != startLoc && loc.occupants.stream().anyMatch(GameObj::hasCollision)) continue;
            loc.setPlayerDistance(dist);

            for (var neighOff : neighOffs) {
                var neighLoc = this.board.get(
                        loc.getCol() + neighOff.width,
                        loc.getRow() + neighOff.height
                );
                if (neighLoc == null) continue;
                if (!visited.contains(neighLoc)) {
                    queue.add(new Pair<>(dist+1, neighLoc));
                }
            }
        }
    }
    public void updateBtns() {
        board.stream().forEach(e -> {
            var target = e.val;
            target.setEnabled(getBoard().getPlayer().canInteract(target));
        });
    }

    public void combat(Zombie zed) {
        if (combatWin != null) return;
        combatWin = new CombatWin(this, board.getPlayer(), zed);
    }

    public void combatEnded(CombatWin.CombatStage stage) {
        combatWin = null;
        switch (stage) {
            case CombatWin.CombatStage.PlayerDead -> gameOver();
        }
    }

    public void gameOver() {
        System.out.println("You died. Game over!");
        JOptionPane.showMessageDialog(this, "You died. Game Over!");
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void chestEncounter(Chest c) {
        board.getPlayer().addItemToInventory(c.getItem());
    }

    public GameBoard getBoard() {
        return board;
    }

    public Random getRand() {
        return rand;
    }

    public List<ActorObj> getActors() {
        return actors;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void removeActor(ActorObj actor) {
        this.actors = actors.stream().filter(a -> a != actor).toList();
        var loc = actor.getLoc();
        loc.setOccupants(loc.getOccupants().stream().filter(occ -> occ != actor).toList());
    }
}
