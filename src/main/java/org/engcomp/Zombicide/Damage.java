package org.engcomp.Zombicide;

public enum Damage {
    Critical(2),
    BareHand(1),
    Blunt(1),
    Piercing(2);

    public final int strength;
    private Damage(int strength) {
        this.strength = strength;
    }
}
