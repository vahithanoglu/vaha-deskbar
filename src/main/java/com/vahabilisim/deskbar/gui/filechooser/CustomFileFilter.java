package com.vahabilisim.deskbar.gui.filechooser;

import java.io.File;
import java.io.FileFilter;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomFileFilter implements FileFilter {

    private final String description;
    private final Set<String> extensions;

    private boolean directoryOnly;

    public CustomFileFilter(String desc, String... exts) {
        description = desc;
        extensions = Stream.of(exts)
                .map(e -> e.toLowerCase())
                .collect(Collectors.toSet());
    }

    public void setDirectoryOnly(boolean onlyDirectory) {
        this.directoryOnly = onlyDirectory;
    }

    public boolean isDirectoryOnly() {
        return directoryOnly;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean accept(File pathname) {
        return Optional.ofNullable(pathname)
                .map(f -> new CustomFile(f.getPath()))
                .filter(f -> false == f.isHidden())
                .filter(f -> f.isDirectory() || (false == directoryOnly && f.isFile() && extensions.contains(f.getExtension())))
                .isPresent();
    }

}
