package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.GameObj;

import javax.swing.*;

public class GridLoc {
    protected JButton btn;
    protected int col;

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    protected int row;

    public int taxiCabDistance(GridLoc loc) {
        return Math.abs(loc.getCol() - this.col) + Math.abs(loc.getRow() - this.row);
    }

    public GameObj getOccupant() {
        return occupant;
    }

    public void setOccupant(GameObj occupant) {
        this.occupant = occupant;
        this.occupant.setLoc(this);
        this.btn.setText(occupant.toString());
    }

    protected GameObj occupant;

    public GridLoc(JButton btn, int col, int row) {
        this.btn = btn; this.col = col; this.row = row;
    }

    @Override
    public String toString() {
        return "(" + this.getCol() + "," + getRow() + ") " + occupant;
    }
}
