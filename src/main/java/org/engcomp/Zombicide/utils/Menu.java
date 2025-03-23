package org.engcomp.Zombicide.utils;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Menu extends Box {
    protected ArrayList<JButton> btns;
    protected Pair<String, ActionListener> entries;

    public Menu(Stream<Pair<String, ActionListener>> entries, int axis, JLabel title) {
        super(axis);
        if (title != null) {
            add(title);
        }
        btns = new ArrayList<>();

        entries.forEach(entry -> {
            var btn = new JButton(entry.l);
            btn.addActionListener(entry.r);
            btns.add(btn);
            add(btn);
        });

        setVisible(true);
    }
    public Menu(Stream<Pair<String, ActionListener>> entries, int axis) {
        this(entries, axis, null);
    }
}
