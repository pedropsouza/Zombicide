package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.GameObj;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

// https://stackoverflow.com/a/46360964
public class GridLoc extends JLayeredPane {
    protected int col;
    protected int row;
    protected List<GameObj> occupants = new ArrayList<>();

    public GridLoc(List<GameObj> occupants, int col, int row) {
        super();
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

    @Override
    public String toString() {
        return "(" + this.getCol() + "," + getRow() + ") " + occupants;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (GameObj occupant : occupants) {
            var imgRepr = occupant.getImgRepr();
            if (imgRepr != null) {
                g.drawImage(imgRepr.getImage(), 0, 0, this);
            } else {
                g.drawString(occupant.toString(), 00,40);
            }
            //for (var img : this.imgOverlays) {
            //    g.drawImage(img.getImage(), 0, 0, this);
            //}
        }
    }
}
