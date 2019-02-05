package com.vahabilisim.deskbar.gui;

import com.vahabilisim.deskbar.db.model.Shortcut;
import com.vahabilisim.deskbar.gui.icon.IconManager;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class ShortcutListItem extends AbstractListItem<Shortcut> {

    private static final Cursor CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    private static final Color UNSELECTED_BG = Color.WHITE;
    private static final Color SELECTED_BG = Color.DARK_GRAY;

    private static final Color UNSELECTED_COLOR = Color.BLACK;
    private static final Color SELECTED_COLOR = Color.WHITE;

    private final Shortcut shortcut;
    private final JLabel nameLabel;
    private final JLabel typeLabel;
    private final JLabel valueLabel;
    private final JSeparator separator;

    public ShortcutListItem(Shortcut shortcut) {
        this.shortcut = shortcut;

        nameLabel = new JLabel(shortcut.getName(),
                IconManager.getShortcutIcon(shortcut),
                SwingConstants.LEFT);
        typeLabel = new JLabel(shortcut.getType().name(), SwingConstants.RIGHT);
        valueLabel = new JLabel(shortcut.getValue());
        separator = new JSeparator(SwingConstants.HORIZONTAL);

        final GroupLayout layout = new GroupLayout(this);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(nameLabel, 1, 1, Short.MAX_VALUE)
                                .addGap(4)
                                .addComponent(typeLabel, 100, 100, 100)
                                .addGap(4))
                        .addComponent(valueLabel, 1, 1, Short.MAX_VALUE)
                        .addComponent(separator, 1, 1, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(4)
                        .addGroup(layout.createParallelGroup()
                                .addComponent(nameLabel, 32, 32, 32)
                                .addComponent(typeLabel, 32, 32, 32))
                        .addComponent(valueLabel)
                        .addGap(2)
                        .addComponent(separator, 2, 2, 2)
        );

        setLayout(layout);
        setCursor(CURSOR);
        setSeleted(false);
    }

    @Override
    public Shortcut getData() {
        return shortcut;
    }

    @Override
    public String getUniqueId() {
        return String.valueOf(shortcut.id);
    }

    @Override
    public void setSeleted(boolean selected) {
        setBackground(selected ? SELECTED_BG : UNSELECTED_BG);
        nameLabel.setForeground(selected ? SELECTED_COLOR : UNSELECTED_COLOR);
        typeLabel.setForeground(selected ? SELECTED_COLOR : UNSELECTED_COLOR);
        valueLabel.setForeground(selected ? SELECTED_COLOR : UNSELECTED_COLOR);
    }
}
