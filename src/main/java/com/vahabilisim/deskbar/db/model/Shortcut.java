package com.vahabilisim.deskbar.db.model;

public class Shortcut {

    public final long id;
    private ShortcutType type;
    private String name;
    private String icon;
    private String value;

    public Shortcut(long id, ShortcutType type, String name, String icon, String value) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.icon = icon;
        this.value = value;
    }

    public ShortcutType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getValue() {
        return value;
    }

    public void setType(ShortcutType type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
