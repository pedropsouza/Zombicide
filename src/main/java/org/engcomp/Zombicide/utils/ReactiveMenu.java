package org.engcomp.Zombicide.utils;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

///  Not really reactive, couldn't think of a better name
public class ReactiveMenu extends Menu {
    private List<Pair<JButton, Consumer<JButton>>> stylers = new ArrayList<>();
    protected ReactiveMenu(int axis, JLabel title) {
        super(axis, title);
    }

    public static ReactiveMenu fromReactive(Stream<Pair<String, Pair<ActionListener, Consumer<JButton>>>> entries, int axis) {
        return fromReactive(entries, axis, null);
    }
    public static ReactiveMenu fromReactive(Stream<Pair<String, Pair<ActionListener, Consumer<JButton>>>> entries, int axis, JLabel title) {
        ReactiveMenu m = new ReactiveMenu(axis, title);

        entries.forEach(entry -> {
            var btn = new JButton(entry.l);
            btn.addActionListener(entry.r.l);
            m.btns.add(btn);
            m.stylers.add(new Pair<>(btn, entry.r.r));
            m.add(btn, m.gbc);
            m.gbcAdvance();
        });
        m.updateAllButtons();
        return m;
    }

    public void updateAllButtons() {
        stylers.forEach(i-> i.r.accept(i.l));
    }
}
