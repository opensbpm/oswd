package org.opensbpm.oswd.model

class Process {
    String name
    int version
    String description
    List<Subject> subjects = []

    @Override
    String toString() {
        return "Process(name: $name, version: $version, description: $description, subjects: $subjects)"
    }
}
