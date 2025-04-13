package org.opensbpm.oswd.model

class Task implements Taskable{
    String name
    String proceedTo
    Object object

    @Override
    String toString() {
        return "Task(name: $name)"
    }
}
