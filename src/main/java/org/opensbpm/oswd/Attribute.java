package org.opensbpm.oswd;

public interface Attribute extends HasName{

    AttributeType getAttributeType();

    boolean isRequired();
    boolean isReadonly();

}
