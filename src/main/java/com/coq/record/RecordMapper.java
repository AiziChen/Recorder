package com.coq.record;

import com.coq.record.files.DataFile;
import com.coq.record.files.IndexFile;
import com.coq.record.files.MetaDataFile;
import com.coq.record.files.RecordMapperFile;
import com.coq.record.tools.C_;
import com.coq.record.tools.DefaultIndexIncrementer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordMapper<T> {

    private RecordMapperFile recordMapperFile;
    private DataFile dataFile;
    private Map<String, IndexFile> indexFiles;
    private MetaDataFile metaDataFile;
    private Class<T> tClass;
    private Recorder recorder;
    private Map<String, long[]> recordRefs = new HashMap<>();
    private static Map<Object, long[]> records = new HashMap<>();

    private String arrayClassName;
    private static List<long[]> arrayRefs = new ArrayList<>();

    protected RecordMapper(RecordMapperFile recordMapperFile, DataFile dataFile, Map<String, IndexFile> indexFiles, MetaDataFile metaDataFile, Class<T> tClass, Recorder recorder) {
        this.recordMapperFile = recordMapperFile;
        this.dataFile = dataFile;
        this.indexFiles = indexFiles;
        this.metaDataFile = metaDataFile;
        this.tClass = tClass;
        this.recorder = recorder;
        this.arrayClassName = "";
    }

    /**
     * insert new record
     *
     * @param newT the new-record
     */
    public void insert(T newT) {
        if (newT == null) {
            throw new NullPointerException();
        }
        String className = tClass.getSimpleName();
        Field[] fields = tClass.getDeclaredFields();
        /* save the data */
        try {
            if (fields.length >= 1) {
                dataFile.seek(dataFile.length());
            } else {
                return;
            }
            for (Field field : fields) {
                field.setAccessible(true);
                String fName = field.getName();
                Object fValue = field.get(newT);
                if (C_.isBaseType(fValue)) {
                    String value = fValue.toString();
                    long pre = dataFile.length();
                    // write data
                    dataFile.write(value.getBytes());
                    long len = dataFile.length() - pre;
                    recordRefs.put(fName, new long[]{pre, len});
                } else if (C_.isRecord(fValue)) {
                    if (!records.containsKey(fValue)) {
                        long[] ps = new long[]{recordMapperFile.length(), -1};
                        recordRefs.put(fName, ps);
                        RecordMapper<Object> r = recorder.findRecord(fValue.getClass());
                        r.insert(fValue);
                        records.put(fValue, ps);
                    } else {
                        long[] ps = records.get(fValue);
                        recordRefs.put(fName, ps);
                    }
                } else if (fValue.getClass().isArray()) {
                    Class<?> componentType = fValue.getClass().getComponentType();
                    Object[] arr = (Object[]) fValue;
                    RecordMapper<Object> r = recorder.findRecord(componentType);
                    for (Object o : arr) {
                        r.setArrayClassName(componentType.getSimpleName());
                        r.insert(o);
                        recordRefs.put(fName, new long[]{recordMapperFile.length(), -1});
                    }
                    recordMapperFile.saveArrayRecordMapper(arrayRefs);
                    arrayRefs.clear();
                }
            }
            if (arrayClassName.equals(className)) {
                arrayRefs.add(new long[]{recordMapperFile.length(), -1});
            }
            writeIndex(className, recordMapperFile.length());
            if (recordRefs.size() > 0) {
                recordMapperFile.saveRecordMapper(className, recordRefs);
                recordRefs.clear();
            }
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void update(T t, long index) {
    }

    public void delete(long index) {
        String className = tClass.getSimpleName();
        IndexFile raf = indexFiles.get(className + C$.DEFAULT_INDEX + C$.RECORD_INDEX_FILE_SUFFIX);
        long pointer = raf.findPointerByIndex(index);
        dataFile.deleteRecord(recordMapperFile, pointer, tClass);
    }

    public void unDelete(long index) {
        String className = tClass.getSimpleName();
        IndexFile raf = indexFiles.get(className + C$.DEFAULT_INDEX + C$.RECORD_INDEX_FILE_SUFFIX);
        long pointer = raf.findPointerByIndex(index);
        dataFile.unDeleteRecord(recordMapperFile, pointer, tClass);
    }

    public T find(long index) {
        String className = tClass.getSimpleName();
        IndexFile raf = indexFiles.get(className + C$.DEFAULT_INDEX + C$.RECORD_INDEX_FILE_SUFFIX);
        long pointer = raf.findPointerByIndex(index);
        return dataFile.getRecord(recordMapperFile, pointer, tClass);
    }

    public long size() {
        return -1;
    }


    private void setArrayClassName(String name) {
        this.arrayClassName = name;
    }

    /* save the default index */
    private void writeIndex(String className, long recordPointer) {
        IndexFile raf = indexFiles.get(className + C$.DEFAULT_INDEX + C$.RECORD_INDEX_FILE_SUFFIX);
        long index = DefaultIndexIncrementer.getInstance(raf).incrementNext();
        raf.writeIndexAndPoint(index + "", recordPointer);
    }
}
