package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Item;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class Player extends ActorObj {
    protected Map<Item, Integer> inventory = new HashMap<>();
    public Player() {
        super();
        this.health = 5;
        this.textRepr = "Player";
        this.imgRepr = new ImageIcon("assets/idle.gif");
    }

    @Override
    public void run() {
    }

    public void addItemToInventory(Item item) {
        var count = inventory.getOrDefault(item, 0);
        inventory.put(item, count+1);
    }
    public boolean canUseItem(Item item) {
        var count = inventory.get(item);
        return count > 0;
    }

    public void useItem(Item item) {
        var count = inventory.get(item);
        assert count > 0;
        inventory.put(item, count-1);
    }

    @Override
    public String toString() {
        return "Player " + inventory;
    }
}
