package org.opensbpm.oswd;

public interface Task extends HasName {
    void accept(OswdVisitor visitor);
}
