package org.opensbpm.oswd.model

class Task implements HasName, Taskable {
    String name
    Object object
    List<ProceedTo> proceedTos = []

    @Override
    void accept(OswdVisitor visitor) {
        visitor.visitTask(this);
        if (object != null) {
            visitor.visitObject(object)
        }
        proceedTos.forEach(proceedTo -> proceedTo.accept(visitor))
    }

    @Override
    String toString() {
        return "Task(name: $name)"
    }
}
