package org.opensbpm.oswd.model

class Process implements HasName{
    String name
    int version
    String description
    List<Subject> subjects = []


    void accept(OswdVisitor visitor) {
        visitor.visitProcess(this);
        subjects.forEach(subject -> subject.accept(visitor));
    }

}
