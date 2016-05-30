package com.benbr;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BiMap implements Map {

    // Unsure about how generics work here.
    // Tried using <K, V>, but all of the implemented methods require <Object, Object>
    private Map<Object, Object> forwards = new HashMap<>();
    private Map<Object, Object> backwards = new HashMap<>();

    @Override
    public int size() {
        return forwards.size();
    }

    @Override
    public boolean isEmpty() {
        throw new  NotImplementedException();
    }

    @Override
    public boolean containsKey(Object key) {
        throw new  NotImplementedException();
    }

    @Override
    public boolean containsValue(Object value) {
        throw new  NotImplementedException();
    }

    @Override
    public Object get(Object key) {
        throw new  NotImplementedException();
    }

    @Override
    public Object put(Object key, Object value) {
        backwards.put(value, key);
        return forwards.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        throw new  NotImplementedException();
    }

    @Override
    public void putAll(Map m) {
        throw new  NotImplementedException();
    }

    @Override
    public void clear() {
        forwards.clear();
        backwards.clear();
    }

    @Override
    public Set keySet() {
        throw new  NotImplementedException();
    }

    @Override
    public Collection values() {
        throw new  NotImplementedException();
    }

    @Override
    public Set<Entry> entrySet() {
        throw new  NotImplementedException();
    }

    public Object getForwards(Object key) {
        return forwards.get(key);
    }

    public Object getBackwards(Object key) {
        return backwards.get(key);
    }
}
