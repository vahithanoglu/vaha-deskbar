package com.vahabilisim.deskbar.gui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public abstract class AbstractListPane<D, I extends AbstractListItem<D>> extends JScrollPane implements MouseListener {

    public abstract I createItem(D data);

    public abstract void onItemSelected(I item);

    public abstract void onItemDoubleClicked(I item);

    private final Map<String, I> itemMap;

    private final JComponent panel;
    private final GroupLayout layout;
    private final ParallelGroup horizontal;
    private final SequentialGroup vertical;

    private boolean autoSelect = true;

    private String selectedItemId;

    public AbstractListPane() {
        super(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        itemMap = new LinkedHashMap<>(); // insertion order is important

        panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);

        layout = new GroupLayout(panel);
        horizontal = layout.createParallelGroup();
        vertical = layout.createSequentialGroup();

        panel.setLayout(layout);
        layout.setHorizontalGroup(horizontal);
        layout.setVerticalGroup(vertical);

        setViewportView(panel);
    }

    public int getCount() {
        return itemMap.size();
    }

    public void setAutoSelect(boolean autoSelect) {
        this.autoSelect = autoSelect;
    }

    public void setList(List<D> list) {
        panel.removeAll();
        itemMap.clear();

        list.forEach(data -> {
            final I item = createItem(data);
            item.addMouseListener(this);

            itemMap.put(item.getUniqueId(), item);
            horizontal.addComponent(item, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
            vertical.addComponent(item);
        });

        revalidate();
        repaint();

        if (autoSelect) {
            selectFirstItem();
        }
    }

    public void addOrUpdate(D data) {
        final I item = createItem(data);
        item.addMouseListener(this);

        Optional.ofNullable(itemMap.put(item.getUniqueId(), item))
                .ifPresentOrElse(prevItem -> {
                    layout.replace(prevItem, item);

                }, () -> {
                    horizontal.addComponent(item, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
                    vertical.addComponent(item);
                });

        revalidate();
        repaint();

        if (autoSelect) {
            setSelectedItem(item);
        }
    }

    public void delete(D data) {
        Optional.ofNullable(itemMap.remove(createItem(data).getUniqueId()))
                .ifPresent(prevItem -> {
                    panel.remove(prevItem);

                    if (autoSelect && prevItem.getUniqueId().equals(selectedItemId)) {
                        selectFirstItem();
                    }
                });

        revalidate();
        repaint();
    }

    private void selectFirstItem() {
        getVerticalScrollBar().setValue(0);
        itemMap.values().stream()
                .findFirst()
                .ifPresent(item -> {
                    setSelectedItem(item);
                });
    }

    private void setSelectedItem(I selectedItem) {
        itemMap.values().stream()
                .forEach(item -> {
                    item.setSeleted(false);
                });
        selectedItem.setSeleted(true);

        if (false == selectedItem.getUniqueId().equals(selectedItemId)) {
            selectedItemId = selectedItem.getUniqueId();
            onItemSelected(selectedItem);
        }
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        switch (evt.getClickCount()) {
            case 2:
                onItemDoubleClicked((I) evt.getComponent());
                break;

            default:
                setSelectedItem((I) evt.getComponent());
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent evt) {
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
    }

    @Override
    public void mouseEntered(MouseEvent evt) {
    }

    @Override
    public void mouseExited(MouseEvent evt) {
    }

}
