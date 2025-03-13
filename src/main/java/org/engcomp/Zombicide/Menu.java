package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.Chest;
import org.engcomp.Zombicide.Actors.Zombie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Menu extends JFrame {
    protected ArrayList<JButton> btns;
    protected GridLayout btnGridLayout;
    protected GameBoard board = null;
    public record MenuEntry(String text, ActionListener callback) {};

    public Menu(Stream<MenuEntry> entries) {
        super("Zombicide Main Menu");
        btns = new ArrayList<>();

        entries.forEach(entry -> {
            var btn = new JButton(entry.text);
            btn.addActionListener(entry.callback);
            btns.add(btn);
            add(btn);
        });

        setSize(300, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        btnGridLayout = new GridLayout(btns.size(), 1);
        setLayout(btnGridLayout);
        setVisible(true);
    }
}
