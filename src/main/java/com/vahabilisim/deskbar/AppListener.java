package com.vahabilisim.deskbar;

import com.vahabilisim.deskbar.db.model.Deskbar;
import com.vahabilisim.deskbar.db.model.Shortcut;

public interface AppListener {

    public void deskbarAddedOrUpdated(Deskbar deskbar);

    public void deskbarDeleted(Deskbar deskbar);

    public void shortcutClicked(Shortcut shortcut);

    public void showSettings();

    public void exitApp();
}
