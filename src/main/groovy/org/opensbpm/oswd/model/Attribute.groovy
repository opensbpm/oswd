package org.opensbpm.oswd.model

import org.opensbpm.oswd.AttributeType

class Attribute implements HasName{
    String name
    AttributeType type
    boolean readonly
    boolean required

    void accept(OswdVisitor visitor) {
        visitor.visitAttribute(this)
    }
}
