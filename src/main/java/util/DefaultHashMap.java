package util;

import java.util.HashMap;

// THis implementation of a util.DefaultHashMap was taken from https://stackoverflow.com/a/7519422
public class DefaultHashMap<K,V> extends HashMap<K,V> {
    protected V defaultValue;
    public DefaultHashMap(V defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public V get(Object k) {
        return containsKey(k) ? super.get(k) : defaultValue;
    }
}
