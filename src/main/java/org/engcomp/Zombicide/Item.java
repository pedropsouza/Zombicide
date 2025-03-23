package org.engcomp.Zombicide;

public enum Item {
    Revolver("revólver"),
    Bandages("bandagens"),
    BaseballBat("taco de beisebol");

    final String fullName;
    Item(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
