package com.vahabilisim.deskbar.gui;

import javax.swing.JPanel;

public abstract class AbstractListItem<D> extends JPanel {

    public abstract D getData();

    public abstract String getUniqueId();

    public abstract void setSeleted(boolean selected);
}
