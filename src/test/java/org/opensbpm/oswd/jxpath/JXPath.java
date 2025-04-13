package org.opensbpm.oswd.jxpath;

import org.apache.commons.jxpath.JXPathContext;

public class JXPath<T> {
    public static <T> JXPath<T> of(T contextBean) {
        return new JXPath<>(contextBean);
    }

    private final JXPathContext jxPathContext;

    private JXPath(T contextBean) {
        this.jxPathContext = JXPathContext.newContext(contextBean);
    }

    public <V> V getValue(Class<V> type, String xpath) {
        Object value = jxPathContext.getValue(xpath);
        return type.cast(value);
    }
}
