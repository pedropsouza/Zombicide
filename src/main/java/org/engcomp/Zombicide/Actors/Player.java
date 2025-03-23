package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Game;
import org.engcomp.Zombicide.GridLoc;
import org.engcomp.Zombicide.Interaction;
import org.engcomp.Zombicide.Item;

import javax.swing.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Player extends ActorObj {
    protected Map<Item, Integer> inventory = new HashMap<>();
    protected int healCap;

    public Player(Game game) {
        super(game);
        this.health = 5;
        this.healCap = this.health;
        this.textRepr = "Player";
        this.imgRepr = new ImageIcon(Objects.requireNonNull(getClass().getResource("idle.gif")));
    }

    public boolean canInteract(GridLoc loc) {
        var withinDist = loc.getPlayerDistance() <= speed;
        var hasInteractions = !loc.possibleInteractions(this).isEmpty();
        return withinDist && hasInteractions;
    }

    public void addItemToInventory(Item item) {
        var count = inventory.getOrDefault(item, 0);
        inventory.put(item, count+1);
        reportChange();
    }
    public boolean canUseItem(Item item) {
        var count = inventory.getOrDefault(item, 0);
        return count > 0;
    }

    public void useItem(Item item) {
        var count = inventory.get(item);
        assert count > 0;
        inventory.put(item, count-1);
        reportChange();
    }

    ///  Called by the bandage logic
    public void heal(int amount) {
        setHealth(Math.min(getHealth()+amount, this.healCap));
    }

    public Map<Item, Integer> getInventory() {
        return Collections.unmodifiableMap(inventory);
    }

    @Override
    public String toString() {
        return "Player " + inventory;
    }
}
