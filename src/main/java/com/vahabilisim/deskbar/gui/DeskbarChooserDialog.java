package com.vahabilisim.deskbar.gui;

import com.vahabilisim.deskbar.db.model.Deskbar;
import com.vahabilisim.deskbar.gui.icon.IconManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

public class DeskbarChooserDialog extends JDialog implements ActionListener {

    private final JButton chooseButton;
    private final AbstractListPane<Deskbar, DeskbarListItem> deskbarListPane;

    private Deskbar selectedDeskbar;
    private Deskbar choosenDeskbar;

    public DeskbarChooserDialog(JFrame parent) {
        super(parent, "Choose Deskbar", true);

        chooseButton = new JButton("Choose", IconManager.getButtonIcon("choose"));
        chooseButton.setActionCommand("Choose Deskbar");
        chooseButton.setToolTipText("Choose the deskbar");
        chooseButton.addActionListener(this);

        deskbarListPane = new AbstractListPane<Deskbar, DeskbarListItem>() {
            @Override
            public DeskbarListItem createItem(Deskbar data) {
                return new DeskbarListItem(data);
            }

            @Override
            public void onItemSelected(DeskbarListItem item) {
                selectedDeskbar = item.getData();
            }

            @Override
            public void onItemDoubleClicked(DeskbarListItem item) {
            }

            @Override
            public void onItemRightClicked(DeskbarListItem item, int x, int y) {
            }
        };
        deskbarListPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        setLayout();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        switch (evt.getActionCommand()) {
            case "Choose Deskbar":
                Optional.ofNullable(selectedDeskbar)
                        .ifPresent(deskbar -> {
                            choosenDeskbar = deskbar;
                            setVisible(false);
                        });
                break;
        }
    }

    public Deskbar chooseDeskbar(List<Deskbar> deskbarList) {
        selectedDeskbar = null;
        choosenDeskbar = null;

        deskbarListPane.setList(deskbarList);

        setLocationRelativeTo(getOwner());
        setVisible(true);

        return choosenDeskbar;
    }

    private void setLayout() {
        final GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGap(8)
                .addGroup(layout.createParallelGroup()
                        .addComponent(deskbarListPane, 200, 200, 200)
                        .addComponent(chooseButton, 200, 200, 200))
                .addGap(8)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGap(8)
                .addComponent(deskbarListPane, 200, 200, 200)
                .addGap(8)
                .addComponent(chooseButton, 32, 32, 32)
                .addGap(8)
        );

        pack();

        setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }
}
