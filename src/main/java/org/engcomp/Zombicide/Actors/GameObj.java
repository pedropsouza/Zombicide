package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.GridLoc;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class GameObj {
    protected boolean hasCollision = false;
    protected boolean hasRun = false;
    protected String textRepr = "abstract GameActor";
    protected ImageIcon imgRepr = null;
    protected List<ImageIcon> imgOverlays = new ArrayList<>();

    public GridLoc getLoc() {
        return loc;
    }

    public void setLoc(GridLoc loc) {
        this.loc = loc;
    }

    protected GridLoc loc = null;
    public void run() {};

    @Override
    public String toString() {
        return this.textRepr;
    }

    public ImageIcon getImgRepr() {
        return imgRepr;
    }
}
