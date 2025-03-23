package org.engcomp.Zombicide;
import org.engcomp.Zombicide.Actors.*;
import org.engcomp.Zombicide.utils.Matrix;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
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

    public static GameBoard load(Game game, URL boardFile) throws IOException {
        var charMatrix = tryReadMatrix(boardFile.openStream(), e -> (char) e.getBytes(StandardCharsets.UTF_8)[0]);
        assert charMatrix != null;
        Player p = null;
        ArrayList<GridLoc> data = new ArrayList<>();
        for (var entry : charMatrix.stream().toList()) {
            List<GameObj> objs = switch(entry.val) {
            case 'P' -> {
            p = new Player(game);
            yield List.of(new Floor(game), p);
            }
            case 'X' -> List.of(new Wall(game));
            case 'C' -> List.of(new Floor(game), new ZombieCrawler(game));
            case 'R' -> List.of(new Floor(game), new ZombieRunner(game));
            case 'Z' -> List.of(new Floor(game), new ZombieRegular(game));
            case 'G' -> List.of(new Floor(game), new ZombieGiant(game));

            case 'H' -> List.of(new Floor(game), new Chest(game, Item.Revolver));
            case 'A' -> List.of(new Floor(game), new Chest(game, Item.Bandages));
            case 'T' -> List.of(new Floor(game), new Chest(game, Item.BaseballBat));
            case '.' -> List.of(new Floor(game));
            default -> { assert false; yield null; }
            };

            var loc = new GridLoc(objs, entry.idx.col, entry.idx.row);
            objs.forEach(obj -> obj.setLoc(loc));
            data.add(loc);
        }
        GameBoard board = new GameBoard(game, charMatrix.getCols(), charMatrix.getRows(), data);
        board.stream().forEach(entry -> {
            var loc = entry.val;
            loc.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!loc.isEnabled()) return;
                    board.playerInput(loc);
                    board.game.finishTurn();
                    System.out.println("Novo estado:\n" + board);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    loc.setTargeted(true);
                    loc.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    loc.setTargeted(false);
                    loc.repaint();
                }
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
        var temp = a.getOccupants();
        a.setOccupants(b.getOccupants());
        b.setOccupants(temp);
    }

    public void playerInput(GridLoc loc) {
        assert this.player != null;

        List<Interaction> possibleInteractions = loc.possibleInteractions(getPlayer());

        if (!possibleInteractions.isEmpty()) {
            Interaction maxPrioInteraction = possibleInteractions.stream().max(Interaction::compareTo).orElseThrow();
            maxPrioInteraction.run(game);
        }
    }

    public Set<GridLoc> getOrthogonals(GridLoc loc) {
        Set<Dimension> offsets = Set.of(
                new Dimension(1,0),
                new Dimension(-1,0),
                new Dimension(0,1),
                new Dimension(0,-1)
                );
        Set<GridLoc> acc = new HashSet<>(4);
        offsets.forEach(off -> {
            var retrieved = this.get(loc.getCol() + off.width, loc.getRow() + off.height);
            if (retrieved != null) {
                acc.add(retrieved);
            }
        });
        return acc;
    }

    private static <T> Matrix<T> tryReadMatrix(InputStream in, Function<String, T> deserFunc) {
        try {
            String data = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return Matrix.stringDeserialize((Class<T>) Character.TYPE, deserFunc, data);
        } catch (Exception e) {
            /* ignore */
            System.err.println("read matrix error: " + e);
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("board:\n");
        for (int y = 0; y < getRows(); y++) {
            for (int x = 0; x < getCols(); x++) {
                var repr = get(x,y).toString();
                String massaged = String.format("%-30s", repr.substring(repr.indexOf(' ')));
                sb.append(massaged);
                sb.append(", ");
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
