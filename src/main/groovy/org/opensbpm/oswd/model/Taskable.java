package org.opensbpm.oswd.model;

public interface Taskable extends HasName {

    void accept(OswdVisitor visitor);
}
