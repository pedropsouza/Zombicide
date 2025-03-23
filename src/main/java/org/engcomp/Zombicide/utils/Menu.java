package org.engcomp.Zombicide.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.stream.Stream;

import static java.awt.GridBagConstraints.HORIZONTAL;

public class Menu extends Panel {
    protected ArrayList<JButton> btns;
    protected Pair<String, ActionListener> entries;
    protected GridBagConstraints gbc;
    protected int axis;

    protected Menu(int axis, JLabel title) {
        super();
        this.axis = axis;
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = HORIZONTAL;
        if (title != null) {
            add(title, gbc);
            gbcAdvance();
            var strut = switch(axis) {
                case BoxLayout.LINE_AXIS, BoxLayout.X_AXIS -> Box.createHorizontalStrut(10);
                default -> Box.createVerticalStrut(10);

            };
            add(strut, gbc);
            gbcAdvance();
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
            m.add(btn, m.gbc);
            m.gbcAdvance();
        });

        m.setVisible(true);
        return m;
    }

    protected void gbcAdvance() {
        switch (axis) {
            case BoxLayout.PAGE_AXIS, BoxLayout.Y_AXIS: gbc.gridy += 1; break;
            case BoxLayout.LINE_AXIS, BoxLayout.X_AXIS: gbc.gridx += 1; break;
        }
    }
}
