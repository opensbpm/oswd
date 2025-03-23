package org.opensbpm.oswd;

public interface Attribute extends  HasName {

     boolean isRequired();

     boolean isReadonly();

    default void accept(OswdVisitor visitor) {
        visitor.visitAttribute(this);
    }
}
