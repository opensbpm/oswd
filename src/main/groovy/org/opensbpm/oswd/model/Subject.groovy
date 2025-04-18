package org.opensbpm.oswd.model

class Subject implements HasName{
    String name
    String role
    List<Taskable> tasks = []

    void accept(OswdVisitor visitor) {
        visitor.visitSubject(this);
        tasks.forEach(task -> task.accept(visitor));
    }

}
