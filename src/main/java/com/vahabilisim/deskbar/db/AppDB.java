package com.vahabilisim.deskbar.db;

import com.vahabilisim.deskbar.db.model.Deskbar;
import com.vahabilisim.deskbar.db.model.Shortcut;
import com.vahabilisim.deskbar.db.model.ShortcutType;
import com.vahabilisim.localdb.LocalDBCore;
import com.vahabilisim.localdb.LocalDBCursor;
import com.vahabilisim.localdb.LocalDBException;
import com.vahabilisim.localdb.LocalDBTrans;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class AppDB {

    private static final Logger LOGGER = Logger.getLogger("vahabilisim.deskbar");

    private final LocalDBCore core;

    public AppDB(LocalDBCore core) {
        this.core = core;
    }

    private List<Shortcut> getShortcutList(long deskbarid) {
        final List<Shortcut> retVal = new LinkedList<>();
        LocalDBTrans trans = null;
        try {
            trans = core.startReadableTrans();
            final LocalDBCursor cursor = trans.query("shortcut",
                    new String[]{"id", "type", "name", "icon", "value"},
                    "deskbarid = ?", new String[]{String.valueOf(deskbarid)},
                    null, null, "id", null);
            if (trans.success() && cursor.moveToFirst()) {
                do {
                    final Shortcut shortcut = new Shortcut(cursor.getLong(0),
                            ShortcutType.valueOf(cursor.getString(1)),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4));

                    retVal.add(shortcut);
                } while (cursor.moveToNext());
            }
        } catch (LocalDBException ex) {
            LOGGER.error("Cannot get shortcut list", ex);

        } finally {
            if (null != trans) {
                trans.commit();
            }
        }

        return retVal;
    }

    public List<Deskbar> getDeskbarList() {
        final List<Deskbar> retVal = new LinkedList<>();
        LocalDBTrans trans = null;
        try {
            trans = core.startReadableTrans();
            final LocalDBCursor cursor = trans.query("deskbar",
                    new String[]{"id", "name", "icon", "x", "y"},
                    null, null, null, null, "id", null);
            if (trans.success() && cursor.moveToFirst()) {
                do {
                    final long id = cursor.getLong(0);
                    final Deskbar deskbar = new Deskbar(id,
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getInt(3),
                            cursor.getInt(4),
                            getShortcutList(id));

                    retVal.add(deskbar);
                } while (cursor.moveToNext());
            }
        } catch (LocalDBException ex) {
            LOGGER.error("Cannot get deskbar list", ex);

        } finally {
            if (null != trans) {
                trans.commit();
            }
        }

        return retVal;
    }

    public void updateDeskbar(Deskbar deskbar) {
        deleteDeskbar(deskbar);

        LocalDBTrans trans = null;
        try {
            trans = core.startWritableTrans();

            final Map<String, Object> values = new HashMap<>();
            values.put("id", deskbar.id);
            values.put("name", deskbar.getName());
            values.put("icon", deskbar.getIcon());
            values.put("x", deskbar.getX());
            values.put("y", deskbar.getY());
            trans.insert("deskbar", null, values);

            for (Shortcut shortcut : deskbar.getShortcuts()) {
                values.clear();
                values.put("deskbarid", deskbar.id);
                values.put("id", shortcut.id);
                values.put("type", shortcut.getType().name());
                values.put("name", shortcut.getName());
                values.put("icon", shortcut.getIcon());
                values.put("value", shortcut.getValue());
                trans.insert("shortcut", null, values);
            }
        } catch (LocalDBException ex) {
            LOGGER.error("Cannot update deskbar", ex);

        } finally {
            if (null != trans) {
                trans.commit();
            }
        }
    }

    public void deleteDeskbar(Deskbar deskbar) {
        LocalDBTrans trans = null;
        try {
            trans = core.startWritableTrans();
            trans.delete("deskbar", "id = ?", new String[]{String.valueOf(deskbar.id)});
            trans.delete("shortcut", "deskbarid = ?", new String[]{String.valueOf(deskbar.id)});

        } catch (LocalDBException ex) {
            LOGGER.error("Cannot delete deskbar", ex);

        } finally {
            if (null != trans) {
                trans.commit();
            }
        }
    }

}
