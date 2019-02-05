package com.vahabilisim.deskbar.db.model;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Deskbar {

    public final long id;
    private String name;
    private String icon;
    private int x;
    private int y;
    private Map<Long, Shortcut> shortcutMap;

    public Deskbar(long id, String name, String icon, int x, int y, List<Shortcut> shortcutList) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.x = x;
        this.y = y;

        shortcutMap = new LinkedHashMap<>(); // insertion order is important
        shortcutList.forEach(shortcut -> {
            shortcutMap.put(shortcut.id, shortcut);
        });
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Shortcut> getShortcuts() {
        return new LinkedList<>(shortcutMap.values());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void addOrUpdateShortcut(Shortcut shortcut) {
        Optional.ofNullable(shortcut)
                .ifPresent(shortct -> {
                    shortcutMap.put(shortct.id, shortct);
                });
    }

    public void deleteShortcut(Shortcut shortcut) {
        Optional.ofNullable(shortcut)
                .ifPresent(shortct -> {
                    shortcutMap.remove(shortct.id);
                });
    }

}
