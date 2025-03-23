package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.Player;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sidebar extends Box {
    private final Game game;
    private final DefaultListModel<String> inventoryListModel = new DefaultListModel<>();

    public Sidebar(Game game) {
        super(BoxLayout.PAGE_AXIS);
        this.game = game;
        var board = game.getBoard();

        var title = new JLabel("Zombicide");
        add(title);
        { // Inventory and health
            var health = new JLabel(); add(health);
            var items = new JList<String>(inventoryListModel); add(items);
            board.getPlayer().addChangedCallback(pUncased -> {
                assert pUncased instanceof Player;
                var p = (Player)pUncased;
                health.setText("Health: " + p.getHealth());
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
        setPreferredSize(new Dimension(120, 800));
    }
}
