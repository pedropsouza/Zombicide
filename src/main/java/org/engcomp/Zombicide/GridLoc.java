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
    protected int col;
    protected int row;
    protected int playerDistance = 0;
    protected ArrayList<GameObj> occupants = new ArrayList<>();
    protected boolean targeted = false;
    protected ImageIcon targetedOverlay = new ImageIcon(Objects.requireNonNull(getClass().getResource("target.png")));

    public GridLoc(ArrayList<GameObj> occupants, int col, int row) {
        super();
        this.setOccupants(occupants); this.col = col; this.row = row;
        var dims = new Dimension(80, 80);
        setSize(dims);
        setPreferredSize(dims);
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

    public void setOccupants(ArrayList<GameObj> occupants) {
        this.occupants = occupants;
        rebuild();
    }
    private void rebuild() {
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

    public boolean isExclusivelyOccupied() {
        var occupied = !occupants.isEmpty();
        var anyHasCollision = occupied && occupants.stream().anyMatch(GameObj::hasCollision);
        return occupied && anyHasCollision;
    }

    public List<Interaction> possibleInteractions(ActorObj forActor) {
        var isPlayer = forActor instanceof Player;
        var acceptingMoves = !isExclusivelyOccupied();
        var chest = occupants.stream().flatMap(o -> (o instanceof Chest)? Stream.of((Chest)o) : Stream.empty()).findFirst();
        var actor = occupants.stream().flatMap(o -> (o instanceof ActorObj)? Stream.of((ActorObj)o) : Stream.empty()).findFirst();
        var acc = new ArrayList<Interaction>();
        if (acceptingMoves) acc.add(new Interaction.MoveToLoc(forActor, this));
        if (isPlayer) { chest.ifPresent(chestObj -> acc.add(new Interaction.OpenChest(chestObj))); }
        actor.ifPresent(opponent -> {
            if (isPlayer && opponent instanceof Zombie)
                acc.add(new Interaction.EnterCombat(opponent));
            if (forActor instanceof Zombie && opponent instanceof Player)
                acc.add(new Interaction.EnterCombat(forActor));
            }
        );
        return acc;
    }

    @Override
    public String toString() {
        return "(" + this.getCol() + "," + getRow() + ") " + occupants + ", d " + getPlayerDistance();
    }

    @Override
    protected void paintComponent(Graphics g) {
        //super.paintComponent(g);
        for (GameObj occupant : occupants) {
            var imgRepr = occupant.getImgRepr();
            String[] stringsToDraw = new String[2];
            stringsToDraw[0] = "";
            stringsToDraw[1] = "";
            if (imgRepr != null) {
                g.drawImage(imgRepr.getImage(), 0, 0, this);
            } else {
                stringsToDraw[0] = occupant.toString();
            }
            if (!isEnabled()) {
                g.setColor(new Color(0x3c, 0x3c, 0x3c, 0x3c));
                g.fillRect(0,0,80,80);
            }
            g.setColor(Color.BLACK);
            if (occupant.getGame().isDebug()) {
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
}
