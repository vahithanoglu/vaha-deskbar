package com.vahabilisim.deskbar.db;

import com.vahabilisim.localdb.LocalDBException;
import com.vahabilisim.localdb.LocalDBTrans;
import com.vahabilisim.localdb.sqlite.SQLiteDBCore;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AppDBCore extends SQLiteDBCore {

    private static final int VERSION = 1;
    private static final int TIMEOUT_IN_SEC = 5;

    public AppDBCore(String appDir) throws LocalDBException {
        super(new File(appDir, "localdb.sqlite").getAbsolutePath(), VERSION, TIMEOUT_IN_SEC);
    }

    @Override
    public void onCreate(LocalDBTrans trans) {
        trans.execSQL("CREATE TABLE IF NOT EXISTS deskbar (id TEXT PRIMARY KEY, name TEXT, icon TEXT, x TEXT, y TEXT)");
        trans.execSQL("CREATE TABLE IF NOT EXISTS shortcut (id TEXT PRIMARY KEY, deskbarid TEXT, type TEXT, name TEXT, icon TEXT, value TEXT)");

        final Map<String, Object> values = new HashMap<>();
        values.put("id", System.currentTimeMillis());
        values.put("name", "Default");
        values.put("icon", null);
        values.put("x", 400);
        values.put("y", 300);
        trans.insert("deskbar", null, values);
    }

    @Override
    public void onUpgrade(LocalDBTrans trans, int oldVersion, int newVersion) {
    }

}
