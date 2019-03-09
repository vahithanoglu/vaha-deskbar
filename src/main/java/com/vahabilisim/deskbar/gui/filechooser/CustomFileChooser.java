package com.vahabilisim.deskbar.gui.filechooser;

import com.vahabilisim.deskbar.OSInfo;
import com.vahabilisim.deskbar.gui.AbstractListPane;
import com.vahabilisim.deskbar.gui.FileListItem;
import com.vahabilisim.deskbar.gui.icon.IconManager;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

public class CustomFileChooser extends JDialog implements ActionListener {

    private final static CustomFileFilter FOLDER_FILTER = new CustomFileFilter("Folders", "*");
    private final static CustomFileFilter IMAGE_FILTER = new CustomFileFilter("PNG Images (*.png)", "png");

    private final static CustomFileFilter MAC_EXE_FILTER = new CustomFileFilter("Applications (*.app)", "app");
    private final static CustomFileFilter UNX_EXE_FILTER = new CustomFileFilter("Bash Scripts (*.sh)", "sh");
    private final static CustomFileFilter WIN_EXE_FILTER = new CustomFileFilter("Executables (*.exe, *.bat)", "exe", "bat");

    static {
        FOLDER_FILTER.setDirectoryOnly(true);
    }

    private final JButton computerButton;
    private final JButton homeButton;
    private final JButton parentButton;
    private final JLabel currDirLabel;
    private final JComboBox<String> filterComboBox;
    private final JButton chooseButton;

    private final AbstractListPane<CustomFile, FileListItem> fileListPane;
    private final JLabel previewLabel;

    private CustomFile currentDir;
    private CustomFile selectedFile;
    private CustomFile choosenFile;
    private CustomFileFilter fileFilter;

    public CustomFileChooser(JFrame parent) {
        super(parent, "Choose File", true);

        computerButton = new JButton("Computer", IconManager.getButtonIcon("computer"));
        computerButton.setActionCommand("Computer");
        computerButton.setToolTipText("Go to the computer");
        computerButton.addActionListener(this);

        homeButton = new JButton("Home Folder", IconManager.getButtonIcon("home"));
        homeButton.setActionCommand("Home Folder");
        homeButton.setToolTipText("Go to the user home");
        homeButton.addActionListener(this);

        parentButton = new JButton(IconManager.getButtonIcon("parent"));
        parentButton.setActionCommand("Parent Directory");
        parentButton.setToolTipText("Go to the parent directory");
        parentButton.addActionListener(this);

        currDirLabel = new JLabel();
        filterComboBox = new JComboBox<>();

        chooseButton = new JButton("Choose", IconManager.getButtonIcon("choose"));
        chooseButton.setActionCommand("Choose File");
        chooseButton.setToolTipText("Choose the file");
        chooseButton.addActionListener(this);

        fileListPane = new AbstractListPane<CustomFile, FileListItem>() {
            @Override
            public FileListItem createItem(CustomFile data) {
                return new FileListItem(data);
            }

            @Override
            public void onItemSelected(FileListItem item) {
                setSelectedFile(item.getData());
            }

            @Override
            public void onItemDoubleClicked(FileListItem item) {
                Optional.ofNullable(item.getData())
                        .filter(file -> file.isDirectory())
                        .ifPresent(file -> {
                            setCurrentDirectory(file.getAbsolutePath());
                        });
            }

            @Override
            public void onItemRightClicked(FileListItem item, int x, int y) {
            }
        };

        fileListPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        previewLabel = new JLabel();
        previewLabel.setBackground(Color.RED);

        setLayout();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        switch (evt.getActionCommand()) {
            case "Computer":
                currentDir = new CustomFile("");
                currDirLabel.setText("Computer");
                fileListPane.setList(CustomFile.listRootFiles());
                setSelectedFile(null);
                break;

            case "Home Folder":
                setCurrentDirectory(OSInfo.getUserHome());
                break;

            case "Parent Directory":
                setCurrentDirectory(currentDir.getParent());
                break;

            case "Choose File":
                Optional.ofNullable(selectedFile)
                        .ifPresent(file -> {
                            choosenFile = file;
                            setVisible(false);
                        });
                break;
        }
    }

    public File chooseFolder() {
        fileFilter = FOLDER_FILTER;
        setPreviewEnabled(false);
        return chooseFile();
    }

    public File chooseImage() {
        fileFilter = IMAGE_FILTER;
        setPreviewEnabled(true);
        return chooseFile();
    }

    public File chooseExecutable() {
        if (OSInfo.isMac()) {
            fileFilter = MAC_EXE_FILTER;

        } else if (OSInfo.isUnix()) {
            fileFilter = UNX_EXE_FILTER;

        } else if (OSInfo.isWindows()) {
            fileFilter = WIN_EXE_FILTER;
        }

        setPreviewEnabled(false);
        return chooseFile();
    }

    private void setSelectedFile(CustomFile file) {
        selectedFile = Optional.ofNullable(file)
                .filter(f -> fileFilter.isDirectoryOnly() == f.isDirectory())
                .orElse(null);

        previewLabel.setIcon(IconManager.loadIcon(
                selectedFile,
                previewLabel.getWidth(),
                previewLabel.getHeight()));
    }

    private void setPreviewEnabled(boolean enabled) {
        previewLabel.setVisible(enabled);
        setLayout(); // reset layout due to change of preview visibility
    }

    private File chooseFile() {
        choosenFile = null;

        setSelectedFile(null);

        filterComboBox.removeAllItems();
        filterComboBox.addItem(fileFilter.getDescription());

        setCurrentDirectory(OSInfo.getUserHome());
        setLocationRelativeTo(getOwner());
        setVisible(true);

        return choosenFile;
    }

    private void setCurrentDirectory(String dirPath) {
        Optional.ofNullable(dirPath)
                .map(path -> new CustomFile(path))
                .filter(file -> file.isDirectory())
                .ifPresent(file -> {
                    selectedFile = null;
                    currentDir = file;
                    currDirLabel.setText(currentDir.getAbsolutePath());
                    currDirLabel.setToolTipText(currentDir.getAbsolutePath());
                    fileListPane.setList(List.of(currentDir.listFiles(fileFilter)));
                });
    }

    private void setLayout() {
        final GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGap(8)
                .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(computerButton)
                                .addGap(4)
                                .addComponent(homeButton))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(parentButton, 32, 32, 32)
                                .addGap(4)
                                .addComponent(currDirLabel, 360, 360, 360))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(fileListPane, 400, 400, 400)
                                .addGap(4)
                                .addComponent(previewLabel, 160, 160, 160))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(filterComboBox, 240, 240, 240)
                                .addGap(4, 4, Short.MAX_VALUE)
                                .addComponent(chooseButton)))
                .addGap(8)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGap(8)
                .addGroup(layout.createParallelGroup()
                        .addComponent(computerButton, 32, 32, 32)
                        .addComponent(homeButton, 32, 32, 32))
                .addGap(4)
                .addGroup(layout.createParallelGroup()
                        .addComponent(parentButton, 32, 32, 32)
                        .addComponent(currDirLabel, 32, 32, 32))
                .addGap(4)
                .addGroup(layout.createParallelGroup()
                        .addComponent(fileListPane, 300, 300, 300)
                        .addComponent(previewLabel, GroupLayout.Alignment.CENTER, 160, 160, 160))
                .addGap(4)
                .addGroup(layout.createParallelGroup()
                        .addComponent(filterComboBox, 32, 32, 32)
                        .addComponent(chooseButton, 32, 32, 32))
                .addGap(8)
        );

        pack();

        setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }
}
