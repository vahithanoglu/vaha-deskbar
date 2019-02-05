package com.vahabilisim.deskbar.gui.filechooser;

import com.vahabilisim.deskbar.OSInfo;
import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomFile extends File {

    public static List<CustomFile> listRootFiles() {
        return Stream.of(File.listRoots())
                .map(f -> new CustomFile(f.getPath()))
                .collect(Collectors.toList());
    }

    public CustomFile(String pathname) {
        super(pathname);
    }

    @Override
    public boolean isDirectory() {
        return super.isDirectory()
                && (false == OSInfo.isMac() || OSInfo.isMac() != "app".equals(getExtension()));
    }

    @Override
    public boolean isFile() {
        return super.isFile()
                || (super.isDirectory() && OSInfo.isMac() == "app".equals(getExtension()));
    }

    @Override
    public File[] listFiles() {
        return Stream.of(super.listFiles())
                .map(f -> new CustomFile(f.getPath()))
                .sorted()
                .toArray(CustomFile[]::new);
    }

    @Override
    public CustomFile[] listFiles(FileFilter filter) {
        return Stream.of(super.listFiles(filter))
                .map(f -> new CustomFile(f.getPath()))
                .sorted()
                .toArray(CustomFile[]::new);
    }

    @Override
    public int compareTo(File pathname) {
        final int val = Boolean.compare(this.isFile(), pathname.isFile());
        switch (val) {
            case 0:
                return this.getName().compareToIgnoreCase(pathname.getName());
            default:
                return val;
        }
    }

    public String getExtension() {
        final String fname = getName();
        final int idx = fname.lastIndexOf('.');
        return idx < 0 ? "" : fname.substring(idx + 1).toLowerCase();
    }

}
