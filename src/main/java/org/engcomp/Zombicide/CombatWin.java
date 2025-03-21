package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.ActorObj;
import org.engcomp.Zombicide.Actors.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import org.engcomp.Zombicide.Menu.MenuEntry;

public class CombatWin extends JFrame {
    //protected Map<String, JButton> btns = new HashMap<>();
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

    public CombatWin(Player player, ActorObj foe) {
        super("Zombicide Combat");
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
        } else {
            System.out.println("No ammo / no revolver!");
        }
        afterAction();
    }

    public void melee(ActionEvent actionEvent) {
        System.out.println("Pow" + ((player.canUseItem(Item.BaseballBat))? " with bat" : ""));
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

    private void afterAction() {
        setStage(switch (getStage()) {
            case CombatStage.Starting -> {
                actionBtns = new Menu(Arrays.stream(combatEntries), BoxLayout.LINE_AXIS, new JLabel("Actions"));
                yield CombatStage.Started;
            }
            case CombatStage.Started -> {
                if (player.isDead()) {
                    cleanUpBodies();
                    yield CombatStage.PlayerDead;
                }
                if (foe.isDead()) {
                    cleanUpBodies();
                    yield CombatStage.FoeDead;
                }
                yield getStage();
            }
            default -> getStage();
        });


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
            var loc = body.getLoc();
            loc.getOccupants().remove(body);
        }
    }
}
