package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.ActorObj;
import org.engcomp.Zombicide.Actors.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class CombatWin extends JFrame {
    protected Map<String, JButton> btns = new HashMap<>();
    //protected Layout btnGridLayout;
    //protected GridLayout btnGridLayout;
    protected Player player;
    protected ActorObj foe;

    private record BtnsEntry(String text, ActionListener callback) {}

    public CombatWin(Player player, ActorObj foe) {
        super("Zombicide Combat");

        BtnsEntry[] entries = {
                new BtnsEntry("Melee", this::melee),
                new BtnsEntry("Shoot", this::shoot),
        };

        Arrays.stream(entries).forEach(entry -> {
            var btn = new JButton(entry.text);
            btn.addActionListener(entry.callback);
            btns.put(entry.text, btn);
            add(btn);
        });

        setSize(300, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //btnGridLayout = new GridLayout(btns.size(), 1);
        //setLayout(btnGridLayout);
        setVisible(true);

        this.player = player;
        this.foe = foe;
    }

    private void shoot(ActionEvent actionEvent) {

    }

    public void melee(ActionEvent actionEvent) {

    }
}
