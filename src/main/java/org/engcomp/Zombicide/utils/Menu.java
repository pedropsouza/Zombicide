package org.engcomp.Zombicide.utils;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Menu extends Box {
    protected ArrayList<JButton> btns;
    protected Pair<String, ActionListener> entries;

    protected Menu(int axis, JLabel title) {
        super(axis);
        if (title != null) {
            add(title);
        }
        btns = new ArrayList<>();

    }
    public static Menu from(Stream<Pair<String, ActionListener>> entries, int axis) {
        return from(entries, axis, null);
    }

    public static Menu from(Stream<Pair<String, ActionListener>> entries, int axis, JLabel title) {
        Menu m = new Menu(axis, title);
        entries.forEach(entry -> {
            var btn = new JButton(entry.l);
            btn.addActionListener(entry.r);
            m.btns.add(btn);
            m.add(btn);
        });

        m.setVisible(true);
        return m;
    }
}
