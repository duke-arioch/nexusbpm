package com.nexusbpm.services.excel;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class LRUHashMap<K, V> extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = 1l;
    
    private int maxCapacity;
    
    public LRUHashMap(int initialCapacity, int maxCapacity) {
        super(initialCapacity);
        this.maxCapacity = maxCapacity;
    }
    
    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest) {
        return size() > maxCapacity;
    }
}
