package com.vahabilisim.deskbar.gui;

import com.vahabilisim.deskbar.gui.filechooser.CustomFileChooser;
import com.vahabilisim.deskbar.AppListener;
import com.vahabilisim.deskbar.db.model.Deskbar;
import com.vahabilisim.deskbar.db.model.Shortcut;
import com.vahabilisim.deskbar.gui.icon.IconManager;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class SettingsFrame extends JFrame implements ActionListener {

    private final AppListener appListener;

    private final AbstractListPane<Deskbar, DeskbarListItem> deskbarListPane;
    private final AbstractListPane<Shortcut, ShortcutListItem> shortcutScrollPane;

    private final JButton newDeskbarButton;

    private final JButton iconDeskbarButton;
    private final JButton renameDeskbarButton;
    private final JButton deleteDeskbarButton;
    private final JButton newShortcutButton;

    private final CustomFileChooser customFileChooser;
    private final ShortcutSettingsDialog shortcutSettingsDialog;

    private Deskbar selectedDeskbar;
    private Shortcut selectedShortcut;

    public SettingsFrame(AppListener appListener) {
        super("Settings");

        this.appListener = appListener;

        deskbarListPane = new AbstractListPane<Deskbar, DeskbarListItem>() {
            @Override
            public DeskbarListItem createItem(Deskbar data) {
                return new DeskbarListItem(data);
            }

            @Override
            public void onItemSelected(DeskbarListItem item) {
                selectedDeskbar = item.getData();
                refresh();
            }

            @Override
            public void onItemDoubleClicked(DeskbarListItem item) {
            }
        };
        deskbarListPane.setBorder(BorderFactory.createTitledBorder("Deskbars"));

        shortcutScrollPane = new AbstractListPane<Shortcut, ShortcutListItem>() {
            @Override
            public ShortcutListItem createItem(Shortcut data) {
                return new ShortcutListItem(data);
            }

            @Override
            public void onItemSelected(ShortcutListItem item) {
            }

            @Override
            public void onItemDoubleClicked(ShortcutListItem item) {
                selectedShortcut = shortcutSettingsDialog.setShortcut(item.getData());
                shortcutSettingsDialog.setLocationRelativeTo(SettingsFrame.this);
                shortcutSettingsDialog.setVisible(true);
            }
        };
        shortcutScrollPane.setBorder(BorderFactory.createTitledBorder("Shortcuts"));

        newDeskbarButton = new JButton("New Deskbar", IconManager.getButtonIcon("deskbar"));
        newDeskbarButton.setActionCommand("New Deskbar");
        newDeskbarButton.setToolTipText("Create a new deskbar");
        newDeskbarButton.addActionListener(this);

        iconDeskbarButton = new JButton();
        iconDeskbarButton.setActionCommand("Change Deskbar Icon");
        iconDeskbarButton.setToolTipText("Change icon of the deskbar");
        iconDeskbarButton.addActionListener(this);
        iconDeskbarButton.setFocusPainted(false);
        iconDeskbarButton.setBorderPainted(false);
        iconDeskbarButton.setContentAreaFilled(false);
        iconDeskbarButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        renameDeskbarButton = new JButton(IconManager.getButtonIcon("edit"));
        renameDeskbarButton.setActionCommand("Rename Deskbar");
        renameDeskbarButton.setToolTipText("Edit name of the deskbar");
        renameDeskbarButton.setHorizontalTextPosition(SwingConstants.LEFT);
        renameDeskbarButton.addActionListener(this);

        deleteDeskbarButton = new JButton(IconManager.getButtonIcon("delete"));
        deleteDeskbarButton.setActionCommand("Delete Deskbar");
        deleteDeskbarButton.setToolTipText("Delete the deskbar");
        deleteDeskbarButton.addActionListener(this);

        newShortcutButton = new JButton("New Shortcut", IconManager.getButtonIcon("shortcut"));
        newShortcutButton.setActionCommand("New Shortcut");
        newShortcutButton.setToolTipText("Add a new shortcut to the deskbar");
        newShortcutButton.addActionListener(this);

        customFileChooser = new CustomFileChooser(SettingsFrame.this);
        shortcutSettingsDialog = new ShortcutSettingsDialog(SettingsFrame.this, this);

        setLayout();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        switch (evt.getActionCommand()) {
            case "New Deskbar":
                Optional.ofNullable(JOptionPane.showInputDialog(SettingsFrame.this, "Enter the deskbar name", "New Deskbar"))
                        .map(name -> name.trim())
                        .filter(name -> false == name.isEmpty())
                        .ifPresent(name -> {
                            selectedDeskbar = new Deskbar(System.currentTimeMillis(), name, null, getX() + 250, getY() + 100, new LinkedList<>());
                            appListener.deskbarAddedOrUpdated(selectedDeskbar);
                            deskbarListPane.addOrUpdate(selectedDeskbar);
                            refresh();
                        });
                break;

            case "Change Deskbar Icon":
                Optional.ofNullable(customFileChooser.chooseImage())
                        .map(file -> IconManager.importAsIcon(file))
                        .ifPresent(iconName -> {
                            selectedDeskbar.setIcon(iconName);
                            appListener.deskbarAddedOrUpdated(selectedDeskbar);
                            deskbarListPane.addOrUpdate(selectedDeskbar);
                            refresh();
                        });
                break;

            case "Rename Deskbar":
                Optional.ofNullable(JOptionPane.showInputDialog(SettingsFrame.this, "Edit the deskbar name", selectedDeskbar.getName()))
                        .map(name -> name.trim())
                        .filter(name -> false == name.isEmpty())
                        .ifPresent(name -> {
                            selectedDeskbar.setName(name);
                            appListener.deskbarAddedOrUpdated(selectedDeskbar);
                            deskbarListPane.addOrUpdate(selectedDeskbar);
                            refresh();
                        });
                break;

            case "Delete Deskbar":
                Optional.ofNullable(JOptionPane.showConfirmDialog(SettingsFrame.this, "Are you sure to delete the deskbar?", "Delete Deskbar", JOptionPane.YES_NO_OPTION))
                        .filter(response -> JOptionPane.YES_OPTION == response)
                        .ifPresent(response -> {
                            appListener.deskbarDeleted(selectedDeskbar);
                            deskbarListPane.delete(selectedDeskbar);
                        });
                break;

            case "New Shortcut":
                selectedShortcut = shortcutSettingsDialog.setShortcut(null);
                shortcutSettingsDialog.setLocationRelativeTo(SettingsFrame.this);
                shortcutSettingsDialog.setVisible(true);
                break;

            case "Test Shortcut":
                appListener.shortcutClicked(selectedShortcut);
                break;

            case "Save Shortcut Changes":
                Optional.ofNullable(selectedShortcut.getValue())
                        .ifPresent(val -> {
                            selectedDeskbar.addOrUpdateShortcut(selectedShortcut);
                            appListener.deskbarAddedOrUpdated(selectedDeskbar);
                            shortcutScrollPane.addOrUpdate(selectedShortcut);
                            shortcutSettingsDialog.setVisible(false);
                        });
                break;

            case "Change Shortcut Icon":
                Optional.ofNullable(customFileChooser.chooseImage())
                        .map(file -> IconManager.importAsIcon(file))
                        .ifPresent(iconName -> {
                            selectedShortcut.setIcon(iconName);
                            shortcutSettingsDialog.refresh();
                        });
                break;

            case "Browse Shortcut":
                switch (selectedShortcut.getType()) {
                    case FOLDER:
                        Optional.ofNullable(customFileChooser.chooseFolder())
                                .ifPresent(file -> {
                                    selectedShortcut.setValue(file.getAbsolutePath());
                                    shortcutSettingsDialog.refresh();
                                });
                        break;

                    case EXECUTABLE:
                        Optional.ofNullable(customFileChooser.chooseExecutable())
                                .ifPresent(file -> {
                                    selectedShortcut.setValue(file.getAbsolutePath());
                                    shortcutSettingsDialog.refresh();
                                });
                        break;

                    case LINK:
                    case COMMAND:
                        Optional.ofNullable(JOptionPane.showInputDialog(SettingsFrame.this, "Edit value of the shortcut", selectedShortcut.getValue()))
                                .map(val -> val.trim())
                                .filter(val -> false == val.isEmpty())
                                .ifPresent(val -> {
                                    selectedShortcut.setValue(val);
                                    shortcutSettingsDialog.refresh();
                                });
                        break;
                }
                break;

            case "Rename Shortcut":
                Optional.ofNullable(JOptionPane.showInputDialog(SettingsFrame.this, "Edit the shortcut name", selectedShortcut.getName()))
                        .map(name -> name.trim())
                        .filter(name -> false == name.isEmpty())
                        .ifPresent(name -> {
                            selectedShortcut.setName(name);
                            shortcutSettingsDialog.refresh();
                        });
                break;

            case "Delete Shortcut":
                Optional.ofNullable(JOptionPane.showConfirmDialog(SettingsFrame.this, "Are you sure to delete the shortcut?", "Delete Shortcut", JOptionPane.YES_NO_OPTION))
                        .filter(response -> JOptionPane.YES_OPTION == response)
                        .ifPresent(response -> {
                            selectedDeskbar.deleteShortcut(selectedShortcut);
                            appListener.deskbarAddedOrUpdated(selectedDeskbar);
                            shortcutScrollPane.delete(selectedShortcut);
                            shortcutSettingsDialog.setVisible(false);
                        });
                break;
        }
    }

    public void setDeskbars(List<Deskbar> deskbarList) {
        deskbarListPane.setList(deskbarList);
    }

    private void refresh() {
        Optional.ofNullable(selectedDeskbar)
                .ifPresent(deskbar -> {
                    final String name = deskbar.getName();
                    renameDeskbarButton.setText(name.length() < 16 ? name : name.substring(0, 12) + "...");
                    iconDeskbarButton.setIcon(IconManager.getDeskbarIcon(deskbar));
                    shortcutScrollPane.setList(deskbar.getShortcuts());

                    // prevent delete button to be visible in case of having a single deskbar item
                    deleteDeskbarButton.setVisible(deskbarListPane.getCount() > 1);
                });
    }

    private void setLayout() {
        final GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGap(8)
                .addGroup(layout.createParallelGroup()
                        .addComponent(deskbarListPane, 200, 200, 200)
                        .addComponent(newDeskbarButton, 200, 200, 200))
                .addGap(8)
                .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(iconDeskbarButton, 32, 32, 32)
                                .addGap(4)
                                .addComponent(renameDeskbarButton)
                                .addGap(4, 4, Short.MAX_VALUE)
                                .addComponent(deleteDeskbarButton, 32, 32, 32)
                                .addGap(4)
                                .addComponent(newShortcutButton))
                        .addComponent(shortcutScrollPane, 400, 400, 400))
                .addGap(8)
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGap(8)
                .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(iconDeskbarButton, 32, 32, 32)
                                        .addComponent(renameDeskbarButton, 32, 32, 32)
                                        .addComponent(deleteDeskbarButton, 32, 32, 32)
                                        .addComponent(newShortcutButton, 32, 32, 32))
                                .addGap(8)
                                .addComponent(shortcutScrollPane, 400, 400, 400))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(deskbarListPane)
                                .addGap(8)
                                .addComponent(newDeskbarButton, 32, 32, 32)))
                .addGap(8)
        );

        pack();

        setResizable(false);
        setIconImages(IconManager.getAppIcons());
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }

}
