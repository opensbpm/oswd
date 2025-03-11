package org.opensbpm.oswd;

import java.util.Collection;

public interface Process extends HasName {

    int getVersion();

    String getDescription();

    Collection<Subject> getSubjects();

    default void accept(OswdVisitor visitor) {
        visitor.visitProcess(this);
        getSubjects().forEach(subject -> subject.accept(visitor));
    }
}
