package com.coq.record;

import com.coq.record.annotation.Record;
import com.coq.record.files.DataFile;
import com.coq.record.files.IndexFile;
import com.coq.record.files.MetaDataFile;
import com.coq.record.files.RecordMapperFile;
import com.coq.record.tools.C_;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

public class Recorder {
    private Configuration configuration;
    private Map<String, IndexFile> indexFiles = new HashMap<>();
    private Map<String, RecordMapper> recordMap = new HashMap<>();

    private RecordMapperFile recordMapperFile;
    private DataFile dataFile;
    private MetaDataFile metaDataFile;

    public Recorder(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Load one Record
     *
     * @param tClass Record-class
     * @return Record
     * @throws FileNotFoundException
     */
    public <T> RecordMapper<T> load(Class<T> tClass) throws FileNotFoundException {
        String className = tClass.getSimpleName();
        if (!C_.isRecord(tClass)) {
            throw new RuntimeException("class `" + className + "` is not a @Record");
        }
        // init `otherFiles`
        String indexFileName = className + C$.DEFAULT_INDEX + C$.RECORD_INDEX_FILE_SUFFIX;
        IndexFile index = new IndexFile(configuration.getDirectory(), indexFileName, "rw");
        indexFiles.put(indexFileName, index);

        if (recordMapperFile == null) {
            recordMapperFile = new RecordMapperFile(configuration.getDirectory(), C$.RECORD_FILE, "rw");
        }
        if (dataFile == null) {
            dataFile = new DataFile(configuration.getDirectory(), C$.RECORD_DATA_FILE, "rw");
        }
        if (metaDataFile == null) {
            metaDataFile = new MetaDataFile(configuration.getDirectory(), C$.RECORD_METADATA_FILE, "rw");
        }

        RecordMapper<T> recordMapper = new <T>RecordMapper<T>(recordMapperFile, dataFile, indexFiles, metaDataFile, tClass, this);
        recordMap.put(className, recordMapper);
        return recordMapper;
    }

    /**
     * @param tClass
     * @return name's Record instance or null when not found
     */
    public RecordMapper findRecord(Class<?> tClass) {
        return recordMap.get(tClass.getSimpleName());
    }

    /**
     * Close all of the opened files.
     *
     * @throws IOException
     */
    public void closeAll() throws IOException {
        if (recordMapperFile != null) {
            recordMapperFile.close();
        }
        if (dataFile != null) {
            dataFile.close();
        }
        if (metaDataFile != null) {
            metaDataFile.close();
        }
        for (RandomAccessFile raf : indexFiles.values()) {
            if (raf != null) {
                raf.close();
            }
        }
    }
}
