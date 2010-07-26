package org.nexusbpm.service.excel;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class LRUHashMap<K, V> extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = 1l;
    
    private transient final int maxCapacity;
    
    public LRUHashMap(final int initialCapacity, final int maxCapacity) {
        super(initialCapacity);
        this.maxCapacity = maxCapacity;
    }
    
    @Override
    protected boolean removeEldestEntry(final Entry<K, V> eldest) {
        return size() > maxCapacity;
    }
}
