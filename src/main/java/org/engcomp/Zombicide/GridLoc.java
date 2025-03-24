package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

// https://stackoverflow.com/a/46360964
public class GridLoc extends JLayeredPane {
    protected Game game;
    protected int col;
    protected int row;
    protected int playerDistance = 0;
    protected ArrayList<GameObj> occupants = new ArrayList<>();
    protected boolean targeted = false;
    protected boolean solid;
    protected boolean exclusivelyOccupied;
    protected boolean playerHere;
    protected boolean inFogOfWar = false;
    protected ImageIcon targetedOverlay = new ImageIcon(Objects.requireNonNull(getClass().getResource("target.png")));

    public GridLoc(Game game, ArrayList<GameObj> occupants, int col, int row) {
        super();
        this.setOccupants(occupants); this.col = col; this.row = row;
        var dims = new Dimension(80, 80);
        setSize(dims);
        setPreferredSize(dims);
        setOpaque(false);
        this.game = game;
    }

    public int getCol() {
        return col;
    }
    public int getRow() {
        return row;
    }

    public int taxiCabDistance(GridLoc loc) {
        return Math.abs(loc.getCol() - this.col) + Math.abs(loc.getRow() - this.row);
    }

    private void setOccupants(ArrayList<GameObj> occupants) {
        this.occupants = occupants;
        rebuild();
    }

    private void rebuild() {
        this.playerHere = computeIsPlayerHere();
        this.exclusivelyOccupied = computeIsExclusivelyOccupied();
        this.solid = computeIsSolid();
        this.occupants.forEach(o -> o.setLoc(this));
        removeAll();
        for (int i = 0; i < occupants.size(); i++) {
            var occupant = occupants.get(i);
            assert occupant != null;
            var imgRepr = occupant.getImgRepr();
            var txtRepr = occupant.toString();
            var label = (imgRepr != null)?
                    new JLabel(imgRepr)
                    : new JLabel(txtRepr);
            label.setVisible(true);
            label.setBackground(Color.decode("#00000000"));
            label.setFont(new Font("Serif", Font.BOLD, 12));
            setLayer(label, i);
            add(label, BorderLayout.CENTER);
        }
        var txtRepr = occupants.toString();
        if (getComponents().length == 0){
            add(new JLabel(txtRepr));
        }
        revalidate();
        repaint();
    }

    public void mutateOcuppants(Consumer<ArrayList<GameObj>> mutator) {
        mutator.accept(this.occupants);
        rebuild();
    }

    private boolean computeIsExclusivelyOccupied() {
        var occupied = !occupants.isEmpty();
        var anyHasCollision = occupied && occupants.stream().anyMatch(GameObj::hasCollision);
        return occupied && anyHasCollision;
    }

    public boolean isExclusivelyOccupied() {
        return exclusivelyOccupied;
    }

    private boolean computeIsSolid() {
        var occupied = !occupants.isEmpty();
        return occupied && occupants.stream().anyMatch(occ -> occ instanceof Wall);
    }

    public boolean isSolid() {
        return solid;
    }

    private boolean computeIsPlayerHere() {
        return this.occupants.stream().anyMatch(occ -> occ instanceof Player);
    }

    public boolean isPlayerHere() {
        return playerHere;
    }

    public List<Interaction> possibleInteractions(ActorObj forActor) {
        var acc = new ArrayList<Interaction>();
        var relDist = Math.abs(this.getPlayerDistance()-forActor.getLoc().getPlayerDistance());
        if (relDist > forActor.getSpeed()) return acc;
        var isPlayer = forActor instanceof Player;
        var acceptingMoves = this != forActor.getLoc() && !isExclusivelyOccupied();
        var chest = occupants.stream().flatMap(o -> (o instanceof Chest)? Stream.of((Chest)o) : Stream.empty()).findFirst();
        var actor = occupants.stream().flatMap(o -> (o instanceof ActorObj)? Stream.of((ActorObj)o) : Stream.empty()).findFirst();
        if (acceptingMoves) {
            var i = new Interaction.MoveToLoc(forActor, this);
            if (isPlayer) {
                Player p = (Player)forActor;
                if (p.isInCombat() && p.getGame().getCombat().getStage() == CombatPanel.CombatStage.Fled) {
                    i = new Interaction.Flee(p, this);
                }
            }
            acc.add(i);
        }
        if (isPlayer) { chest.ifPresent(chestObj -> acc.add(new Interaction.OpenChest(chestObj))); }
        actor.ifPresent(opponent -> {
            if (!isPlayer && opponent instanceof Zombie) return;
            acc.add(new Interaction.EnterCombat(forActor, opponent));
        });
        return acc;
    }

    @Override
    public String toString() {
        return "(" + this.getCol() + "," + getRow() + ") " + occupants + ", d " + getPlayerDistance();
    }

    @Override
    protected void paintComponent(Graphics g) {
        final Color fogClr = Color.decode("0x1c1c1c");
        final Color debugFogClr = new Color(0x1c, 0x1c, 0x1c, 0x4f);
        if (isInFogOfWar() && !isDebug()) {
            g.setColor(fogClr);
            g.fillRect(0,0, 80,80);
            return;
        }
        for (GameObj occupant : occupants) {
            var imgRepr = occupant.getImgRepr();
            String[] stringsToDraw = new String[2];
            stringsToDraw[0] = "";
            stringsToDraw[1] = "";
            var hidden = occupant instanceof ActorObj
                    && ((ActorObj) occupant).getConcealment() > game.getBoard().getPlayer().getPerception()
                    && !((ActorObj) occupant).isInCombat()
                    && !isDebug();
            if (!hidden) {
                if (imgRepr != null) {
                    g.drawImage(imgRepr.getImage(), 0, 0, this);
                } else {
                    stringsToDraw[0] = occupant.toString();
                }
            }
            if (!isEnabled()) {
                g.setColor(new Color(0x3c, 0x3c, 0x3c, 0x3c));
                g.fillRect(0,0,80,80);
            }
            g.setColor(Color.BLACK);
            if (isDebug()) {
                stringsToDraw[0] += this.toString();
                stringsToDraw[1] += "dist " + playerDistance;
            }
            for (int i = 0; i < stringsToDraw.length; i++) {
                if (!stringsToDraw[i].isEmpty()) {
                    g.drawString(stringsToDraw[i], 0, 40 + i * 12);
                }
            }
            if (isTargeted()) {
                g.drawImage(targetedOverlay.getImage(), 0, 0, this);
            }
        }
        if (isInFogOfWar() && isDebug()) {
            g.setColor(debugFogClr);
            g.fillRect(0,0, 80,80);
        }
    }

    public int getPlayerDistance() {
        return playerDistance;
    }

    public void setPlayerDistance(int playerDistance) {
        this.playerDistance = playerDistance;
    }

    public void setTargeted(boolean targeted) {
        this.targeted = targeted;
    }

    public boolean isTargeted() {
        return targeted;
    }

    public boolean isInFogOfWar() {
        return inFogOfWar;
    }

    public void setInFogOfWar(boolean inFogOfWar) {
        this.inFogOfWar = inFogOfWar;
    }

    public boolean isDebug() {
        return game.isDebug();
    }
}
