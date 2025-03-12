package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Items.Item;

public class Chest extends GameObj {
    protected Item item;
    public Chest(Item item) {
        this.textRepr = "Chest";
        this.item = item;
    }
}
