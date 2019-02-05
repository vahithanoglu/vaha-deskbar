package com.vahabilisim.deskbar.gui;

import com.vahabilisim.deskbar.db.model.Shortcut;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class ShortcutsMenu extends JPopupMenu {

    private static final int DEFAULT_GAP = 2;

    private final ActionListener actionlistener;
    private final MouseListener mouseListener;

    private final Map<String, Shortcut> shortcutMap;

    public ShortcutsMenu(ActionListener actionlistener, MouseListener mouseListener) {
        this.actionlistener = actionlistener;
        this.mouseListener = mouseListener;
        shortcutMap = new HashMap<>();
    }

    public Shortcut getShortcut(String actionCommand) {
        return shortcutMap.get(actionCommand);
    }

    public void refresh(List<Shortcut> shortcutList) {
        shortcutMap.clear();
        removeAll();

        final int cnt = shortcutList.size();
        final double sqrt = Math.sqrt(cnt);
        final int rowCnt = (int) Math.round(sqrt);
        final int colCnt = (int) Math.ceil(sqrt);

        int counter = 0;
        for (int row = 0; row < rowCnt; row++) {
            final JComponent rowPanel = new JPanel();
            final GroupLayout layout = new GroupLayout(rowPanel);
            rowPanel.setLayout(layout);
            rowPanel.setOpaque(true);
            rowPanel.setBackground(Color.WHITE);

            final GroupLayout.SequentialGroup horizontal = layout.createSequentialGroup();
            final GroupLayout.ParallelGroup vertical = layout.createParallelGroup();
            for (int col = 0; col < colCnt && counter < cnt; col++) {
                final Shortcut shortcut = shortcutList.get(counter);
                final ShortcutButton btn = new ShortcutButton(shortcut);
                shortcutMap.put(btn.getActionCommand(), shortcut);
                btn.addActionListener(actionlistener);
                btn.addMouseListener(mouseListener);

                horizontal.addGap(DEFAULT_GAP).addComponent(btn).addGap(DEFAULT_GAP);
                vertical.addComponent(btn);

                counter++;
                if (cnt == counter) {
                    horizontal.addGap((colCnt - col - 1) * (DEFAULT_GAP + DEFAULT_GAP + btn.getWidth()));
                }
            }

            layout.setHorizontalGroup(horizontal);
            layout.setVerticalGroup(layout.createSequentialGroup().addGap(DEFAULT_GAP).addGroup(vertical).addGap(DEFAULT_GAP));

            insert(rowPanel, row);
        }
    }

}
