package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.ActorObj;
import org.engcomp.Zombicide.Actors.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.engcomp.Zombicide.Actors.ZombieRunner;
import org.engcomp.Zombicide.Menu.MenuEntry;

public class CombatWin extends JFrame {
    //protected Map<String, JButton> btns = new HashMap<>();
    protected Game game;
    protected Menu actionBtns;
    protected Player player;
    protected ActorObj foe;
    public enum CombatStage {
        Starting,
        Started,
        PlayerDead,
        FoeDead,
        Fled,
        Reconsidered,
    };
    protected CombatStage stage = CombatStage.Starting;

    private MenuEntry[] combatEntries = {
            new MenuEntry("Melee", this::melee),
            new MenuEntry("Shoot", this::shoot),
            new MenuEntry("Flee", this::flee),
    };

    private MenuEntry[] startEntries = {
            new MenuEntry("Melee", this::melee),
            new MenuEntry("Shoot", this::shoot),
            new MenuEntry("Reconsider", this::reconsider),
    };

    public CombatWin(Game game, Player player, ActorObj foe) {
        super("Zombicide Combat");
        this.game = game;
        var layout = new BorderLayout();
        setLayout(layout);
        setSize(300, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        actionBtns = new Menu(Arrays.stream(startEntries), BoxLayout.LINE_AXIS, new JLabel("Actions"));
        add(actionBtns, BorderLayout.SOUTH);

        setVisible(true);

        this.player = player;
        this.foe = foe;
    }


    // Combat actions
    private void shoot(ActionEvent actionEvent) {
        if (player.canUseItem(Item.Revolver)) {
            player.useItem(Item.Revolver);
            System.out.println("Bang");
            if (foe instanceof ZombieRunner) {
                System.out.println("You tried to shoot the zombie, but he was too fast for your aim!");
                return;
            }
            final int dmg = 2;
            attack(dmg);
        } else {
            System.out.println("No ammo / no revolver!");
            return;
        }
        afterAction();
    }

    public void melee(ActionEvent actionEvent) {
        System.out.println("Pow" + ((player.canUseItem(Item.BaseballBat))? " with bat" : ""));
        var diceRoll = game.getRand().nextInt(6+1);
        var threshold = player.canUseItem(Item.BaseballBat)? 3 : 5;
        var dmg = 1;
        if (diceRoll > threshold) {
            System.out.println("Critical hit!");
            dmg = 2;
        }
        attack(dmg);
        afterAction();
    }

    private void flee(ActionEvent actionEvent) {
        setStage(CombatStage.Fled);
        afterAction();
    }

    private void reconsider(ActionEvent actionEvent) {
        setStage(CombatStage.Reconsidered);
        afterAction();
    }

    public CombatStage getStage() {
        return stage;
    }

    protected void setStage(CombatStage stage) {
        this.stage = stage;
    }

    protected void attack(int dmg) {
        JOptionPane.showMessageDialog(this, "attacked " + foe + " for " + dmg + " damage!");
        foe.setHealth(foe.getHealth() - dmg);
        if (foe.isDead()) {
            cleanUpBodies();
            setStage(CombatStage.FoeDead);
        }
    }

    protected void defend(int dmg) {
        JOptionPane.showMessageDialog(this, "lost " + dmg + " health defending from " + foe + "!");
        if (player.isDead()) {
            cleanUpBodies();
            setStage(CombatStage.PlayerDead);
        }

    }

    protected void foeTurn() {
        int diceRoll = game.getRand().nextInt(3+1);
        int threshold = game.getBoard().getPlayer().getPerception();
        if (diceRoll > threshold) {
            defend(1);
        }
    }

    private void afterAction() {
        System.out.println("player hp = " + player.getHealth() + " , " + "zombie hp = " + foe.getHealth());
        setStage(switch (getStage()) {
            case CombatStage.Starting -> {
                remove(actionBtns);
                actionBtns = new Menu(Arrays.stream(combatEntries), BoxLayout.LINE_AXIS, new JLabel("Actions"));
                add(actionBtns, BorderLayout.SOUTH);
                revalidate();
                foeTurn();
                yield CombatStage.Started;
            }
            case CombatStage.Started -> {
                foeTurn();
                yield getStage();
            }
            case CombatStage.Reconsidered, CombatStage.Fled, CombatStage.FoeDead, CombatStage.PlayerDead -> {
                game.combatEnded(getStage());
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                yield getStage();
            }
            default -> getStage();
        });
        game.finishTurn();
    }

    private void cleanUpBodies() {
        List<ActorObj> bodies = new ArrayList<>(1);
        if (player.isDead()) {
            bodies.add(player);
        }
        if (foe.isDead()) {
            bodies.add(foe);
        }

        for (var body : bodies) {
            game.removeActor(body);
        }
    }
}
