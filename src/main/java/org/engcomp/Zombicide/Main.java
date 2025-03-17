package org.engcomp.Zombicide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class Main {
    private static Game game;
    static int percepcao = 2;

    private static class MainMenu extends JFrame {
        private Menu menu;
        private Menu difficultyMenu;
        private Box box;
        public MainMenu() {
            super("Zombicide Main Menu");
            box = Box.createHorizontalBox();
            add(box);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(300, 250);
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
            { // Menu de inicio
                Menu.MenuEntry[] entries = {
                        new Menu.MenuEntry("Start", _ -> {
                            game = new Game(percepcao);
                            setVisible(false);
                        }),
                        new Menu.MenuEntry("DEBUG", _ -> {
                            game = new Game(percepcao);
                            setVisible(false);
                        }),
                        new Menu.MenuEntry("Exit", _ -> {
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