package org.engcomp.Zombicide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class Main {
    private static Menu menu;
    private static Menu difficultyMenu;
    private static Game game;
    static int percepcao = 2;
    public static void main(String[] args) {
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
            difficultyMenu = new Menu(Arrays.stream(entries));
        }
        { // Menu de inicio
            Menu.MenuEntry[] entries = {
                    new Menu.MenuEntry("Start", _ -> {
                        menu.setVisible(false);
                        difficultyMenu.setVisible(false);
                        game = new Game(percepcao);
                    }),
                    new Menu.MenuEntry("DEBUG", _ -> {
                        menu.setVisible(false);
                        difficultyMenu.setVisible(false);
                        game = new Game(percepcao);
                    }),
                    new Menu.MenuEntry("Exit", _ -> {
                        menu.dispatchEvent(new WindowEvent(menu, WindowEvent.WINDOW_CLOSING));
                        difficultyMenu.dispatchEvent(new WindowEvent(difficultyMenu, WindowEvent.WINDOW_CLOSING));
                    }),
            };
            menu = new Menu(Arrays.stream(entries));
        }
        game = null;
    }
}