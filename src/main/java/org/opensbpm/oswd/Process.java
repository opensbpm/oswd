package org.opensbpm.oswd;

import java.util.Collection;

public interface Process {

    String getName();

    int getVersion();

    Collection<Subject> getSubjects();
}
