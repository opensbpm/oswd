package org.opensbpm.oswd.model

class Message {
    String object
    String proceedTo

    void accept(OswdVisitor visitor) {
        visitor.visitMessage(this);
    }
}
