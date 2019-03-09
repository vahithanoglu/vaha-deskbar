package com.vahabilisim.deskbar.gui;

import com.vahabilisim.deskbar.AppListener;
import com.vahabilisim.deskbar.gui.icon.IconManager;
import com.vahabilisim.deskbar.db.model.Deskbar;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

public final class DeskbarDialog extends JDialog implements ActionListener, MouseInputListener {

    private static final Cursor CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private static final Dimension DEFAULT_DIMENSION = new Dimension(32, 32);

    private static final int UPDATE_DELAY = 500;
    private static final int FADE_OUT_DELAY = 2000;

    private final Deskbar deskbar;
    private final AppListener appListener;

    private final JLabel iconLabel;
    private final JPopupMenu popupMenu;
    private final ShortcutsMenu shortcutsMenu;

    public DeskbarDialog(Deskbar deskbar, AppListener appListener) {
        super((JFrame) null, false);

        this.deskbar = deskbar;
        this.appListener = appListener;

        iconLabel = new JLabel("", SwingConstants.CENTER);
        iconLabel.setOpaque(true);
        iconLabel.setBackground(Color.WHITE);
        iconLabel.setCursor(CURSOR);
        iconLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        iconLabel.setSize(DEFAULT_DIMENSION);
        iconLabel.setMinimumSize(DEFAULT_DIMENSION);
        iconLabel.setMaximumSize(DEFAULT_DIMENSION);
        iconLabel.setPreferredSize(DEFAULT_DIMENSION);

        popupMenu = new JPopupMenu();
        popupMenu.add(new JMenuItem("Settings",
                IconManager.getButtonIcon("settings")))
                .addActionListener(this);
        popupMenu.add(new JMenuItem("Exit",
                IconManager.getButtonIcon("exit")))
                .addActionListener(this);

        shortcutsMenu = new ShortcutsMenu(this, this);

        addMouseListener(this);
        addMouseMotionListener(this);

        setAlwaysOnTop(true);
        setResizable(false);
        setUndecorated(true);

        add(iconLabel);
        pack();

        refresh();
    }

    public void refresh() {
        // TODO : fix -> this tooltip block mouse events to be occurred on DeskbarDialog
        // iconLabel.setToolTipText(deskbar.name); 

        iconLabel.setIcon(IconManager.getDeskbarIcon(deskbar));
        shortcutsMenu.refresh(deskbar.getShortcuts());
        setLocation(deskbar.getX(), deskbar.getY());

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent act) {
        final String cmd = act.getActionCommand();

        switch (cmd) {
            case "Settings":
                appListener.showSettings();
                break;

            case "Exit":
                appListener.exitApp();
                break;

            default: // shortcut action
                shortcutsMenu.setVisible(false);
                appListener.shortcutClicked(shortcutsMenu.getShortcut(cmd));
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        Optional.ofNullable(evt.getComponent())
                .filter(comp -> this == comp)
                .ifPresent(comp -> {
                    if (SwingUtilities.isRightMouseButton(evt)) {
                        popupMenu.show(comp, evt.getX(), evt.getY());

                    } else if (SwingUtilities.isLeftMouseButton(evt)) {
                        shortcutsMenu.show(comp, comp.getWidth(), comp.getHeight());
                    }
                });
    }

    @Override
    public void mousePressed(MouseEvent evt) {
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        Optional.ofNullable(evt.getComponent())
                .filter(comp -> mouseDragged)
                .ifPresent(comp -> {
                    mouseDragged = false;

                    // save changes, in case of dragging
                    new Timer(true).schedule(new TimerTask() {

                        @Override
                        public void run() {
                            if (false == popupMenu.isVisible() && false == shortcutsMenu.isVisible()) {
                                appListener.deskbarAddedOrUpdated(deskbar);
                            }
                        }
                    }, UPDATE_DELAY);
                });
    }

    private boolean userActivity;
    private final Object lockUserActivity = new Object();
    private final List<Timer> timerList = new LinkedList<>();

    @Override
    public void mouseEntered(MouseEvent evt) {
        synchronized (lockUserActivity) {
            userActivity = true;
            timerList.forEach(timer -> {
                timer.cancel();
                timer.purge();
            });
            timerList.clear();
        }
    }

    @Override
    public void mouseExited(MouseEvent evt) {
        synchronized (lockUserActivity) {
            userActivity = false;

            if (shortcutsMenu.isVisible()) {
                final Timer fadeOut = new Timer(true);
                timerList.add(fadeOut);

                fadeOut.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (lockUserActivity) {
                            if (false == userActivity) {
                                SwingUtilities.invokeLater(() -> {
                                    shortcutsMenu.setVisible(false);
                                });
                            }
                        }
                    }
                }, FADE_OUT_DELAY);
            }
        }
    }

    private Point dragSrc;
    private Point dragDst;
    private boolean mouseDragged;

    @Override
    public void mouseMoved(MouseEvent evt) {
        dragSrc = evt.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
        mouseDragged = true;

        dragDst = evt.getPoint();

        final Point loc = getLocationOnScreen();
        loc.translate(dragDst.x, dragDst.y);
        loc.translate(-dragSrc.x, -dragSrc.y);

        deskbar.setXY(loc.x, loc.y);
        setLocation(loc.x, loc.y);
    }

}
