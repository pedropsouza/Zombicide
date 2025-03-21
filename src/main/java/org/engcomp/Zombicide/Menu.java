package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.Chest;
import org.engcomp.Zombicide.Actors.Zombie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Menu extends Box {
    protected ArrayList<JButton> btns;
    public record MenuEntry(String text, ActionListener callback) {
        public String getText() { return text; }
        public ActionListener getCallback() { return callback; }
    };

    public Menu(Stream<MenuEntry> entries, int axis, JLabel title) {
        super(axis);
        if (title != null) {
            add(title);
        }
        btns = new ArrayList<>();

        entries.forEach(entry -> {
            var btn = new JButton(entry.text);
            btn.addActionListener(entry.callback);
            btns.add(btn);
            add(btn);
        });

        setVisible(true);
    }
    public Menu(Stream<MenuEntry> entries, int axis) {
        this(entries, axis, null);
    }
}
