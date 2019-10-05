package com.coq.record.files;

import com.coq.record.C$;
import com.coq.record.files.base.BaseFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Record File
 *
 * @author Quanyec
 */
public class RecordMapperFile extends BaseFile {
    public RecordMapperFile(File baseDir, String name, String mode) throws FileNotFoundException {
        super(baseDir, name, mode);
    }

    /**
     * Save pointers
     * stored-pointers syntax: (fieldName <start-point> <length>)
     *
     * @param className
     * @param pointers
     */
    public void saveRecordMapper(String className, Map<String, long[]> pointers) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(C$.REC_PREFIX).append(className);
        for (String key : pointers.keySet()) {
            long[] ps = pointers.get(key);
            sb.append("(").append(key).append(" ")
                    .append(ps[0]).append(" ")
                    .append(ps[1]).append(")");
        }
        sb.append(C$.REC_SUFFIX);
        seek(length());
        write(sb.toString().getBytes());
    }

    public void saveArrayRecordMapper(List<long[]> arrPointers) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (arrPointers.size() <= 0) {
            return;
        }
        sb.append("(list");
        for (long[] ps : arrPointers) {
            sb.append("(")
                    .append(ps[0]).append(" ")
                    .append(ps[1]).append(")");
        }
        sb.append(C$.REC_SUFFIX);
        seek(length());
        write(sb.toString().getBytes());
    }

    /**
     * Get Record-Map
     *
     * @param pointer
     * @return "(<start-point> <end-point>)" or null.
     */
    public String getRecordMapper(long pointer) {
        if (pointer < 0) {
            throw new IndexOutOfBoundsException();
        }
        StringBuilder sb = new StringBuilder();
        try {
            // 跳转到该pointer
            seek(pointer);
            byte[] buff = new byte[1];
            int leftBrackets = 0;
            int rightBrackets = 0;
            while (read(buff) != -1) {
                String v = new String(buff);
                if (v.equals("(")) {
                    leftBrackets++;
                } else if (v.equals(")")) {
                    rightBrackets++;
                } else if (v.equals(C$.DELETED_PREFIX)) {
                    return null;
                }
                sb.append(v);
                if (leftBrackets != 0 && rightBrackets >= leftBrackets) {
                    break;
                }
            }
            // 回到末尾
            seek(length());
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get length
     *
     * @param pointer
     * @return
     * @throws IOException
     */
    private int getMapperLength(long pointer) throws IOException {
        seek(pointer);
        int len = 0;
        int leftBracket = 0;
        int rightBracket = 0;
        byte[] buff = new byte[1];
        while (read(buff) != -1) {
            len++;
            String ns = new String(buff);
            if (ns.equals("(")) {
                leftBracket++;
            } else if (ns.equals(")")) {
                rightBracket++;
            }
            if (leftBracket != 0 && rightBracket >= leftBracket) {
                return len;
            }
        }
        return -1;
    }

    /**
     * Delete record mapper from pointer
     *
     * @param pointer
     */
    public void deleteRecordMapperFrom(long pointer) {
        if (pointer < 0) {
            throw new IndexOutOfBoundsException();
        }
        try {
            seek(pointer);
            int leftBracket = 0;
            int rightBracket = 0;
            byte[] buff = new byte[1];
            while (read(buff) != -1) {
                String ns = new String(buff);
                if (ns.equals("(")) {
                    leftBracket++;
                    seek(pointer);
                    write(C$.DELETED_PREFIX.getBytes());
                } else if (ns.equals(")")) {
                    rightBracket++;
                    seek(pointer);
                    write(C$.DELETED_SUFFIX.getBytes());
                } else if (ns.equals(C$.DELETED_PREFIX)) {
                    break;
                }
                pointer++;
                if (leftBracket != 0 && rightBracket >= leftBracket) {
                    break;
                }
            }
            seek(length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unDeleteRecordMapperFrom(long pointer) {
        if (pointer < 0) {
            throw new IndexOutOfBoundsException();
        }
        try {
            seek(pointer);
            int leftBracket = 0;
            int rightBracket = 0;
            byte[] buff = new byte[1];
            while (read(buff) != -1) {
                String ns = new String(buff);
                if (ns.equals(C$.DELETED_PREFIX)) {
                    leftBracket++;
                    seek(pointer);
                    write("(".getBytes());
                } else if (ns.equals(C$.DELETED_SUFFIX)) {
                    rightBracket++;
                    seek(pointer);
                    write(")".getBytes());
                } else if (ns.equals(")")) {
                    break;
                }
                pointer++;
                if (leftBracket != 0 && rightBracket >= leftBracket) {
                    break;
                }
            }
            seek(length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
