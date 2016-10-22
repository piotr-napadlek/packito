package org.pockito.scanner.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MultiHashMap<T, U> extends HashMap<T, List<U>> implements MultiMap<T, U> {

    @Override
    public U putSingle(T t, U u) {
        if (!this.containsKey(t)) {
            this.put(t, new ArrayList<>());
        }
        return this.get(t).add(u) ? u : null;
    }

    @Override
    public U getSingle(T t) {
        return this.get(t) != null && this.get(t).size() > 0 ? this.get(t).get(0) : null;
    }
}
