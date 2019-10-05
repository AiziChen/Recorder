package com.coq.record.files;

import com.coq.record.files.base.BaseFile;

import java.io.File;
import java.io.FileNotFoundException;

public class MetaDataFile extends BaseFile {
    public MetaDataFile(File baseDir, String name, String mode) throws FileNotFoundException {
        super(baseDir, name, mode);
    }
}
