package org.engcomp.Zombicide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class Main {
    private static Menu menu;
    private static Game game;
    public static void main(String[] args) {
        Menu.MenuEntry[] mainMenuEntries = {
                new Menu.MenuEntry("Start", _ -> {
                    menu.setVisible(false);
                    game = new Game();
                }),
                new Menu.MenuEntry("DEBUG", _ -> {
                    menu.setVisible(false);
                    game = new Game();
                }),
                new Menu.MenuEntry("Exit", _ -> {
                    menu.dispatchEvent(new WindowEvent(menu, WindowEvent.WINDOW_CLOSING));
                }),
        };
        menu = new Menu(Arrays.stream(mainMenuEntries));
        game = null;
    }
}