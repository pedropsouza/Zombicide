package org.engcomp.Zombicide;

import org.engcomp.Zombicide.utils.Menu;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class Main {
    private static Game game;
    static int percepcao = 2;

    private static class MainMenu extends JFrame {
        private org.engcomp.Zombicide.utils.Menu menu;
        private org.engcomp.Zombicide.utils.Menu difficultyMenu;
        private Box box;
        public MainMenu() {
            super("Zombicide Main Menu");
            box = Box.createHorizontalBox();
            add(box);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(300, 250);
            { // Menu de dificuldade
                org.engcomp.Zombicide.utils.Menu.MenuEntry[] entries = {
                        new org.engcomp.Zombicide.utils.Menu.MenuEntry("Fácil", _ -> {
                            percepcao = 3;
                        }),
                        new org.engcomp.Zombicide.utils.Menu.MenuEntry("Normal", _ -> {
                            percepcao = 2;
                        }),
                        new org.engcomp.Zombicide.utils.Menu.MenuEntry("Difícil", _ -> {
                            percepcao = 1;
                        }),
                };
                difficultyMenu = new org.engcomp.Zombicide.utils.Menu(
                        Arrays.stream(entries),
                        BoxLayout.PAGE_AXIS,
                        new JLabel("Dificuldade")
                );
            }
            { // Menu de inicio
                org.engcomp.Zombicide.utils.Menu.MenuEntry[] entries = {
                        new org.engcomp.Zombicide.utils.Menu.MenuEntry("Start", _ -> {
                            game = new Game(getClass().getResource("matrix.txt"), percepcao);
                            setVisible(false);
                        }),
                        new org.engcomp.Zombicide.utils.Menu.MenuEntry("DEBUG", _ -> {
                            game = new Game(getClass().getResource("matrix.txt"), percepcao);
                            game.setDebug(true);
                            setVisible(false);
                        }),
                        new org.engcomp.Zombicide.utils.Menu.MenuEntry("DEBUG MAP", _ -> {
                            game = new Game(getClass().getResource("testmap.txt"), percepcao);
                            game.setDebug(true);
                            setVisible(false);
                        }),
                        new org.engcomp.Zombicide.utils.Menu.MenuEntry("SINGLE ZED MAP", _ -> {
                            game = new Game(getClass().getResource("matrix_single_z.txt"), percepcao);
                            game.setDebug(true);
                            setVisible(false);
                        }),
                        new org.engcomp.Zombicide.utils.Menu.MenuEntry("Exit", _ -> {
                            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                        }),
                };
                menu = new Menu(Arrays.stream(entries), BoxLayout.PAGE_AXIS);
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