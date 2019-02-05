package com.vahabilisim.deskbar.gui;

import com.vahabilisim.deskbar.AppListener;
import com.vahabilisim.deskbar.db.model.Deskbar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class AppGUI {

    private final AppListener appListener;
    private final Map<Long, DeskbarDialog> deskbarMap;

    private SettingsFrame settingsFrame;

    public AppGUI(AppListener appListener) {
        this.appListener = appListener;

        deskbarMap = new HashMap<>();

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                JFrame.setDefaultLookAndFeelDecorated(true);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            }

            settingsFrame = new SettingsFrame(appListener);
        });
    }

    public void showSettings() {
        SwingUtilities.invokeLater(() -> {
            settingsFrame.setLocationRelativeTo(null);
            settingsFrame.setVisible(true);
        });
    }

    public void setDeskbars(List<Deskbar> deskbarList) {
        SwingUtilities.invokeLater(() -> {
            deskbarList.forEach(deskbar -> {
                deskbarMap.put(deskbar.id, new DeskbarDialog(deskbar, appListener));
            });
            settingsFrame.setDeskbars(deskbarList);
        });
    }

    public void refreshDeskbar(Deskbar deskbar) {
        Optional.ofNullable(deskbar)
                .map(dskbar -> deskbarMap.get(dskbar.id))
                .ifPresentOrElse(dialog -> {
                    SwingUtilities.invokeLater(() -> {
                        dialog.refresh();
                    });
                }, () -> {
                    deskbarMap.put(deskbar.id, new DeskbarDialog(deskbar, appListener));
                });
    }

    public void removeDeskbar(Deskbar deskbar) {
        Optional.ofNullable(deskbar)
                .map(dskbar -> deskbarMap.remove(dskbar.id))
                .ifPresent(dialog -> {
                    SwingUtilities.invokeLater(() -> {
                        dialog.setVisible(false);
                    });
                });
    }

}
