package com.vahabilisim.deskbar.gui;

import com.vahabilisim.deskbar.gui.filechooser.CustomFile;
import com.vahabilisim.deskbar.gui.icon.IconManager;
import java.awt.Color;
import java.awt.Cursor;
import java.util.Optional;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class FileListItem extends AbstractListItem<CustomFile> {

    private static final Cursor CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    private static final Color UNSELECTED_BG = Color.WHITE;
    private static final Color SELECTED_BG = Color.DARK_GRAY;

    private static final Color UNSELECTED_COLOR = Color.BLACK;
    private static final Color SELECTED_COLOR = Color.WHITE;

    private final CustomFile file;
    private final JLabel label;

    public FileListItem(CustomFile file) {
        this.file = file;

        label = new JLabel(
                // file system roots do not have names, so getName returns empty string
                Optional.ofNullable(file.getName())
                        .map(name -> name.trim())
                        .filter(name -> false == name.isEmpty())
                        .orElse(file.getPath()),
                IconManager.getFileIcon(file),
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
    public CustomFile getData() {
        return file;
    }

    @Override
    public String getUniqueId() {
        return file.getAbsolutePath();
    }

    @Override
    public void setSeleted(boolean selected) {
        setBackground(selected ? SELECTED_BG : UNSELECTED_BG);
        label.setForeground(selected ? SELECTED_COLOR : UNSELECTED_COLOR);
    }
}
