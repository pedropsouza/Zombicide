package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.GameObj;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class GridLoc extends Box {
    protected int col;
    protected int row;
    protected List<GameObj> occupants = new ArrayList<>();

    public GridLoc(List<GameObj> occupants, int col, int row) {
        super(BoxLayout.LINE_AXIS);
        this.setOccupants(occupants); this.col = col; this.row = row;
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

    public List<GameObj> getOccupants() {
        return occupants;
    }

    public void setOccupants(List<GameObj> occupants) {
        this.occupants = occupants;
        this.occupants.forEach(o -> o.setLoc(this));
        removeAll();
        occupants.forEach(occupant -> {
            add(new JLabel(occupant.getImgRepr()), BorderLayout.CENTER);
        });
        var txtRepr = occupants.toString();
        if (getComponents().length == 0){
            add(new JLabel(txtRepr));
        }
        revalidate();
        repaint();
    }

    @Override
    public String toString() {
        return "(" + this.getCol() + "," + getRow() + ") " + occupants;
    }
}
