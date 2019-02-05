package com.vahabilisim.deskbar.gui;

import com.vahabilisim.deskbar.gui.icon.IconManager;
import com.vahabilisim.deskbar.db.model.Shortcut;
import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.JButton;

public final class ShortcutButton extends JButton {

    private static final Cursor CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private static final Dimension DEFAULT_DIMENSION = new Dimension(32, 32);

    public ShortcutButton(Shortcut shortcut) {
        super(IconManager.getShortcutIcon(shortcut));

        setActionCommand(String.valueOf(shortcut.id));
        setToolTipText(shortcut.getName());

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(CURSOR);

        setSize(DEFAULT_DIMENSION);
        setMinimumSize(DEFAULT_DIMENSION);
        setMaximumSize(DEFAULT_DIMENSION);
        setPreferredSize(DEFAULT_DIMENSION);
    }

}
