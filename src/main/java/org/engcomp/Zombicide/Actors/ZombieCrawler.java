package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Game;
import org.engcomp.Zombicide.Interaction;

public class ZombieCrawler extends Zombie {
    public ZombieCrawler(Game owner) {
        super(owner);
        this.textRepr = "Crawling Zombie";
        this.health = 1;
    }
}
