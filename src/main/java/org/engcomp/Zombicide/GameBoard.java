package org.engcomp.Zombicide;
import org.engcomp.Zombicide.Actors.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

            var loc = new GridLoc(List.of(obj), entry.idx.col, entry.idx.row);
            obj.setLoc(loc);
            data.add(loc);
        }
        GameBoard board = new GameBoard(game, charMatrix.getCols(), charMatrix.getRows(), data);
        board.stream().forEach(entry -> {
            var loc = entry.val;
            loc.guiCmpnt.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    board.playerInput(loc);
                    board.game.finishTurn();
                    System.out.println("Novo estado:\n" + board);
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

        var occupants = loc.getOccupants();
        Zombie combatTarget = null;
        Chest chestTarget = null;
        GridLoc moveTarget = null;

        enum InputValidationResult {
            Combat(2),
            Chest(1),
            MoveToLoc(0),
            Invalid(-1);

            public int priority = -1;
            InputValidationResult(int priority) {
                this.priority = priority;
            }
            public boolean trumps(InputValidationResult other) {
                return this.priority > other.priority;
            }
        };
        InputValidationResult result = InputValidationResult.Invalid;

        if (!occupants.isEmpty()) {
            if (occupants.stream().noneMatch(occ -> player.canInteract(occ.getLoc()))) {
                return;
            }
            for (GameObj occ : occupants) {
                switch (occ) {
                    case Zombie zombie -> {
                        if (InputValidationResult.Combat.trumps(result)) {
                            combatTarget = zombie;
                            result = InputValidationResult.Combat;
                        }
                    }
                    case Chest chest -> {
                        if (InputValidationResult.Chest.trumps(result)) {
                            chestTarget = chest;
                            result = InputValidationResult.Chest;
                        }
                    }
                    case Floor floor -> {
                        if (InputValidationResult.MoveToLoc.trumps(result)) {
                            //moveTarget = floor.getLoc();
                            result = InputValidationResult.MoveToLoc;
                        }
                    }
                    default -> {}
                }
            }
        }
        switch (result) {
            case InputValidationResult.Invalid -> System.err.println("Invalid player input to grid location + " + loc + "!");
            case InputValidationResult.MoveToLoc -> swap(player.getLoc(), loc);
            case InputValidationResult.Chest -> {
                var oldPlayerLoc = player.getLoc();
                loc.setOccupants(Collections.singletonList(player));
                oldPlayerLoc.setOccupants(List.of(new Floor())); // this is *really* ugly
                game.chestEncounter(chestTarget);
            }
            case InputValidationResult.Combat -> {
                game.combat(combatTarget);
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
