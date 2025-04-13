package org.opensbpm.oswd.model

class Attribute implements HasName{
    String name
    AttributeType type
    boolean readonly
    boolean required

    void accept(OswdVisitor visitor) {
        visitor.visitAttribute(this)
    }
}
