package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Game;
import org.engcomp.Zombicide.GridLoc;
import org.engcomp.Zombicide.Interaction;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public abstract class GameObj {
    private Game owner;
    protected boolean hasCollision = false;
    protected boolean hasRun = false;
    protected String textRepr = "abstract GameActor";
    protected ImageIcon imgRepr = null;
    protected List<ImageIcon> imgOverlays = new ArrayList<>();

    public GameObj(Game owner) {
        this.owner = owner;
    }

    public Game getOwner() {
        return owner;
    }

    public GridLoc getLoc() {
        return loc;
    }

    public void setLoc(GridLoc loc) {
        this.loc = loc;
    }

    protected GridLoc loc = null;
    public Interaction run() { return null; };

    @Override
    public String toString() {
        return this.textRepr;
    }

    public ImageIcon getImgRepr() {
        return imgRepr;
    }

    public boolean hasCollision() {
        return hasCollision;
    }
}
