package org.opensbpm.oswd.model

class Object implements HasName{
    String name;
    List<Attribute> attributes = []

    void accept(OswdVisitor visitor) {
        visitor.visitObject(this);
        attributes.forEach(attribute -> attribute.accept(visitor));
    }
}
