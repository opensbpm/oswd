package org.opensbpm.oswd.model

class Receive implements Taskable {
    String name
    List<Message> messages = []

    @Override
    void accept(OswdVisitor visitor) {
        visitor.visitReceive(this);
    }

}
