package org.engcomp.Zombicide;

import org.engcomp.Zombicide.Actors.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.engcomp.Zombicide.utils.Pair;
import org.engcomp.Zombicide.utils.ReactiveMenu;

public class CombatPanel extends Panel {
    //protected Map<String, JButton> btns = new HashMap<>();
    protected Game game;
    protected JLabel attackRollInfo;
    protected JLabel defenseRollInfo;
    protected JScrollPane combatLogListSPane;
    protected JList<String> combatLogList;
    protected DefaultListModel<String> combatLogData;
    protected ReactiveMenu actionBtns;
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
    protected boolean done = false;
    protected GridBagConstraints gbc;

    protected Color successClr = Color.decode("#3c9530");
    protected Color neutralClr = Color.decode("#000000");
    protected Color failureClr = Color.decode("#953c30");

    private final ArrayList<Pair<String, Pair<ActionListener, Consumer<JButton>>>> combatEntries = new ArrayList<>(List.of(
            new Pair<>("Melee", new Pair<>(this::melee, this::actionButtonEnabler)),
            new Pair<>("Shoot", new Pair<>(this::shoot, this::shootButtonEnabler)),
            new Pair<>("Flee",  new Pair<>(this::flee, this::actionButtonEnabler))
    ));

    private final ArrayList<Pair<String, Pair<ActionListener, Consumer<JButton>>>> startEntries = new ArrayList<>(List.of(
            new Pair<>("Melee", new Pair<>(this::melee, this::actionButtonEnabler)),
            new Pair<>("Shoot", new Pair<>(this::shoot, this::shootButtonEnabler)),
            new Pair<>("Reconsider", new Pair<>(this::reconsider, this::actionButtonEnabler))
    ));

    private void initLogs() {
        combatLogData = game.getCombatLog();
        combatLogList = new JList<>(combatLogData);
        combatLogListSPane = new JScrollPane(combatLogList);
    }

    public CombatPanel(Game game, Player player, Zombie foe) {
        super(new GridBagLayout());
        setName("Combat");
        this.game = game;
        setSize(250, 500);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        initLogs();

        attackRollInfo = new JLabel();
        attackRollInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(attackRollInfo, gbc);
        gbc.gridy++;
        defenseRollInfo = new JLabel();
        defenseRollInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(defenseRollInfo, gbc);
        gbc.gridy++;

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.5;
        add(combatLogListSPane, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.gridy++;
        actionBtns = ReactiveMenu.fromReactive(startEntries.stream(), BoxLayout.LINE_AXIS, new JLabel("Actions"));
        add(actionBtns, gbc); // We don't update gbc cuz this'll get overwritten later
        resetDiceInfo();
        actionBtns.updateAllButtons();

        setVisible(true);

        this.player = player; player.setInCombat(true);
        this.foe = foe; player.setInCombat(true);
        combatLog("— Started combat with " + foe + " —");
    }


    // Combat actions
    private void shoot(ActionEvent actionEvent) {
        resetDiceInfo();
        if (player.canUseItem(Item.Revolver)) {
            player.useItem(Item.Revolver);
            combatLog("Bang");
            if (foe instanceof ZombieRunner) {
                combatLog("You tried to shoot the zombie, but he was too fast for your aim!");
                return;
            }
            attack(Damage.Piercing);
        } else {
            combatLog("No ammo / no revolver!");
            return;
        }
        afterAction();
    }

    public void shootButtonEnabler(JButton btn) {
        btn.setEnabled(!done && game.getBoard().getPlayer().canUseItem(Item.Revolver));
    }

    public void actionButtonEnabler(JButton btn) {
        btn.setEnabled(!done);
    }

    public void melee(ActionEvent actionEvent) {
        resetDiceInfo();
        boolean withBat = player.canUseItem(Item.BaseballBat);
        combatLog("Pow" + (withBat? " with bat" : " barehanded"));
        var diceRoll = game.getRand().nextInt(6+1);
        var threshold = player.canUseItem(Item.BaseballBat)? 3 : 5;
        var dmg = withBat? Damage.Blunt : Damage.BareHand;
        var diceMsg = "Attack rolled a " + diceRoll + "; " + diceRoll + " > " + threshold + "?";
        if (diceRoll > threshold) {
            diceMsg += " Yes! Critical hit!";
            attackRollInfo.setForeground(successClr);
            dmg = Damage.Critical;
        } else {
            attackRollInfo.setForeground(neutralClr);
            diceMsg += " No.";
        }
        combatLog(diceMsg);
        attackRollInfo.setText(diceMsg);

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
        combatLog("You attacked " + foe + " for " + dealt + " damage!");
        if (foe.isDead()) {
            cleanUpBodies();
            setStage(CombatStage.FoeDead);
            combatLog("You killed " + foe + "!");
        }
    }

    protected void defend(Damage dmg) {
        switch (getStage()) {
            case CombatStage.Started, CombatStage.Starting: break;
            default: return;
        }
        var taken = player.dealDamage(dmg);
        combatLog("You lost " + taken + " health defending from " + foe + "!");
        if (player.isDead()) {
            cleanUpBodies();
            setStage(CombatStage.PlayerDead);
            combatLog("You died at the hands of " + foe + "!");
        }

    }

    protected void foeTurn() {
        if (getStage() == CombatStage.FoeDead) return;
        int diceRoll = game.getRand().nextInt(3+1);
        int threshold = game.getBoard().getPlayer().getPerception();
        var diceMsg = "The zombie rolled a " + diceRoll + "; " + diceRoll + " > " + threshold + "?";
        if (diceRoll > threshold) {
            defenseRollInfo.setForeground(failureClr);
            diceMsg += " Yes!";
            combatLog(diceMsg);
            defend(foe.getAttackDamage());
        } else {
            defenseRollInfo.setForeground(successClr);
            diceMsg += " No!";
            combatLog(diceMsg);
        }
        defenseRollInfo.setText(diceMsg);
    }

    public void afterAction() {
        foeTurn();
        System.out.println("player hp = " + player.getHealth() + " , " + "zombie hp = " + foe.getHealth());
        setStage(switch (getStage()) {
            case CombatStage.Starting -> {
                remove(actionBtns);
                actionBtns = ReactiveMenu.fromReactive(combatEntries.stream(), BoxLayout.LINE_AXIS, new JLabel("Actions"));
                add(actionBtns, gbc);
                revalidate();
                yield CombatStage.Started;
            }
            case CombatStage.Started -> {
                yield getStage();
            }
            case CombatStage.Reconsidered, CombatStage.Fled, CombatStage.FoeDead, CombatStage.PlayerDead -> {
                game.combatEnded(getStage());
                done = true;
                player.setInCombat(false);
                foe.setInCombat(false);
                yield getStage();
            }
        });
        actionBtns.updateAllButtons();
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

    public void resetDiceInfo() {
        attackRollInfo.setForeground(neutralClr);
        defenseRollInfo.setForeground(neutralClr);
        attackRollInfo.setText("—");
        defenseRollInfo.setText("—");
    }

    protected void combatLog(String msg) {
        combatLogData.addElement(game.getTurn() + ": " + msg);
        combatLogList.ensureIndexIsVisible(combatLogData.size()-1);
    }

}
