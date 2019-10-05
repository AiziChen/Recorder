package com.coq.record;

import java.io.File;
import java.nio.file.NotDirectoryException;

public class Configuration {
    private static Configuration instance;

    private File directory;

    private Configuration() {
    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
            return instance;
        } else {
            return instance;
        }
    }

    public Configuration setStoredLocation(String directory) {
        return setStoredLocation(new File(directory));
    }

    public Configuration setStoredLocation(File directory) {
        if (!directory.isDirectory()) {
            try {
                throw new NotDirectoryException(directory.getAbsolutePath() + " is not a directory!");
            } catch (NotDirectoryException e) {
                e.printStackTrace();
            }
        } else {
            this.directory = directory;
        }
        return instance;
    }

    public File getDirectory() {
        return this.directory;
    }
}
