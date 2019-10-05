package com.coq.record.tools;

import com.coq.record.files.IndexFile;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Default Index Incrementer
 *
 * @author Quanyec
 */
public class DefaultIndexIncrementer {
    private static DefaultIndexIncrementer instance;

    private static HashMap<IndexFile, AtomicLong> maps = new HashMap<>();
    private static IndexFile currentIndexFile;

    private DefaultIndexIncrementer() {
    }

    public static DefaultIndexIncrementer getInstance(IndexFile raf) {
        currentIndexFile = raf;
        if (!maps.keySet().contains(raf)) {
            long[] pointers = raf.readLastIndexAndPointer();
            if (pointers != null) {
                AtomicLong increment = new AtomicLong(pointers[0]);
                maps.put(raf, increment);
            }
        }
        return Objects.requireNonNullElseGet(instance, DefaultIndexIncrementer::new);
    }

    public long incrementNext() {
        return maps.get(currentIndexFile).incrementAndGet();
    }

    public long readValue() {
        return maps.get(currentIndexFile).get();
    }
}
