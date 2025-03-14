package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.*;

import javax.swing.*;
import java.awt.*;
import java.nio.file.FileSystems;

public class Game extends JFrame {
    protected JButton loadBtn;
    protected GridLayout btnGridLayout;
    protected GameBoard board = null;
    protected CombatWin combatWin = null;

    public Game(int playerPerception) {
        super("Zombicide");
        loadBtn = new JButton("load");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        var path = FileSystems.getDefault().getPath(".", "matrix.txt");
        board = GameBoard.load(this, path);
        board.stream().forEach(e -> add(e.val));
        board.getPlayer().setPerception(playerPerception);
        System.out.println(board);
        btnGridLayout = new GridLayout(board.getCols(), board.getRows());
        setLayout(btnGridLayout);
        setVisible(true);
        updateBtns();
    }

    public void finishTurn() {
        updateBtns();
    }
    public void updateBtns() {
        board.stream().forEach(e -> {
            var targetBtn = e.val;
            targetBtn.setEnabled(
                    this.board.getPlayer().canInteract(e.val)
            );
        });
    }

    public void combat(Zombie zed) {

    }

    public void chestEncounter(Chest c) {
        board.getPlayer().addItemToInventory(c.getItem());
    }
}
