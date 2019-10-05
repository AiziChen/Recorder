package com.coq.record;

/**
 * Constants
 * @author Quanyec
 */
public class C$ {
    // Files
    public static final String RECORD_INDEX_FILE_SUFFIX = ".index";  // auto-generated little index
    public static final String RECORD_METADATA_FILE = "Records.metadata";
    public static final String RECORD_FILE = "Records.record";
    public static final String RECORD_DATA_FILE = "Records.data";
    public static final String DEFAULT_INDEX = "$_";

    // Symbolic Expressions Syntax
    public final static String REC_PREFIX = "(rec-";
    public final static String REC_SUFFIX = ")";
    public static final String META_DATA_PREFIX = "(meta-";
    public static final String DELETED_PREFIX = "[";
    public static final String DELETED_SUFFIX = "]";
}
