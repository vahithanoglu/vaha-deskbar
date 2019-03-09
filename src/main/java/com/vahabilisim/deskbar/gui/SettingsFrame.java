package com.vahabilisim.deskbar.gui;

import com.vahabilisim.deskbar.gui.filechooser.CustomFileChooser;
import com.vahabilisim.deskbar.AppListener;
import com.vahabilisim.deskbar.db.model.Deskbar;
import com.vahabilisim.deskbar.db.model.Shortcut;
import com.vahabilisim.deskbar.gui.icon.IconManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;

public class SettingsFrame extends JFrame implements ActionListener {

    private final AppListener appListener;

    private final AbstractListPane<Deskbar, DeskbarListItem> deskbarListPane;
    private final AbstractListPane<Shortcut, ShortcutListItem> shortcutScrollPane;

    private final JButton newDeskbarButton;

    private final JMenuItem iconDeskbar;
    private final JMenuItem renameDeskbar;
    private final JMenuItem deleteDeskbar;
    private final JMenuItem addShortcut;
    private final JMenuItem mergeDeskbar;

    private final CustomFileChooser customFileChooser;
    private final DeskbarChooserDialog deskbarChooseDialog;
    private final ShortcutSettingsDialog shortcutSettingsDialog;

    private final JPopupMenu popupMenu;

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

            @Override
            public void onItemRightClicked(DeskbarListItem item, int x, int y) {
                popupMenu.show(item, x, y);
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

            @Override
            public void onItemRightClicked(ShortcutListItem item, int x, int y) {
            }
        };
        shortcutScrollPane.setBorder(BorderFactory.createTitledBorder("Shortcuts"));

        newDeskbarButton = new JButton("New Deskbar", IconManager.getButtonIcon("deskbar"));
        newDeskbarButton.setActionCommand("New Deskbar");
        newDeskbarButton.setToolTipText("Create a new deskbar");
        newDeskbarButton.addActionListener(this);

        iconDeskbar = new JMenuItem("Change icon", IconManager.getButtonIcon("icon"));
        iconDeskbar.setActionCommand("Change Deskbar Icon");
        iconDeskbar.addActionListener(this);

        renameDeskbar = new JMenuItem("Rename", IconManager.getButtonIcon("edit"));
        renameDeskbar.setActionCommand("Rename Deskbar");
        renameDeskbar.addActionListener(this);

        deleteDeskbar = new JMenuItem("Delete", IconManager.getButtonIcon("delete"));
        deleteDeskbar.setActionCommand("Delete Deskbar");
        deleteDeskbar.addActionListener(this);

        addShortcut = new JMenuItem("Add shortcut", IconManager.getButtonIcon("shortcut"));
        addShortcut.setActionCommand("Add Shortcut");
        addShortcut.addActionListener(this);

        mergeDeskbar = new JMenuItem("Merge with...", IconManager.getButtonIcon("deskbar"));
        mergeDeskbar.setActionCommand("Merge Deskbar");
        mergeDeskbar.addActionListener(this);

        popupMenu = new JPopupMenu();
        popupMenu.add(iconDeskbar);
        popupMenu.add(renameDeskbar);
        popupMenu.add(deleteDeskbar);
        popupMenu.add(addShortcut);
        popupMenu.add(mergeDeskbar);

        customFileChooser = new CustomFileChooser(SettingsFrame.this);
        deskbarChooseDialog = new DeskbarChooserDialog(SettingsFrame.this);
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

            case "Merge Deskbar":
                final List<Deskbar> deskbarList = deskbarListPane.getList();
                deskbarList.remove(selectedDeskbar);

                Optional.ofNullable(deskbarChooseDialog.chooseDeskbar(deskbarList))
                        .ifPresent(deskbar -> {
                            final Deskbar deletedDeskbar = selectedDeskbar;
                            appListener.deskbarDeleted(selectedDeskbar);
                            deskbarListPane.setAutoSelect(false);
                            deskbarListPane.delete(selectedDeskbar);

                            selectedDeskbar.getShortcuts().forEach(shortcut -> {
                                deskbar.addOrUpdateShortcut(shortcut);
                            });

                            selectedDeskbar = deskbar;
                            appListener.deskbarAddedOrUpdated(selectedDeskbar);
                            deskbarListPane.setAutoSelect(true);
                            deskbarListPane.addOrUpdate(selectedDeskbar);
                        });
                break;

            case "Add Shortcut":
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
                    shortcutScrollPane.setList(deskbar.getShortcuts());

                    // prevent deleteDeskbar and mergeDeskbar menu items to be visible in case of having a single deskbar item
                    deleteDeskbar.setVisible(deskbarListPane.getCount() > 1);
                    mergeDeskbar.setVisible(deskbarListPane.getCount() > 1);
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
                .addComponent(shortcutScrollPane, 400, 400, 400)
                .addGap(8)
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGap(8)
                .addGroup(layout.createParallelGroup()
                        .addComponent(shortcutScrollPane, 400, 400, 400)
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
