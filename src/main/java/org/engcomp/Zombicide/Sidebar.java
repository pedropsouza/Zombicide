package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.Player;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Stream;

public class Sidebar extends Box {
    private final Game game;
    private final DefaultListModel<String> inventoryListModel = new DefaultListModel<>();
    private JScrollPane combatScrollPane;

    public Sidebar(Game game) {
        super(BoxLayout.PAGE_AXIS);
        this.game = game;
        this.combatScrollPane = new JScrollPane();
        var board = game.getBoard();

        var upper = new Box(BoxLayout.PAGE_AXIS);
        upper.setPreferredSize(new Dimension(280, 300));
        var title = new JLabel("Zombicide");
        title.setAlignmentX(CENTER_ALIGNMENT);
        upper.add(title);
        upper.add(Box.createGlue());
        { // Inventory and health
            var health = new JLabel(); upper.add(health);
            var perception = new JLabel(); upper.add(perception);
            var bandages = new JButton("Use bandage"); upper.add(bandages);
            var wait = new JButton("Skip turn"); upper.add(wait);
            health.setAlignmentX(CENTER_ALIGNMENT);
            bandages.setAlignmentX(CENTER_ALIGNMENT);
            wait.setAlignmentX(CENTER_ALIGNMENT);

            upper.add(Box.createGlue());
            bandages.addActionListener(_ -> {
                var p = board.getPlayer();
                p.useItem(Item.Bandages);
                p.heal(1);
                CombatPanel c = game.getCombat();
                if (c != null) {
                    c.afterAction();
                } else {
                    game.finishTurn();
                }
            });
            wait.addActionListener(_ -> {
                var p = board.getPlayer();
                CombatPanel c = game.getCombat();
                if (c != null) {
                    c.afterAction();
                } else {
                    game.finishTurn();
                }
            });
            var items = new JList<String>(inventoryListModel); upper.add(items);
            board.getPlayer().addChangedCallback(pUncased -> {
                assert pUncased instanceof Player;
                var p = (Player)pUncased;
                health.setText("Health: " + p.getHealth());
                perception.setText("Perception: " + p.getPerception());
                bandages.setEnabled(p.canUseItem(Item.Bandages));
                List<String> inventoryList = p.getInventory().entrySet().stream().flatMap(e -> {
                    var item = e.getKey();
                    var count = e.getValue();
                    return (count > 0)? Stream.of(item + " x" + count) : Stream.empty();
                }).toList();
                inventoryListModel.clear();
                inventoryListModel.addAll(inventoryList);
            });
            board.getPlayer().reportChange();
        }
        var verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upper, combatScrollPane);
        verticalSplit.setResizeWeight(0.20);
        add(verticalSplit);
        setPreferredSize(new Dimension(280, 800));
    }

    public void setCombatView(CombatPanel c) {
        this.combatScrollPane.setViewportView(c);
    }
}
