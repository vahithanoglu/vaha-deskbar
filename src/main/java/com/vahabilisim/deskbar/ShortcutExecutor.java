package com.vahabilisim.deskbar;

import com.vahabilisim.deskbar.db.model.Shortcut;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import org.apache.log4j.Logger;

public class ShortcutExecutor implements Runnable {

    private static final Logger LOGGER = Logger.getLogger("vahabilisim.deskbar");

    private final Shortcut shortcut;

    public ShortcutExecutor(Shortcut shortcut) {
        this.shortcut = shortcut;
    }

    @Override
    public void run() {
        Optional.ofNullable(shortcut)
                .filter(shortct -> null != shortct.getValue())
                .map(shortct -> shortct.getType())
                .ifPresent(type -> {
                    try {
                        switch (type) {
                            case FOLDER:
                                Desktop.getDesktop().open(new File(shortcut.getValue()));
                                break;

                            case EXECUTABLE:
                                if (OSInfo.isMac()) {
                                    Desktop.getDesktop().open(new File(shortcut.getValue()));

                                } else {
                                    Runtime.getRuntime().exec(shortcut.getValue(), null, new File(shortcut.getValue()).getParentFile());
                                }
                                break;

                            case LINK:
                                Desktop.getDesktop().browse(new URI(shortcut.getValue()));
                                break;

                            case COMMAND:
                                Runtime.getRuntime().exec(shortcut.getValue(), null, new File(OSInfo.getUserHome()));
                                break;
                        }
                    } catch (IOException | URISyntaxException ex) {
                        LOGGER.error("Cannot execute shortcut", ex);
                    }
                });
    }

}
