package com.vahabilisim.deskbar.gui;

import com.vahabilisim.deskbar.db.model.Shortcut;
import com.vahabilisim.deskbar.db.model.ShortcutType;
import com.vahabilisim.deskbar.gui.icon.IconManager;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Optional;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class ShortcutSettingsDialog extends JDialog implements ItemListener {

    private final JButton iconShortcutButton;
    private final JButton renameShortcutButton;
    private final JButton deleteShortcutButton;

    private final JComboBox<ShortcutType> typeComboBox;
    private final JButton browseShortcutButton;

    private final JButton testShortcutButton;
    private final JButton saveChangesButton;

    private Shortcut currShortcut;

    public ShortcutSettingsDialog(JFrame parent, ActionListener actionListener) {
        super(parent, "Shortcut", true);

        iconShortcutButton = new JButton();
        iconShortcutButton.setActionCommand("Change Shortcut Icon");
        iconShortcutButton.setToolTipText("Change icon of the shortcut");
        iconShortcutButton.addActionListener(actionListener);
        iconShortcutButton.setFocusPainted(false);
        iconShortcutButton.setBorderPainted(false);
        iconShortcutButton.setContentAreaFilled(false);
        iconShortcutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        renameShortcutButton = new JButton(IconManager.getButtonIcon("edit"));
        renameShortcutButton.setActionCommand("Rename Shortcut");
        renameShortcutButton.setToolTipText("Edit name of the shortcut");
        renameShortcutButton.setHorizontalTextPosition(SwingConstants.LEFT);
        renameShortcutButton.addActionListener(actionListener);

        deleteShortcutButton = new JButton(IconManager.getButtonIcon("delete"));
        deleteShortcutButton.setActionCommand("Delete Shortcut");
        deleteShortcutButton.setToolTipText("Delete the shortcut");
        deleteShortcutButton.addActionListener(actionListener);

        typeComboBox = new JComboBox<>(ShortcutType.values());
        typeComboBox.addItemListener(this);

        browseShortcutButton = new JButton();
        browseShortcutButton.setActionCommand("Browse Shortcut");
        browseShortcutButton.setToolTipText("Edit value of the shortcut");
        browseShortcutButton.setHorizontalAlignment(SwingConstants.LEFT);
        browseShortcutButton.addActionListener(actionListener);

        testShortcutButton = new JButton("Test", IconManager.getButtonIcon("run"));
        testShortcutButton.setActionCommand("Test Shortcut");
        testShortcutButton.setToolTipText("Execute the shortcut for testing purpose");
        testShortcutButton.addActionListener(actionListener);

        saveChangesButton = new JButton("Save Changes", IconManager.getButtonIcon("save"));
        saveChangesButton.setActionCommand("Save Shortcut Changes");
        saveChangesButton.setToolTipText("Save changes on the shortcut");
        saveChangesButton.addActionListener(actionListener);

        setLayout();
    }

    @Override
    public void itemStateChanged(ItemEvent evt) {
        Optional.ofNullable(evt.getStateChange())
                .filter(stateChanged -> ItemEvent.SELECTED == stateChanged)
                .map(stateChanged -> (ShortcutType) typeComboBox.getSelectedItem())
                .ifPresent(type -> {
                    currShortcut.setType(type);
                    currShortcut.setValue(null);
                    refresh();
                });
    }

    public Shortcut setShortcut(Shortcut shortcut) {
        Optional.ofNullable(shortcut)
                .ifPresentOrElse(shortct -> {
                    currShortcut = new Shortcut(shortct.id, shortct.getType(), shortct.getName(), shortct.getIcon(), shortct.getValue());
                    deleteShortcutButton.setVisible(true);

                }, () -> {
                    currShortcut = new Shortcut(System.currentTimeMillis(), ShortcutType.FOLDER, "New Shortcut", null, null);
                    deleteShortcutButton.setVisible(false);
                });

        // due to duplicate call of refresh by itemStateChanged, we remove the listener
        typeComboBox.removeItemListener(this);
        refresh();
        typeComboBox.addItemListener(this);

        return currShortcut;
    }

    public void refresh() {
        Optional.ofNullable(currShortcut)
                .ifPresent(shortct -> {
                    final String name = shortct.getName();
                    renameShortcutButton.setText(name.length() < 16 ? name : name.substring(0, 12) + "...");
                    iconShortcutButton.setIcon(IconManager.getShortcutIcon(shortct));
                    typeComboBox.setSelectedItem(shortct.getType());
                    browseShortcutButton.setText(shortct.getValue());

                    switch (shortct.getType()) {
                        case FOLDER:
                        case EXECUTABLE:
                            browseShortcutButton.setIcon(IconManager.getButtonIcon("browse"));
                            break;
                        case LINK:
                        case COMMAND:
                            browseShortcutButton.setIcon(IconManager.getButtonIcon("edit"));
                            break;
                    }
                });
    }

    private void setLayout() {
        final GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGap(8)
                .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(iconShortcutButton, 32, 32, 32)
                                .addGap(4)
                                .addComponent(renameShortcutButton)
                                .addGap(4, 4, Short.MAX_VALUE)
                                .addComponent(deleteShortcutButton, 32, 32, 32))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(typeComboBox, 150, 150, 150)
                                .addGap(4)
                                .addComponent(browseShortcutButton, 250, 250, 250))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(testShortcutButton)
                                .addGap(4, 4, Short.MAX_VALUE)
                                .addComponent(saveChangesButton)))
                .addGap(8)
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGap(8)
                .addGroup(layout.createParallelGroup()
                        .addComponent(iconShortcutButton, 32, 32, 32)
                        .addComponent(renameShortcutButton, 32, 32, 32)
                        .addComponent(deleteShortcutButton, 32, 32, 32))
                .addGap(8)
                .addGroup(layout.createParallelGroup()
                        .addComponent(typeComboBox, 32, 32, 32)
                        .addComponent(browseShortcutButton, 32, 32, 32))
                .addGap(8)
                .addGroup(layout.createParallelGroup()
                        .addComponent(testShortcutButton, 32, 32, 32)
                        .addComponent(saveChangesButton, 32, 32, 32))
                .addGap(8)
        );

        pack();

        setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }

}
