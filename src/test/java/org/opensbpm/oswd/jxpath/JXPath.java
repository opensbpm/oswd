package org.opensbpm.oswd.jxpath;

import org.apache.commons.jxpath.JXPathContext;

public class JXPath<T> {
    private final JXPathContext jxPathContext;

    public JXPath(T contextBean) {
        this.jxPathContext = JXPathContext.newContext(contextBean);
    }

    public <V> V getValue(Class<V> type, String xpath) {
        Object value = jxPathContext.getValue(xpath);
        return type.cast(value);
    }
}
