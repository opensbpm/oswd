package org.opensbpm.oswd.model

class Subject {
    String name
    List<Taskable> tasks = []

    @Override
    String toString() {
        return "Subject(name: $name, tasks: $tasks)"
    }
}
