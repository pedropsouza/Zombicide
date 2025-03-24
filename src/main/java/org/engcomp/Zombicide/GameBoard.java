package org.engcomp.Zombicide;
import org.engcomp.Zombicide.Actors.*;
import org.engcomp.Zombicide.utils.Matrix;
import org.engcomp.Zombicide.utils.Pair;

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
import java.util.stream.IntStream;
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

            var loc = new GridLoc(game, new ArrayList<>(objs), entry.idx.col, entry.idx.row);
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
                    loc.setTargeted(loc.isEnabled());
                    loc.repaint();
                    game.revalidate();
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

    public Stream<GridLoc> getStraightPath(GridLoc a, GridLoc b) {
        return getStraightPath(a.col, a.row, b.col, b.row);
    }
    public Stream<GridLoc> getStraightPath(int ax, int ay, int bx, int by) {
        int dx = (bx - ax);
        int dy = (by - ay);

        // Keep dy/dx <= 1
        boolean swapXY = false;

        boolean reflectX = false;
        boolean reflectY = false;

        if (dx == 0) {
            var step = (dy >= 0)? 1 : -1;
            return Stream.iterate(ay, y -> y != by, y -> y + step)
                    .flatMap(y -> {
                        var loc = get(ax, y);
                        return (loc != null)? Stream.of(loc) : Stream.empty();
                    });
        } else if (dx < 0) {
            reflectX = true;
        }
        if (dy == 0) {
            var step = (dx >= 0)? 1 : -1;
            return Stream.iterate(ax, x -> x != bx, x -> x + step)
                    .flatMap(x -> {
                        var loc = get(x, ay);
                        return (loc != null)? Stream.of(loc) : Stream.empty();
                    });
        } else if (dy < 0) {
            reflectY = true;
        }
        if (reflectX) { dx = -dx; }
        if (reflectY) { dy = -dy; }
        if (dy/dx > 0) {
            var temp = dx;
            dx = dy;
            dy = temp;
            swapXY = true;
        }
        var offs = bresenham(dx, dy);
        final boolean reflectXf = reflectX;
        final boolean reflectYf = reflectY;
        final boolean swapXYf = swapXY;
        return offs.stream().flatMap(off -> {
            var x = ax + (reflectXf? -1 : 1)*off.l;
            var y = ay + (reflectYf? -1 : 1)*off.r;
            if (swapXYf) {
                var temp = x;
                x = y;
                y = temp;
            }
            var loc = get(x,y);
            return (loc != null)? Stream.of(loc) : Stream.empty();
        });
    }

    /// Assumes ax < bx && ay < by && 0 <= dy/dx <= 1
    private List<Pair<Integer, Integer>> bresenham(int dx, int dy) {
        var acc = new ArrayList<Pair<Integer, Integer>>();
        int error = 0;

        for (int x = 0, y = 0; x <= dx; x++) {
            acc.add(new Pair<>(x,y));
            error += dy;
            if(2*error >= dx) {
                y++;
                error -= dx;
            }
        }
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
