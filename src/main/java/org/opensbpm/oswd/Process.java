package org.opensbpm.oswd;

import java.util.Collection;

public interface Process extends HasName{

    int getVersion();

    Collection<Subject> getSubjects();
}
