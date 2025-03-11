package org.opensbpm.oswd;

import java.util.Collection;

public interface Subject extends HasName {

    Role getRole();

    Collection<Task> getTasks();

    default void accept(OswdVisitor visitor) {
        visitor.visitSubject(this);
        getRole().accept(visitor);
        getTasks().forEach(task -> task.accept(visitor));
    }

    public interface Role extends HasName {
        default void accept(OswdVisitor visitor) {
            visitor.visitRole(this);
        }
    }
}
