package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Game;

import javax.swing.*;
import java.util.Objects;

public class Floor extends GameObj {
    public Floor(Game owner) {
        super(owner);
        this.hasCollision = false;
        this.textRepr = "Floor";
        this.imgRepr = new ImageIcon(Objects.requireNonNull(getClass().getResource("cement_floor.scaled.png")));
    }
}
