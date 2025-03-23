package org.engcomp.Zombicide;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.engcomp.Zombicide.utils.Menu;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Main {
    private static Game game;
    static int percepcao = 2;

    private static class MainMenu extends JFrame {
        public MainMenu() {
            super("Zombicide Main Menu");
            Box box = Box.createHorizontalBox();
            add(box);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(300, 250);
            Menu difficultyMenu;
            { // Menu de dificuldade
                Menu.MenuEntry[] entries = {
                    new Menu.MenuEntry("Fácil", _ -> {
                        percepcao = 3;
                    }),
                    new Menu.MenuEntry("Normal", _ -> {
                        percepcao = 2;
                    }),
                    new Menu.MenuEntry("Difícil", _ -> {
                        percepcao = 1;
                    }),
                };
                difficultyMenu = new Menu(
                        Arrays.stream(entries),
                        BoxLayout.PAGE_AXIS,
                        new JLabel("Dificuldade")
                );
            }
            Menu menu;
            { // Menu de inicio
                ArrayList<Menu.MenuEntry> entries = new ArrayList<>();
                entries.add(new Menu.MenuEntry("Start", _ -> {
                    game = new Game(getClass().getResource("maps/mapa01.txt"), percepcao);
                    setVisible(false);
                }));
                { // find all maps
                    try (ScanResult scanResult =
                             new ClassGraph()
                                 .acceptPaths("org/engcomp/Zombicide")
                                 .verbose()
                                 .scan()) {
                        var pattern = Pattern.compile("maps/.*?\\.txt");
                        var list = scanResult
                            .getAllResources();

                        System.out.println(list.size() + " resources detected");
                        for (var resource : list) {
                            var relPath = resource.getPath();
                            var matcher = pattern.matcher(relPath);
                            if (!matcher.find()) { continue; }
                            System.out.println("found map " + relPath);
                            entries.add(new Menu.MenuEntry(matcher.group(0), _ -> {
                                game = new Game(getClass().getResource(matcher.group(0)), percepcao);
                                setVisible(false);
                            }));
                        }
                    }
                }
                entries.add(new Menu.MenuEntry("DEBUG", _ -> {
                    game = new Game(getClass().getResource("maps/mapa01.txt"), percepcao);
                    game.setDebug(true);
                    setVisible(false);
                }));
                entries.add(new Menu.MenuEntry("Exit", _ -> {
                    dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                }));

                menu = new Menu(entries.stream(), BoxLayout.PAGE_AXIS);
            }
            box.add(Box.createHorizontalGlue());
            box.add(menu);
            box.add(Box.createHorizontalGlue());
            box.add(difficultyMenu);
            box.add(Box.createHorizontalGlue());
            box.setVisible(true);
            setVisible(true);
        }
    }

    public static void main(String[] args) {

        var mainMenu = new MainMenu();
        game = null;
    }
}