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


    @Override
    String toString() {
        return "Process(name: $name, version: $version, description: $description, subjects: $subjects)"
    }
}
