package org.engcomp.Zombicide.Actors;

import org.engcomp.Zombicide.Game;
import org.engcomp.Zombicide.Interaction;

public class ZombieCrawler extends Zombie {
    public ZombieCrawler(Game game) {
        super(game);
        this.textRepr = "Crawling Zombie";
        this.health = 1;
        this.negatedDamages.clear();
        this.concealment = 3;
    }
}
