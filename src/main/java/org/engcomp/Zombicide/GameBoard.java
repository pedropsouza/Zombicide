package org.engcomp.Zombicide;
import org.engcomp.Zombicide.Actors.*;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class GameBoard extends Matrix<GridLoc> {
    protected Game game;

    protected Player player;

    public GameBoard(Game game, int cols, int rows, List<GridLoc> data) {
        super(cols, rows, data);
        this.game = game;
    }

    public GameBoard(Game game, Stream<Matrix<GridLoc>.MatrixEntry> data) {
        super(data);
        this.game = game;
    }

    public static GameBoard load(Game game, Path boardFile) {
        var charMatrix = tryReadMatrix(boardFile, e -> (char) e.getBytes(StandardCharsets.UTF_8)[0]);
        assert charMatrix != null;
        Player p = null;
        ArrayList<GridLoc> data = new ArrayList<>();
        for (var entry : charMatrix.stream().toList()) {
            GameObj obj = switch(entry.val) {
            case 'P' -> {
            p = new Player();
            yield p;
            }
            case 'X' -> new Wall();
            case 'R' -> new ZombieCrawler();
            case 'C' -> new ZombieRunner();
            case 'H' -> new Chest(Item.Revolver);
            case 'A' -> new Chest(Item.Bandages);
            case 'T' -> new Chest(Item.BaseballBat);
            case 'Z' -> new ZombieRegular();
            case 'G' -> new ZombieGiant();
            case '.' -> new Floor();
            default -> { assert false; yield null; }
            };

            var btn = new JButton(obj.toString());
            btn.setVisible(true);
            var loc = new GridLoc(btn, entry.idx.col, entry.idx.row);
            loc.setOccupant(obj);
            obj.setLoc(loc);
            data.add(loc);
        }
        GameBoard board = new GameBoard(game, charMatrix.getCols(), charMatrix.getRows(), data);
        board.stream().forEach(entry -> {
            var loc = entry.val;
            loc.btn.addActionListener(e -> {
                board.playerInput(loc);
                board.game.finishTurn();
                System.out.println("Novo estado:\n" + board);
            });
        });
        if (p == null) {
            throw new RuntimeException("Player not found in board file!");
        }
        board.setPlayer(p);
        return board;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void swap(GridLoc a, GridLoc b) {
        var temp = a.getOccupant();
        a.setOccupant(b.getOccupant());
        b.setOccupant(temp);
    }

    public void playerInput(GridLoc loc) {
        assert this.player != null;

        var occ = loc.getOccupant();
        if (occ != null) {
            assert !(occ instanceof Wall);
            if (occ instanceof Zombie) {
                game.combat((Zombie) occ);
            } else if (occ instanceof Chest) {
                var oldPlayerLoc = player.getLoc();
                loc.setOccupant(player);
                oldPlayerLoc.setOccupant(new Floor()); // this is *really* ugly
                game.chestEncounter((Chest) occ);
            } else { // Floor
                swap(player.getLoc(), loc);
            }
        }
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
}
