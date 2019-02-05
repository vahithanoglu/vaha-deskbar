package com.vahabilisim.deskbar.gui.icon;

import com.vahabilisim.deskbar.OSInfo;
import com.vahabilisim.deskbar.db.model.Deskbar;
import com.vahabilisim.deskbar.db.model.Shortcut;
import com.vahabilisim.deskbar.db.model.ShortcutType;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

public class IconManager {

    private static final Map<Integer, ImageIcon> APP_MAP = new LinkedHashMap<>(); // insertion order is important
    private static final Map<String, Icon> BUTTON_MAP = new HashMap<>();
    private static final Map<ShortcutType, Icon> SHORTCUT_MAP = new HashMap<>();

    private static final String ICON_PKG = "/com/vahabilisim/deskbar/gui/icon";
    private static final String APP_PKG_TEMPLATE = ICON_PKG + "/app/app_icon_%d.png";
    private static final String BUTTON_PKG_TEMPLATE = ICON_PKG + "/button/btn_%s.png";
    private static final String SHORTCUT_PKG_TEMPLATE = ICON_PKG + "/shortcut/shortcut_%s.png";

    static {
        for (int res : new int[]{16, 24, 32, 48, 64, 72, 80, 96, 128}) {
            APP_MAP.put(res, new ImageIcon(IconManager.class.getResource(String.format(APP_PKG_TEMPLATE, res))));
        }

        for (String btn : new String[]{"browse", "choose", "computer", "delete", "deskbar", "edit", "exit", "home", "parent", "run", "save", "shortcut", "settings"}) {
            BUTTON_MAP.put(btn, new ImageIcon(IconManager.class.getResource(String.format(BUTTON_PKG_TEMPLATE, btn))));
        }

        for (ShortcutType type : ShortcutType.values()) {
            SHORTCUT_MAP.put(type, new ImageIcon(IconManager.class.getResource(String.format(SHORTCUT_PKG_TEMPLATE, type.name().toLowerCase()))));
        }
    }

    private static String appDir;

    public static void init(String appDir, List<Deskbar> deskbarList) {
        IconManager.appDir = appDir;

        final Set<String> inUseSet = deskbarList.stream()
                .map(deskbar -> deskbar.getIcon())
                .filter(icon -> null != icon)
                .collect(Collectors.toSet());

        deskbarList.stream()
                .map(deskbar -> deskbar.getShortcuts())
                .flatMap(List::stream)
                .map(shortcut -> shortcut.getIcon())
                .filter(icon -> null != icon)
                .forEach(inUseSet::add);

        Stream.of(new File(appDir).listFiles())
                .filter(file -> file.isFile())
                .filter(file -> file.getName().toLowerCase().endsWith(".png"))
                .filter(file -> false == inUseSet.contains(file.getName()))
                .forEach(file -> {
                    file.delete();
                });
    }

    public static String importAsIcon(File file) {
        final String iconName = System.currentTimeMillis() + ".png";
        return Optional.ofNullable(createCenteredImage(file, 32, 32))
                .filter(image -> writeImage(image, new File(appDir, iconName)))
                .map(image -> iconName)
                .orElse(null);
    }

    public static Icon loadIcon(File file, int w, int h) {
        return Optional.ofNullable(createCenteredImage(file, w, h))
                .map(img -> new ImageIcon(img))
                .orElse(null);
    }

    public static List<Image> getAppIcons() {
        return APP_MAP.values().stream()
                .map(imgIcon -> imgIcon.getImage())
                .collect(Collectors.toList());
    }

    public static Icon getButtonIcon(String button) {
        return Optional.ofNullable(button)
                .map(btn -> BUTTON_MAP.get(btn))
                .orElse(null);
    }

    public static Icon getFileIcon(File file) {
        return Optional.ofNullable(file)
                .filter(f -> f.isFile() || f.isDirectory())
                .map(f -> FileSystemView.getFileSystemView().getSystemIcon(f))
                .orElse(null);
    }

    public static Icon getDeskbarIcon(Deskbar deskbar) {
        Icon icon = Optional.ofNullable(deskbar)
                .map(dskbar -> dskbar.getIcon())
                .filter(iconName -> iconName.endsWith(".png"))
                .map(iconName -> new File(appDir, iconName))
                .filter(file -> file.isFile())
                .map(file -> file.getAbsolutePath())
                .map(path -> (Icon) new ImageIcon(path))
                .orElse(APP_MAP.get(32));
        return icon;
    }

    public static Icon getShortcutIcon(Shortcut shortcut) {
        return Optional.ofNullable(shortcut)
                //
                // custom icon
                .map(shortct -> shortct.getIcon())
                .filter(iconName -> iconName.endsWith(".png"))
                .map(iconName -> new File(appDir, iconName))
                .filter(file -> file.isFile())
                .map(file -> file.getAbsolutePath())
                .map(path -> (Icon) new ImageIcon(path))
                //
                // native icon
                .or(() -> Optional.ofNullable(shortcut)
                .filter(shortct -> OSInfo.isWindows())
                .filter(shortct -> ShortcutType.FOLDER == shortct.getType() || ShortcutType.EXECUTABLE == shortct.getType())
                .map(shortct -> shortct.getValue())
                .map(val -> new File(val))
                .filter(file -> file.isFile() || file.isDirectory())
                .map(file -> FileSystemView.getFileSystemView().getSystemIcon(file)))
                //
                // default icon
                .or(() -> Optional.ofNullable(shortcut)
                .map(shortct -> shortct.getType())
                .map(type -> SHORTCUT_MAP.get(type)))
                .get();
    }

    private static BufferedImage createCenteredImage(File file, int w, int h) {
        return Optional.ofNullable(file)
                .map(f -> readImage(f))
                .map(img -> {
                    final float r = (float) w / (float) h;

                    final int imgW = img.getWidth();
                    final int imgH = img.getHeight();
                    final float imgR = (float) imgW / (float) imgH;

                    final float scale = imgR > r
                            ? (imgW > w ? (float) w / (float) imgW : 1.0f) // gap on top and bottom
                            : (imgH > h ? (float) h / (float) imgH : 1.0f); // gap on left and right

                    final int newW = (int) (imgW * scale);
                    final int newH = (int) (imgH * scale);
                    final int gapLeft = (w - newW) / 2;
                    final int gapTop = (h - newH) / 2;

                    final BufferedImage retVal = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                    final Graphics2D g = retVal.createGraphics();
                    g.drawImage(img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH),
                            gapLeft, gapTop, gapLeft + newW, gapTop + newH, 0, 0, newW, newH, null);
                    g.dispose();

                    return retVal;
                })
                .orElse(null);
    }

    private static BufferedImage readImage(File f) {
        try {
            return ImageIO.read(f);
        } catch (IOException ex) {
            return null;
        }
    }

    private static boolean writeImage(BufferedImage img, File f) {
        try {
            return ImageIO.write(img, "png", f);
        } catch (IOException ex) {
            return false;
        }
    }
}
