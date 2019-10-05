package com.coq.record.files;

import com.coq.record.C$;
import com.coq.record.files.base.BaseFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Index file
 *
 * @author Quanyec
 */
public class IndexFile extends BaseFile {
    public IndexFile(File baseDir, String name, String mode) throws FileNotFoundException {
        super(baseDir, name, mode);
    }

    /**
     * save index&pointer
     * @param index
     * @param pointer
     */
    public void writeIndexAndPoint(String index, long pointer) {
        try {
            String indexStr = "(" + index + " " + pointer + ")";
            seek(length());
            write(indexStr.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * read the last-index's pointer
     *
     * @return null if error occurs, else number is that pointer
     */
    public long[] readLastIndexAndPointer() {
        try {
            long preP = getFilePointer();
            long len = length();
            if (len > 20) {
                seek(length() - 20);
            } else {
                seek(0);
            }
            String endStr = readLine();
            seek(preP);
            if (endStr == null) {
                return new long[]{0, 0};
            }
            String[] lastEle = endStr.split("\\(");
            String last = lastEle[lastEle.length - 1];
            String[] pointers = last.split("\\s+");
            long first = Long.parseLong(pointers[0]);
            String secondStr = pointers[1].substring(0, pointers[1].length() - 1);
            long second = Long.parseLong(secondStr);
            return new long[]{first, second};
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Find pointers by index
     *
     * @param index
     * @return -1 if errors occurs, or n when successful
     */
    public long findPointerByIndex(long index) {
        if (index <= 0) {
            throw new IndexOutOfBoundsException();
        }
        try {
            seek(0);
            while (true) {
                long[] indexs = readNextIndex();
                if (indexs != null) {
                    if (indexs[0] == index) {
                        seek(length());
                        return indexs[1];
                    }
                } else {
                    return -1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private long[] readNextIndex() {
        boolean flag = false;
        int len = -1;
        try {
            byte[] buff = new byte[1];
            while (read(buff) != -1) {
                String v = new String(buff);
                if (v.equals("(")) {
                    flag = true;
                } else if (v.equals(")")) {
                    break;
                }
                if (flag) {
                    len++;
                }
            }
            // 无blocks
            if (len <= -1) {
                return null;
            }
            seek(getFilePointer() - len - 1);
            byte[] buff2 = new byte[len];
            read(buff2);
            // current index is `(`, so we need increase the pointer by 1
            seek(getFilePointer() + 1);
            String indexStr = new String(buff2);
            String[] indexs = indexStr.split("\\s+");
            return new long[]{Long.parseLong(indexs[0]), Long.parseLong(indexs[1])};
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete(long pointer) {
        if (pointer <= 0) {
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
