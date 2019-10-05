package com.coq.record.files;

import com.coq.record.files.base.BaseFile;
import com.coq.record.tools.C_;
import com.coq.record.tools.SObj;
import com.coq.record.type.Pointers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data File
 *
 * @author Quaneyc
 */
public class DataFile extends BaseFile {
    public DataFile(File baseDir, String name, String mode) throws FileNotFoundException {
        super(baseDir, name, mode);
    }

    /**
     * Get Record by the <pointer>
     *
     * @param recordMapperFile
     * @param pointer
     * @param tClass     target class-type
     * @param <T>        tClass
     * @return tClass Object
     */
    public <T> T getRecord(RecordMapperFile recordMapperFile, long pointer, Class<T> tClass) {
        // get record-block-mapper
        String rMapper = recordMapperFile.getRecordMapper(pointer);
        if (rMapper == null) {
            return null;
        }
        Map<String, String> valuesMap = new HashMap<>();
        Map<String, Pointers> pointersMap = null;
        try {
            pointersMap = SObj.mapPointers(rMapper, tClass);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        assert pointersMap != null;
        for (String key : pointersMap.keySet()) {
            Pointers ps = pointersMap.get(key);
            if (ps.getLen() != -1) {
                int len = (int) ps.getLen();
                byte[] buff = new byte[len];
                try {
                    seek(ps.getStart());
                    read(buff);
                    String v = new String(buff);
                    valuesMap.put(key, v);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            T newObj = tClass.getDeclaredConstructor().newInstance();
            Field[] fields = tClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Class<?> type = field.getType();
                String typeName = type.getTypeName();
                if (typeName.equals(String.class.getTypeName())) {
                    field.set(newObj, valuesMap.get(fieldName));
                } else if (typeName.equals(Integer.class.getTypeName())) {
                    int value = Integer.parseInt(valuesMap.get(fieldName));
                    field.set(newObj, value);
                } else if (typeName.equals(Long.class.getTypeName())) {
                    long value = Long.parseLong(valuesMap.get(fieldName));
                    field.set(newObj, value);
                } else if (typeName.equals(Float.class.getTypeName())) {
                    float value = Float.parseFloat(valuesMap.get(fieldName));
                    field.set(newObj, value);
                } else if (typeName.equals(Double.class.getTypeName())) {
                    double value = Double.parseDouble(valuesMap.get(fieldName));
                    field.set(newObj, value);
                } else if (typeName.equals(Byte.class.getTypeName())) {
                    byte value = Byte.parseByte(valuesMap.get(fieldName));
                    field.set(newObj, value);
                } else if (typeName.equals(Byte.class.getTypeName())) {
                    byte value = Byte.parseByte(valuesMap.get(fieldName));
                    field.set(newObj, value);
                } else if (typeName.equals(Short.class.getTypeName())) {
                    short value = Short.parseShort(valuesMap.get(fieldName));
                    field.set(newObj, value);
                } else if (typeName.equals(Boolean.class.getTypeName())) {
                    boolean value = Boolean.parseBoolean(valuesMap.get(fieldName));
                    field.set(newObj, value);
                } else if (C_.isRecord(type)) {
                    Pointers ps = pointersMap.get(fieldName);
                    if (ps != null) {
                        long sonPointer = ps.getStart();
                        Object sonRecord = getRecord(recordMapperFile, sonPointer, type);
                        field.set(newObj, sonRecord);
                    }
                } else if (type.isArray()) {
                    Pointers ps = pointersMap.get(fieldName);
                    if (ps != null) {
                        long sonPointer = ps.getStart();
                        List<Object> records = getArrayRecord(recordMapperFile, sonPointer, type.componentType());
                        Object arr = Array.newInstance(type.componentType(), records.size());
                        C_.putArray(records, arr, newObj, field);
                    }
                }
            }
            return newObj;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Object> getArrayRecord(RecordMapperFile recordMapperFile, long sonPointer, Class<?> componentType) {
        List<Object> result = new ArrayList<>();
        String listMapper = recordMapperFile.getRecordMapper(sonPointer);
        List<Integer> listPointers = SObj.getListPointer(listMapper);
        for (Integer pointer : listPointers) {
            Object obj = getRecord(recordMapperFile, pointer, componentType);
            result.add(obj);
        }
        return result;
    }

    public <T> void deleteRecord(RecordMapperFile recordMapperFile, long pointer, Class<T> tClass) {
        recordMapperFile.deleteRecordMapperFrom(pointer);
    }

    public <T> void unDeleteRecord(RecordMapperFile recordMapperFile, long pointer, Class<T> tClass) {
        recordMapperFile.unDeleteRecordMapperFrom(pointer);
    }
}
