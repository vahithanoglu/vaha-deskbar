package com.vahabilisim.deskbar.gui;

import com.vahabilisim.deskbar.db.model.Deskbar;
import com.vahabilisim.deskbar.gui.icon.IconManager;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class DeskbarListItem extends AbstractListItem<Deskbar> {

    private static final Cursor CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    private static final Color UNSELECTED_BG = Color.WHITE;
    private static final Color SELECTED_BG = Color.DARK_GRAY;

    private static final Color UNSELECTED_COLOR = Color.BLACK;
    private static final Color SELECTED_COLOR = Color.WHITE;

    private final Deskbar deskbar;
    private final JLabel label;

    public DeskbarListItem(Deskbar deskbar) {
        this.deskbar = deskbar;

        label = new JLabel(deskbar.getName(),
                IconManager.getDeskbarIcon(deskbar),
                SwingConstants.LEFT);

        final GroupLayout layout = new GroupLayout(this);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addComponent(label, 1, 1, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(label)
                        .addGap(4)
        );

        setLayout(layout);
        setCursor(CURSOR);
        setSeleted(false);
    }

    @Override
    public Deskbar getData() {
        return deskbar;
    }

    @Override
    public String getUniqueId() {
        return String.valueOf(deskbar.id);
    }

    @Override
    public void setSeleted(boolean selected) {
        setBackground(selected ? SELECTED_BG : UNSELECTED_BG);
        label.setForeground(selected ? SELECTED_COLOR : UNSELECTED_COLOR);
    }
}
