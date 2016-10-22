package org.pockito.scanner.util;

import java.util.List;
import java.util.Map;

public interface MultiMap<T, U> extends Map<T, List<U>> {
    U putSingle(T t, U u);
    U getSingle(T t);
}
