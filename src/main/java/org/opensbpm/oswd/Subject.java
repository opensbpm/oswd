package org.opensbpm.oswd;

import java.util.Collection;

public interface Subject extends HasName {

    Role getRole();

    Collection<Task> getTasks();

    public interface Role extends HasName{
    }
}
