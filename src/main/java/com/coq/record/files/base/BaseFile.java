package com.coq.record.files.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * BaseFile
 * @author Quanyec
 */
public class BaseFile extends RandomAccessFile {
    public BaseFile(File baseFile, String name, String mode) throws FileNotFoundException {
        super(baseFile.getAbsolutePath() + "/" + name, mode);
    }
}
