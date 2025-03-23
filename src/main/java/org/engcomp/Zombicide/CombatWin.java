package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.engcomp.Zombicide.utils.Menu;
import org.engcomp.Zombicide.utils.Menu.MenuEntry;

public class CombatWin extends Box {
    //protected Map<String, JButton> btns = new HashMap<>();
    protected Game game;
    protected JLabel diceRollInfo;
    protected JList<String> combatLogList;
    protected DefaultListModel<String> combatLog;
    protected Menu actionBtns;
    protected Player player;
    protected Zombie foe;
    public enum CombatStage {
        Starting,
        Started,
        PlayerDead,
        FoeDead,
        Fled,
        Reconsidered,
    };
    protected CombatStage stage = CombatStage.Starting;

    private final MenuEntry[] combatEntries = {
            new MenuEntry("Melee", this::melee),
            new MenuEntry("Shoot", this::shoot),
            new MenuEntry("Flee", this::flee),
    };

    private final MenuEntry[] startEntries = {
            new MenuEntry("Melee", this::melee),
            new MenuEntry("Shoot", this::shoot),
            new MenuEntry("Reconsider", this::reconsider),
    };

    public CombatWin(Game game, Player player, Zombie foe) {
        super(BoxLayout.PAGE_AXIS);
        this.game = game;
        //setSize(300, 500);

        diceRollInfo = new JLabel("-- ROLL --");
        add(diceRollInfo);
        combatLog = new DefaultListModel<>();
        combatLogList = new JList<>(combatLog);
        add(combatLogList);
        actionBtns = new Menu(Arrays.stream(startEntries), BoxLayout.LINE_AXIS, new JLabel("Actions"));
        add(actionBtns);

        setVisible(true);

        this.player = player;
        this.foe = foe;
    }


    // Combat actions
    private void shoot(ActionEvent actionEvent) {
        if (player.canUseItem(Item.Revolver)) {
            player.useItem(Item.Revolver);
            combatLog.addElement("Bang");
            if (foe instanceof ZombieRunner) {
                combatLog.addElement("You tried to shoot the zombie, but he was too fast for your aim!");
                return;
            }
            attack(Damage.Piercing);
        } else {
            combatLog.addElement("No ammo / no revolver!");
            return;
        }
        afterAction();
    }

    public void melee(ActionEvent actionEvent) {
        boolean withBat = player.canUseItem(Item.BaseballBat);
        combatLog.addElement("Pow" + (withBat? " with bat" : " barehanded"));
        var diceRoll = game.getRand().nextInt(6+1);
        var threshold = player.canUseItem(Item.BaseballBat)? 3 : 5;
        var dmg = withBat? Damage.Blunt : Damage.BareHand;
        if (diceRoll > threshold) {
            combatLog.addElement("Critical hit!");
            dmg = Damage.Critical;
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

    protected void attack(Damage dmg) {
        var dealt = foe.dealDamage(dmg);
        combatLog.addElement("You attacked " + foe + " for " + dealt + " damage!");
        if (foe.isDead()) {
            cleanUpBodies();
            setStage(CombatStage.FoeDead);
        }
    }

    protected void defend(Damage dmg) {
        var taken = player.dealDamage(dmg);
        combatLog.addElement("You lost " + taken + " health defending from " + foe + "!");
        if (player.isDead()) {
            cleanUpBodies();
            setStage(CombatStage.PlayerDead);
        }

    }

    protected void foeTurn() {
        int diceRoll = game.getRand().nextInt(3+1);
        int threshold = game.getBoard().getPlayer().getPerception();
        if (diceRoll > threshold) {
            defend(foe.getAttackDamage());
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
