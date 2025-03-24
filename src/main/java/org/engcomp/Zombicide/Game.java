package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.*;
import org.engcomp.Zombicide.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
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
    protected JScrollPane boardScrollPane;
    protected Sidebar sidebar;
    protected JSplitPane splitPane;
    protected GridBagLayout btnGridLayout;
    protected GameBoard board = null;
    protected CombatPanel combatPanel = null;
    protected DefaultListModel<String> combatLog;
    protected Random rand = new Random();
    protected List<ActorObj> actors = new ArrayList<>();
    protected boolean debug = false;
    protected GameStage stage;
    protected int turn;
    protected Timer animationTimer;
    protected Runnable gameEndCallback;

    protected URL mapUrl;
    protected int playerPerception;

    public Game(URL map, int playerPerception) {
        super("Zombicide");
        this.playerPerception = playerPerception;
        this.mapUrl = map;
        this.turn = 0;
        init();

    }
    private void init() {
        loadBtn = new JButton("load");
        Dimension windowDims = new Dimension(800, 800);
        setSize(windowDims);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            board = GameBoard.load(this, mapUrl);
            board.getPlayer().setPerception(playerPerception);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading map: " + e.getLocalizedMessage());
            throw new RuntimeException("Error loading map");
        }
        { // Setup board panel

            btnGridLayout = new GridBagLayout();
            var boardPanel = new JPanel(btnGridLayout);
            var c = new GridBagConstraints();
            boardScrollPane = new JScrollPane(boardPanel);
            for (var entry : getBoard().stream().toList()) {
                c.gridx = entry.idx.col;
                c.gridy = entry.idx.row;
                boardPanel.add(entry.val, c);
            }
            boardPanel.setLayout(btnGridLayout);
            boardPanel.setPreferredSize(new Dimension(80*getBoard().getCols(), 80*getBoard().getRows()));
            boardPanel.setVisible(true);
        }

        this.sidebar = new Sidebar(this);

        calcDistancesAndFog();
        updateBtns();
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, boardScrollPane);
        add(splitPane);

        combatLog = new DefaultListModel<>();
        combatLog.addElement("Game start!");
        revalidate();
        repaint();
        setVisible(true);
    }

    public void doStage() {
        updateBtns();
        switch (getStage()) {
            case GameStage.PLAYER_TURN: {
                calcDistancesAndFog();
                break;
            }
            case GameStage.PLAYER_ANIMATION: {
                //getBoard().getPlayer().animateLastInteraction();
                break;
            }
            case GameStage.AI_TURN: {
                for (ActorObj actor : actors) {
                    Interaction i = actor.run();
                    if (i == null) continue;
                    System.out.println("got interaction " + i);
                    i.run(this);
                }
            }
        }
    }

    public void doAllStages() {
        setStage(GameStage.PLAYER_TURN);
        doStage();
        setStage(GameStage.PLAYER_ANIMATION);
        doStage();
        setStage(GameStage.AI_TURN);
        doStage();
        setStage(GameStage.AI_ANIMATION);
        doStage();
    }

    public void finishTurn() {
        doAllStages();
        updateBtns();
        revalidate();
        repaint();
        turn++;
    }

    public void calcDistancesAndFog() {
        calcDistancesAndFog(board.getPlayer().getLoc());
    }
    public void calcDistancesAndFog(GridLoc startLoc) {
        assert startLoc != null;
        board.stream().forEach(loc -> loc.val.setPlayerDistance(Integer.MAX_VALUE));
        var setSizeHeuristic = board.getCols()*board.getRows();
        Set<GridLoc> visited = new HashSet<>(setSizeHeuristic);
        Queue<Pair<Pair<Integer,Boolean>, GridLoc>> queue = new ArrayDeque<>(setSizeHeuristic/10);
        queue.add(new Pair<>(new Pair<>(0, false), startLoc));

        while(!queue.isEmpty()) {
            var pair = queue.remove();
            int dist = pair.l.l;
            boolean fog = pair.l.r;
            GridLoc loc = pair.r;
            visited.add(loc);

            loc.setInFogOfWar(fog);
            if (loc != startLoc && loc.isSolid()) continue;
            loc.setPlayerDistance(dist);

            for (var neigh : board.getOrthogonals(loc)) {
                if (!visited.contains(neigh)) {
                    var nextFog = fog;
                    if(!fog) {
                        nextFog = board.getStraightPath(startLoc, neigh)
                                .anyMatch(GridLoc::isSolid);
                    }
                    queue.add(new Pair<>(new Pair<>(dist+1, nextFog), neigh));
                }
            }
        }
    }
    public void updateBtns() {
        board.stream().forEach(e -> {
            var target = e.val;
            var combatCond = combatPanel == null;
            var interactionCond = getBoard().getPlayer().canInteract(target);
            target.setEnabled(combatCond && interactionCond);
        });
    }

    public void combat(Zombie zed) {
        if (combatPanel != null) return;
        combatPanel = new CombatPanel(this, board.getPlayer(), zed);
        sidebar.setCombatView(combatPanel);
        updateBtns();
    }

    public void combatEnded(CombatPanel.CombatStage stage) {
        combatPanel = null;
        switch (stage) {
            case CombatPanel.CombatStage.PlayerDead -> gameOver();
            case CombatPanel.CombatStage.FoeDead -> {
                if (actors.size() == 1 && actors.getFirst() instanceof Player) {
                    gameWon();
                }
            }
        }
    }

    public void gameOver() {
        System.out.println("You died. Game over!");
        JOptionPane.showMessageDialog(this, "You died. Game Over!");
        gameEndCallback.run();
        setVisible(false);
    }
    public void gameWon() {
        System.out.println("You win! There are no zombies left.");
        JOptionPane.showMessageDialog(this, "You win! There are no zombies left.");
        if (gameEndCallback != null) gameEndCallback.run();
        setVisible(false);
    }

    public void chestEncounter(Chest c) {
        board.getPlayer().addItemToInventory(c.getItem());
    }

    public int getTurn() {
        return turn;
    }

    public GameStage getStage() {
        return stage;
    }

    public void setStage(GameStage stage) {
        this.stage = stage;
    }

    public GameBoard getBoard() {
        return board;
    }

    public Random getRand() {
        return rand;
    }

    public CombatPanel getCombat() {
        return combatPanel;
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
        loc.mutateOcuppants(occupants -> {
            occupants.remove(actor);
        });
    }

    public DefaultListModel<String> getCombatLog() {
        return combatLog;
    }

    public void setGameEndCallback(Runnable gameEndCallback) {
        this.gameEndCallback = gameEndCallback;
    }

    public void retry() {
        init();
    }

}
