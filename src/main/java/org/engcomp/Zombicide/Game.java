package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.*;

import javax.swing.*;
import java.awt.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Game {
    protected JFrame frame;
    protected JButton loadBtn;
    protected GridLayout btnGridLayout;
    protected GameBoard board = null;
    private final Path matrixFilePath = Paths.get("matrix.txt");

    public Game() {
        frame = new JFrame("Zombicide");
        loadBtn = new JButton("load");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        var path = FileSystems.getDefault().getPath(".", "matrix.txt");
        board = GameBoard.load(this, path);
        board.stream().forEach(e -> frame.add(e.val.btn));
        System.out.println(board);
        btnGridLayout = new GridLayout(board.getCols(), board.getRows());
        frame.setLayout(btnGridLayout);
        frame.setVisible(true);
        updateBtns();
    }

    public void finishTurn() {
        updateBtns();
    }
    public void updateBtns() {
        board.stream().forEach(e -> {
            var targetBtn = e.val.btn;
            targetBtn.setEnabled(
                    this.board.getPlayer().canInteract(e.val)
            );
        });
    }

    public void combat(Zombie zed) {
    }

    public void chestEncounter(Chest c) {

    }
}
