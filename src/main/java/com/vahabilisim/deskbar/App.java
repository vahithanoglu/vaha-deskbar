package com.vahabilisim.deskbar;

import com.vahabilisim.deskbar.db.AppDB;
import com.vahabilisim.deskbar.db.AppDBCore;
import com.vahabilisim.deskbar.gui.AppGUI;
import com.vahabilisim.deskbar.db.model.Deskbar;
import com.vahabilisim.deskbar.db.model.Shortcut;
import com.vahabilisim.deskbar.gui.icon.IconManager;
import com.vahabilisim.localdb.LocalDBCore;
import com.vahabilisim.localdb.LocalDBException;
import java.io.File;
import java.util.Optional;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class App implements AppListener {

    private static final Logger LOGGER = Logger.getLogger("vahabilisim.deskbar");

    public static void main(String[] args) {
        PropertyConfigurator.configure(App.class.getResource("/com/vahabilisim/deskbar/log4j.properties"));

        // create application directory named "vahadeskbar" under user home directory
        final File appDir = new File(OSInfo.getUserHome(), "vahadeskbar");
        appDir.mkdir();

        try {
            new App(appDir.getAbsolutePath()).init();

        } catch (LocalDBException ex) {
            LOGGER.error("Cannot init app", ex);
            System.exit(1);
        }
    }

    private final String appDir;
    private final LocalDBCore dbCore;

    private final AppDB appDB;
    private final AppGUI appGUI;

    private App(String appDir) throws LocalDBException {
        this.appDir = appDir;
        dbCore = new AppDBCore(appDir);

        appDB = new AppDB(dbCore);
        appGUI = new AppGUI(this);
    }

    private void init() {
        Optional.ofNullable(appDB.getDeskbarList())
                .ifPresent(deskbarList -> {
                    IconManager.init(appDir, deskbarList);
                    appGUI.setDeskbars(deskbarList);
                });
    }

    @Override
    public void deskbarAddedOrUpdated(Deskbar deskbar) {
        appDB.updateDeskbar(deskbar);
        appGUI.refreshDeskbar(deskbar);
    }

    @Override
    public void deskbarDeleted(Deskbar deskbar) {
        appDB.deleteDeskbar(deskbar);
        appGUI.removeDeskbar(deskbar);
    }

    @Override
    public void shortcutClicked(Shortcut shortcut) {
        new Thread(new ShortcutExecutor(shortcut)).start();
    }

    @Override
    public void showSettings() {
        appGUI.showSettings();
    }

    @Override
    public void exitApp() {
        System.exit(0);
    }
}
