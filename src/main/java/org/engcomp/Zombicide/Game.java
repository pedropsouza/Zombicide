package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

public class Game {
    protected JFrame frame;
    protected JButton loadBtn;
    protected GridLayout btnGridLayout;
    protected Matrix<GridLoc> mat = null;
    private final Path matrixFilePath = Paths.get("matrix.txt");
    protected Player p = null;

    public Game() {
        frame = new JFrame("Mapa Grid");
        loadBtn = new JButton("load");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        var path = FileSystems.getDefault().getPath(".", "matrix.txt");
        var charMatrix = tryReadMatrix(path, e -> (char) e.getBytes(StandardCharsets.UTF_8)[0]);
        assert charMatrix != null;
        mat = new Matrix<>(charMatrix.getCols(), charMatrix.getRows(), charMatrix.stream().map(e -> {
            GameActor actor = switch(e.val) {
                case 'P' -> new Player();
                case 'X' -> new Wall();
                case 'R' -> new ZombieCrawler();
                case 'C' -> new ZombieRunner();
                case 'B' -> new Chest();
                case 'Z' -> new ZombieRegular();
                case 'G' -> new ZombieGiant();
                case '.' -> null;
                default -> null;
            };

            if (actor instanceof Player) {
                this.p = (Player)actor;
            }
            var btn = new JButton((actor != null)? actor.toString() : "");
            btn.setVisible(true);
            var loc = new GridLoc(btn, e.idx.col, e.idx.row);
            loc.setOccupant(actor);
            if (actor != null) {
                actor.setLoc(loc);
            }
            //btn.addActionListener(e -> {
                //mat.set(loc.getCol(), loc.getRow(), new GridLoc)
            //});
            return loc;
        }).toList());
        mat.stream().forEach(e -> frame.add(e.val.btn));
        System.out.println(mat);
        btnGridLayout = new GridLayout(mat.getCols(), mat.getRows());
        frame.setLayout(btnGridLayout);
        frame.setVisible(true);
        doTurn();
    }

    public void doTurn() {
        mat.stream().forEach(e -> {
            var targetBtn = e.val.btn;
            targetBtn.setEnabled(this.p.canMove(e.val));
        });
    }

    private static <T> void tryWriteMatrix(Matrix<T> mat) {
        try {
            File matFile = new File("matrix.txt");
            FileWriter writer = new FileWriter(matFile);
            writer.write(mat.stringSerialize(Object::toString));
            writer.flush();
            writer.close();
        } catch (Exception e) {
            /* ignore */
        }
    }
    private static <T> Matrix<T> tryReadMatrix(Path p, Function<String, T> deserFunc) {
        try {
            String data = String.join("\n", Files.readAllLines(p));
            return Matrix.stringDeserialize((Class<T>) Character.TYPE, deserFunc, data);
        } catch (Exception e) {
            /* ignore */
            System.err.println("read matrix error: " + e);
        }
        return null;
    }
    //private void genBtns() {
        //for (int y = 0; y < mat.getRows(); y++) {
            //for (int x = 0; x < mat.getCols(); x++) {
                //var btn = new JButton();
                //btn.setText(mat.get(x,y).toString());
                //this.frame.add(btn);
                //btn.addActionListener(e -> btn.setFont(new Font("Comic Sans", Font.PLAIN, 12)));
                //btn.setVisible(true);
            //}
        //}
    //}
}
