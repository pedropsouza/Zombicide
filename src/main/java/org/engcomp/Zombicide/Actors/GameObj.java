package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.GridLoc;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class GameObj extends JLabel {
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imgRepr != null) { g.drawImage(imgRepr.getImage(), 0, 0, this); }
        for (var img : this.imgOverlays) {
            g.drawImage(img.getImage(), 0, 0, this);
        }
    }
}
