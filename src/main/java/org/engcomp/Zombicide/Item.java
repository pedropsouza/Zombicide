package org.engcomp.Zombicide;

public enum Item {
    Revolver("revolver"),
    Bandages("bandages"),
    BaseballBat("baseball bat");

    final String fullName;
    Item(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
